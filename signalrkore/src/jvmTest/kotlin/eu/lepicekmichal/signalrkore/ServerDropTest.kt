package eu.lepicekmichal.signalrkore

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.request.path
import io.ktor.server.response.respondText
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds

class ServerDropTest {

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

    private fun startServer(abrupt: Boolean): Int {
        val upgrades = AtomicInteger(0)
        val server = embeddedServer(CIO, port = 0) {
            install(WebSockets)
            intercept(ApplicationCallPipeline.Plugins) {
                if (call.request.path() == "/hub" && upgrades.getAndIncrement() > 0) {
                    call.respondText("No Connection with that ID", status = HttpStatusCode.NotFound)
                    finish()
                }
            }
            routing {
                post("/hub/negotiate") {
                    call.respondText(
                        """{"negotiateVersion":1,"connectionId":"abc","connectionToken":"token","availableTransports":[{"transport":"WebSockets","transferFormats":["Text","Binary"]}]}""",
                        ContentType.Application.Json,
                    )
                }
                webSocket("/hub") {
                    incoming.receive()
                    send(Frame.Text("{}\u001e"))
                    delay(200)
                    if (abrupt) cancel() else close(CloseReason(CloseReason.Codes.GOING_AWAY, "bye"))
                }
            }
        }.start(wait = false)
        this.server = server
        return runBlocking { server.engine.resolvedConnectors().first().port }
    }

    private fun run(abrupt: Boolean, automaticReconnect: AutomaticReconnect) = runBlocking {
        val port = startServer(abrupt)
        val logs = Collections.synchronizedList(mutableListOf<String>())
        val connection = HubConnectionBuilder.create("http://localhost:$port/hub") {
            this.automaticReconnect = automaticReconnect
            serverTimeout = 500.milliseconds
            logger = Logger { severity, message, cause -> logs += "$severity ${message()} ${cause ?: ""}" }
        }

        connection.start()
        delay(2000)

        assertTrue(uncaught.isEmpty(), "Uncaught exceptions: $uncaught\nLogs:\n${logs.joinToString("\n")}")
    }

    @Test
    fun `graceful server close without reconnect does not crash`() = run(abrupt = false, AutomaticReconnect.Inactive)

    @Test
    fun `graceful server close with reconnect does not crash`() =
        run(abrupt = false, AutomaticReconnect.Custom { n, _ -> if (n < 2) 10L else null })

    @Test
    fun `abrupt server close without reconnect does not crash`() = run(abrupt = true, AutomaticReconnect.Inactive)

    @Test
    fun `abrupt server close with reconnect does not crash`() =
        run(abrupt = true, AutomaticReconnect.Custom { n, _ -> if (n < 2) 10L else null })
}
