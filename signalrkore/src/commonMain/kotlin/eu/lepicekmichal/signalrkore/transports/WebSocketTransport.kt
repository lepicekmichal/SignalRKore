package eu.lepicekmichal.signalrkore.transports

import eu.lepicekmichal.signalrkore.Transport
import eu.lepicekmichal.signalrkore.utils.headers
import io.ktor.client.HttpClient
import io.ktor.client.plugins.timeout
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.websocket.CloseReason
import io.ktor.websocket.close
import io.ktor.websocket.readBytes
import io.ktor.websocket.send
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.io.EOFException

internal class WebSocketTransport(
    private val headers: Map<String, String>,
    private val client: HttpClient,
) : Transport {

    private var session: DefaultClientWebSocketSession? = null

    private fun formatUrl(url: String): String = when {
        url.startsWith(HTTPS) -> WSS + url.substring(HTTPS.length)
        url.startsWith(HTTP) -> WS + url.substring(HTTP.length)
        else -> url
    }

    override suspend fun start(url: String) {
        val formattedUrl = formatUrl(url)

        session = client.webSocketSession(urlString = formattedUrl) {
            headers(this@WebSocketTransport.headers)

            timeout {
                requestTimeoutMillis = Long.MAX_VALUE // HttpTimeout.INFINITE_TIMEOUT_MS or HttpTimeoutConfig.INFINITE_TIMEOUT_MS
            }
        }

        session?.ensureActive()
    }

    override suspend fun send(message: ByteArray) {
        try {
            session?.send(message) ?: throw IllegalStateException("WebSocket connection has not been started")
        } catch (e: EOFException) {
            throw IllegalStateException("WebSocket connection has been closed", e)
        }
    }

    override fun receive(): Flow<ByteArray> = session?.let { session ->
        session.incoming
            .receiveAsFlow()
            .map { it.readBytes() }
            .onCompletion { cause ->
                if (cause != null || !session.closeReason.isCompleted) return@onCompletion
                val reason = session.closeReason.await()
                if (reason != null && reason.code != CloseReason.Codes.NORMAL.code) {
                    throw RuntimeException("WebSocket closed with an error: $reason")
                }
            }
    } ?: throw IllegalStateException("WebSocket connection has not been started")

    override suspend fun stop() {
        session?.close()
    }

    companion object {
        private const val HTTP = "http"
        private const val HTTPS = "https"
        private const val WS = "ws"
        private const val WSS = "wss"
    }
}