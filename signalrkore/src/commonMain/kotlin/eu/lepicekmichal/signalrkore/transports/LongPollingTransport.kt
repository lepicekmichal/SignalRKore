package eu.lepicekmichal.signalrkore.transports

import eu.lepicekmichal.signalrkore.Transport
import eu.lepicekmichal.signalrkore.utils.headers
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.timeout
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentLength
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlin.concurrent.Volatile
import kotlin.time.Clock

internal class LongPollingTransport(private val headers: Map<String, String>, private val client: HttpClient) : Transport {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.IO)

    private val incoming: Channel<ByteArray> = Channel(Channel.UNLIMITED)

    @Volatile
    private var active = false

    @Volatile
    private var stopping = false

    private lateinit var url: String
    private val pollUrl: String
        get() = url + "&_=" + Clock.System.now().toEpochMilliseconds()

    override suspend fun start(url: String) {
        this.url = url

        val response = client.get(pollUrl) {
            headers(this@LongPollingTransport.headers)
        }

        if (response.status != HttpStatusCode.OK) {
            //log("Unexpected response code ${response.status.value}")
            throw RuntimeException("Failed to connect.")
        }

        active = true
        polling()
    }

    private fun polling() {
        scope.launch {
            try {
                while (active) {
                    val response = try {
                        client.get(pollUrl) {
                            headers(this@LongPollingTransport.headers)
                            timeout {
                                socketTimeoutMillis = Long.MAX_VALUE
                                requestTimeoutMillis = POLL_TIMEOUT
                            }
                        }
                    } catch (ex: HttpRequestTimeoutException) {
                        continue
                    }

                    when (response.status) {
                        HttpStatusCode.NoContent -> throw RuntimeException("LongPolling transport terminated by server.")
                        HttpStatusCode.OK -> {
                            if (response.contentLength() != 0L) {
                                incoming.send(response.body())
                            } else {
                                //log("Poll timed out, reissuing.")
                            }
                        }
                        else -> throw RuntimeException("Unexpected response code ${response.status.value} while polling.")
                    }
                }
                incoming.close()
            } catch (ex: CancellationException) {
                throw ex
            } catch (ex: Exception) {
                active = false
                incoming.close(ex)
            }
        }
    }

    override suspend fun send(message: ByteArray) {
        if (!active) throw IllegalStateException("Cannot send unless the transport is active")

        client.post(url) {
            headers(this@LongPollingTransport.headers)
            setBody(message)
        }
    }

    override fun receive(): Flow<ByteArray> = incoming.receiveAsFlow()

    override suspend fun stop() {
        if (stopping) return
        stopping = true
        active = false
        try {
            client.delete(url) { headers(this@LongPollingTransport.headers) }
        } finally {
            //log("LongPolling transport stopped.")
            dispose()
        }
    }

    private fun dispose() {
        scope.cancel()
        incoming.close()
    }

    companion object {
        private const val POLL_TIMEOUT = 100 * 1000L
    }
}