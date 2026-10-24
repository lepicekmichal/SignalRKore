package eu.lepicekmichal.signalrkore

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.request.path
import io.ktor.server.response.respondText
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.websocket.CloseReason
import io.ktor.websocket.close
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.builtins.serializer
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class ReconnectTest : HubTest() {

    private val retry = AutomaticReconnect.Custom { previousRetryCount, _ -> if (previousRetryCount < 2) 10L else null }

    private fun startHubDroppingFirst(drop: suspend HubSession.() -> Unit): Pair<Int, AtomicInteger> {
        val connections = AtomicInteger(0)
        return startHub {
            if (connections.incrementAndGet() == 1) drop()
            else ignoreMessages()
        } to connections
    }

    @Test
    fun `reconnects after server drops the connection`() = runBlocking<Unit> {
        val (port, connections) = startHubDroppingFirst {
            delay(200)
            cancel()
        }
        val connection = connection(port) { automaticReconnect = retry }
        val states = async {
            connection.connectionState.takeWhile { connections.get() < 2 || it != HubConnectionState.CONNECTED }.toList()
        }

        connection.start()
        withTimeout(5.seconds) { while (connections.get() < 2) delay(10) }
        connection.awaitState(HubConnectionState.CONNECTED)

        assertTrue(HubConnectionState.RECONNECTING in withTimeout(5.seconds) { states.await() }, logsText())
        assertEquals(2, connections.get())
        connection.stop()
    }

    @Test
    fun `reconnects after graceful websocket close`() = runBlocking<Unit> {
        val (port, connections) = startHubDroppingFirst {
            delay(200)
            close(CloseReason(CloseReason.Codes.NORMAL, "bye"))
        }
        val connection = connection(port) { automaticReconnect = retry }

        connection.start()
        withTimeout(5.seconds) { while (connections.get() < 2) delay(10) }
        connection.awaitState(HubConnectionState.CONNECTED)
        connection.stop()
    }

    @Test
    fun `close message allowing reconnect reconnects`() = runBlocking<Unit> {
        val (port, connections) = startHubDroppingFirst {
            sendMessage("""{"type":7,"error":"Server restarting","allowReconnect":true}""")
            ignoreMessages()
        }
        val connection = connection(port) { automaticReconnect = retry }

        connection.start()
        withTimeout(5.seconds) { while (connections.get() < 2) delay(10) }
        connection.awaitState(HubConnectionState.CONNECTED)
        connection.stop()
    }

    @Test
    fun `close message without allowing reconnect stops`() = runBlocking<Unit> {
        val (port, connections) = startHubDroppingFirst {
            sendMessage("""{"type":7,"error":"Go away"}""")
            ignoreMessages()
        }
        val connection = connection(port) { automaticReconnect = retry }

        connection.start()
        connection.awaitState(HubConnectionState.DISCONNECTED)
        delay(300)

        assertEquals(HubConnectionState.DISCONNECTED, connection.connectionState.value)
        assertEquals(1, connections.get())
    }

    @Test
    fun `gives up after policy stops retrying`() = runBlocking<Unit> {
        val upgrades = AtomicInteger(0)
        val port = serve {
            install(WebSockets)
            intercept(ApplicationCallPipeline.Plugins) {
                if (call.request.path() == "/hub" && upgrades.getAndIncrement() > 0) {
                    call.respondText("No Connection with that ID", status = HttpStatusCode.NotFound)
                    finish()
                }
            }
            routing {
                negotiate()
                hub {
                    delay(200)
                    cancel()
                }
            }
        }
        val connection = connection(port) { automaticReconnect = retry }

        connection.start()
        withTimeout(5.seconds) { while (upgrades.get() < 3) delay(10) }
        connection.awaitState(HubConnectionState.DISCONNECTED)

        val last = awaitLog { it.severity == Logger.Severity.ERROR && it.message.contains("Reconnection unsuccessful") }
        assertTrue(last.cause?.message.orEmpty().contains("404"), logsText())
        delay(200)
        assertEquals(3, upgrades.get())
    }

    @Test
    fun `stop during reconnecting stops retrying`() = runBlocking<Unit> {
        val (port, _) = startHubDroppingFirst {
            delay(200)
            cancel()
        }
        val connection = connection(port) { automaticReconnect = AutomaticReconnect.Custom { _, _ -> 1_000L } }

        connection.start()
        connection.awaitState(HubConnectionState.RECONNECTING)
        connection.stop()
        delay(1_500)

        assertEquals(HubConnectionState.DISCONNECTED, connection.connectionState.value)
    }

    @Test
    fun `pending invokes fail when reconnecting`() = runBlocking<Unit> {
        val (port, _) = startHubDroppingFirst {
            receiveMessage()
            cancel()
        }
        val connection = connection(port) { automaticReconnect = retry }
        connection.start()

        val ex = runCatching { withTimeout(5.seconds) { connection.invoke("Never", Int.serializer()) } }.exceptionOrNull()

        assertTrue(ex is ConnectionClosedException, "Got $ex")
        connection.awaitState(HubConnectionState.CONNECTED)
        connection.stop()
    }

    @Test
    fun `server timeout triggers reconnect`() = runBlocking<Unit> {
        val (port, connections) = startHubDroppingFirst { ignoreMessages() }
        val connection = connection(port) {
            automaticReconnect = retry
            serverTimeout = 300.milliseconds
        }

        connection.start()
        withTimeout(5.seconds) { while (connections.get() < 2) delay(10) }
        connection.stop()
    }
}
