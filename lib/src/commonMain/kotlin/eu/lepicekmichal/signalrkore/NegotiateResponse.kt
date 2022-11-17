package eu.lepicekmichal.signalrkore

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject

@Serializable(with = NegotiateResponse.NegotiateSerializer::class)
internal sealed class NegotiateResponse {

    @Serializable
    data class Success(
        val connectionToken: String,
        val connectionId: String,
        val negotiateVersion: Int,
        val availableTransports: List<AvailableTransport>,
    ) : NegotiateResponse()

    @Serializable
    data class Redirect(val url: String, val accessToken: String?) : NegotiateResponse()

    @Serializable
    data class Error(val error: String) : NegotiateResponse()

    object NegotiateSerializer : JsonContentPolymorphicSerializer<NegotiateResponse>(NegotiateResponse::class) {
        override fun selectDeserializer(element: JsonElement) = when {
            "error" in element.jsonObject -> Error.serializer()
            "url" in element.jsonObject -> Redirect.serializer()
            "ProtocolVersion" in element.jsonObject -> throw RuntimeException("Detected an ASP.NET SignalR Server. This client only supports connecting to an ASP.NET Core SignalR Server. See https://aka.ms/signalr-core-differences for details.")
            else -> Success.serializer()
        }
    }
}