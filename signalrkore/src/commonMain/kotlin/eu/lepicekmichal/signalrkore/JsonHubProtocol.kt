package eu.lepicekmichal.signalrkore

import io.ktor.utils.io.core.String
import io.ktor.utils.io.core.toByteArray
import io.ktor.utils.io.errors.IOException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class JsonHubProtocol(private val logger: Logger) : HubProtocol {

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
                    logger.log(Logger.Level.INFO, "Decoding message: $str")
                    json.decodeFromString(str)
                } catch (ex: IOException) {
                    throw RuntimeException("Error reading JSON.", ex)
                }
            }
    }

    override fun writeMessage(message: HubMessage): ByteArray =
        (json.encodeToString(message).also { logger.log(Logger.Level.INFO, "Encoded message: $it") } + RECORD_SEPARATOR).toByteArray()

    companion object {
        private const val PROTOCOL_NAME = "json"
        private const val PROTOCOL_VERSION = 1
    }
}