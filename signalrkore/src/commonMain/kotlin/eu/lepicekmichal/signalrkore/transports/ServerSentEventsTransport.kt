package eu.lepicekmichal.signalrkore.transports

import eu.lepicekmichal.signalrkore.HubMessage
import eu.lepicekmichal.signalrkore.RECORD_SEPARATOR
import eu.lepicekmichal.signalrkore.Transport
import eu.lepicekmichal.signalrkore.utils.headers
import io.ktor.client.HttpClient
import io.ktor.client.plugins.sse.ClientSSESession
import io.ktor.client.plugins.sse.sseSession
import io.ktor.client.plugins.timeout
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.utils.io.core.toByteArray
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okio.EOFException

internal class ServerSentEventsTransport(
    private val headers: Map<String, String>,
    private val client: HttpClient,
) : Transport {

    private var session: ClientSSESession? = null

    private lateinit var url: String

    override suspend fun start(url: String) {
        this.url = url

        session = client.sseSession(url) {
            headers(this@ServerSentEventsTransport.headers)

            timeout {
                socketTimeoutMillis = Long.MAX_VALUE
            }
        }
    }

    override fun receive(): Flow<ByteArray> = session?.incoming
        ?.mapNotNull { it.data?.toByteArray() }
        ?.catch {
            if (it is EOFException) emit((Json.encodeToString(HubMessage.Close()) + RECORD_SEPARATOR).toByteArray())
            else throw it
        }
        ?: throw IllegalStateException("SSE connection has not been started")

    override suspend fun send(message: ByteArray) {
        session ?: throw IllegalStateException("Cannot send unless the transport is active")

        client.post(url) {
            headers(this@ServerSentEventsTransport.headers)
            setBody(message)
        }
    }

    override suspend fun stop() {
        session?.cancel()
    }
}