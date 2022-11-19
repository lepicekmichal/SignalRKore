package eu.lepicekmichal.signalrkore

import eu.lepicekmichal.signalrkore.transports.LongPollingTransport
import eu.lepicekmichal.signalrkore.transports.ServerSentEventsTransport
import eu.lepicekmichal.signalrkore.transports.WebSocketTransport
import eu.lepicekmichal.signalrkore.utils.dispatchers
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
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
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.serializer
import kotlin.reflect.KClass
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class HubConnection private constructor(
    private val baseUrl: String,
    private val protocol: HubProtocol,
    private val httpClient: HttpClient,
    private val transportEnum: TransportEnum,
    private val handshakeResponseTimeout: Duration,
    private val headers: Map<String, String>,
    private val skipNegotiate: Boolean,
    private val json: Json,
) : HubCommunication() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(job + dispatchers.io)

    private val pingReset = MutableSharedFlow<Unit>()
    private val pingTicker = pingReset
        .onStart { emit(Unit) }
        .flatMapLatest {
            flow {
                while (true) {
                    emit(Unit)
                    delay(KEEP_ALIVE_INTERVAL.milliseconds)
                }
            }
        }

    private val serverTimeoutReset = MutableSharedFlow<Unit>()
    private val serverTimeoutTicker = serverTimeoutReset
        .onStart { emit(Unit) }
        .flatMapLatest {
            flow<Nothing> {
                while (true) {
                    delay(SERVER_TIMEOUT.milliseconds)
                    throw RuntimeException("Server timeout elapsed without receiving a message from the server.")
                }
            }
        }

    override val receivedInvocations = MutableSharedFlow<HubMessage.Invocation>()
    private val receivedCompletions = MutableSharedFlow<HubMessage.Completion>()

    private val _connectionState: MutableStateFlow<HubConnectionState> = MutableStateFlow(HubConnectionState.DISCONNECTED)
    val connectionState: StateFlow<HubConnectionState> = _connectionState.asStateFlow()

    private lateinit var transport: Transport

    internal constructor(
        url: String,
        skipNegotiate: Boolean,
        httpClient: HttpClient?,
        protocol: HubProtocol,
        handshakeResponseTimeout: Duration,
        headers: Map<String, String>,
        transportEnum: TransportEnum,
        json: Json,
    ) : this(
        baseUrl = url.takeIf { it.isNotBlank() } ?: throw IllegalArgumentException("A valid url is required."),
        protocol = protocol,
        httpClient = (httpClient ?: HttpClient(CIO)).config {
            install(WebSockets)
            install(HttpTimeout)
            install(ContentNegotiation) { json(Json) }
        },
        transportEnum = transportEnum,
        handshakeResponseTimeout = if (handshakeResponseTimeout.isPositive()) handshakeResponseTimeout else 15.seconds,
        headers = headers,
        skipNegotiate = skipNegotiate,
        json = json,
    )

    suspend fun start() {
        if (connectionState.value != HubConnectionState.DISCONNECTED) return

        _connectionState.value = HubConnectionState.CONNECTING

        if (skipNegotiate && transportEnum != TransportEnum.WebSockets)
            throw RuntimeException("Negotiation can only be skipped when using the WebSocket transport")

        val (negotiationTransport, negotiationUrl) = if (!skipNegotiate) {
            startNegotiate(baseUrl, 0, headers)
        } else {
            Negotiation(TransportEnum.WebSockets, baseUrl)
        }

        transport = when (negotiationTransport) {
            TransportEnum.LongPolling -> LongPollingTransport(headers, httpClient)
            TransportEnum.ServerSentEvents -> ServerSentEventsTransport(headers)
            else -> WebSocketTransport(headers, httpClient)
        }

        transport.start(negotiationUrl)

        if (connectionState.value != HubConnectionState.CONNECTING) throw RuntimeException("Connection closed while trying to connect.")

        scope.launch {
            val handshake = Json.encodeToString(Handshake(protocol = protocol.name, version = protocol.version)) + RECORD_SEPARATOR
            transport.send(handshake.toByteArray())
        }

        handleHandshake(transport)

        _connectionState.value = HubConnectionState.CONNECTED

        if (negotiationTransport != TransportEnum.LongPolling) {
            scope.launch {
                pingTicker
                    .catch { stop(it.message) }
                    .collect { sendHubMessageWithLock(message = HubMessage.Ping()) }
            }
            scope.launch {
                serverTimeoutTicker
                    .catch { stop(it.message) }
                    .collect()
            }
        }

        resetServerTimeout()

        scope.launch {
            transport.receive()
                .catch { it.printStack() }
                .collect { processReceived(it) }
        }
    }

    private suspend fun startNegotiate(
        url: String,
        negotiateAttempts: Int,
        headers: Map<String, String>
    ): Negotiation {
        if (connectionState.value !== HubConnectionState.CONNECTING)
            throw RuntimeException("HubConnection trying to negotiate when not in the CONNECTING state.")

        val response = handleNegotiate(
            url,
            headers
        )

        when (response) {
            is NegotiateResponse.Error -> throw RuntimeException(response.error)
            is NegotiateResponse.Redirect -> {
                if (negotiateAttempts >= MAX_NEGOTIATE_ATTEMPTS) throw RuntimeException("Negotiate redirection limit exceeded.")

                return startNegotiate(response.url, negotiateAttempts + 1, headers.map { (key, value) ->
                    key to (if (key == "Authorization") "Bearer " + response.accessToken else value)
                }.toMap())
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
        headers: Map<String, String>
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

    suspend fun stop(errorMessage: String? = null) {
        if (connectionState.value == HubConnectionState.DISCONNECTED) return

        println(errorMessage ?: "Stopping connection")

        transport.stop()
        job.cancelChildren()
    }

    private fun sendHubMessageWithLock(message: HubMessage) {
        if (connectionState.value != HubConnectionState.CONNECTED)
            throw RuntimeException("Trying to send and message while the connection is not active.")

        val serializedMessage: ByteArray = protocol.writeMessage(message)
        scope.launch {
            transport.send(serializedMessage)
            resetKeepAlive()
        }
    }

    private suspend fun processReceived(payload: ByteArray) {
        resetServerTimeout()
        val messages = protocol.parseMessages(payload)

        messages.forEach { message ->
            when (message) {
                is HubMessage.Close -> stop(message.error)
                is HubMessage.Invocation -> receivedInvocations.emit(message)
                is HubMessage.Ping -> Unit
//                is CancelInvocationMessage ->
//                is HubMessage.StreamItem ->
                is HubMessage.Completion -> receivedCompletions.emit(message)
            }
        }
    }

    @OptIn(InternalSerializationApi::class)
    override fun <T : Any> T.toJson(kClass: KClass<T>): JsonElement = json.encodeToJsonElement(kClass.serializer(), this)

    @OptIn(InternalSerializationApi::class)
    override fun <T : Any> JsonElement.fromJson(kClass: KClass<T>): T = json.decodeFromJsonElement(kClass.serializer(), this)

    override fun send(method: String, args: List<JsonElement>) {
        connectedCheck("send")

        val invocationMessage = HubMessage.Invocation.NonBlocking(target = method, arguments = args)
        sendHubMessageWithLock(invocationMessage)
    }

    override suspend fun invoke(method: String, args: List<JsonElement>) = internalInvoke(
        method = method,
        args = args,
        processSimple = { },
        processResult = { println("Result of a completion message has been ignored: ${it.result}") },
    )

    override suspend fun <T : Any> invoke(result: KClass<T>, method: String, args: List<JsonElement>): T = internalInvoke(
        method = method,
        args = args,
        processSimple = { throw RuntimeException("The completion message has no result, but one is expected") },
        processResult = {
            try {
                it.result.fromJson(result)
            } catch (ex: SerializationException) {
                throw RuntimeException("Completion result could not be parsed as ${result.simpleName}: ${it.result}")
            } catch (ex: IllegalArgumentException) {
                throw RuntimeException("${result.simpleName} could not be initialized from the completion result: ${it.result}")
            }
        },
    )

    private suspend fun <T : Any> internalInvoke(
        method: String,
        args: List<JsonElement>,
        processSimple: (HubMessage.Completion.Simple) -> T,
        processResult: (HubMessage.Completion.Resulted) -> T,
    ): T {
        connectedCheck("invoke")

        val invocationId = UUID.randomUUID()

        val invocationMessage = HubMessage.Invocation.Blocking(target = method, arguments = args, invocationId = invocationId)
        sendHubMessageWithLock(invocationMessage)

        return when (val completion = receivedCompletions.filter { it.invocationId == invocationId }.first()) {
            is HubMessage.Completion.Error -> throw RuntimeException(completion.error)
            is HubMessage.Completion.Simple -> processSimple(completion)
            is HubMessage.Completion.Resulted -> processResult(completion)
        }
    }

    private fun resetServerTimeout() {
        serverTimeoutReset.tryEmit(Unit)
    }

    private fun resetKeepAlive() {
        pingReset.tryEmit(Unit)
    }

    private fun connectedCheck(method: String) {
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
