package eu.lepicekmichal.signalrkore

import io.ktor.utils.io.core.toByteArray
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.put

/**
 * The JSON hub protocol (`json`, version 1), the default protocol of SignalR.
 *
 * @param json The Json instance that encodes and decodes the payloads of messages (arguments, stream items and results).
 * By default unknown keys are ignored, like in the official SignalR clients.
 */
class JsonHubProtocol(private val json: Json = DefaultJson) : HubProtocol {

    override val name: String = PROTOCOL_NAME
    override val version: Int = PROTOCOL_VERSION
    override val transferFormat: TransferFormat = TransferFormat.Text

    override fun parseMessages(payload: ByteArray, logger: Logger): List<HubMessage> {
        val payloadString = payload.decodeToString(0, payload.size)
        if (payloadString.isEmpty()) return emptyList()
        if (payloadString.substring(payloadString.length - 1)[0] != RECORD_SEPARATOR) throw RuntimeException("HubMessage is incomplete.")

        return payloadString
            .split(RECORD_SEPARATOR)
            .filter { it.isNotEmpty() }
            .mapNotNull { str ->
                try {
                    logger.log(Logger.Severity.INFO, { "Decoding message: $str" }, null)
                    Json.parseToJsonElement(str).jsonObject.toHubMessage()
                } catch (ex: Exception) {
                    logger.log(Logger.Severity.ERROR, { "Failed to decode message: $str" }, ex)
                    null
                }
            }
    }

    override fun writeMessage(message: HubMessage): ByteArray =
        (Json.encodeToString(JsonObject.serializer(), message.toJsonObject()) + RECORD_SEPARATOR).toByteArray()

    private fun HubMessage.toJsonObject(): JsonObject = buildJsonObject {
        put("type", type)
        when (val message = this@toJsonObject) {
            is HubMessage.Invocation.Blocking -> {
                put("invocationId", message.invocationId)
                put("target", message.target)
                put("arguments", message.arguments.toJsonArray())
                message.streamIds?.let { put("streamIds", JsonArray(it.map(::JsonPrimitive))) }
            }

            is HubMessage.Invocation.NonBlocking -> {
                put("target", message.target)
                put("arguments", message.arguments.toJsonArray())
                message.streamIds?.let { put("streamIds", JsonArray(it.map(::JsonPrimitive))) }
            }

            is HubMessage.StreamInvocation -> {
                put("invocationId", message.invocationId)
                put("target", message.target)
                put("arguments", message.arguments.toJsonArray())
                message.streamIds?.let { put("streamIds", JsonArray(it.map(::JsonPrimitive))) }
            }

            is HubMessage.StreamItem -> {
                put("invocationId", message.invocationId)
                put("item", message.item.toJsonElement())
            }

            is HubMessage.Completion.Simple -> put("invocationId", message.invocationId)
            is HubMessage.Completion.Resulted -> {
                put("invocationId", message.invocationId)
                put("result", message.result.toJsonElement())
            }

            is HubMessage.Completion.Error -> {
                put("invocationId", message.invocationId)
                put("error", message.error)
            }

            is HubMessage.CancelInvocation -> put("invocationId", message.invocationId)
            is HubMessage.Ping -> Unit
            is HubMessage.Close -> {
                if (message.allowReconnect) put("allowReconnect", true)
                message.error?.let { put("error", it) }
            }
        }
    }

    private fun List<HubValue>.toJsonArray() = JsonArray(map { it.toJsonElement() })

    private fun HubValue.toJsonElement(): JsonElement = when (this) {
        is HubValue.Encodable -> json.encodeToJsonElement(serializer, value)
        is JsonValue -> element
        else -> throw IllegalArgumentException("$this cannot be written by the JSON protocol")
    }

    private fun JsonObject.toHubMessage(): HubMessage = when (val type = get("type")?.jsonPrimitive?.intOrNull) {
        HubMessageType.INVOCATION.value -> when (val invocationId = optionalString("invocationId")) {
            null -> HubMessage.Invocation.NonBlocking(target = string("target"), arguments = arguments(), streamIds = streamIds())
            else -> HubMessage.Invocation.Blocking(target = string("target"), arguments = arguments(), invocationId = invocationId, streamIds = streamIds())
        }

        HubMessageType.STREAM_INVOCATION.value -> HubMessage.StreamInvocation(
            target = string("target"),
            arguments = arguments(),
            invocationId = string("invocationId"),
            streamIds = streamIds(),
        )

        HubMessageType.PING.value -> HubMessage.Ping()
        HubMessageType.CLOSE.value -> HubMessage.Close(
            allowReconnect = get("allowReconnect")?.jsonPrimitive?.boolean ?: false,
            error = optionalString("error"),
        )

        HubMessageType.STREAM_ITEM.value -> HubMessage.StreamItem(invocationId = string("invocationId"), item = JsonValue(getValue("item"), json))
        HubMessageType.CANCEL_INVOCATION.value -> HubMessage.CancelInvocation(invocationId = string("invocationId"))
        HubMessageType.COMPLETION.value -> when {
            get("error").let { it != null && it !is JsonNull } -> HubMessage.Completion.Error(invocationId = string("invocationId"), error = string("error"))
            get("result").let { it != null && it !is JsonNull } -> HubMessage.Completion.Resulted(
                invocationId = string("invocationId"),
                result = JsonValue(getValue("result"), json),
            )

            else -> HubMessage.Completion.Simple(invocationId = string("invocationId"))
        }

        else -> error("Unknown type ($type) when deserializing SignalR structure: $this")
    }

    private fun JsonObject.arguments(): List<HubValue> = getValue("arguments").jsonArray.map { JsonValue(it, json) }

    private fun JsonObject.streamIds(): List<String>? =
        get("streamIds")?.takeIf { it !is JsonNull }?.jsonArray?.map { it.jsonPrimitive.quotedContent("streamIds") }

    private fun JsonObject.string(key: String): String = optionalString(key) ?: throw SerializationException("Field '$key' is required")

    private fun JsonObject.optionalString(key: String): String? = get(key)?.takeIf { it !is JsonNull }?.jsonPrimitive?.quotedContent(key)

    private fun JsonPrimitive.quotedContent(key: String): String =
        if (isString) content else throw SerializationException("String literal for key '$key' should be quoted: $this")

    companion object {
        private const val PROTOCOL_NAME = "json"
        private const val PROTOCOL_VERSION = 1
        private val DefaultJson = Json { ignoreUnknownKeys = true }
    }
}

internal class JsonValue(val element: JsonElement, private val json: Json) : HubValue.Decodable {

    override fun <T> decode(deserializer: DeserializationStrategy<T>): T = json.decodeFromJsonElement(deserializer, element)

    override fun equals(other: Any?): Boolean = other is JsonValue && element == other.element

    override fun hashCode(): Int = element.hashCode()

    override fun toString(): String = element.toString()
}
