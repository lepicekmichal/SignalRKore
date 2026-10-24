package eu.lepicekmichal.signalrkore

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException

abstract class HubCommunicationLink : HubCommunication() {

    protected abstract val scope: CoroutineScope

    private val handlerScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val receivedInvocations = MutableSharedFlow<HubMessage.Invocation>()

    private val pendingInvocations = MutableStateFlow<Map<String, Channel<HubMessage>>>(emptyMap())

    private val resultProviderRegistry: MutableSet<String> = mutableSetOf()

    protected abstract val logger: Logger

    final override fun <T : Any> T.toHubValue(serializer: KSerializer<T>): HubValue = HubValue.Encodable(this, serializer)

    final override fun <T> HubValue.fromHubValue(deserializer: KSerializer<T>): T = when (this) {
        is HubValue.Decodable -> decode(deserializer)
        else -> throw IllegalArgumentException("$this is not a received value and cannot be decoded")
    }

    protected abstract fun sendHubMessage(message: HubMessage)

    protected abstract fun connectedCheck(method: String)

    private fun complete(message: HubMessage.Completion) {
        try {
            connectedCheck("complete")
            sendHubMessage(message)
        } catch (ex: Exception) {
            logger.log(
                severity = Logger.Severity.ERROR,
                message = { "Sending completion message has thrown an exception" },
                cause = ex,
            )
        }
    }

    final override fun send(method: String, args: List<HubValue>, streams: List<Flow<HubValue>>) {
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

    private fun launchStreams(streamIds: List<String>, uploadStreams: List<Flow<HubValue>>) {
        streamIds.zip(uploadStreams) { id, stream ->
            scope.launch {
                stream
                    .map<_, HubMessage> { HubMessage.StreamItem(invocationId = id, item = it) }
                    .onCompletion { throwable ->
                        if (throwable != null) throw throwable
                        else emit(HubMessage.Completion.Simple(invocationId = id))
                    }
                    .catch { emit(HubMessage.Completion.Error(invocationId = id, error = it.message.orEmpty())) }
                    .collect { sendHubMessage(it) }
            }
        }
    }

    final override suspend fun invoke(method: String, args: List<HubValue>, streams: List<Flow<HubValue>>) = internalInvoke(
        method = method,
        args = args,
        uploadStreams = streams,
        processSimple = { },
        processResult = { logger.log(Logger.Severity.INFO, { "Result of a completion message has been ignored: ${it.result}" }, null) },
    )

    final override suspend fun <T : Any> invoke(
        method: String,
        resultSerializer: KSerializer<T>,
        args: List<HubValue>,
        streams: List<Flow<HubValue>>,
    ): T = internalInvoke(
        method = method,
        args = args,
        uploadStreams = streams,
        processSimple = { throw RuntimeException("The completion message has no result, but one is expected") },
        processResult = {
            try {
                it.result.fromHubValue(resultSerializer)
            } catch (ex: SerializationException) {
                throw RuntimeException("Completion result could not be parsed as $resultSerializer: ${it.result}", ex)
            } catch (ex: IllegalArgumentException) {
                throw RuntimeException("$resultSerializer could not be initialized from the completion result: ${it.result}", ex)
            }
        },
    )

    private suspend fun <T : Any> internalInvoke(
        method: String,
        args: List<HubValue>,
        uploadStreams: List<Flow<HubValue>>,
        processSimple: (HubMessage.Completion.Simple) -> T,
        processResult: (HubMessage.Completion.Resulted) -> T,
    ): T {
        val invocationId = UUID.randomUUID()

        val streamIds = uploadStreams.map { UUID.randomUUID() }
        val invocationMessage = HubMessage.Invocation.Blocking(
            target = method,
            arguments = args,
            invocationId = invocationId,
            streamIds = streamIds.takeIf { it.isNotEmpty() })

        val messages = registerInvocation(invocationId)
        try {
            connectedCheck("invoke")
            sendHubMessage(invocationMessage)
            launchStreams(streamIds, uploadStreams)

            return when (val completion = messages.receiveAsFlow().filterIsInstance<HubMessage.Completion>().first()) {
                is HubMessage.Completion.Error -> throw HubException(completion.error)
                is HubMessage.Completion.Simple -> processSimple(completion)
                is HubMessage.Completion.Resulted -> processResult(completion)
            }
        } finally {
            unregisterInvocation(invocationId)
        }
    }

    final override fun <T : Any> stream(
        method: String,
        itemSerializer: KSerializer<T>,
        args: List<HubValue>,
        streams: List<Flow<HubValue>>,
    ): Flow<T> {
        connectedCheck("stream")

        return flow {
            val invocationId = UUID.randomUUID()

            val streamIds = streams.map { UUID.randomUUID() }
            val invocationMessage = HubMessage.StreamInvocation(
                target = method,
                arguments = args,
                invocationId = invocationId,
                streamIds = streamIds.takeIf { it.isNotEmpty() },
            )

            val messages = registerInvocation(invocationId)
            var awaitingCompletion = false
            try {
                connectedCheck("stream")
                sendHubMessage(invocationMessage)
                launchStreams(streamIds, streams)
                awaitingCompletion = true

                for (message in messages) {
                    when (message) {
                        is HubMessage.StreamItem -> emit(
                            try {
                                message.item.fromHubValue(itemSerializer)
                            } catch (ex: SerializationException) {
                                throw RuntimeException("Completion result could not be parsed as $itemSerializer: ${message.item}", ex)
                            } catch (ex: IllegalArgumentException) {
                                throw RuntimeException(
                                    "$itemSerializer could not be initialized from the completion result: ${message.item}",
                                    ex
                                )
                            }
                        )

                        is HubMessage.Completion -> {
                            awaitingCompletion = false
                            when (message) {
                                is HubMessage.Completion.Error -> throw HubException(message.error)
                                is HubMessage.Completion.Resulted -> throw IllegalStateException("According to standard, stream cannot be finished with result")
                                is HubMessage.Completion.Simple -> break
                            }
                        }

                        else -> Unit
                    }
                }
            } finally {
                unregisterInvocation(invocationId)
                if (awaitingCompletion && !messages.isClosedForReceive) {
                    sendHubMessage(HubMessage.CancelInvocation(invocationId))
                }
            }
        }
    }

    private fun registerInvocation(invocationId: String): ReceiveChannel<HubMessage> {
        val messages = Channel<HubMessage>(Channel.UNLIMITED)
        pendingInvocations.update { it + (invocationId to messages) }
        return messages
    }

    private fun unregisterInvocation(invocationId: String) {
        pendingInvocations.update { it - invocationId }
    }

    protected fun cancelPendingInvocations(cause: Throwable?) {
        pendingInvocations.getAndUpdate { emptyMap() }.values.forEach {
            it.close(ConnectionClosedException("Invocation canceled due to the underlying connection being closed.", cause))
        }
    }

    private fun dispatchToInvocation(invocationId: String, message: HubMessage) {
        val messages = pendingInvocations.value[invocationId]
        if (messages == null) {
            logger.log(Logger.Severity.INFO, { "Dropped unsolicited message for invocation '$invocationId': $message" }, null)
            return
        }

        messages.trySend(message)
    }

    protected suspend fun processReceivedInvocation(message: HubMessage.Invocation) {
        if (message is HubMessage.Invocation.Blocking && !resultProviderRegistry.contains(message.target)) {
            logger.log(
                severity = Logger.Severity.WARNING,
                message = { "There is no result provider for '${message.target}' despite server expecting it." },
                cause = null,
            )

            complete(
                HubMessage.Completion.Error(
                    invocationId = message.invocationId,
                    error = "Client did not provide a result.",
                ),
            )
        }

        receivedInvocations.emit(message)
    }

    protected fun processReceivedStreamItem(message: HubMessage.StreamItem) =
        dispatchToInvocation(message.invocationId, message)

    protected fun processReceivedCompletion(message: HubMessage.Completion) =
        dispatchToInvocation(message.invocationId, message)

    final override fun <T : Any> Flow<HubMessage.Invocation>.handleIncomingInvocation(
        resultSerializer: KSerializer<T>,
        callback: suspend (HubMessage.Invocation) -> T,
    ) {
        collectInScope(handlerScope) { message ->
            when (message) {
                is HubMessage.Invocation.NonBlocking -> {
                    try {
                        callback(message)
                    } catch (ex: Exception) {
                        logger.log(
                            severity = Logger.Severity.ERROR,
                            message = { "Getting result for non-blocking invocation of '${message.target}' method has thrown an exception" },
                            cause = ex,
                        )
                    }
                }

                is HubMessage.Invocation.Blocking -> {
                    val result = try {
                        callback(message).toHubValue(resultSerializer)
                    } catch (ex: Exception) {
                        logger.log(
                            severity = Logger.Severity.ERROR,
                            message = { "Getting result for blocking invocation of '${message.target}' method has thrown an exception" },
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
                            result = result,
                        )
                    )
                }
            }
        }
    }

    final override fun <R : Any> Flow<HubMessage.Invocation>.mapCatching(transform: suspend (value: HubMessage.Invocation) -> R): Flow<R> =
        mapNotNull { message ->
            try {
                transform(message)
            } catch (ex: Exception) {
                logger.log(
                    severity = Logger.Severity.ERROR,
                    message = {
                        "Getting result for ${
                            when (message) {
                                is HubMessage.Invocation.Blocking -> "blocking"
                                is HubMessage.Invocation.NonBlocking -> "non-blocking"
                            }
                        } invocation of '${message.target}' method has thrown an exception"
                    },
                    cause = ex,
                )
                null
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
            .onEach { logger.log(Logger.Severity.INFO, { "Received invocation: $it" }, null) }
    }
}