package eu.lepicekmichal.signalrkore

import io.ktor.utils.io.core.*
import io.ktor.utils.io.errors.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class JsonHubProtocol : HubProtocol {

    override val name: String = PROTOCOL_NAME
    override val version: Int = PROTOCOL_VERSION

    private val json by lazy { Json { ignoreUnknownKeys = true } }

    override fun parseMessages(payload: ByteArray): List<HubMessage> {
        val payloadString = String(payload)
        if (payloadString.isEmpty()) return emptyList()
        if (payloadString.substring(payloadString.length - 1)[0] != RECORD_SEPARATOR) throw RuntimeException("HubMessage is incomplete.")

        return payloadString
            .split(RECORD_SEPARATOR)
            .filter { it.isNotEmpty() }
            .map { str ->
                try {
                    println("AAAAAAAA parsed $str") //todo logging
                    json.decodeFromString(str)
                } catch (ex: IOException) {
                    throw RuntimeException("Error reading JSON.", ex)
                }
            }
    }

    override fun writeMessage(message: HubMessage): ByteArray =
        (json.encodeToString(message).also { println("AAAAAAAA encoded $it") } + RECORD_SEPARATOR).toByteArray()

    companion object {
        private const val PROTOCOL_NAME = "json"
        private const val PROTOCOL_VERSION = 1
    }
}