package eu.lepicekmichal.signalrkore.transports

import eu.lepicekmichal.signalrkore.Transport
import eu.lepicekmichal.signalrkore.utils.dispatchers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okio.BufferedSource
import okio.Closeable
import okio.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

interface Response : Closeable {
    val isSuccessful: Boolean
    val body: Body?

    interface Body {
        val contentType: String?
        fun source(): BufferedSource
    }
}

internal expect class ServerSentEventsDelegate {
    fun get(
        url: String,
        headers: Map<String, String>,
        onFailure: (Exception) -> Unit,
        onSuccess: (Response) -> Unit,
    )

    fun post(url: String, headers: Map<String, String>, message: ByteArray)
}

internal class ServerSentEventsTransport(
    private val headers: Map<String, String>,
) : Transport {

    private val delegate = ServerSentEventsDelegate()

    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + dispatchers.io)

    private val incoming: MutableSharedFlow<ByteArray> = MutableSharedFlow()

    @Volatile
    private var active = false

    private lateinit var url: String

    override suspend fun start(url: String) {
        this.url = url

        suspendCoroutine { continuation ->
            delegate.get(
                url = this.url,
                headers = headers.plus("Accept" to "text/event-stream"),
                onFailure = { continuation.resumeWithException(RuntimeException("Failed to connect.", it)) },
                onSuccess = { response ->
                    scope.launch {
                        response.use {
                            if (!response.isSuccessful) throw RuntimeException("Failed to connect.")

                            val body = response.body ?: throw RuntimeException("Failed to connect.")
                            if (body.contentType != "text/event-stream")
                                throw IllegalStateException("Invalid content-type: ${body.contentType}")

                            continuation.resume(Unit)

                            body.source().use { source ->
                                while (isActive) {
                                    if (!processNextEvent(source) && isActive && active) {
                                        throw IOException("Canceled")
                                    }
                                }
                            }
                        }
                    }
                }
            )
        }

        active = true
    }

    private suspend fun processNextEvent(source: BufferedSource): Boolean {
        val buffer = StringBuilder()
        while (true) {
            val line = try {
                source.readUtf8LineStrict().takeIf { it.isNotEmpty() }
            } catch (ex: IOException) {
                return false
            }
            line ?: break
            buffer.append(line)
        }

        incoming.emit(buffer.toString().substringAfter("data: ").toByteArray())
        return true
    }

    override suspend fun send(message: ByteArray) {
        if (!active) throw IllegalStateException("Cannot send unless the transport is active")

        delegate.post(
            url = url,
            headers = headers,
            message = message
        )
    }

    override fun receive(): Flow<ByteArray> = incoming.asSharedFlow()

    override suspend fun stop() {
        active = false
        job.cancelChildren()
        //log("LongPolling transport stopped.")
    }
}