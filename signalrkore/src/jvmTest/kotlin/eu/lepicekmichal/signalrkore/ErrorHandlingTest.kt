package eu.lepicekmichal.signalrkore

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class ErrorHandlingTest {

    private var server: EmbeddedServer<*, *>? = null

    private val uncaught: MutableList<Throwable> = Collections.synchronizedList(mutableListOf())
    private val previousHandler = Thread.getDefaultUncaughtExceptionHandler()

    private val logs: MutableList<Triple<Logger.Severity, String, Throwable?>> = Collections.synchronizedList(mutableListOf())

    init {
        Thread.setDefaultUncaughtExceptionHandler { _, throwable -> uncaught += throwable }
    }

    @AfterTest
    fun tearDown() {
        server?.stop(0, 0)
        Thread.setDefaultUncaughtExceptionHandler(previousHandler)
        assertTrue(uncaught.isEmpty(), "Uncaught exceptions: $uncaught")
    }

    private fun serve(module: Application.() -> Unit): Int {
        val server = embeddedServer(CIO, port = 0, module = module).start(wait = false)
        this.server = server
        return runBlocking { server.engine.resolvedConnectors().first().port }
    }

    private fun startServer(transport: String = "WebSockets", module: Application.() -> Unit) = serve {
        routing {
            post("/hub/negotiate") {
                call.respondText(
                    """{"negotiateVersion":1,"connectionId":"abc","connectionToken":"token","availableTransports":[{"transport":"$transport","transferFormats":["Text","Binary"]}]}""",
                    ContentType.Application.Json,
                )
            }
        }
        module()
    }

    private fun startHub(handler: suspend DefaultWebSocketServerSession.() -> Unit) = startServer {
        install(WebSockets)
        routing {
            webSocket("/hub") {
                incoming.receive()
                send(Frame.Text("{}\u001e"))
                handler()
            }
        }
    }

    private val received = ArrayDeque<JsonObject>()

    private suspend fun DefaultWebSocketServerSession.receiveMessage(): JsonObject {
        while (true) {
            while (received.isEmpty()) {
                incoming.receive().data.decodeToString()
                    .split('\u001e')
                    .filter { it.isNotBlank() }
                    .mapTo(received) { Json.parseToJsonElement(it).jsonObject }
            }
            val message = received.removeFirst()
            if (message["type"]?.jsonPrimitive?.content != HubMessageType.PING.value.toString()) return message
        }
    }

    private suspend fun DefaultWebSocketServerSession.ignoreMessages() {
        for (frame in incoming) Unit
    }

    private fun connection(port: Int, block: HttpHubConnectionBuilder.() -> Unit = {}) =
        HubConnectionBuilder.create("http://localhost:$port/hub") {
            logger = Logger { severity, message, cause -> logs += Triple(severity, message(), cause) }
            block()
        }

    private fun logsText() = logs.joinToString("\n") { (severity, message, cause) -> "$severity $message ${cause ?: ""}" }

    @Test
    fun `start throws HttpRequestException when negotiate fails`() = runBlocking {
        val port = serve {
            routing {
                post("/hub/negotiate") { call.respond(HttpStatusCode.InternalServerError, "") }
            }
        }
        val connection = connection(port)

        val ex = assertFailsWith<HttpRequestException> { connection.start() }

        assertEquals(500, ex.statusCode)
        assertEquals(HubConnectionState.DISCONNECTED, connection.connectionState.value)
    }

    @Test
    fun `start throws when websocket returns 404 and does not reconnect`() = runBlocking {
        val upgrades = AtomicInteger(0)
        val port = startServer {
            routing {
                get("/hub") {
                    upgrades.incrementAndGet()
                    call.respondText("No Connection with that ID", status = HttpStatusCode.NotFound)
                }
            }
        }
        val connection = connection(port) {
            automaticReconnect = AutomaticReconnect.Custom { _, _ -> 10L }
        }

        assertFailsWith<Exception> { connection.start() }
        delay(500)

        assertEquals(HubConnectionState.DISCONNECTED, connection.connectionState.value)
        assertEquals(1, upgrades.get())
    }

    @Test
    fun `start throws when access token provider throws`() = runBlocking {
        val connection = connection(port = 1) {
            accessTokenProvider = { throw IllegalStateException("no token") }
        }

        val ex = assertFailsWith<IllegalStateException> { connection.start() }

        assertEquals("no token", ex.message)
        assertEquals(HubConnectionState.DISCONNECTED, connection.connectionState.value)
    }

    @Test
    fun `invoke throws HubException when server returns an error`() = runBlocking {
        val port = startHub {
            val invocation = receiveMessage()
            val invocationId = invocation["invocationId"]!!.jsonPrimitive.content
            send(Frame.Text("""{"type":3,"invocationId":"$invocationId","error":"It didn't work!"}""" + "\u001e"))
            ignoreMessages()
        }
        val connection = connection(port)
        connection.start()

        val ex = assertFailsWith<HubException> { connection.invoke("Fail", Int.serializer()) }

        assertEquals("It didn't work!", ex.message)
        assertEquals(HubConnectionState.CONNECTED, connection.connectionState.value)
        connection.stop()
    }

    @Test
    fun `pending invoke throws ConnectionClosedException when connection drops`() = runBlocking {
        val port = startHub {
            receiveMessage()
            cancel()
        }
        val connection = connection(port)
        connection.start()

        val ex = assertFailsWith<ConnectionClosedException> {
            withTimeout(5.seconds) { connection.invoke("Never", Int.serializer()) }
        }

        assertNotNull(ex.cause, "Logs:\n${logsText()}")
        assertEquals(HubConnectionState.DISCONNECTED, connection.connectionState.value)
    }

    @Test
    fun `stream ends with HubException or ConnectionClosedException`() = runBlocking {
        val port = startHub {
            val first = receiveMessage()["invocationId"]!!.jsonPrimitive.content
            send(Frame.Text("""{"type":2,"invocationId":"$first","item":1}""" + "\u001e"))
            send(Frame.Text("""{"type":3,"invocationId":"$first","error":"Stream failed"}""" + "\u001e"))
            val second = receiveMessage()["invocationId"]!!.jsonPrimitive.content
            send(Frame.Text("""{"type":2,"invocationId":"$second","item":2}""" + "\u001e"))
            delay(200)
            close(CloseReason(CloseReason.Codes.INTERNAL_ERROR, "boom"))
        }
        val connection = connection(port)
        connection.start()

        val items = mutableListOf<Int>()
        val hubError = assertFailsWith<HubException> {
            connection.stream("Numbers", Int.serializer()).collect { items += it }
        }
        assertEquals("Stream failed", hubError.message)
        assertEquals(listOf(1), items)

        val closed = runCatching {
            withTimeout(5.seconds) { connection.stream("Numbers", Int.serializer()).toList(items) }
        }.exceptionOrNull()
        assertTrue(closed is ConnectionClosedException, "Got $closed\nLogs:\n${logsText()}")
        assertEquals(listOf(1, 2), items)
    }

    @Test
    fun `graceful websocket close is detected without waiting for server timeout`() = runBlocking {
        val port = startHub {
            delay(200)
            close(CloseReason(CloseReason.Codes.NORMAL, "bye"))
        }
        val connection = connection(port)
        connection.start()

        delay(1000)

        assertEquals(HubConnectionState.DISCONNECTED, connection.connectionState.value, "Logs:\n${logsText()}")
        assertTrue(logs.none { it.first == Logger.Severity.ERROR }, "Logs:\n${logsText()}")
    }

    @Test
    fun `websocket error close is logged as error with cause`() = runBlocking {
        val port = startHub {
            delay(200)
            close(CloseReason(CloseReason.Codes.INTERNAL_ERROR, "boom"))
        }
        val connection = connection(port)
        connection.start()

        delay(1000)

        assertEquals(HubConnectionState.DISCONNECTED, connection.connectionState.value)
        assertTrue(logs.any { it.first == Logger.Severity.ERROR && it.third != null }, "Logs:\n${logsText()}")
    }

    @Test
    fun `server close message with error is logged as error with cause`() = runBlocking {
        val port = startHub {
            send(Frame.Text("""{"type":7,"error":"Server is shutting down"}""" + "\u001e"))
            ignoreMessages()
        }
        val connection = connection(port)
        connection.start()

        delay(500)

        assertEquals(HubConnectionState.DISCONNECTED, connection.connectionState.value)
        val error = logs.firstOrNull { it.first == Logger.Severity.ERROR }
        assertNotNull(error, "Logs:\n${logsText()}")
        assertTrue(error.third?.message.orEmpty().contains("Server is shutting down"))
    }

    @Test
    fun `stop sends close message to the server`() = runBlocking {
        val received = CompletableDeferred<String>()
        val port = startHub {
            received.complete(receiveMessage()["type"]!!.jsonPrimitive.content)
            ignoreMessages()
        }
        val connection = connection(port)
        connection.start()

        connection.stop()

        assertEquals(HubMessageType.CLOSE.value.toString(), withTimeout(5.seconds) { received.await() })
        assertEquals(HubConnectionState.DISCONNECTED, connection.connectionState.value)
    }

    @Test
    fun `long polling send rejected by server closes the connection`() = runBlocking {
        val polls = AtomicInteger(0)
        val posts = AtomicInteger(0)
        val port = startServer("LongPolling") {
            routing {
                get("/hub") {
                    when (polls.getAndIncrement()) {
                        0 -> call.respond(HttpStatusCode.OK, "")
                        1 -> call.respondText("{}\u001e")
                        else -> {
                            delay(10_000)
                            call.respond(HttpStatusCode.NoContent)
                        }
                    }
                }
                post("/hub") {
                    if (posts.getAndIncrement() == 0) call.respond(HttpStatusCode.OK, "")
                    else call.respond(HttpStatusCode.NotFound, "")
                }
                delete("/hub") { call.respond(HttpStatusCode.Accepted, "") }
            }
        }
        val connection = connection(port)
        connection.start()

        connection.send("Hello")
        delay(1000)

        assertEquals(HubConnectionState.DISCONNECTED, connection.connectionState.value, "Logs:\n${logsText()}")
        assertTrue(logs.any { it.first == Logger.Severity.ERROR && it.third is HttpRequestException }, "Logs:\n${logsText()}")
    }

    @Test
    fun `invoke registered before sending gets a fast completion`() = runBlocking {
        val port = startHub {
            while (true) {
                val invocationId = receiveMessage()["invocationId"]!!.jsonPrimitive.content
                send(Frame.Text("""{"type":3,"invocationId":"$invocationId","result":42}""" + "\u001e"))
            }
        }
        val connection = connection(port)
        connection.start()

        val results = List(50) { async { withTimeout(5.seconds) { connection.invoke("Answer", Int.serializer()) } } }.map { it.await() }

        assertEquals(List(50) { 42 }, results)
        connection.stop()
    }
}
