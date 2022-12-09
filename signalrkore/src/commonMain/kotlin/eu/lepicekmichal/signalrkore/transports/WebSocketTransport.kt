package eu.lepicekmichal.signalrkore.transports

import eu.lepicekmichal.signalrkore.HubMessage
import eu.lepicekmichal.signalrkore.RECORD_SEPARATOR
import eu.lepicekmichal.signalrkore.Transport
import eu.lepicekmichal.signalrkore.utils.buildAsHeaders
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.utils.io.core.*
import io.ktor.websocket.*
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okio.EOFException
import kotlin.text.toByteArray

internal class WebSocketTransport(
    private val headers: Map<String, String>,
    private val client: HttpClient,
) : Transport {

    private var session: WebSocketSession? = null

    private fun formatUrl(url: String): String = when {
        url.startsWith(HTTPS) -> WSS + url.substring(HTTPS.length)
        url.startsWith(HTTP) -> WS + url.substring(HTTP.length)
        else -> url
    }

    override suspend fun start(url: String) {
        val formattedUrl = formatUrl(url)

        session = client.webSocketSession(urlString = formattedUrl) {
            this@WebSocketTransport.headers.buildAsHeaders()

            timeout {
                requestTimeoutMillis = HttpTimeout.INFINITE_TIMEOUT_MS
            }
        }

        session?.ensureActive()
    }

    override suspend fun send(message: ByteArray) {
        session?.send(message) ?: throw IllegalStateException("WebSocket connection has not been started")
    }

    override fun receive(): Flow<ByteArray> = session?.incoming
        ?.receiveAsFlow()
        ?.map { it.readBytes() }
        ?.catch {
            if (it is EOFException) emit((Json.encodeToString(HubMessage.Close()) + RECORD_SEPARATOR).toByteArray())
            else throw it
        }
        ?: throw IllegalStateException("WebSocket connection has not been started")

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