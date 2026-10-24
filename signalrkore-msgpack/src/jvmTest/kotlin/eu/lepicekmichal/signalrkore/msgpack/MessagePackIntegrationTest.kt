package eu.lepicekmichal.signalrkore.msgpack

import eu.lepicekmichal.signalrkore.HubConnection
import eu.lepicekmichal.signalrkore.HubConnectionBuilder
import eu.lepicekmichal.signalrkore.HubConnectionState
import eu.lepicekmichal.signalrkore.HubException
import eu.lepicekmichal.signalrkore.HubMessage
import eu.lepicekmichal.signalrkore.HubValue
import eu.lepicekmichal.signalrkore.Logger
import eu.lepicekmichal.signalrkore.TransportEnum
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondText
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.readBytes
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

class WebSocketsMessagePackTest : MessagePackIntegrationTest(TransportEnum.WebSockets)

class LongPollingMessagePackTest : MessagePackIntegrationTest(TransportEnum.LongPolling)

abstract class MessagePackIntegrationTest(private val transport: TransportEnum) {

    @Serializable
    enum class Priority { Low, High }

    @Serializable
    data class Note(
        @SerialName("Text") val text: String,
        @SerialName("Created") val created: Instant,
        @SerialName("Priority") val priority: Priority,
    )

    private val note = Note("hello", Instant.fromEpochSeconds(1600000000, 250_000_000), Priority.High)

    private val protocol = MessagePackHubProtocol(logger = { _, _, _ -> })

    private var server: EmbeddedServer<*, *>? = null

    private val handshakes = Channel<String>(Channel.UNLIMITED)
    private val fromClient = Channel<HubMessage>(Channel.UNLIMITED)
    private val toClient = Channel<ByteArray>(Channel.UNLIMITED)
    private val polls = AtomicInteger(0)

    @Volatile
    private var handshakeDone = false

    @AfterTest
    fun stopServer() {
        server?.stop(0, 0)
    }

    private suspend fun receivePayload(payload: ByteArray, sentWithHandshakeResponse: ByteArray) {
        if (handshakeDone) return protocol.parseMessages(payload).forEach { fromClient.send(it) }

        val separator = payload.indexOf(0x1E)
        handshakes.send(payload.decodeToString(0, separator))
        handshakeDone = true
        toClient.send("{}\u001e".encodeToByteArray() + sentWithHandshakeResponse)
        protocol.parseMessages(payload.copyOfRange(separator + 1, payload.size)).forEach { fromClient.send(it) }
    }

    private fun startHub(sentWithHandshakeResponse: ByteArray = ByteArray(0)): Int {
        val module: Application.() -> Unit = {
            install(WebSockets)
            routing {
                post("/hub/negotiate") {
                    call.respondText(
                        """{"negotiateVersion":1,"connectionId":"abc","connectionToken":"token","availableTransports":[""" +
                            """{"transport":"ServerSentEvents","transferFormats":["Text"]},""" +
                            """{"transport":"$transport","transferFormats":["Text","Binary"]}]}""",
                        ContentType.Application.Json,
                    )
                }
                if (transport == TransportEnum.WebSockets) {
                    webSocket("/hub") {
                        launch { for (payload in toClient) send(Frame.Binary(true, payload)) }
                        for (frame in incoming) receivePayload(frame.readBytes(), sentWithHandshakeResponse)
                    }
                } else {
                    get("/hub") {
                        if (polls.getAndIncrement() == 0) return@get call.respondBytes(ByteArray(0))
                        val payload = withTimeoutOrNull(2.seconds) { toClient.receiveCatching() }
                        when {
                            payload == null -> call.respondBytes(ByteArray(0))
                            payload.isClosed -> call.respond(HttpStatusCode.NoContent)
                            else -> call.respondBytes(payload.getOrThrow(), ContentType.Application.OctetStream)
                        }
                    }
                    post("/hub") {
                        receivePayload(call.receive<ByteArray>(), sentWithHandshakeResponse)
                        call.respond(HttpStatusCode.OK, "")
                    }
                    delete("/hub") {
                        toClient.close()
                        call.respond(HttpStatusCode.Accepted, "")
                    }
                }
            }
        }
        val server = embeddedServer(CIO, port = 0, host = "127.0.0.1", module = module).start(wait = false)
        this.server = server
        return runBlocking { server.engine.resolvedConnectors().first().port }
    }

    private fun connection(port: Int): HubConnection = HubConnectionBuilder.create("http://127.0.0.1:$port/hub") {
        transports = setOf(TransportEnum.ServerSentEvents, transport)
        protocol = MessagePackHubProtocol(logger = Logger { _, _, _ -> })
    }

    private suspend fun sendToClient(message: HubMessage) = toClient.send(protocol.writeMessage(message))

    private suspend fun receiveFromClient(): HubMessage = withTimeout(5.seconds) {
        var message = fromClient.receive()
        while (message is HubMessage.Ping) message = fromClient.receive()
        message
    }

    private fun <T> HubValue.decode(deserializer: DeserializationStrategy<T>): T = assertIs<HubValue.Decodable>(this).decode(deserializer)

    @Test
    fun `handshake requests the messagepack protocol`() = runBlocking<Unit> {
        val connection = connection(startHub())

        connection.start()

        assertEquals(Json.parseToJsonElement("""{"protocol":"messagepack","version":1}"""), Json.parseToJsonElement(handshakes.receive()))
        assertEquals(HubConnectionState.CONNECTED, connection.connectionState.value)
        connection.stop()
    }

    @Test
    fun `send encodes arguments as messagepack`() = runBlocking<Unit> {
        val connection = connection(startHub()).apply { start() }

        connection.send("Send", note, byteArrayOf(1, 2, 3), 42)

        val message = assertIs<HubMessage.Invocation.NonBlocking>(receiveFromClient())
        assertEquals("Send", message.target)
        assertEquals(note, message.arguments[0].decode(Note.serializer()))
        assertEquals("c403010203", (message.arguments[1] as MessagePackValue).bytes.toHex())
        assertEquals(42, message.arguments[2].decode(Int.serializer()))
        connection.stop()
    }

    @Test
    fun `invoke decodes the result`() = runBlocking<Unit> {
        val connection = connection(startHub()).apply { start() }

        val result = async {
            connection.invoke("Echo", note.text, String.serializer(), Note.serializer())
        }
        val invocation = assertIs<HubMessage.Invocation.Blocking>(receiveFromClient())
        sendToClient(
            HubMessage.Completion.Resulted(
                invocationId = invocation.invocationId,
                result = HubValue.Encodable(note.copy(text = invocation.arguments.single().decode(String.serializer())), Note.serializer()),
            )
        )

        assertEquals(note, withTimeout(5.seconds) { result.await() })
        connection.stop()
    }

    @Test
    fun `invoke error completion throws`() = runBlocking<Unit> {
        val connection = connection(startHub()).apply { start() }

        val result = async { runCatching { connection.invoke("Fail", Int.serializer()) }.exceptionOrNull() }
        val invocation = assertIs<HubMessage.Invocation.Blocking>(receiveFromClient())
        sendToClient(HubMessage.Completion.Error(invocationId = invocation.invocationId, error = "failed"))

        assertEquals("failed", assertIs<HubException>(withTimeout(5.seconds) { result.await() }).message)
        connection.stop()
    }

    @Test
    fun `server invocations are decoded`() = runBlocking<Unit> {
        val connection = connection(startHub()).apply { start() }
        val received = CompletableDeferred<Pair<Note, ByteArray>>()
        val collecting = launch(start = CoroutineStart.UNDISPATCHED) {
            connection.on("Notify", Note.serializer(), ByteArraySerializer()).collect { received.complete(it.arg1 to it.arg2) }
        }

        sendToClient(
            HubMessage.Invocation.NonBlocking(
                target = "Notify",
                arguments = listOf(HubValue.Encodable(note, Note.serializer()), HubValue.Encodable(byteArrayOf(-1, 0, 1), ByteArraySerializer())),
            )
        )

        val (receivedNote, bytes) = withTimeout(5.seconds) { received.await() }
        assertEquals(note, receivedNote)
        assertContentEquals(byteArrayOf(-1, 0, 1), bytes)
        collecting.cancel()
        connection.stop()
    }

    @Test
    fun `client result is encoded`() = runBlocking<Unit> {
        val connection = connection(startHub()).apply { start() }
        connection.on("Double", Int.serializer(), Int.serializer()) { it * 2 }

        sendToClient(HubMessage.Invocation.Blocking(target = "Double", arguments = listOf(HubValue.Encodable(21, Int.serializer())), invocationId = "1"))

        val completion = assertIs<HubMessage.Completion.Resulted>(receiveFromClient())
        assertEquals("1", completion.invocationId)
        assertEquals(42, completion.result.decode(Int.serializer()))
        connection.stop()
    }

    @Test
    fun `stream items are decoded`() = runBlocking<Unit> {
        val connection = connection(startHub()).apply { start() }

        val items = async { connection.stream("Count", Note.serializer(), 2).toList() }
        val invocation = assertIs<HubMessage.StreamInvocation>(receiveFromClient())
        assertEquals(2, invocation.arguments.single().decode(Int.serializer()))
        repeat(2) { sendToClient(HubMessage.StreamItem(invocation.invocationId, HubValue.Encodable(note.copy(text = "$it"), Note.serializer()))) }
        sendToClient(HubMessage.Completion.Simple(invocation.invocationId))

        assertEquals(listOf(note.copy(text = "0"), note.copy(text = "1")), withTimeout(5.seconds) { items.await() })
        connection.stop()
    }

    @Test
    fun `upload stream items are encoded`() = runBlocking<Unit> {
        val connection = connection(startHub()).apply { start() }
        val instants = listOf(Instant.fromEpochSeconds(0), Instant.fromEpochSeconds(-1, 1))

        connection.send("Upload", flowOf(*instants.toTypedArray()))

        val invocation = assertIs<HubMessage.Invocation.NonBlocking>(receiveFromClient())
        val streamId = assertNotNull(invocation.streamIds).single()
        val first = assertIs<HubMessage.StreamItem>(receiveFromClient())
        val second = assertIs<HubMessage.StreamItem>(receiveFromClient())
        assertEquals(listOf(streamId, streamId), listOf(first.invocationId, second.invocationId))
        assertEquals(instants, listOf(first.item.decode(Instant.serializer()), second.item.decode(Instant.serializer())))
        assertEquals("d6ff00000000", (first.item as MessagePackValue).bytes.toHex())
        assertEquals(HubMessage.Completion.Simple(streamId), receiveFromClient())
        connection.stop()
    }

    @Test
    fun `message sent together with handshake response is received`() = runBlocking<Unit> {
        val hello = HubMessage.Invocation.NonBlocking(target = "Hello", arguments = listOf(HubValue.Encodable("world\u001e", String.serializer())))
        val connection = connection(startHub(sentWithHandshakeResponse = protocol.writeMessage(hello)))
        val received = CompletableDeferred<String>()
        val collecting = launch(start = CoroutineStart.UNDISPATCHED) {
            connection.on("Hello", String.serializer()).collect { received.complete(it.arg1) }
        }

        connection.start()

        assertEquals("world\u001e", withTimeout(5.seconds) { received.await() })
        collecting.cancel()
        connection.stop()
    }

    @Test
    fun `close message from server closes the connection`() = runBlocking<Unit> {
        val connection = connection(startHub()).apply { start() }

        sendToClient(HubMessage.Close(error = "bye"))

        withTimeout(5.seconds) { connection.connectionState.first { it == HubConnectionState.DISCONNECTED } }
    }

    @Test
    fun `stop sends close message`() = runBlocking<Unit> {
        val connection = connection(startHub()).apply { start() }

        connection.stop()

        assertEquals(HubMessage.Close(), receiveFromClient())
        assertFailsWith<RuntimeException> { connection.send("Send") }
    }
}
