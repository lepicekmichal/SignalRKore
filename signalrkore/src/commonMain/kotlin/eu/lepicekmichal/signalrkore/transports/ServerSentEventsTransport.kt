package eu.lepicekmichal.signalrkore.transports

import eu.lepicekmichal.signalrkore.Transport
import io.ktor.client.HttpClient
import io.ktor.utils.io.core.toByteArray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.BufferedSource
import okio.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.jvm.Volatile

internal class ServerSentEventsTransport(
    private val headers: Map<String, String>,
    client: HttpClient,
) : Transport {

    private val delegate = ServerSentEventsDelegate(client)

    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + Dispatchers.IO)

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
                        try {
                            if (!response.isSuccessful) throw RuntimeException("Failed to connect.")

                            val body = response.body ?: throw RuntimeException("Failed to connect.")
                            if (body.contentType != "text/event-stream")
                                throw IllegalStateException("Invalid content-type: ${body.contentType}")

                            continuation.resume(Unit)

                            val source = body.source()
                            try {
                                while (isActive) {
                                    if (!processNextEvent(source) && isActive && active) {
                                        throw IOException("Canceled")
                                    }
                                }
                            } finally {
                                withContext(Dispatchers.IO) {
                                    source.close()
                                }
                            }
                        } finally {
                            withContext(Dispatchers.IO) {
                                response.close()
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
    }
}