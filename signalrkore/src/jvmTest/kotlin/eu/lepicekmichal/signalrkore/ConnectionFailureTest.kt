package eu.lepicekmichal.signalrkore

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.cio.CIO
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.util.Collections
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ConnectionFailureTest {

    private lateinit var server: EmbeddedServer<*, *>
    private var port: Int = 0

    private val uncaught: MutableList<Throwable> = Collections.synchronizedList(mutableListOf())
    private var previousHandler: Thread.UncaughtExceptionHandler? = null

    @BeforeTest
    fun setUp() {
        previousHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { _, throwable -> uncaught += throwable }

        server = embeddedServer(CIO, port = 0) {
            routing {
                post("/hub/negotiate") {
                    call.respondText(
                        """{"negotiateVersion":1,"connectionId":"abc","connectionToken":"token","availableTransports":[{"transport":"WebSockets","transferFormats":["Text","Binary"]},{"transport":"ServerSentEvents","transferFormats":["Text"]},{"transport":"LongPolling","transferFormats":["Text","Binary"]}]}""",
                        ContentType.Application.Json,
                    )
                }
                get("/hub") {
                    call.respondText("No Connection with that ID", status = HttpStatusCode.NotFound)
                }
            }
        }.start(wait = false)

        port = runBlocking { server.engine.resolvedConnectors().first().port }
    }

    @AfterTest
    fun tearDown() {
        server.stop(0, 0)
        Thread.setDefaultUncaughtExceptionHandler(previousHandler)
    }

    private fun connection(automaticReconnect: AutomaticReconnect, transport: TransportEnum = TransportEnum.All) =
        HubConnectionBuilder.create("http://localhost:$port/hub") {
            this.automaticReconnect = automaticReconnect
            this.transportEnum = transport
        }

    @Test
    fun `sse and long polling 404 after negotiate does not crash`() = runBlocking {
        listOf(TransportEnum.ServerSentEvents, TransportEnum.LongPolling).forEach { transport ->
            listOf(AutomaticReconnect.Inactive, AutomaticReconnect.Custom { n, _ -> if (n < 2) 10L else null }).forEach { reconnect ->
                val connection = connection(reconnect, transport)

                runCatching { connection.start() }
                delay(1000)

                assertTrue(uncaught.isEmpty(), "$transport: Uncaught exceptions: $uncaught")
                assertEquals(HubConnectionState.DISCONNECTED, connection.connectionState.value, "$transport")
            }
        }
    }

    @Test
    fun `websocket 404 after negotiate does not crash without reconnect`() = runBlocking {
        val connection = connection(AutomaticReconnect.Inactive)

        runCatching { connection.start() }
        delay(1000)

        assertTrue(uncaught.isEmpty(), "Uncaught exceptions: $uncaught")
        assertEquals(HubConnectionState.DISCONNECTED, connection.connectionState.value)
    }

    @Test
    fun `websocket 404 after negotiate does not crash with reconnect`() = runBlocking {
        val connection = connection(AutomaticReconnect.Custom { previousRetryCount, _ -> if (previousRetryCount < 2) 10L else null })

        runCatching { connection.start() }
        delay(1000)

        assertTrue(uncaught.isEmpty(), "Uncaught exceptions: $uncaught")
        assertEquals(HubConnectionState.DISCONNECTED, connection.connectionState.value)
    }
}
