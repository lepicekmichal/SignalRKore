package eu.lepicekmichal.signalrkore

import eu.lepicekmichal.signalrkore.transports.LongPollingTransport
import eu.lepicekmichal.signalrkore.transports.ServerSentEventsTransport
import eu.lepicekmichal.signalrkore.transports.WebSocketTransport
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.sse.SSE
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.http.appendPathSegments
import io.ktor.serialization.kotlinx.json.json
import io.ktor.util.appendIfNameAbsent
import io.ktor.utils.io.core.String
import io.ktor.utils.io.core.toByteArray
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

/**
 * The main class for connecting to a SignalR hub.
 *
 * HubConnection provides methods for starting and stopping the connection,
 * sending and receiving messages, and monitoring the connection status.
 *
 * This class is not instantiated directly. Use [HubConnectionBuilder] to create instances.
 *
 * Example:
 * ```
 * val connection = HubConnectionBuilder.create("https://example.com/chathub")
 * connection.start()
 * connection.send("broadcastMessage", "User", "Hello, SignalR!")
 * connection.stop()
 * ```
 *
 * @property baseUrl The URL of the SignalR hub
 * @property protocol The protocol used for communication with the hub
 * @property httpClient The HTTP client used for communication
 * @property transports The transports allowed for communication
 * @property handshakeResponseTimeout The timeout for handshake response
 * @property headers HTTP headers to include in requests
 * @property accessTokenProvider Lazy access token to include in HTTP headers in requests
 * @property skipNegotiate Whether to skip the negotiate step (WebSockets only)
 * @property automaticReconnect The automatic reconnect policy
 * @property serverTimeout The server timeout duration
 * @property keepAliveInterval The keep-alive interval duration
 * @property logger The logger for logging messages
 */
@OptIn(ExperimentalCoroutinesApi::class)
class HubConnection private constructor(
    private val baseUrl: String,
    private val protocol: HubProtocol,
    private val httpClient: HttpClient,
    private val transports: Set<TransportEnum>,
    private val handshakeResponseTimeout: Duration,
    private val headers: Map<String, String>,
    private val accessTokenProvider: (suspend () -> String)?,
    private val skipNegotiate: Boolean,
    private val automaticReconnect: AutomaticReconnect,
    private val serverTimeout: Duration,
    private val keepAliveInterval: Duration,
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
                    delay(keepAliveInterval)
                    emit(Unit)
                }
            }
        }

    private val serverTimeoutReset = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    private val serverTimeoutTicker = serverTimeoutReset
        .onStart { emit(Unit) }
        .flatMapLatest {
            flow<Nothing> {
                delay(serverTimeout)
                throw RuntimeException("Server timeout elapsed without receiving a message from the server.")
            }
        }

    private val _connectionState: MutableStateFlow<HubConnectionState> = MutableStateFlow(HubConnectionState.DISCONNECTED)
    val connectionState: StateFlow<HubConnectionState> = _connectionState.asStateFlow()

    private val _connectionId: MutableStateFlow<String?> = MutableStateFlow(null)
    val connectionId: StateFlow<String?> = _connectionId.asStateFlow()

    private var transport: Transport? = null

    private var outgoing: Channel<Pair<HubMessage, ByteArray>>? = null

    internal constructor(
        url: String,
        skipNegotiate: Boolean,
        automaticReconnect: AutomaticReconnect,
        httpClient: HttpClient?,
        protocol: HubProtocol,
        handshakeResponseTimeout: Duration,
        headers: Map<String, String>,
        accessTokenProvider: (suspend () -> String)?,
        transports: Set<TransportEnum>,
        json: Json,
        serverTimeout: Duration,
        keepAliveInterval: Duration,
        logger: Logger,
    ) : this(
        baseUrl = url.takeIf { it.isNotBlank() } ?: throw IllegalArgumentException("A valid url is required."),
        protocol = protocol,
        httpClient = httpClient ?: HttpClient().config {
            install(WebSockets)
            install(SSE)
            install(HttpTimeout)
            install(ContentNegotiation) { json(Json(json) { ignoreUnknownKeys = true }) }
        },
        transports = transports,
        handshakeResponseTimeout = if (handshakeResponseTimeout.isPositive()) handshakeResponseTimeout else 15.seconds,
        headers = headers,
        accessTokenProvider = accessTokenProvider,
        skipNegotiate = skipNegotiate,
        automaticReconnect = automaticReconnect,
        json = json,
        serverTimeout = serverTimeout,
        keepAliveInterval = keepAliveInterval,
        logger = logger,
    )

    suspend fun start() {
        if (connectionState.value != HubConnectionState.DISCONNECTED && connectionState.value != HubConnectionState.RECONNECTING) return

        try {
            connect()
        } catch (ex: Exception) {
            if (ex is CancellationException) scope.launch { close(cause = null, message = "Start was cancelled") }
            else close(cause = ex, message = "Failed to start connection: ${ex.message}")
            throw ex
        }
    }

    private suspend fun connect() {
        if (connectionState.value == HubConnectionState.DISCONNECTED) {
            _connectionState.value = HubConnectionState.CONNECTING
        }

        if (skipNegotiate && transports != setOf(TransportEnum.WebSockets))
            throw RuntimeException("Negotiation can only be skipped when using a WebSocket transport")

        val headersWithAccessToken = accessTokenProvider?.invoke()?.let { accessToken ->
            headers.toMutableMap().apply { this["Authorization"] = "Bearer $accessToken" }
        } ?: headers

        val (transportType, transport) = if (!skipNegotiate) {
            startAvailableTransport(headersWithAccessToken)
        } else {
            TransportEnum.WebSockets to startTransport(TransportEnum.WebSockets, baseUrl, headersWithAccessToken)
        }

        if (connectionState.value != HubConnectionState.CONNECTING && connectionState.value != HubConnectionState.RECONNECTING) {
            throw RuntimeException("Connection closed while trying to connect.")
        }

        val handshake = Json.encodeToString(Handshake(protocol = protocol.name, version = protocol.version)) + RECORD_SEPARATOR
        transport.send(handshake.toByteArray())

        val handshakeRemaining = handleHandshake(transport)

        val outgoing = Channel<Pair<HubMessage, ByteArray>>(Channel.UNLIMITED)
        this.outgoing = outgoing
        scope.launch {
            for ((message, payload) in outgoing) {
                try {
                    transport.send(payload)
                    logger.log(Logger.Severity.INFO, { "Sent hub data: $message" }, null)
                    resetKeepAlive()
                } catch (e: Exception) {
                    e.rethrowIfCancelled()
                    logger.log(Logger.Severity.ERROR, { "Failed to send hub data: $message" }, e)
                    handleConnectionLost(e)
                    return@launch
                }
            }
        }

        _connectionState.value = HubConnectionState.CONNECTED

        if (transportType != TransportEnum.LongPolling) {
            scope.launch {
                pingTicker
                    .onEach { sendHubMessage(message = HubMessage.Ping()) }
                    .catch { handleConnectionLost(it) }
                    .collect()
            }
            scope.launch {
                serverTimeoutTicker
                    .catch { handleConnectionLost(it) }
                    .collect()
            }
        }

        resetServerTimeout()

        scope.launch {
            transport.receive()
                .onStart { handshakeRemaining?.let { emit(it) } }
                .onEach { processReceived(it) }
                .onCompletion { if (it == null) handleConnectionLost(cause = null, message = "Server closed the connection") }
                .catch { handleConnectionLost(it) }
                .collect()
        }
    }

    private suspend fun startAvailableTransport(headers: Map<String, String>): Pair<TransportEnum, Transport> {
        val initialNegotiation = startNegotiate(url = baseUrl, headers = headers, negotiateAttempts = 0)

        val candidates = initialNegotiation.transports.filter { it in transports }
        if (candidates.isEmpty()) throw RuntimeException("There were no compatible transports on the server.")

        var negotiation: Negotiation? = initialNegotiation
        val failures = mutableMapOf<TransportEnum, Throwable>()
        for (candidate in candidates) {
            val current = negotiation ?: startNegotiate(url = baseUrl, headers = headers, negotiateAttempts = 0)
            try {
                return candidate to startTransport(candidate, current.url, current.headers)
            } catch (ex: Exception) {
                ex.rethrowIfCancelled()
                logger.log(Logger.Severity.WARNING, { "[$baseUrl] Failed to start the transport $candidate" }, ex)
                failures[candidate] = ex
                negotiation = null
            }
        }

        throw failures.values.singleOrNull() ?: TransportsFailedException(failures)
    }

    private suspend fun startTransport(type: TransportEnum, url: String, headers: Map<String, String>): Transport {
        stopTransport()

        val transport = when (type) {
            TransportEnum.LongPolling -> LongPollingTransport(headers, httpClient)
            TransportEnum.ServerSentEvents -> ServerSentEventsTransport(headers, httpClient)
            TransportEnum.WebSockets -> WebSocketTransport(headers, httpClient)
        }

        this.transport = transport

        transport.start(url)
        return transport
    }

    private suspend fun handleConnectionLost(cause: Throwable?, message: String? = null) {
        if (connectionState.value != HubConnectionState.CONNECTED) return

        if (automaticReconnect !is AutomaticReconnect.Inactive) reconnect(cause, message)
        else close(cause, message)
    }

    private suspend fun startNegotiate(
        url: String,
        headers: Map<String, String>,
        negotiateAttempts: Int,
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

                val newHeaders = response.accessToken?.let { token ->
                    headers.toMutableMap().apply { put("Authorization", "Bearer $token") }
                } ?: headers

                return startNegotiate(
                    url = response.url,
                    headers = newHeaders,
                    negotiateAttempts = negotiateAttempts + 1,
                )
            }

            is NegotiateResponse.Success -> {
                val connectionToken = if (response.negotiateVersion > 0) {
                    response.connectionToken ?: throw RuntimeException("Negotiate response is missing the connection token.")
                } else {
                    response.connectionId
                }
                _connectionId.update { response.connectionId }
                val finalUrl: String = URLBuilder(url).apply { parameters.append("id", connectionToken) }.buildString()

                return Negotiation(
                    transports = response.availableTransports.transports,
                    url = finalUrl,
                    headers = headers,
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
            throw HttpRequestException("Unexpected status code returned from negotiate: ${response.status} ${response.status.description}.", response.status.value)

        return response.body()
    }

    private fun resolveNegotiateUrl(url: String): String = URLBuilder(url).apply {
        appendPathSegments("negotiate")
        parameters.appendIfNameAbsent("negotiateVersion", NEGOTIATE_VERSION.toString())
    }.buildString()

    private suspend fun handleHandshake(transport: Transport): ByteArray? {
        val payload = withTimeoutOrNull(handshakeResponseTimeout) { transport.receive().first() }
            ?.let { String(it) }
            ?: throw RuntimeException("Timed out waiting for the handshake response from the server.")

        val separatorIndex = payload.indexOf(RECORD_SEPARATOR)
        if (separatorIndex == -1) throw RuntimeException("HubMessage is incomplete.")

        val handshake = try {
            Json.decodeFromString<HandshakeResponse>(payload.substring(0, separatorIndex))
        } catch (ex: SerializationException) {
            throw RuntimeException("An invalid handshake response was received from the server.", ex)
        }

        if (handshake.error != null) {
            throw RuntimeException("Error in handshake ${handshake.error}")
        }

        return payload.substring(separatorIndex + 1).takeIf { it.isNotEmpty() }?.toByteArray()
    }

    private suspend fun reconnect(cause: Throwable?, message: String? = null) {
        close(cause, message)

        if (automaticReconnect !is AutomaticReconnect.Custom) return

        _connectionState.value = HubConnectionState.RECONNECTING

        scope.launch(Dispatchers.IO) {
            val mark = TimeSource.Monotonic.markNow()
            var retryCount = 0
            var lastError = cause

            while (true) {
                val delayTime = try {
                    automaticReconnect.invoke(
                        previousRetryCount = retryCount++,
                        elapsedTime = mark.elapsedNow(),
                    )
                } catch (ex: Exception) {
                    logger.log(Logger.Severity.ERROR, { "[$baseUrl] Automatic reconnect policy has thrown an exception" }, ex)
                    null
                }

                delay(timeMillis = delayTime ?: break)

                try {
                    logger.log(Logger.Severity.INFO, { "[$baseUrl] Reconnecting - #${retryCount} attempt" }, null)
                    connect()
                } catch (ex: Exception) {
                    ex.rethrowIfCancelled()
                    logger.log(Logger.Severity.WARNING, { "[$baseUrl] Reconnecting error" }, ex)
                    lastError = ex
                    continue
                }
                break
            }

            if (_connectionState.value != HubConnectionState.CONNECTED) {
                close(lastError, message = "Reconnection unsuccessful, terminating")
            }
        }.invokeOnCompletion {
            if (it != null) _connectionState.value = HubConnectionState.DISCONNECTED
        }
    }

    suspend fun stop(errorMessage: String? = null) {
        if (connectionState.value == HubConnectionState.CONNECTED) sendCloseMessage()

        close(cause = null, message = errorMessage)
    }

    private suspend fun close(cause: Throwable?, message: String? = null) {
        if (connectionState.value == HubConnectionState.DISCONNECTED) return

        _connectionState.value = HubConnectionState.DISCONNECTED

        if (cause != null) logger.log(Logger.Severity.ERROR, { "[$baseUrl] ${message ?: cause.message}" }, cause)
        else logger.log(Logger.Severity.INFO, { "[$baseUrl] ${message ?: "Stopping connection"}" }, null)

        cancelPendingInvocations(cause)

        try {
            stopTransport()
        } finally {
            job.cancelChildren()
        }
    }

    private suspend fun sendCloseMessage() {
        try {
            transport?.send(protocol.writeMessage(HubMessage.Close()))
        } catch (ex: Exception) {
            ex.rethrowIfCancelled()
            logger.log(Logger.Severity.WARNING, { "[$baseUrl] Failed to send close message" }, ex)
        }
    }

    private suspend fun stopTransport() {
        try {
            transport?.stop()
        } catch (ex: Exception) {
            ex.rethrowIfCancelled()
            logger.log(Logger.Severity.WARNING, { "[$baseUrl] Failed to stop transport" }, ex)
        }
    }

    private suspend fun Exception.rethrowIfCancelled() {
        if (this is CancellationException) currentCoroutineContext().ensureActive()
    }

    override fun sendHubMessage(message: HubMessage) {
        if (connectionState.value != HubConnectionState.CONNECTED) {
            logger.log(Logger.Severity.ERROR, { "Trying to send a message while the connection is not active. ($message)" }, null)
            return
        }

        val serializedMessage: ByteArray = protocol.writeMessage(message)
        outgoing?.trySend(message to serializedMessage)
    }

    private suspend fun processReceived(payload: ByteArray) {
        resetServerTimeout()
        val messages = protocol.parseMessages(payload)

        messages.forEach { message ->
            when (message) {
                is HubMessage.Close -> {
                    val cause = message.error?.let { RuntimeException("Server returned an error on close: $it") }
                    if (message.allowReconnect && automaticReconnect !is AutomaticReconnect.Inactive) reconnect(cause, "Server closed the connection")
                    else close(cause, "Server closed the connection")
                }

                is HubMessage.Invocation -> processReceivedInvocation(message)
                is HubMessage.StreamInvocation -> {
                    logger.log(Logger.Severity.WARNING, { "Server invoked streaming method '${message.target}', which is not supported" }, null)
                    sendHubMessage(HubMessage.Completion.Error(invocationId = message.invocationId, error = "Client does not support streaming invocations."))
                }
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
    }
}