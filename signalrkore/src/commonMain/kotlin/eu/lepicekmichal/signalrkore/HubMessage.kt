package eu.lepicekmichal.signalrkore

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

@OptIn(ExperimentalSerializationApi::class)
@Serializable(with = HubMessage.MessageSerializer::class)
sealed class HubMessage {
    @EncodeDefault
    abstract val type: Int

    @Serializable
    sealed class Invocation(
        @EncodeDefault override val type: Int = HubMessageType.INVOCATION.value,
    ) : HubMessage() {

        abstract val target: String
        abstract val arguments: List<JsonElement>

        @Serializable
        data class Blocking(override val target: String, override val arguments: List<JsonElement>, val invocationId: String) : Invocation()

        @Serializable
        data class NonBlocking(override val target: String, override val arguments: List<JsonElement>) : Invocation()

        @Serializable
        data class Streaming(
            override val target: String,
            override val arguments: List<JsonElement>,
            val invocationId: String,
            val streamIds: List<String>
        ) : Invocation()
    }

    @Serializable
    sealed class Completion(
        @EncodeDefault override val type: Int = HubMessageType.COMPLETION.value,
    ) : HubMessage() {

        abstract val invocationId: String

        @Serializable
        data class Simple(override val invocationId: String) : Completion()

        @Serializable
        data class Resulted(override val invocationId: String, val result: JsonElement) : Completion()

        @Serializable
        data class Error(override val invocationId: String, val error: String) : Completion()
    }

    @Serializable
    data class Ping(
        @EncodeDefault override val type: Int = HubMessageType.PING.value,
    ) : HubMessage()

    @Serializable
    data class Close(
        val allowReconnect: Boolean = false,
        val error: String? = null,
        @EncodeDefault override val type: Int = HubMessageType.CLOSE.value,
    ) : HubMessage()

    object MessageSerializer : JsonContentPolymorphicSerializer<HubMessage>(HubMessage::class) {
        override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out HubMessage> {
            val jsonObject = element.jsonObject
            return when (val type = jsonObject["type"]?.jsonPrimitive?.int) {
                HubMessageType.INVOCATION.value -> when {
                    jsonObject["streamIds"]?.jsonArray != null -> Invocation.Blocking.serializer()
                    jsonObject["invocationId"]?.jsonPrimitive?.contentOrNull != null -> Invocation.Streaming.serializer()
                    else -> Invocation.NonBlocking.serializer()
                }
                HubMessageType.PING.value -> Ping.serializer()
                HubMessageType.CLOSE.value -> Close.serializer()
                HubMessageType.COMPLETION.value -> when {
                    jsonObject["error"]?.jsonPrimitive?.isString != null -> Completion.Error.serializer()
                    jsonObject["result"] != null && jsonObject["result"] !is JsonNull -> Completion.Resulted.serializer()
                    else -> Completion.Simple.serializer()
                }
                else -> error("unknown type $type")
            }
        }
    }
}