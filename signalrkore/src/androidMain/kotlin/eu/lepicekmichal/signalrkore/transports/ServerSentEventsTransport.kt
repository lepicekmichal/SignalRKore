package eu.lepicekmichal.signalrkore.transports

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Headers.Companion.toHeaders
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.internal.connection.RealCall
import okio.BufferedSource
import java.io.IOException
import java.util.concurrent.TimeUnit

internal actual class ServerSentEventsDelegate actual constructor(client: HttpClient) {

    private val client = ((client.engine as? OkHttpEngine)?.config?.preconfigured?.newBuilder() ?: OkHttpClient.Builder()).apply {
        readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
    }.build()

    actual fun get(
        url: String,
        headers: Map<String, String>,
        onFailure: (Exception) -> Unit,
        onSuccess: (eu.lepicekmichal.signalrkore.transports.Response) -> Unit,
    ) {
        val request = okhttp3.Request.Builder()
            .get()
            .url(url)
            .headers(headers.toHeaders())
            .build()

        (client.newCall(request) as RealCall).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = onFailure(e)

            override fun onResponse(call: Call, response: Response) {
                onSuccess(
                    object : eu.lepicekmichal.signalrkore.transports.Response {
                        override val isSuccessful: Boolean = response.isSuccessful
                        override val body: eu.lepicekmichal.signalrkore.transports.Response.Body? = response.body?.let { body ->
                            object : eu.lepicekmichal.signalrkore.transports.Response.Body {
                                override val contentType: String? = body.contentType()?.toString()
                                override fun source(): BufferedSource = body.source()
                            }
                        }

                        override fun close() = response.close()
                    }
                )
            }
        })
    }

    actual fun post(url: String, headers: Map<String, String>, message: ByteArray) {
        val request = okhttp3.Request.Builder()
            .post(message.toRequestBody())
            .url(url)
            .headers(headers.toHeaders())
            .build()

        client.newCall(request).execute()
    }

    companion object {
        private const val READ_TIMEOUT = 100 * 1000L
    }

}