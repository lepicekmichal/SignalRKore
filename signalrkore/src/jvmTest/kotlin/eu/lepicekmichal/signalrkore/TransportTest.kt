package eu.lepicekmichal.signalrkore

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.sse.SSE
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.websocket.Frame
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds
import io.ktor.server.websocket.WebSockets as ServerWebSockets

class TransportTest : HubTest() {

    private val sseClient
        get() = HttpClient {
            install(WebSockets)
            install(SSE)
            install(HttpTimeout)
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }

    private suspend fun HttpHub.answerInvocations() {
        while (true) {
            val message = receiveMessage()
            if (message.type == HubMessageType.INVOCATION.value) {
                sendMessage("""{"type":3,"invocationId":"${message.invocationId}","result":"${message.target}"}""")
            }
        }
    }

    @Test
    fun `all transports prefer websockets`() = runBlocking<Unit> {
        val port = serve {
            install(ServerWebSockets)
            routing {
                negotiate("LongPolling", "ServerSentEvents", "WebSockets")
                hub { ignoreMessages() }
            }
        }
        val connection = connection(port)

        connection.start()

        assertEquals(HubConnectionState.CONNECTED, connection.connectionState.value)
        connection.stop()
    }

    @Test
    fun `requested transport not offered by server fails start`() = runBlocking<Unit> {
        val port = serve { routing { negotiate("LongPolling") } }
        val connection = connection(port) { transportEnum = TransportEnum.WebSockets }

        val ex = assertFailsWith<RuntimeException> { connection.start() }

        assertEquals("There were no compatible transports on the server.", ex.message)
        assertEquals(HubConnectionState.DISCONNECTED, connection.connectionState.value)
    }

    @Test
    fun `long polling round trip`() = runBlocking<Unit> {
        val hub = HttpHub()
        val connection = connection(startHttpHub("LongPolling", hub))
        val hello = CompletableDeferred<String>()
        val handler = launchCollecting(connection.on("Hello", String.serializer())) { hello.complete(it.arg1) }
        val answering = launch { hub.answerInvocations() }

        connection.start()

        assertEquals("Echo", withTimeout(5.seconds) { connection.invoke("Echo", String.serializer()) })
        hub.sendMessage("""{"type":1,"target":"Hello","arguments":["world"]}""")
        assertEquals("world", withTimeout(5.seconds) { hello.await() })

        connection.stop()
        answering.cancel()
        handler.cancel()
        assertEquals(1, hub.deletes.get())
    }

    @Test
    fun `long polling sends headers with every request`() = runBlocking<Unit> {
        val hub = HttpHub()
        val connection = connection(startHttpHub("LongPolling", hub)) {
            headers["X-Test"] = "value"
            accessTokenProvider = { "token" }
        }

        connection.start()
        connection.send("Hello")
        withTimeout(5.seconds) { hub.receiveMessage() }
        connection.stop()

        assertTrue(hub.requests.size >= 3)
        assertTrue(hub.requests.all { it["X-Test"] == "value" && it["Authorization"] == "Bearer token" }, hub.requests.toString())
    }

    @Test
    fun `long polling terminated by server closes the connection`() = runBlocking<Unit> {
        val hub = HttpHub()
        val connection = connection(startHttpHub("LongPolling", hub))

        connection.start()
        hub.toClient.close()
        connection.awaitState(HubConnectionState.DISCONNECTED)
    }

    @Test
    fun `long polling initial poll failure fails start`() = runBlocking<Unit> {
        val port = serve {
            routing {
                negotiate("LongPolling")
                get("/hub") { call.respond(HttpStatusCode.NotFound, "No Connection with that ID") }
            }
        }
        val connection = connection(port)

        val ex = assertFailsWith<HttpRequestException> { connection.start() }

        assertEquals(404, ex.statusCode)
        assertEquals(HubConnectionState.DISCONNECTED, connection.connectionState.value)
    }

    @Test
    fun `long polling unexpected poll status closes the connection`() = runBlocking<Unit> {
        val polls = AtomicInteger(0)
        val port = serve {
            routing {
                negotiate("LongPolling")
                get("/hub") {
                    when (polls.getAndIncrement()) {
                        0 -> call.respond(HttpStatusCode.OK, "")
                        1 -> call.respondText("{}\u001e")
                        else -> call.respond(HttpStatusCode.NotFound, "No Connection with that ID")
                    }
                }
                post("/hub") { call.respond(HttpStatusCode.OK, "") }
                delete("/hub") { call.respond(HttpStatusCode.Accepted, "") }
            }
        }
        val connection = connection(port)

        connection.start()
        connection.awaitState(HubConnectionState.DISCONNECTED)

        awaitLog { it.severity == Logger.Severity.ERROR && (it.cause as? HttpRequestException)?.statusCode == 404 }
    }

    @Test
    fun `long polling send rejected by server closes the connection`() = runBlocking<Unit> {
        val hub = HttpHub()
        val connection = connection(startHttpHub("LongPolling", hub))

        connection.start()
        hub.sendStatus = HttpStatusCode.NotFound
        connection.send("Hello")
        connection.awaitState(HubConnectionState.DISCONNECTED)

        awaitLog { it.severity == Logger.Severity.ERROR && it.cause is HttpRequestException }
    }

    @Test
    fun `long polling network failure closes the connection`() = runBlocking<Unit> {
        val connection = connection(startHttpHub("LongPolling"))

        connection.start()
        stopServer()
        connection.awaitState(HubConnectionState.DISCONNECTED)
    }

    @Ignore
    @Test
    fun `server sent events round trip`() = runBlocking<Unit> {
        val hub = HttpHub()
        val connection = connection(startHttpHub("ServerSentEvents", hub)) { httpClient = sseClient }
        val answering = launch { hub.answerInvocations() }

        connection.start()

        assertEquals("Echo", withTimeout(5.seconds) { connection.invoke("Echo", String.serializer()) })

        connection.stop()
        answering.cancel()
    }

    @Ignore
    @Test
    fun `server sent events work with the default http client`() = runBlocking<Unit> {
        val connection = connection(startHttpHub("ServerSentEvents"))

        connection.start()

        assertEquals(HubConnectionState.CONNECTED, connection.connectionState.value, logsText())
        connection.stop()
    }

    @Ignore
    @Test
    fun `server sent events send rejected by server closes the connection`() = runBlocking<Unit> {
        val hub = HttpHub()
        val connection = connection(startHttpHub("ServerSentEvents", hub)) { httpClient = sseClient }

        connection.start()
        hub.sendStatus = HttpStatusCode.NotFound
        connection.send("Hello")
        connection.awaitState(HubConnectionState.DISCONNECTED)

        awaitLog { it.severity == Logger.Severity.ERROR && it.cause is HttpRequestException }
    }

    @Test
    fun `404 after negotiate fails start for every transport`() = runBlocking<Unit> {
        val upgrades = AtomicInteger(0)
        val port = serve {
            routing {
                negotiate("WebSockets", "ServerSentEvents", "LongPolling")
                get("/hub") {
                    upgrades.incrementAndGet()
                    call.respondText("No Connection with that ID", status = HttpStatusCode.NotFound)
                }
            }
        }

        listOf(TransportEnum.WebSockets, TransportEnum.ServerSentEvents, TransportEnum.LongPolling).forEach { transport ->
            listOf(AutomaticReconnect.Inactive, AutomaticReconnect.Custom { _, _ -> 10L }).forEach { reconnect ->
                val connection = connection(port) {
                    transportEnum = transport
                    automaticReconnect = reconnect
                    httpClient = sseClient
                }

                assertFailsWith<Exception>("$transport") { connection.start() }
                delay(100)

                assertEquals(HubConnectionState.DISCONNECTED, connection.connectionState.value, "$transport")
            }
        }
        assertEquals(6, upgrades.get())
    }

    @Test
    fun `malformed message from server stops the connection`() = runBlocking<Unit> {
        val port = startHub {
            send(Frame.Text("""{"type":6}"""))
            ignoreMessages()
        }
        val connection = connection(port)

        connection.start()
        connection.awaitState(HubConnectionState.DISCONNECTED)

        awaitLog { it.severity == Logger.Severity.ERROR && it.cause?.message == "HubMessage is incomplete." }
    }
}
