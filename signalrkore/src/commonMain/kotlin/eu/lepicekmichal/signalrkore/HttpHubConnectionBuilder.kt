package eu.lepicekmichal.signalrkore

import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class HttpHubConnectionBuilder(private val url: String) {

    /**
     * The transport type to be used by the [eu.lepicekmichal.signalrkore.HubConnection]
     */
    var transportEnum: TransportEnum = TransportEnum.All

    /**
     * The [HttpClient] to be used by the [eu.lepicekmichal.signalrkore.HubConnection]
     */
    var httpClient: HttpClient? = null

    /**
     * The [HubProtocol] to be used by the [eu.lepicekmichal.signalrkore.HubConnection]
     */
    lateinit var protocol: HubProtocol

    /**
     * Boolean indicating if the [eu.lepicekmichal.signalrkore.HubConnection] should skip the negotiate step
     */
    var skipNegotiate: Boolean = false

    /**
     * The access token provider to be used by the [eu.lepicekmichal.signalrkore.HubConnection]
     */
    var accessToken: String?
        get() = headers["Authorization"]
        set(token) {
            headers["Authorization"] = "Bearer $token"
        }

    /**
     * The duration that the [eu.lepicekmichal.signalrkore.HubConnection] should wait for a Handshake Response from the server
     */
    var handshakeResponseTimeout: Duration = 10.seconds

    /**
     * A Map representing the collection of Headers that the [eu.lepicekmichal.signalrkore.HubConnection] should send.
     */
    var headers: MutableMap<String, String> = HashMap()

    /**
     * Json instance for (de)serializing custom models coming through as payloads
     */
    var json: Json = Json

    /**
     * Json instance for (de)serializing custom models coming through as payloads
     */
    var logger: Logger = Logger { _, _ -> }

    /**
     * Defines whether and how to automatically reconnect on close
     */
    var automaticReconnect: AutomaticReconnect = AutomaticReconnect.Inactive

    /**
     * @return A new instance of [eu.lepicekmichal.signalrkore.HubConnection].
     */
    fun build(): HubConnection = HubConnection(
        url,
        skipNegotiate,
        automaticReconnect,
        httpClient,
        if (::protocol.isInitialized) protocol else JsonHubProtocol(logger),
        handshakeResponseTimeout,
        headers.toMap(),
        transportEnum,
        json,
        logger,
    )
}