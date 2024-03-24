package eu.lepicekmichal.signalrkore.transports

import eu.lepicekmichal.signalrkore.Transport
import eu.lepicekmichal.signalrkore.utils.buildAsHeaders
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.jvm.Volatile

internal class LongPollingTransport(private val headers: Map<String, String>, private val client: HttpClient) : Transport {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.IO)

    private val incoming: MutableSharedFlow<ByteArray> = MutableSharedFlow()

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
            this@LongPollingTransport.headers.buildAsHeaders()
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
            while (true) {
                if (!isActive) break
                if (!active) {
                    //log("Long Polling transport polling complete.");
                    stop()
                    break
                }

                val response = client.get(pollUrl) {
                    this@LongPollingTransport.headers.buildAsHeaders()
                    timeout {
                        requestTimeoutMillis = POLL_TIMEOUT
                    }
                }

                when (response.status) {
                    HttpStatusCode.NoContent -> {
                        //log("LongPolling transport terminated by server.")
                        active = false
                    }
                    HttpStatusCode.OK -> {
                        if (response.contentLength() != 0L) {
                            incoming.emit(response.body())
                        } else {
                            //log("Poll timed out, reissuing.")
                        }
                    }
                    else -> {
                        //log("Unexpected response code ${response.status.value}")
                        active = false
                    }
                }
            }
        }
    }

    override suspend fun send(message: ByteArray) {
        if (!active) throw IllegalStateException("Cannot send unless the transport is active")

        client.post(url) {
            this@LongPollingTransport.headers.buildAsHeaders()
            setBody(message)
        }
    }

    override fun receive(): Flow<ByteArray> = incoming.asSharedFlow()

    override suspend fun stop() {
        if (stopping) return
        stopping = true
        active = false
        try {
            client.delete(url) { this@LongPollingTransport.headers.buildAsHeaders() }
        } finally {
            //log("LongPolling transport stopped.")
            dispose()
        }
    }

    private fun dispose() {
        scope.cancel()
    }

    companion object {
        private const val POLL_TIMEOUT = 100 * 1000L
    }
}