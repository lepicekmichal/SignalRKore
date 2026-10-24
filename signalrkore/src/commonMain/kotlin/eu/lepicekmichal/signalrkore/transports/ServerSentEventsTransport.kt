package eu.lepicekmichal.signalrkore.transports

import eu.lepicekmichal.signalrkore.HttpRequestException
import eu.lepicekmichal.signalrkore.Transport
import eu.lepicekmichal.signalrkore.utils.headers
import io.ktor.client.HttpClient
import io.ktor.client.plugins.sse.ClientSSESession
import io.ktor.client.plugins.sse.sseSession
import io.ktor.client.plugins.timeout
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.core.toByteArray
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal class ServerSentEventsTransport(
    private val headers: Map<String, String>,
    private val client: HttpClient,
) : Transport {

    private var session: ClientSSESession? = null

    private val incoming: Channel<ByteArray> = Channel(Channel.UNLIMITED)

    private lateinit var url: String

    override suspend fun start(url: String) {
        this.url = url

        val session = client.sseSession(url) {
            headers(this@ServerSentEventsTransport.headers)

            timeout {
                socketTimeoutMillis = Long.MAX_VALUE
            }
        }
        this.session = session

        session.launch {
            try {
                session.incoming.collect { event -> event.data?.let { incoming.send(it.toByteArray()) } }
                incoming.close()
            } catch (ex: CancellationException) {
                incoming.close()
                throw ex
            } catch (ex: Exception) {
                incoming.close(ex)
            }
        }
    }

    override fun receive(): Flow<ByteArray> = session?.let { incoming.receiveAsFlow() }
        ?: throw IllegalStateException("SSE connection has not been started")

    override suspend fun send(message: ByteArray) {
        session ?: throw IllegalStateException("Cannot send unless the transport is active")

        val response = client.post(url) {
            headers(this@ServerSentEventsTransport.headers)
            setBody(message)
        }

        if (response.status != HttpStatusCode.OK && response.status != HttpStatusCode.Conflict) {
            throw HttpRequestException("Unexpected response code ${response.status.value} while sending.", response.status.value)
        }
    }

    override suspend fun stop() {
        session?.cancel()
    }
}