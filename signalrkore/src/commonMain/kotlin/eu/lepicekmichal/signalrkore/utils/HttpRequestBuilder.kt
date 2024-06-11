package eu.lepicekmichal.signalrkore.utils

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.headers

fun HttpRequestBuilder.headers(headers: Map<String, String>) {
    headers {
        headers.forEach { (key, value) -> header(key, value) }
    }
}