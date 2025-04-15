package eu.lepicekmichal.signalrkore

import io.ktor.utils.io.core.toByteArray
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class JsonHubProtocol(private val logger: Logger) : HubProtocol {

    override val name: String = PROTOCOL_NAME
    override val version: Int = PROTOCOL_VERSION

    private val json by lazy { Json { ignoreUnknownKeys = true } }

    override fun parseMessages(payload: ByteArray): List<HubMessage> {
        val payloadString = payload.decodeToString(0, payload.size)
        if (payloadString.isEmpty()) return emptyList()
        if (payloadString.substring(payloadString.length - 1)[0] != RECORD_SEPARATOR) throw RuntimeException("HubMessage is incomplete.")

        return payloadString
            .split(RECORD_SEPARATOR)
            .filter { it.isNotEmpty() }
            .mapNotNull { str ->
                try {
                    logger.log(Logger.Severity.INFO, "Decoding message: $str", null)
                    json.decodeFromString(str)
                } catch (ex: Exception) {
                    logger.log(Logger.Severity.ERROR, "Failed to decode message: $str", ex)
                    null
                }
            }
    }

    override fun writeMessage(message: HubMessage): ByteArray =
        (json.encodeToString(message)
            .also { logger.log(Logger.Severity.INFO, "Encoded message: $it", null) } + RECORD_SEPARATOR).toByteArray()

    companion object {
        private const val PROTOCOL_NAME = "json"
        private const val PROTOCOL_VERSION = 1
    }
}