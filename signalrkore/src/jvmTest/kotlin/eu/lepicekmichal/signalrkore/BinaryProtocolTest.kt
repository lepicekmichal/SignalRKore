package eu.lepicekmichal.signalrkore

import io.ktor.server.application.install
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.websocket.Frame
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import java.util.Collections
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class BinaryProtocolTest : HubTest() {

    private class RecordingBinaryProtocol : HubProtocol by JsonHubProtocol(logger = { _, _, _ -> }) {
        val payloads: MutableList<ByteArray> = Collections.synchronizedList(mutableListOf())

        override val transferFormat: TransferFormat = TransferFormat.Binary

        override fun parseMessages(payload: ByteArray): List<HubMessage> {
            payloads += payload
            return emptyList()
        }
    }

    private fun binaryNegotiateResponse(vararg transports: Pair<String, String>) =
        """{"negotiateVersion":1,"connectionId":"abc","connectionToken":"token","availableTransports":[""" +
            transports.joinToString(",") { (transport, formats) -> """{"transport":"$transport","transferFormats":[$formats]}""" } + "]}"

    @Test
    fun `binary payload sent together with handshake response reaches the protocol intact`() = runBlocking<Unit> {
        val binary = byteArrayOf(0x92.toByte(), 0xFF.toByte(), RECORD_SEPARATOR.code.toByte(), 0x00, 0xC3.toByte(), 0xE2.toByte(), 0x82.toByte())
        val port = startHub(handshakeResponse = null) {
            send(Frame.Binary(true, "{}$RECORD_SEPARATOR".encodeToByteArray() + binary))
            ignoreMessages()
        }
        val protocol = RecordingBinaryProtocol()
        val connection = connection(port) { this.protocol = protocol }

        connection.start()

        withTimeout(5.seconds) { while (protocol.payloads.isEmpty()) delay(10) }
        assertContentEquals(binary, protocol.payloads.first())
        connection.stop()
    }

    @Test
    fun `binary protocol skips server sent events`() = runBlocking<Unit> {
        val port = serve {
            install(WebSockets)
            routing {
                negotiate("ServerSentEvents", "WebSockets")
                hub { ignoreMessages() }
            }
        }
        val connection = connection(port) { protocol = RecordingBinaryProtocol() }

        connection.start()

        assertEquals(HubConnectionState.CONNECTED, connection.connectionState.value)
        assertTrue(logs.none { it.message.contains("Failed to start the transport") }, logsText())
        connection.stop()
    }

    @Test
    fun `binary protocol fails start when only server sent events are offered`() = runBlocking<Unit> {
        val port = serve { routing { negotiate("ServerSentEvents") } }
        val connection = connection(port) { protocol = RecordingBinaryProtocol() }

        val ex = assertFailsWith<RuntimeException> { connection.start() }

        assertEquals("There were no compatible transports on the server.", ex.message)
    }

    @Test
    fun `binary protocol skips transports without binary transfer format`() = runBlocking<Unit> {
        val port = serve {
            routing { negotiate(response = binaryNegotiateResponse("WebSockets" to "\"Text\"", "LongPolling" to "\"Text\"")) }
        }
        val connection = connection(port) { protocol = RecordingBinaryProtocol() }

        val ex = assertFailsWith<RuntimeException> { connection.start() }

        assertEquals("There were no compatible transports on the server.", ex.message)
    }

    @Test
    fun `binary protocol uses the transport offering binary transfer format`() = runBlocking<Unit> {
        val port = serve {
            install(WebSockets)
            routing {
                negotiate(response = binaryNegotiateResponse("LongPolling" to "\"Text\"", "WebSockets" to "\"Text\",\"Binary\""))
                hub { ignoreMessages() }
            }
        }
        val connection = connection(port) { protocol = RecordingBinaryProtocol() }

        connection.start()

        assertEquals(HubConnectionState.CONNECTED, connection.connectionState.value)
        assertTrue(logs.none { it.message.contains("Failed to start the transport") }, logsText())
        connection.stop()
    }
}
