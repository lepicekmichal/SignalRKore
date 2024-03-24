package eu.lepicekmichal.signalrkore.transports

import io.ktor.client.HttpClient

internal actual class ServerSentEventsDelegate actual constructor(client: HttpClient) {

    actual fun get(
        url: String,
        headers: Map<String, String>,
        onFailure: (Exception) -> Unit,
        onSuccess: (Response) -> Unit,
    ) {
        throw IllegalStateException("iOS does not support Server Sent Events.")
    }

    actual fun post(url: String, headers: Map<String, String>, message: ByteArray) {
        throw IllegalStateException("iOS does not support Server Sent Events.")
    }

}