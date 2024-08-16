package eu.lepicekmichal.signalrkore

import eu.lepicekmichal.signalrkore.transports.LongPollingTransport
import eu.lepicekmichal.signalrkore.transports.ServerSentEventsTransport
import eu.lepicekmichal.signalrkore.transports.WebSocketTransport
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

@OptIn(ExperimentalCoroutinesApi::class)
class HubConnection private constructor(
    private val baseUrl: String,
    private val protocol: HubProtocol,
    private val httpClient: HttpClient,
    private val transportEnum: TransportEnum,
    private val handshakeResponseTimeout: Duration,
    private val headers: Map<String, String>,
    private val skipNegotiate: Boolean,
    private val automaticReconnect: AutomaticReconnect,
    override val logger: Logger,
    json: Json,
) : HubCommunicationLink(json) {

    private val job = SupervisorJob()
    override val scope = CoroutineScope(job + Dispatchers.IO)

    private val pingReset = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private val pingTicker = pingReset
        .onStart { emit(Unit) }
        .flatMapLatest {
            flow {
                while (true) {
                    delay(KEEP_ALIVE_INTERVAL.milliseconds)
                    emit(Unit)
                }
            }
        }

    private val serverTimeoutReset = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private val serverTimeoutTicker = serverTimeoutReset
        .onStart { emit(Unit) }
        .flatMapLatest {
            flow<Nothing> {
                delay(SERVER_TIMEOUT.milliseconds)
                throw RuntimeException("Server timeout elapsed without receiving a message from the server.")
            }
        }

    private val _connectionState: MutableStateFlow<HubConnectionState> = MutableStateFlow(HubConnectionState.DISCONNECTED)
    val connectionState: StateFlow<HubConnectionState> = _connectionState.asStateFlow()

    private lateinit var transport: Transport

    internal constructor(
        url: String,
        skipNegotiate: Boolean,
        automaticReconnect: AutomaticReconnect,
        httpClient: HttpClient?,
        protocol: HubProtocol,
        handshakeResponseTimeout: Duration,
        headers: Map<String, String>,
        transportEnum: TransportEnum,
        transport: Transport?,
        json: Json,
        logger: Logger,
    ) : this(
        baseUrl = url.takeIf { it.isNotBlank() } ?: throw IllegalArgumentException("A valid url is required."),
        protocol = protocol,
        httpClient = httpClient ?: HttpClient().config {
            install(WebSockets)
            install(HttpTimeout)
            install(ContentNegotiation) { json(Json(json) { ignoreUnknownKeys = true }) }
        },
        transportEnum = transportEnum,
        handshakeResponseTimeout = if (handshakeResponseTimeout.isPositive()) handshakeResponseTimeout else 15.seconds,
        headers = headers,
        skipNegotiate = skipNegotiate,
        automaticReconnect = automaticReconnect,
        json = json,
        logger = logger,
    ) {
        transport?.let { this.transport = it }
    }

    suspend fun start(reconnectionAttempt: Boolean = false) {
        if (connectionState.value != HubConnectionState.DISCONNECTED && connectionState.value != HubConnectionState.RECONNECTING) return

        if (connectionState.value == HubConnectionState.DISCONNECTED) {
            _connectionState.value = HubConnectionState.CONNECTING
        }

        if (skipNegotiate && transportEnum != TransportEnum.WebSockets)
            throw RuntimeException("Negotiation can only be skipped when using a WebSocket transport")

        val (negotiationTransport, negotiationUrl) = if (!skipNegotiate) {
            try {
                startNegotiate(baseUrl, 0, headers)
            } catch (ex: Exception) {
                if (!reconnectionAttempt) {
                    if (automaticReconnect !is AutomaticReconnect.Inactive) reconnect(ex.message)
                    else stop(ex.message)
                    return
                } else {
                    throw ex
                }
            }
        } else {
            Negotiation(TransportEnum.WebSockets, baseUrl)
        }

        if (!::transport.isInitialized) {
            transport = when (negotiationTransport) {
                TransportEnum.LongPolling -> LongPollingTransport(headers, httpClient)
                TransportEnum.ServerSentEvents -> ServerSentEventsTransport(headers, httpClient)
                else -> WebSocketTransport(headers, httpClient)
            }
        }

        try {
            transport.start(negotiationUrl)
        } catch (ex: Exception) {
            if (!reconnectionAttempt) {
                if (automaticReconnect !is AutomaticReconnect.Inactive) reconnect(ex.message)
                else stop(ex.message)
                return
            } else {
                throw ex
            }
        }

        if (connectionState.value != HubConnectionState.CONNECTING && connectionState.value != HubConnectionState.RECONNECTING) {
            throw RuntimeException("Connection closed while trying to connect.")
        }

        withContext(Dispatchers.IO) {
            launch {
                val handshake = Json.encodeToString(Handshake(protocol = protocol.name, version = protocol.version)) + RECORD_SEPARATOR
                transport.send(handshake.toByteArray())
            }.invokeOnCompletion {
                if (it != null) _connectionState.value = HubConnectionState.DISCONNECTED
            }
        }

        handleHandshake(transport)

        _connectionState.value = HubConnectionState.CONNECTED

        if (negotiationTransport != TransportEnum.LongPolling) {
            scope.launch {
                pingTicker
                    .catch {
                        if (automaticReconnect !is AutomaticReconnect.Inactive) reconnect(it.message)
                        else stop(it.message)
                    }
                    .collect { sendHubMessage(message = HubMessage.Ping()) }
            }
            scope.launch {
                serverTimeoutTicker
                    .catch {
                        if (automaticReconnect !is AutomaticReconnect.Inactive) reconnect(it.message)
                        else stop(it.message)
                    }
                    .collect()
            }
        }

        resetServerTimeout()

        scope.launch {
            transport.receive()
                .catch {
                    if (automaticReconnect !is AutomaticReconnect.Inactive) reconnect(it.message)
                    else stop(it.message)
                }
                .collect { processReceived(it) }
        }
    }

    private suspend fun startNegotiate(
        url: String,
        negotiateAttempts: Int,
        headers: Map<String, String>,
    ): Negotiation {
        if (connectionState.value != HubConnectionState.CONNECTING && connectionState.value != HubConnectionState.RECONNECTING)
            throw RuntimeException("HubConnection trying to negotiate when not in the CONNECTING state.")

        val response = handleNegotiate(
            url,
            headers
        )

        when (response) {
            is NegotiateResponse.Error -> throw RuntimeException(response.error)
            is NegotiateResponse.Redirect -> {
                if (negotiateAttempts >= MAX_NEGOTIATE_ATTEMPTS) throw RuntimeException("Negotiate redirection limit exceeded.")

                return startNegotiate(
                    response.url,
                    negotiateAttempts + 1,
                    headers.map {
                            (
                                key,
                                value,
                            ),
                        ->
                        key to (if (key == "Authorization") "Bearer " + response.accessToken else value)
                    }.toMap()
                )
            }

            is NegotiateResponse.Success -> {
                fun NegotiateResponse.Success.selectTransport(vararg accessibleTransports: TransportEnum): TransportEnum? {
                    val candidates = accessibleTransports.intersect(availableTransports.transports.toSet())
                    return candidates.firstOrNull()
                }

                val chosenTransport = when (transportEnum) {
                    TransportEnum.All -> response.selectTransport(
                        TransportEnum.WebSockets,
                        TransportEnum.ServerSentEvents,
                        TransportEnum.LongPolling
                    )

                    else -> response.selectTransport(transportEnum)
                } ?: throw RuntimeException("There were no compatible transports on the server.")

                val finalUrl: String = URLBuilder(url).apply {
                    parameters.append("id", if (response.negotiateVersion > 0) response.connectionToken else response.connectionId)
                }.buildString()

                return Negotiation(
                    transport = chosenTransport,
                    url = finalUrl,
                )
            }
        }
    }

    private suspend fun handleNegotiate(
        url: String,
        headers: Map<String, String>,
    ): NegotiateResponse {
        val response = httpClient.post(resolveNegotiateUrl(url)) {
            headers {
                headers.forEach { (key, value) -> append(key, value) }
            }
        }

        if (response.status != HttpStatusCode.OK)
            throw RuntimeException("Unexpected status code returned from negotiate: ${response.status} ${response.status.description}.")

        return response.body()
    }

    private fun resolveNegotiateUrl(url: String): String = URLBuilder(url).apply {
        appendPathSegments("negotiate")
        parameters.appendIfNameAbsent("negotiateVersion", NEGOTIATE_VERSION.toString())
    }.buildString()

    private suspend fun handleHandshake(transport: Transport) {
        val handshakeCandidate = String(transport.receive().onEmpty { delay(handshakeResponseTimeout) }.first())

        if (handshakeCandidate.last() != RECORD_SEPARATOR) throw RuntimeException("HubMessage is incomplete.")

        val handshake = try {
            Json.decodeFromString<HandshakeResponse>(handshakeCandidate.substring(0, handshakeCandidate.lastIndex))
        } catch (ex: SerializationException) {
            throw RuntimeException("An invalid handshake response was received from the server.", ex)
        }

        if (handshake.error != null) {
            throw RuntimeException("Error in handshake ${handshake.error}")
        }
    }

    private suspend fun reconnect(errorMessage: String? = null) {
        stop(errorMessage)

        if (automaticReconnect !is AutomaticReconnect.Custom) return

        _connectionState.value = HubConnectionState.RECONNECTING

        scope.launch(Dispatchers.IO) {
            val mark = TimeSource.Monotonic.markNow()
            var retryCount = 0

            while (true) {
                val delayTime = automaticReconnect.invoke(
                    previousRetryCount = retryCount++,
                    elapsedTime = mark.elapsedNow(),
                )

                delay(timeMillis = delayTime ?: break)

                try {
                    logger.log(Logger.Severity.INFO, "[$baseUrl] Reconnecting - #${retryCount} attempt", null)
                    start(reconnectionAttempt = true)
                } catch (ex: Exception) {
                    logger.log(Logger.Severity.INFO, "[$baseUrl] Reconnecting error", ex)
                    continue
                }
                break
            }

            if (_connectionState.value != HubConnectionState.CONNECTED) {
                logger.log(Logger.Severity.INFO, "[$baseUrl] Reconnection unsuccessful, terminating", null)

                _connectionState.value = HubConnectionState.DISCONNECTED

                job.cancelChildren()
            }
        }.invokeOnCompletion {
            if (it != null) _connectionState.value = HubConnectionState.DISCONNECTED
        }
    }

    suspend fun stop(errorMessage: String? = null) {
        if (connectionState.value == HubConnectionState.DISCONNECTED) return

        _connectionState.value = HubConnectionState.DISCONNECTED

        logger.log(Logger.Severity.INFO, "[$baseUrl] ${errorMessage ?: "Stopping connection"}", null)

        if (::transport.isInitialized) transport.stop()
        job.cancelChildren()
    }

    override fun sendHubMessage(message: HubMessage) {
        if (connectionState.value != HubConnectionState.CONNECTED) {
            logger.log(Logger.Severity.ERROR, "Trying to send and message while the connection is not active. ($message)", null)
            return
        }

        val serializedMessage: ByteArray = protocol.writeMessage(message)
        scope.launch {
            if (::transport.isInitialized) transport.send(serializedMessage)
            logger.log(Logger.Severity.INFO, "Sent hub data: $message", null)
            resetKeepAlive()
        }
    }

    private suspend fun processReceived(payload: ByteArray) {
        resetServerTimeout()
        val messages = protocol.parseMessages(payload)

        messages.forEach { message ->
            when (message) {
                is HubMessage.Close -> {
                    if (message.allowReconnect && automaticReconnect !is AutomaticReconnect.Inactive) reconnect(message.error)
                    else stop(message.error)
                }

                is HubMessage.Invocation -> processReceivedInvocation(message)
                is HubMessage.StreamInvocation -> Unit // not supported yet
                is HubMessage.Ping -> Unit
                is HubMessage.CancelInvocation -> Unit // this should not happen according to standard
                is HubMessage.StreamItem -> processReceivedStreamItem(message)
                is HubMessage.Completion -> processReceivedCompletion(message)
            }
        }
    }

    private fun resetServerTimeout() {
        serverTimeoutReset.tryEmit(Unit)
    }

    private fun resetKeepAlive() {
        pingReset.tryEmit(Unit)
    }

    override fun connectedCheck(method: String) {
        if (connectionState.value != HubConnectionState.CONNECTED) {
            throw RuntimeException("The '$method' method cannot be called if the connection is not active.")
        }
    }

    companion object {
        private const val NEGOTIATE_VERSION = 1
        private const val MAX_NEGOTIATE_ATTEMPTS = 100
        private const val SERVER_TIMEOUT = 30 * 1000
        private const val KEEP_ALIVE_INTERVAL = 15 * 1000
    }
}
