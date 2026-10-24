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
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CollectorFailureTest {

    private var server: EmbeddedServer<*, *>? = null

    private val uncaught: MutableList<Throwable> = Collections.synchronizedList(mutableListOf())
    private val previousHandler = Thread.getDefaultUncaughtExceptionHandler()

    init {
        Thread.setDefaultUncaughtExceptionHandler { _, throwable -> uncaught += throwable }
    }

    @AfterTest
    fun tearDown() {
        server?.stop(0, 0)
        Thread.setDefaultUncaughtExceptionHandler(previousHandler)
    }

    private fun startServer(transport: String, module: Application.() -> Unit): Int {
        val server = embeddedServer(CIO, port = 0) {
            routing {
                post("/hub/negotiate") {
                    call.respondText(
                        """{"negotiateVersion":1,"connectionId":"abc","connectionToken":"token","availableTransports":[{"transport":"$transport","transferFormats":["Text","Binary"]}]}""",
                        ContentType.Application.Json,
                    )
                }
            }
            module()
        }.start(wait = false)
        this.server = server
        return runBlocking { server.engine.resolvedConnectors().first().port }
    }

    private fun connection(port: Int, logs: MutableList<String>) = HubConnectionBuilder.create("http://localhost:$port/hub") {
        logger = Logger { severity, message, cause -> logs += "$severity ${message()} ${cause ?: ""}" }
    }

    @Test
    fun `malformed message from server stops the connection instead of crashing`() = runBlocking {
        val port = startServer("WebSockets") {
            install(WebSockets)
            routing {
                webSocket("/hub") {
                    incoming.receive()
                    send(Frame.Text("{}\u001e"))
                    send(Frame.Text("""{"type":6}"""))
                    delay(10_000)
                }
            }
        }
        val logs = Collections.synchronizedList(mutableListOf<String>())
        val connection = connection(port, logs)

        connection.start()
        delay(1000)

        assertTrue(uncaught.isEmpty(), "Uncaught exceptions: $uncaught\nLogs:\n${logs.joinToString("\n")}")
        assertEquals(HubConnectionState.DISCONNECTED, connection.connectionState.value)
    }

    @Test
    fun `long polling network failure stops the connection instead of crashing`() = runBlocking {
        val polls = AtomicInteger(0)
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
                post("/hub") { call.respond(HttpStatusCode.OK, "") }
                delete("/hub") { call.respond(HttpStatusCode.Accepted, "") }
            }
        }
        val logs = Collections.synchronizedList(mutableListOf<String>())
        val connection = connection(port, logs)

        connection.start()
        assertEquals(HubConnectionState.CONNECTED, connection.connectionState.value, "Logs:\n${logs.joinToString("\n")}")

        server?.stop(0, 0)
        delay(1000)

        assertTrue(uncaught.isEmpty(), "Uncaught exceptions: $uncaught\nLogs:\n${logs.joinToString("\n")}")
        assertEquals(HubConnectionState.DISCONNECTED, connection.connectionState.value)
    }
}
