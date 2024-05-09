package eu.lepicekmichal.signalrkore

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

abstract class HubCommunicationLink(private val json: Json) : HubCommunication() {

    protected abstract val scope: CoroutineScope

    private val receivedInvocations = MutableSharedFlow<HubMessage.Invocation>()
    private val receivedCompletions = MutableSharedFlow<HubMessage.Completion>()
    private val receivedStreamItems = MutableSharedFlow<HubMessage.StreamItem>()

    private val resultProviderRegistry: MutableSet<String> = mutableSetOf()

    protected abstract val logger: Logger

    @OptIn(InternalSerializationApi::class)
    final override fun <T : Any> T.toJson(kClass: KClass<T>): JsonElement = json.encodeToJsonElement(kClass.serializer(), this)

    @OptIn(InternalSerializationApi::class)
    final override fun <T : Any> JsonElement.fromJson(kClass: KClass<T>): T = json.decodeFromJsonElement(kClass.serializer(), this)

    protected abstract fun sendHubMessage(message: HubMessage)

    protected abstract fun connectedCheck(method: String)

    private fun complete(message: HubMessage.Completion) {
        connectedCheck("complete")
        sendHubMessage(message)
    }

    final override fun send(method: String, args: List<JsonElement>, streams: List<Flow<JsonElement>>) {
        connectedCheck("send")

        val streamIds = streams.map { UUID.randomUUID() }
        val invocationMessage = HubMessage.Invocation.NonBlocking(
            target = method,
            arguments = args,
            streamIds = streamIds.takeIf { it.isNotEmpty() }
        )
        sendHubMessage(invocationMessage)
        launchStreams(streamIds, streams)
    }

    private fun launchStreams(streamIds: List<String>, uploadStreams: List<Flow<JsonElement>>) {
        streamIds.zip(uploadStreams) { id, stream ->
            scope.launch {
                stream
                    .map<_, HubMessage> { HubMessage.StreamItem(invocationId = id, item = it) }
                    .onCompletion { throwable -> throwable?.let { throw it } ?: emit(HubMessage.Completion.Simple(invocationId = id)) }
                    .catch { emit(HubMessage.Completion.Error(invocationId = id, error = it.message.orEmpty())) }
                    .collect { sendHubMessage(it) }
            }
        }
    }

    final override suspend fun invoke(method: String, args: List<JsonElement>, streams: List<Flow<JsonElement>>) = internalInvoke(
        method = method,
        args = args,
        uploadStreams = streams,
        processSimple = { },
        processResult = { logger.log(Logger.Severity.INFO, "Result of a completion message has been ignored: ${it.result}", null) },
    )

    final override suspend fun <T : Any> invoke(
        method: String,
        resultType: KClass<T>,
        args: List<JsonElement>,
        streams: List<Flow<JsonElement>>,
    ): T = internalInvoke(
        method = method,
        args = args,
        uploadStreams = streams,
        processSimple = { throw RuntimeException("The completion message has no result, but one is expected") },
        processResult = {
            try {
                it.result.fromJson(resultType)
            } catch (ex: SerializationException) {
                throw RuntimeException("Completion result could not be parsed as ${resultType.simpleName}: ${it.result}", ex)
            } catch (ex: IllegalArgumentException) {
                throw RuntimeException("${resultType.simpleName} could not be initialized from the completion result: ${it.result}", ex)
            }
        },
    )

    private suspend fun <T : Any> internalInvoke(
        method: String,
        args: List<JsonElement>,
        uploadStreams: List<Flow<JsonElement>>,
        processSimple: (HubMessage.Completion.Simple) -> T,
        processResult: (HubMessage.Completion.Resulted) -> T,
    ): T {
        connectedCheck("invoke")

        val invocationId = UUID.randomUUID()

        val streamIds = uploadStreams.map { UUID.randomUUID() }
        val invocationMessage = HubMessage.Invocation.Blocking(
            target = method,
            arguments = args,
            invocationId = invocationId,
            streamIds = streamIds.takeIf { it.isNotEmpty() })
        sendHubMessage(invocationMessage)
        launchStreams(streamIds, uploadStreams)

        return when (val completion = receivedCompletions.filter { it.invocationId == invocationId }.first()) {
            is HubMessage.Completion.Error -> throw RuntimeException(completion.error)
            is HubMessage.Completion.Simple -> processSimple(completion)
            is HubMessage.Completion.Resulted -> processResult(completion)
        }
    }

    final override fun <T : Any> stream(
        method: String,
        itemType: KClass<T>,
        args: List<JsonElement>,
        streams: List<Flow<JsonElement>>,
    ): Flow<T> {
        connectedCheck("stream")

        val invocationId = UUID.randomUUID()

        val streamIds = streams.map { UUID.randomUUID() }
        val invocationMessage = HubMessage.StreamInvocation(
            target = method,
            arguments = args,
            invocationId = invocationId,
            streamIds = streamIds.takeIf { it.isNotEmpty() },
        )

        return callbackFlow {
            var completionReceived = false
            val complete = {
                completionReceived = true
                cancel()
            }

            launch {
                receivedStreamItems
                    .filter { it.invocationId == invocationId }
                    .map {
                        try {
                            it.item.fromJson(itemType)
                        } catch (ex: SerializationException) {
                            throw RuntimeException("Completion result could not be parsed as ${itemType.simpleName}: ${it.item}", ex)
                        } catch (ex: IllegalArgumentException) {
                            throw RuntimeException("${itemType.simpleName} could not be initialized from the completion result: ${it.item}", ex)
                        }
                    }
                    .collect { if (!isClosedForSend) send(it) }
            }

            launch {
                when (val completion = receivedCompletions.filter { it.invocationId == invocationId }.first()) {
                    is HubMessage.Completion.Error -> throw RuntimeException(completion.error)
                    is HubMessage.Completion.Resulted -> throw IllegalStateException("According to standard, stream cannot be finished with result")
                    is HubMessage.Completion.Simple -> complete()
                }
            }

            awaitClose {
                if (!completionReceived) {
                    sendHubMessage(HubMessage.CancelInvocation(invocationId))
                }
            }
        }.onStart {
            sendHubMessage(invocationMessage)
            launchStreams(streamIds, streams)
        }
    }

    protected suspend fun processReceivedInvocation(message: HubMessage.Invocation) {
        if (message is HubMessage.Invocation.Blocking && !resultProviderRegistry.contains(message.target)) {
            logger.log(
                severity = Logger.Severity.WARNING,
                message = "There is no result provider for '${message.target}' despite server expecting it.",
                cause = null,
            )

            complete(
                HubMessage.Completion.Error(
                    invocationId = message.invocationId,
                    error = "Client did not provide a result."
                ),
            )
        }

        receivedInvocations.emit(message)
    }

    protected suspend fun processReceivedStreamItem(message: HubMessage.StreamItem) =
        receivedStreamItems.emit(message)

    protected suspend fun processReceivedCompletion(message: HubMessage.Completion) =
        receivedCompletions.emit(message)

    final override fun <T : Any> Flow<HubMessage.Invocation>.handleIncomingInvocation(
        resultType: KClass<T>,
        callback: suspend (HubMessage.Invocation) -> T,
    ) {
        collectInScope(scope) { message ->
            when (message) {
                is HubMessage.Invocation.NonBlocking -> {
                    try {
                        callback(message)
                    } catch (ex: Exception) {
                        logger.log(
                            severity = Logger.Severity.ERROR,
                            message = "Getting result for non-blocking invocation of '${message.target}' method has thrown an exception",
                            cause = ex,
                        )
                    }
                    if (resultType != Unit::class) {
                        // todo should I also print the actual result inside message?
                        logger.log(
                            severity = Logger.Severity.WARNING,
                            message = "Result was returned for '${message.target}' method but server is not expecting any result.",
                            cause = null,
                        )
                    }
                }

                is HubMessage.Invocation.Blocking -> {
                    val result = try {
                        callback(message)
                    } catch (ex: Exception) {
                        logger.log(
                            severity = Logger.Severity.ERROR,
                            message = "Getting result for blocking invocation of '${message.target}' method has thrown an exception",
                            cause = ex,
                        )

                        return@collectInScope complete(
                            HubMessage.Completion.Error(
                                invocationId = message.invocationId,
                                error = ex.message.orEmpty(),
                            )
                        )
                    }

                    complete(
                        HubMessage.Completion.Resulted(
                            invocationId = message.invocationId,
                            result = result.toJson(resultType),
                        )
                    )
                }
            }
        }
    }

    final override fun on(target: String, hasResult: Boolean): Flow<HubMessage.Invocation> {
        if (hasResult && !resultProviderRegistry.add(target)) {
            throw IllegalStateException("There can be only one function for returning result on blocking invocation (method: $target)")
        }
        return receivedInvocations
            .run {
                if (!hasResult) this
                else this
                    .onCompletion { resultProviderRegistry.remove(target) }
            }
            .filter { it.target == target }
            .onEach { logger.log(Logger.Severity.INFO, "Received invocation: $it", null) }
    }
}