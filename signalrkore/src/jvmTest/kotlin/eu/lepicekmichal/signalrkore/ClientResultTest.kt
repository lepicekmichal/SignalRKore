package eu.lepicekmichal.signalrkore

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.time.Duration.Companion.seconds

class ClientResultTest : HubTest() {

    private val received = Channel<JsonObject>(Channel.UNLIMITED)

    private fun startHubSending(vararg invocations: String) = startHub {
        sendMessage(*invocations)
        while (true) received.send(receiveMessage())
    }

    private suspend fun nextMessage() = withTimeout(5.seconds) { received.receive() }

    @Test
    fun `result without parameters is sent back`() = runBlocking<Unit> {
        val connection = connection(startHubSending("""{"type":1,"invocationId":"1","target":"Answer","arguments":[]}"""))
        connection.on("Answer", Int.serializer()) { 42 }

        connection.start()

        assertEquals(Json.parseToJsonElement("""{"type":3,"invocationId":"1","result":42}"""), nextMessage())
        connection.stop()
    }

    @Test
    fun `result with parameters is sent back`() = runBlocking<Unit> {
        val connection = connection(
            startHubSending(
                """{"type":1,"invocationId":"1","target":"Double","arguments":[21]}""",
                """{"type":1,"invocationId":"2","target":"Concat","arguments":["a","b"]}""",
            )
        )
        connection.on("Double", Int.serializer(), Int.serializer()) { it * 2 }
        connection.on("Concat", String.serializer(), String.serializer(), String.serializer()) { a, b -> a + b }

        connection.start()

        val completions = List(2) { nextMessage() }.associateBy { it.invocationId }
        assertEquals(Json.parseToJsonElement("""{"type":3,"invocationId":"1","result":42}"""), completions.getValue("1"))
        assertEquals(Json.parseToJsonElement("""{"type":3,"invocationId":"2","result":"ab"}"""), completions.getValue("2"))
        connection.stop()
    }

    @Test
    fun `missing result handler sends error`() = runBlocking<Unit> {
        val connection = connection(startHubSending("""{"type":1,"invocationId":"1","target":"Answer","arguments":[]}"""))

        connection.start()

        assertEquals(Json.parseToJsonElement("""{"type":3,"invocationId":"1","error":"Client did not provide a result."}"""), nextMessage())
        connection.stop()
    }

    @Test
    fun `throwing result handler sends error`() = runBlocking<Unit> {
        val connection = connection(startHubSending("""{"type":1,"invocationId":"1","target":"Answer","arguments":[]}"""))
        connection.on("Answer", Int.serializer()) { throw IllegalStateException("cannot answer") }

        connection.start()

        assertEquals(Json.parseToJsonElement("""{"type":3,"invocationId":"1","error":"cannot answer"}"""), nextMessage())
        assertEquals(HubConnectionState.CONNECTED, connection.connectionState.value)
        connection.stop()
    }

    @Test
    fun `argument that cannot be parsed sends error`() = runBlocking<Unit> {
        val connection = connection(startHubSending("""{"type":1,"invocationId":"1","target":"Double","arguments":["not a number"]}"""))
        connection.on("Double", Int.serializer(), Int.serializer()) { it * 2 }

        connection.start()

        assertEquals(HubMessageType.COMPLETION.value, nextMessage().also { assertEquals("1", it.invocationId) }.type)
        assertEquals(HubConnectionState.CONNECTED, connection.connectionState.value)
        connection.stop()
    }

    @Test
    fun `stream invocation from server is answered with an error`() = runBlocking<Unit> {
        val connection = connection(startHubSending("""{"type":4,"invocationId":"1","target":"Count","arguments":[10]}"""))

        connection.start()

        assertEquals(Json.parseToJsonElement("""{"type":3,"invocationId":"1","error":"Client does not support streaming invocations."}"""), nextMessage())
        awaitLog { it.severity == Logger.Severity.WARNING && it.message.contains("Count") }
        assertEquals(HubConnectionState.CONNECTED, connection.connectionState.value)
        connection.stop()
    }

    @Test
    fun `only one result handler per method`() = runBlocking<Unit> {
        val connection = connection(port = 1)
        connection.on("Answer", Int.serializer()) { 42 }

        assertFailsWith<IllegalStateException> { connection.on("Answer", Int.serializer()) { 43 } }
    }

    @Test
    fun `non blocking invocation of result handler sends nothing`() = runBlocking<Unit> {
        val connection = connection(
            startHubSending(
                """{"type":1,"target":"Answer","arguments":[]}""",
                """{"type":1,"invocationId":"2","target":"Answer","arguments":[]}""",
            )
        )
        connection.on("Answer", Int.serializer()) { 42 }

        connection.start()

        assertEquals("2", nextMessage().invocationId)
        connection.stop()
    }

    @Test
    fun `slow result handler does not block other handlers`() = runBlocking<Unit> {
        val connection = connection(
            startHubSending(
                """{"type":1,"invocationId":"1","target":"Slow","arguments":[]}""",
                """{"type":1,"target":"Hello","arguments":["world"]}""",
            )
        )
        val release = CompletableDeferred<Unit>()
        val hello = CompletableDeferred<String>()
        connection.on("Slow", Int.serializer()) {
            release.await()
            42
        }
        val job = launchCollecting(connection.on("Hello", String.serializer())) { hello.complete(it.arg1) }

        connection.start()

        assertEquals("world", withTimeout(5.seconds) { hello.await() })
        release.complete(Unit)
        assertEquals("1", nextMessage().invocationId)
        job.cancel()
        connection.stop()
    }

    @Test
    fun `result handler keeps working after restart`() = runBlocking<Unit> {
        val connection = connection(startHubSending("""{"type":1,"invocationId":"1","target":"Answer","arguments":[]}"""))
        connection.on("Answer", Int.serializer()) { 42 }

        connection.start()
        assertEquals(Json.parseToJsonElement("""{"type":3,"invocationId":"1","result":42}"""), nextMessage())
        connection.stop()
        delay(100)
        while (!received.isEmpty) received.receive()

        connection.start()

        assertEquals(Json.parseToJsonElement("""{"type":3,"invocationId":"1","result":42}"""), nextMessage())
        connection.stop()
    }

    @Test
    fun `result handler keeps working after reconnect`() = runBlocking<Unit> {
        val connections = AtomicInteger(0)
        val port = startHub {
            if (connections.incrementAndGet() == 1) {
                delay(200)
                cancel()
            } else {
                sendMessage("""{"type":1,"invocationId":"1","target":"Answer","arguments":[]}""")
                while (true) received.send(receiveMessage())
            }
        }
        val connection = connection(port) { automaticReconnect = AutomaticReconnect.Custom { _, _ -> 10L } }
        connection.on("Answer", Int.serializer()) { 42 }

        connection.start()

        assertEquals(Json.parseToJsonElement("""{"type":3,"invocationId":"1","result":42}"""), nextMessage())
        assertEquals(2, connections.get())
        connection.stop()
    }
}
