package eu.lepicekmichal.signalrkore.utils

import io.ktor.client.utils.*

fun Map<String, String>.buildAsHeaders() = buildHeaders {
    forEach { (key, value) -> append(key, value) }
}