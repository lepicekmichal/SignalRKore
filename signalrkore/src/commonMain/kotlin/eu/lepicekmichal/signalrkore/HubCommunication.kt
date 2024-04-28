package eu.lepicekmichal.signalrkore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onSubscription
import kotlinx.serialization.json.JsonElement
import kotlin.reflect.KClass

abstract class HubCommunication {

    protected abstract val receivedInvocations: SharedFlow<HubMessage.Invocation>

    internal val subscribersWithResult: MutableMap<String, Boolean> = mutableMapOf()

    protected abstract val logger: Logger

    protected abstract fun <T : Any> T.toJson(kClass: KClass<T>): JsonElement

    protected abstract fun <T : Any> JsonElement.fromJson(kClass: KClass<T>): T

    protected abstract suspend fun <TResult> handleInvocation(
        message: HubMessage.Invocation,
        resultType: KClass<TResult>,
        callback: suspend () -> TResult
    ) where TResult : Any

    abstract fun send(method: String, args: List<JsonElement>, uploadStreams: List<Flow<JsonElement>> = emptyList())

    abstract suspend fun invoke(method: String, args: List<JsonElement>, uploadStreams: List<Flow<JsonElement>> = emptyList())

    abstract suspend fun <T : Any> invoke(
        result: KClass<T>,
        method: String,
        args: List<JsonElement>,
        uploadStreams: List<Flow<JsonElement>> = emptyList(),
    ): T

    abstract fun <T : Any> stream(
        itemType: KClass<T>,
        method: String,
        args: List<JsonElement>,
        uploadStreams: List<Flow<JsonElement>> = emptyList(),
    ): Flow<T>

    fun <F1 : Any> send(method: String, uploadStream1: Flow<F1>, streamType1: KClass<F1>) =
        send(
            method = method,
            args = emptyList(),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )

    fun <F1 : Any, F2 : Any> send(
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        send(
            method = method,
            args = emptyList(),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )

    fun <F1 : Any, F2 : Any, F3 : Any> send(
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        send(
            method = method,
            args = emptyList(),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )

    fun <F1 : Any, F2 : Any, F3 : Any, F4 : Any> send(
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        send(
            method = method,
            args = emptyList(),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )

    fun <F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> send(
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        send(
            method = method,
            args = emptyList(),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )

    fun <F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> send(
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        send(
            method = method,
            args = emptyList(),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )

    fun <F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> send(
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        send(
            method = method,
            args = emptyList(),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )

    fun <F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> send(
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        send(
            method = method,
            args = emptyList(),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )

    fun <T1 : Any> send(method: String, arg1: T1, argType1: KClass<T1>) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = emptyList(),
        )

    fun <T1 : Any, F1 : Any> send(method: String, arg1: T1, argType1: KClass<T1>, uploadStream1: Flow<F1>, streamType1: KClass<F1>) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )

    fun <T1 : Any, F1 : Any, F2 : Any> send(
        method: String,
        arg1: T1,
        argType1: KClass<T1>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )

    fun <T1 : Any, F1 : Any, F2 : Any, F3 : Any> send(
        method: String,
        arg1: T1,
        argType1: KClass<T1>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )

    fun <T1 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> send(
        method: String,
        arg1: T1,
        argType1: KClass<T1>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )

    fun <T1 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> send(
        method: String,
        arg1: T1,
        argType1: KClass<T1>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )

    fun <T1 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> send(
        method: String,
        arg1: T1,
        argType1: KClass<T1>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )

    fun <T1 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> send(
        method: String,
        arg1: T1,
        argType1: KClass<T1>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )

    fun <T1 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> send(
        method: String,
        arg1: T1,
        argType1: KClass<T1>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )

    fun <T1 : Any, T2 : Any> send(method: String, arg1: T1, arg2: T2, argType1: KClass<T1>, argType2: KClass<T2>) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = emptyList(),
        )

    fun <T1 : Any, T2 : Any, F1 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        uploadStream1: Flow<F1>,
        streamType1: KClass<F1>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )

    fun <T1 : Any, T2 : Any, F1 : Any, F2 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )

    fun <T1 : Any, T2 : Any, F1 : Any, F2 : Any, F3 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )

    fun <T1 : Any, T2 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )

    fun <T1 : Any, T2 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )

    fun <T1 : Any, T2 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )

    fun <T1 : Any, T2 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )

    fun <T1 : Any, T2 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = emptyList(),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, F1 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        uploadStream1: Flow<F1>,
        streamType1: KClass<F1>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, F1 : Any, F2 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, F1 : Any, F2 : Any, F3 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = emptyList(),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, F1 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        uploadStream1: Flow<F1>,
        streamType1: KClass<F1>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, F1 : Any, F2 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, F1 : Any, F2 : Any, F3 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        send(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = emptyList(),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, F1 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        uploadStream1: Flow<F1>,
        streamType1: KClass<F1>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, F1 : Any, F2 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, F1 : Any, F2 : Any, F3 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = emptyList(),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, F1 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        uploadStream1: Flow<F1>,
        streamType1: KClass<F1>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, F1 : Any, F2 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, F1 : Any, F2 : Any, F3 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = emptyList(),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, F1 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        uploadStream1: Flow<F1>,
        streamType1: KClass<F1>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, F1 : Any, F2 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, F1 : Any, F2 : Any, F3 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = emptyList(),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, F1 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>,
        uploadStream1: Flow<F1>,
        streamType1: KClass<F1>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, F1 : Any, F2 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, F1 : Any, F2 : Any, F3 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )

    fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        send(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )

    inline fun <reified F1 : Any> send(method: String, uploadStream1: Flow<F1>) =
        send(
            method = method,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    inline fun <reified F1 : Any, reified F2 : Any> send(method: String, uploadStream1: Flow<F1>, uploadStream2: Flow<F2>) =
        send(
            method = method,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    inline fun <reified F1 : Any, reified F2 : Any, reified F3 : Any> send(
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        send(
            method = method,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    inline fun <reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> send(
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        send(
            method = method,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    inline fun <reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> send(
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        send(
            method = method,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    inline fun <reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> send(
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        send(
            method = method,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    inline fun <reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> send(
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        send(
            method = method,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    inline fun <reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> send(
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        send(
            method = method,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    inline fun <reified T1 : Any> send(method: String, arg1: T1) =
        send(
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
        )

    inline fun <reified T1 : Any, reified F1 : Any> send(method: String, arg1: T1, uploadStream1: Flow<F1>) =
        send(
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    inline fun <reified T1 : Any, reified F1 : Any, reified F2 : Any> send(
        method: String,
        arg1: T1,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        send(
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    inline fun <reified T1 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> send(
        method: String,
        arg1: T1,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        send(
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    inline fun <reified T1 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> send(
        method: String,
        arg1: T1,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        send(
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    inline fun <reified T1 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> send(
        method: String,
        arg1: T1,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        send(
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    inline fun <reified T1 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> send(
        method: String,
        arg1: T1,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        send(
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    inline fun <reified T1 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> send(
        method: String,
        arg1: T1,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        send(
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    inline fun <reified T1 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> send(
        method: String,
        arg1: T1,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        send(
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any> send(method: String, arg1: T1, arg2: T2) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified F1 : Any> send(method: String, arg1: T1, arg2: T2, uploadStream1: Flow<F1>) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified F1 : Any, reified F2 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any> send(method: String, arg1: T1, arg2: T2, arg3: T3) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified F1 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        uploadStream1: Flow<F1>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified F1 : Any, reified F2 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified F1 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        uploadStream1: Flow<F1>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified F1 : Any, reified F2 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified F1 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        uploadStream1: Flow<F1>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified F1 : Any, reified F2 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified F1 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        uploadStream1: Flow<F1>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified F1 : Any, reified F2 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified F1 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        uploadStream1: Flow<F1>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified F1 : Any, reified F2 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified F1 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        uploadStream1: Flow<F1>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified F1 : Any, reified F2 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> send(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        send(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    suspend fun <F1 : Any> invoke(method: String, uploadStream1: Flow<F1>, streamType1: KClass<F1>) =
        invoke(
            method = method,
            args = emptyList(),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )

    suspend fun <F1 : Any, F2 : Any> invoke(
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        invoke(
            method = method,
            args = emptyList(),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )

    suspend fun <F1 : Any, F2 : Any, F3 : Any> invoke(
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        invoke(
            method = method,
            args = emptyList(),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )

    suspend fun <F1 : Any, F2 : Any, F3 : Any, F4 : Any> invoke(
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        invoke(
            method = method,
            args = emptyList(),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )

    suspend fun <F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> invoke(
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        invoke(
            method = method,
            args = emptyList(),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )

    suspend fun <F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> invoke(
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        invoke(
            method = method,
            args = emptyList(),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )

    suspend fun <F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> invoke(
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        invoke(
            method = method,
            args = emptyList(),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )

    suspend fun <F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> invoke(
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        invoke(
            method = method,
            args = emptyList(),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )

    suspend fun <T1 : Any> invoke(method: String, arg1: T1, argType1: KClass<T1>) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = emptyList(),
        )

    suspend fun <T1 : Any, F1 : Any> invoke(
        method: String,
        arg1: T1,
        argType1: KClass<T1>,
        uploadStream1: Flow<F1>,
        streamType1: KClass<F1>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )

    suspend fun <T1 : Any, F1 : Any, F2 : Any> invoke(
        method: String,
        arg1: T1,
        argType1: KClass<T1>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )

    suspend fun <T1 : Any, F1 : Any, F2 : Any, F3 : Any> invoke(
        method: String,
        arg1: T1,
        argType1: KClass<T1>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )

    suspend fun <T1 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> invoke(
        method: String,
        arg1: T1,
        argType1: KClass<T1>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )

    suspend fun <T1 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> invoke(
        method: String,
        arg1: T1,
        argType1: KClass<T1>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )

    suspend fun <T1 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> invoke(
        method: String,
        arg1: T1,
        argType1: KClass<T1>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )

    suspend fun <T1 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> invoke(
        method: String,
        arg1: T1,
        argType1: KClass<T1>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )

    suspend fun <T1 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> invoke(
        method: String,
        arg1: T1,
        argType1: KClass<T1>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )

    suspend fun <T1 : Any, T2 : Any> invoke(method: String, arg1: T1, arg2: T2, argType1: KClass<T1>, argType2: KClass<T2>) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = emptyList(),
        )

    suspend fun <T1 : Any, T2 : Any, F1 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        uploadStream1: Flow<F1>,
        streamType1: KClass<F1>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )

    suspend fun <T1 : Any, T2 : Any, F1 : Any, F2 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )

    suspend fun <T1 : Any, T2 : Any, F1 : Any, F2 : Any, F3 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )

    suspend fun <T1 : Any, T2 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )

    suspend fun <T1 : Any, T2 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )

    suspend fun <T1 : Any, T2 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )

    suspend fun <T1 : Any, T2 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )

    suspend fun <T1 : Any, T2 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = emptyList(),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, F1 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        uploadStream1: Flow<F1>,
        streamType1: KClass<F1>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, F1 : Any, F2 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, F1 : Any, F2 : Any, F3 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = emptyList(),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, F1 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        uploadStream1: Flow<F1>,
        streamType1: KClass<F1>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, F1 : Any, F2 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, F1 : Any, F2 : Any, F3 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        invoke(
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = emptyList(),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, F1 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        uploadStream1: Flow<F1>,
        streamType1: KClass<F1>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, F1 : Any, F2 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, F1 : Any, F2 : Any, F3 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = emptyList(),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, F1 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        uploadStream1: Flow<F1>,
        streamType1: KClass<F1>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, F1 : Any, F2 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, F1 : Any, F2 : Any, F3 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = emptyList(),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, F1 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        uploadStream1: Flow<F1>,
        streamType1: KClass<F1>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, F1 : Any, F2 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, F1 : Any, F2 : Any, F3 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = emptyList(),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, F1 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>,
        uploadStream1: Flow<F1>,
        streamType1: KClass<F1>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, F1 : Any, F2 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, F1 : Any, F2 : Any, F3 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )

    suspend fun <T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        invoke(
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )

    suspend inline fun <reified F1 : Any> invoke(method: String, uploadStream1: Flow<F1>) =
        invoke(
            method = method,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    suspend inline fun <reified F1 : Any, reified F2 : Any> invoke(method: String, uploadStream1: Flow<F1>, uploadStream2: Flow<F2>) =
        invoke(
            method = method,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    suspend inline fun <reified F1 : Any, reified F2 : Any, reified F3 : Any> invoke(
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        invoke(
            method = method,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    suspend inline fun <reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> invoke(
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        invoke(
            method = method,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    suspend inline fun <reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> invoke(
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        invoke(
            method = method,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    suspend inline fun <reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> invoke(
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        invoke(
            method = method,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    suspend inline fun <reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> invoke(
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        invoke(
            method = method,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    suspend inline fun <reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> invoke(
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        invoke(
            method = method,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    suspend inline fun <reified T1 : Any> invoke(method: String, arg1: T1) =
        invoke(
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
        )

    suspend inline fun <reified T1 : Any, reified F1 : Any> invoke(method: String, arg1: T1, uploadStream1: Flow<F1>) =
        invoke(
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    suspend inline fun <reified T1 : Any, reified F1 : Any, reified F2 : Any> invoke(
        method: String,
        arg1: T1,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    suspend inline fun <reified T1 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> invoke(
        method: String,
        arg1: T1,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    suspend inline fun <reified T1 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> invoke(
        method: String,
        arg1: T1,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    suspend inline fun <reified T1 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> invoke(
        method: String,
        arg1: T1,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    suspend inline fun <reified T1 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> invoke(
        method: String,
        arg1: T1,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    suspend inline fun <reified T1 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> invoke(
        method: String,
        arg1: T1,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    suspend inline fun <reified T1 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> invoke(
        method: String,
        arg1: T1,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any> invoke(method: String, arg1: T1, arg2: T2) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified F1 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        uploadStream1: Flow<F1>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified F1 : Any, reified F2 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any> invoke(method: String, arg1: T1, arg2: T2, arg3: T3) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified F1 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        uploadStream1: Flow<F1>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified F1 : Any, reified F2 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified F1 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        uploadStream1: Flow<F1>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified F1 : Any, reified F2 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified F1 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        uploadStream1: Flow<F1>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified F1 : Any, reified F2 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified F1 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        uploadStream1: Flow<F1>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified F1 : Any, reified F2 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified F1 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        uploadStream1: Flow<F1>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified F1 : Any, reified F2 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified F1 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        uploadStream1: Flow<F1>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified F1 : Any, reified F2 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    suspend inline fun <reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> invoke(
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        invoke(
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    suspend inline fun <A : Any, reified F1 : Any> invoke(result: KClass<A>, method: String, uploadStream1: Flow<F1>) =
        invoke(
            result = result,
            method = method,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    suspend inline fun <A : Any, reified F1 : Any, reified F2 : Any> invoke(
        result: KClass<A>,
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        invoke(
            result = result,
            method = method,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    suspend inline fun <A : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> invoke(
        result: KClass<A>,
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        invoke(
            result = result,
            method = method,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    suspend inline fun <A : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> invoke(
        result: KClass<A>,
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        invoke(
            result = result,
            method = method,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    suspend inline fun <A : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> invoke(
        result: KClass<A>,
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        invoke(
            result = result,
            method = method,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    suspend inline fun <A : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> invoke(
        result: KClass<A>,
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        invoke(
            result = result,
            method = method,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    suspend inline fun <A : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> invoke(
        result: KClass<A>,
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        invoke(
            result = result,
            method = method,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    suspend inline fun <A : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> invoke(
        result: KClass<A>,
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        invoke(
            result = result,
            method = method,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    suspend inline fun <A : Any, reified T1 : Any> invoke(result: KClass<A>, method: String, arg1: T1) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified F1 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        uploadStream1: Flow<F1>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified F1 : Any, reified F2 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any> invoke(result: KClass<A>, method: String, arg1: T1, arg2: T2) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified F1 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        uploadStream1: Flow<F1>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified F1 : Any, reified F2 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified F1 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        uploadStream1: Flow<F1>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified F1 : Any, reified F2 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified F1 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        uploadStream1: Flow<F1>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified F1 : Any, reified F2 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified F1 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        uploadStream1: Flow<F1>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified F1 : Any, reified F2 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified F1 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        uploadStream1: Flow<F1>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified F1 : Any, reified F2 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified F1 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        uploadStream1: Flow<F1>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified F1 : Any, reified F2 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified F1 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        uploadStream1: Flow<F1>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified F1 : Any, reified F2 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    suspend inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        invoke(
            result = result,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    suspend fun <A : Any, F1 : Any> invoke(result: KClass<A>, method: String, uploadStream1: Flow<F1>, streamType1: KClass<F1>) =
        invoke(
            result = result,
            method = method,
            args = emptyList(),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )


    suspend fun <A : Any, F1 : Any, F2 : Any> invoke(
        result: KClass<A>,
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        invoke(
            result = result,
            method = method,
            args = emptyList(),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )


    suspend fun <A : Any, F1 : Any, F2 : Any, F3 : Any> invoke(
        result: KClass<A>,
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        invoke(
            result = result,
            method = method,
            args = emptyList(),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )


    suspend fun <A : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> invoke(
        result: KClass<A>,
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        invoke(
            result = result,
            method = method,
            args = emptyList(),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )


    suspend fun <A : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> invoke(
        result: KClass<A>,
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        invoke(
            result = result,
            method = method,
            args = emptyList(),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )


    suspend fun <A : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> invoke(
        result: KClass<A>,
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        invoke(
            result = result,
            method = method,
            args = emptyList(),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )


    suspend fun <A : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> invoke(
        result: KClass<A>,
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        invoke(
            result = result,
            method = method,
            args = emptyList(),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )


    suspend fun <A : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> invoke(
        result: KClass<A>,
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        invoke(
            result = result,
            method = method,
            args = emptyList(),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )


    suspend fun <A : Any, T1 : Any> invoke(result: KClass<A>, method: String, arg1: T1, argType1: KClass<T1>) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = emptyList(),
        )


    suspend fun <A : Any, T1 : Any, F1 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        argType1: KClass<T1>,
        uploadStream1: Flow<F1>,
        streamType1: KClass<F1>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )


    suspend fun <A : Any, T1 : Any, F1 : Any, F2 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        argType1: KClass<T1>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )


    suspend fun <A : Any, T1 : Any, F1 : Any, F2 : Any, F3 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        argType1: KClass<T1>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )


    suspend fun <A : Any, T1 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        argType1: KClass<T1>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )


    suspend fun <A : Any, T1 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        argType1: KClass<T1>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )


    suspend fun <A : Any, T1 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        argType1: KClass<T1>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )


    suspend fun <A : Any, T1 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        argType1: KClass<T1>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )


    suspend fun <A : Any, T1 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        argType1: KClass<T1>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = emptyList(),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, F1 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        uploadStream1: Flow<F1>,
        streamType1: KClass<F1>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, F1 : Any, F2 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, F1 : Any, F2 : Any, F3 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = emptyList(),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, F1 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        uploadStream1: Flow<F1>,
        streamType1: KClass<F1>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, F1 : Any, F2 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, F1 : Any, F2 : Any, F3 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = emptyList(),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, F1 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        uploadStream1: Flow<F1>,
        streamType1: KClass<F1>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, F1 : Any, F2 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, F1 : Any, F2 : Any, F3 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = emptyList(),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, F1 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        uploadStream1: Flow<F1>,
        streamType1: KClass<F1>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, F1 : Any, F2 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, F1 : Any, F2 : Any, F3 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = emptyList(),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, F1 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        uploadStream1: Flow<F1>,
        streamType1: KClass<F1>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, F1 : Any, F2 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, F1 : Any, F2 : Any, F3 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = emptyList(),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, F1 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        uploadStream1: Flow<F1>,
        streamType1: KClass<F1>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, F1 : Any, F2 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, F1 : Any, F2 : Any, F3 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = emptyList(),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, F1 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>,
        uploadStream1: Flow<F1>,
        streamType1: KClass<F1>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, F1 : Any, F2 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, F1 : Any, F2 : Any, F3 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )


    suspend fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        invoke(
            result = result,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )

    private fun on(target: String): Flow<HubMessage.Invocation> =
        receivedInvocations
            .filter { it.target == target }
            .onEach { logger.log(Logger.Level.INFO, "Received invocation: $it") }

    fun <T1> on(target: String, param1: KClass<T1>): Flow<T1> where T1 : Any =
        on(target)
            .map { it.arguments[0].fromJson(param1) }

    fun <T1, T2> on(target: String, param1: KClass<T1>, param2: KClass<T2>): Flow<OnResult2<T1, T2>> where T1 : Any, T2 : Any =
        on(target)
            .map {
                OnResult2(
                    it.arguments[0].fromJson(param1),
                    it.arguments[1].fromJson(param2),
                )
            }

    fun <T1, T2, T3> on(
        target: String,
        param1: KClass<T1>,
        param2: KClass<T2>,
        param3: KClass<T3>
    ): Flow<OnResult3<T1, T2, T3>> where T1 : Any, T2 : Any, T3 : Any =
        on(target)
            .map {
                OnResult3(
                    it.arguments[0].fromJson(param1),
                    it.arguments[1].fromJson(param2),
                    it.arguments[2].fromJson(param3),
                )
            }

    fun <T1, T2, T3, T4> on(
        target: String,
        param1: KClass<T1>,
        param2: KClass<T2>,
        param3: KClass<T3>,
        param4: KClass<T4>
    ): Flow<OnResult4<T1, T2, T3, T4>> where T1 : Any, T2 : Any, T3 : Any, T4 : Any =
        on(target)
            .map {
                OnResult4(
                    it.arguments[0].fromJson(param1),
                    it.arguments[1].fromJson(param2),
                    it.arguments[2].fromJson(param3),
                    it.arguments[3].fromJson(param4),
                )
            }

    fun <T1, T2, T3, T4, T5> on(
        target: String,
        param1: KClass<T1>,
        param2: KClass<T2>,
        param3: KClass<T3>,
        param4: KClass<T4>,
        param5: KClass<T5>
    ): Flow<OnResult5<T1, T2, T3, T4, T5>> where T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any =
        on(target)
            .map {
                OnResult5(
                    it.arguments[0].fromJson(param1),
                    it.arguments[1].fromJson(param2),
                    it.arguments[2].fromJson(param3),
                    it.arguments[3].fromJson(param4),
                    it.arguments[4].fromJson(param5),
                )
            }

    fun <T1, T2, T3, T4, T5, T6> on(
        target: String,
        param1: KClass<T1>,
        param2: KClass<T2>,
        param3: KClass<T3>,
        param4: KClass<T4>,
        param5: KClass<T5>,
        param6: KClass<T6>
    ): Flow<OnResult6<T1, T2, T3, T4, T5, T6>> where T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any =
        on(target)
            .map {
                OnResult6(
                    it.arguments[0].fromJson(param1),
                    it.arguments[1].fromJson(param2),
                    it.arguments[2].fromJson(param3),
                    it.arguments[3].fromJson(param4),
                    it.arguments[4].fromJson(param5),
                    it.arguments[5].fromJson(param6),
                )
            }

    fun <T1, T2, T3, T4, T5, T6, T7> on(
        target: String,
        param1: KClass<T1>,
        param2: KClass<T2>,
        param3: KClass<T3>,
        param4: KClass<T4>,
        param5: KClass<T5>,
        param6: KClass<T6>,
        param7: KClass<T7>
    ): Flow<OnResult7<T1, T2, T3, T4, T5, T6, T7>> where T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any =
        on(target)
            .map {
                OnResult7(
                    it.arguments[0].fromJson(param1),
                    it.arguments[1].fromJson(param2),
                    it.arguments[2].fromJson(param3),
                    it.arguments[3].fromJson(param4),
                    it.arguments[4].fromJson(param5),
                    it.arguments[5].fromJson(param6),
                    it.arguments[6].fromJson(param7),
                )
            }

    fun <T1, T2, T3, T4, T5, T6, T7, T8> on(
        target: String,
        param1: KClass<T1>,
        param2: KClass<T2>,
        param3: KClass<T3>,
        param4: KClass<T4>,
        param5: KClass<T5>,
        param6: KClass<T6>,
        param7: KClass<T7>,
        param8: KClass<T8>
    ): Flow<OnResult8<T1, T2, T3, T4, T5, T6, T7, T8>> where T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 :
    Any =
        on(target)
            .map {
                OnResult8(
                    it.arguments[0].fromJson(param1),
                    it.arguments[1].fromJson(param2),
                    it.arguments[2].fromJson(param3),
                    it.arguments[3].fromJson(param4),
                    it.arguments[4].fromJson(param5),
                    it.arguments[5].fromJson(param6),
                    it.arguments[6].fromJson(param7),
                    it.arguments[7].fromJson(param8),
                )
            }

    suspend fun on(target: String, callback: () -> Unit) {
        on(target)
            .collect {
                handleInvocation(it, Unit::class) { callback() }
            }
    }

    suspend fun <T1> on(target: String, param1: KClass<T1>, callback: (T1) -> Unit) where T1 : Any {
        on(target)
            .collect {
                handleInvocation(it, Unit::class) {
                    callback(it.arguments[0].fromJson(param1))
                }
            }
    }

    suspend inline fun <reified T1> on(target: String, noinline callback: (T1) -> Unit) where T1: Any =
        on(target, T1::class, callback)

    suspend fun <T1, T2> on(target: String, param1: KClass<T1>, param2: KClass<T2>, callback: (T1, T2) -> Unit) where T1 : Any, T2 : Any {
        on(target)
            .collect {
                handleInvocation(it, Unit::class) {
                    callback(
                        it.arguments[0].fromJson(param1),
                        it.arguments[1].fromJson(param2),
                    )
                }
            }
    }

    suspend inline fun <reified T1, reified T2> on(target: String, noinline callback: (T1, T2) -> Unit) where T1: Any, T2: Any =
        on(target, T1::class, T2::class, callback)

    suspend fun <T1, T2, T3> on(
        target: String,
        param1: KClass<T1>,
        param2: KClass<T2>,
        param3: KClass<T3>,
        callback: (T1, T2, T3) -> Unit
    ) where T1 : Any, T2 : Any, T3 : Any {
        on(target)
            .collect {
                handleInvocation(it, Unit::class) {
                    callback(
                        it.arguments[0].fromJson(param1),
                        it.arguments[1].fromJson(param2),
                        it.arguments[2].fromJson(param3),
                    )
                }
            }
    }
    
    suspend inline fun <reified T1, reified T2, reified T3> on(
        target: String,
        noinline callback: (T1, T2, T3) -> Unit
    ) where T1: Any, T2: Any, T3: Any =
        on(target, T1::class, T2::class, T3::class, callback)

    suspend fun <T1, T2, T3, T4> on(
        target: String,
        param1: KClass<T1>,
        param2: KClass<T2>,
        param3: KClass<T3>,
        param4: KClass<T4>,
        callback: (T1, T2, T3, T4) -> Unit
    ) where T1 : Any, T2 : Any, T3 : Any, T4 : Any {
        on(target)
            .collect {
                handleInvocation(it, Unit::class) {
                    callback(
                        it.arguments[0].fromJson(param1),
                        it.arguments[1].fromJson(param2),
                        it.arguments[2].fromJson(param3),
                        it.arguments[3].fromJson(param4),
                    )
                }
            }
    }

    suspend inline fun <reified T1, reified T2, reified T3, reified T4> on(
        target: String,
        noinline callback: (T1, T2, T3, T4) -> Unit
    ) where T1: Any, T2: Any, T3: Any, T4: Any =
        on(target, T1::class, T2::class, T3::class, T4::class, callback)

    suspend fun <T1, T2, T3, T4, T5> on(
        target: String,
        param1: KClass<T1>,
        param2: KClass<T2>,
        param3: KClass<T3>,
        param4: KClass<T4>,
        param5: KClass<T5>,
        callback: (T1, T2, T3, T4, T5) -> Unit
    ) where T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any {
        on(target)
            .collect {
                handleInvocation(it, Unit::class) {
                    callback(
                        it.arguments[0].fromJson(param1),
                        it.arguments[1].fromJson(param2),
                        it.arguments[2].fromJson(param3),
                        it.arguments[3].fromJson(param4),
                        it.arguments[4].fromJson(param5),
                    )
                }
            }
    }

    suspend inline fun <reified T1, reified T2, reified T3, reified T4, reified T5> on(
        target: String,
        noinline callback: (T1, T2, T3, T4, T5) -> Unit
    ) where T1: Any, T2: Any, T3: Any, T4: Any, T5: Any =
        on(target, T1::class, T2::class, T3::class, T4::class, T5::class, callback)

    suspend fun <T1, T2, T3, T4, T5, T6> on(
        target: String,
        param1: KClass<T1>,
        param2: KClass<T2>,
        param3: KClass<T3>,
        param4: KClass<T4>,
        param5: KClass<T5>,
        param6: KClass<T6>,
        callback: (T1, T2, T3, T4, T5, T6) -> Unit
    ) where T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any {
        on(target)
            .collect {
                handleInvocation(it, Unit::class) {
                    callback(
                        it.arguments[0].fromJson(param1),
                        it.arguments[1].fromJson(param2),
                        it.arguments[2].fromJson(param3),
                        it.arguments[3].fromJson(param4),
                        it.arguments[4].fromJson(param5),
                        it.arguments[5].fromJson(param6),
                    )
                }
            }
    }

    suspend inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6> on(
        target: String,
        noinline callback: (T1, T2, T3, T4, T5, T6) -> Unit
    ) where T1: Any, T2: Any, T3: Any, T4: Any, T5: Any, T6: Any =
        on(target, T1::class, T2::class, T3::class, T4::class, T5::class, T6::class, callback)

    suspend fun <T1, T2, T3, T4, T5, T6, T7> on(
        target: String,
        param1: KClass<T1>,
        param2: KClass<T2>,
        param3: KClass<T3>,
        param4: KClass<T4>,
        param5: KClass<T5>,
        param6: KClass<T6>,
        param7: KClass<T7>,
        callback: (T1, T2, T3, T4, T5, T6, T7) -> Unit
    ) where T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any {
        on(target)
            .collect {
                handleInvocation(it, Unit::class) {
                    callback(
                        it.arguments[0].fromJson(param1),
                        it.arguments[1].fromJson(param2),
                        it.arguments[2].fromJson(param3),
                        it.arguments[3].fromJson(param4),
                        it.arguments[4].fromJson(param5),
                        it.arguments[5].fromJson(param6),
                        it.arguments[6].fromJson(param7),
                    )
                }
            }
    }

    suspend inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7> on(
        target: String,
        noinline callback: (T1, T2, T3, T4, T5, T6, T7) -> Unit
    ) where T1: Any, T2: Any, T3: Any, T4: Any, T5: Any, T6: Any, T7: Any =
        on(target, T1::class, T2::class, T3::class, T4::class, T5::class, T6::class, T7::class, callback)

    suspend fun <T1, T2, T3, T4, T5, T6, T7, T8> on(
        target: String,
        param1: KClass<T1>,
        param2: KClass<T2>,
        param3: KClass<T3>,
        param4: KClass<T4>,
        param5: KClass<T5>,
        param6: KClass<T6>,
        param7: KClass<T7>,
        param8: KClass<T8>,
        callback: (T1, T2, T3, T4, T5, T6, T7, T8) -> Unit
    ) where T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any {
        on(target)
            .collect {
                handleInvocation(it, Unit::class) {
                    callback(
                        it.arguments[0].fromJson(param1),
                        it.arguments[1].fromJson(param2),
                        it.arguments[2].fromJson(param3),
                        it.arguments[3].fromJson(param4),
                        it.arguments[4].fromJson(param5),
                        it.arguments[5].fromJson(param6),
                        it.arguments[6].fromJson(param7),
                        it.arguments[7].fromJson(param8),
                    )
                }
            }
    }

    suspend inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8> on(
        target: String,
        noinline callback: (T1, T2, T3, T4, T5, T6, T7, T8) -> Unit
    ) where T1: Any, T2: Any, T3: Any, T4: Any, T5: Any, T6: Any, T7: Any, T8: Any =
        on(target, T1::class, T2::class, T3::class, T4::class, T5::class, T6::class, T7::class, T8::class, callback)

    private fun onWithResult(target: String): Flow<HubMessage.Invocation> {
        if (subscribersWithResult[target] == true) {
            throw RuntimeException("'$target' already has a value returning handler. Multiple return values are not supported.")
        }

        return receivedInvocations
            .onSubscription {
                subscribersWithResult[target] = true
            }
            .onCompletion {
                subscribersWithResult[target] = false
            }
            .filter { it.target == target }
            .onEach { logger.log(Logger.Level.INFO, "Received invocation: $it") }
    }

    suspend fun <TResult> onWithResult(
        target: String,
        resultType: KClass<TResult>,
        callback: suspend () -> TResult
    ) where TResult: Any {
        onWithResult(target)
            .collect {
                handleInvocation(it, resultType) {
                    callback()
                }
            }
    }

    suspend inline fun <reified TResult> onWithResult(
        target: String,
        noinline callback: suspend () -> TResult
    ) where TResult: Any =
        onWithResult(target, TResult::class, callback)

    suspend fun <T1, TResult> onWithResult(
        target: String,
        param1: KClass<T1>,
        resultType: KClass<TResult>,
        callback: suspend (T1) -> TResult
    ) where T1 : Any, TResult: Any {
        onWithResult(target)
            .collect {
                handleInvocation(it, resultType) {
                    callback(it.arguments[0].fromJson(param1))
                }
            }
    }

    suspend inline fun <reified T1, reified TResult> onWithResult(
        target: String,
        noinline callback: suspend (T1) -> TResult
    ) where T1 : Any, TResult: Any =
        onWithResult(target, T1::class, TResult::class, callback)

    suspend fun <T1, T2, TResult> onWithResult(
        target: String,
        param1: KClass<T1>,
        param2: KClass<T2>,
        resultType: KClass<TResult>,
        callback: suspend (T1, T2) -> TResult
    ) where T1 : Any, T2 : Any, TResult: Any {
        onWithResult(target)
            .collect {
                handleInvocation(it, resultType) {
                    callback(
                        it.arguments[0].fromJson(param1),
                        it.arguments[1].fromJson(param2)
                    )
                }
            }
    }

    suspend inline fun <reified T1, reified T2, reified TResult> onWithResult(
        target: String,
        noinline callback: suspend (T1, T2) -> TResult
    ) where T1 : Any, T2 : Any, TResult: Any =
        onWithResult(target, T1::class, T2::class, TResult::class, callback)

    suspend fun <T1, T2, T3, TResult> onWithResult(
        target: String,
        param1: KClass<T1>,
        param2: KClass<T2>,
        param3: KClass<T3>,
        resultType: KClass<TResult>,
        callback: suspend (T1, T2, T3) -> TResult
    ) where T1 : Any, T2 : Any, T3 : Any, TResult: Any {
        onWithResult(target)
            .collect {
                handleInvocation(it, resultType) {
                    callback(
                        it.arguments[0].fromJson(param1),
                        it.arguments[1].fromJson(param2),
                        it.arguments[2].fromJson(param3)
                    )
                }
            }
    }

    suspend inline fun <reified T1, reified T2, reified T3, reified TResult> onWithResult(
        target: String,
        noinline callback: suspend (T1, T2, T3) -> TResult
    ) where T1 : Any, T2 : Any, T3 : Any, TResult: Any =
        onWithResult(target, T1::class, T2::class, T3::class, TResult::class, callback)

    suspend fun <T1, T2, T3, T4, TResult> onWithResult(
        target: String,
        param1: KClass<T1>,
        param2: KClass<T2>,
        param3: KClass<T3>,
        param4: KClass<T4>,
        resultType: KClass<TResult>,
        callback: suspend (T1, T2, T3, T4) -> TResult
    ) where T1 : Any, T2 : Any, T3 : Any, T4 : Any, TResult: Any {
        onWithResult(target)
            .collect {
                handleInvocation(it, resultType) {
                    callback(
                        it.arguments[0].fromJson(param1),
                        it.arguments[1].fromJson(param2),
                        it.arguments[2].fromJson(param3),
                        it.arguments[3].fromJson(param4)
                    )
                }
            }
    }

    suspend inline fun <reified T1, reified T2, reified T3, reified T4, reified TResult> onWithResult(
        target: String,
        noinline callback: suspend (T1, T2, T3, T4) -> TResult
    ) where T1 : Any, T2 : Any, T3 : Any, T4 : Any, TResult: Any =
        onWithResult(target, T1::class, T2::class, T3::class, T4::class, TResult::class, callback)

    suspend fun <T1, T2, T3, T4, T5, TResult> onWithResult(
        target: String,
        param1: KClass<T1>,
        param2: KClass<T2>,
        param3: KClass<T3>,
        param4: KClass<T4>,
        param5: KClass<T5>,
        resultType: KClass<TResult>,
        callback: suspend (T1, T2, T3, T4, T5) -> TResult
    ) where T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, TResult: Any {
        onWithResult(target)
            .collect {
                handleInvocation(it, resultType) {
                    callback(
                        it.arguments[0].fromJson(param1),
                        it.arguments[1].fromJson(param2),
                        it.arguments[2].fromJson(param3),
                        it.arguments[3].fromJson(param4),
                        it.arguments[4].fromJson(param5)
                    )
                }
            }
    }

    suspend inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified TResult> onWithResult(
        target: String,
        noinline callback: suspend (T1, T2, T3, T4, T5) -> TResult
    ) where T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, TResult: Any =
        onWithResult(target, T1::class, T2::class, T3::class, T4::class, T5::class, TResult::class, callback)

    suspend fun <T1, T2, T3, T4, T5, T6, TResult> onWithResult(
        target: String,
        param1: KClass<T1>,
        param2: KClass<T2>,
        param3: KClass<T3>,
        param4: KClass<T4>,
        param5: KClass<T5>,
        param6: KClass<T6>,
        resultType: KClass<TResult>,
        callback: suspend (T1, T2, T3, T4, T5, T6) -> TResult
    ) where T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, TResult: Any {
        onWithResult(target)
            .collect {
                handleInvocation(it, resultType) {
                    callback(
                        it.arguments[0].fromJson(param1),
                        it.arguments[1].fromJson(param2),
                        it.arguments[2].fromJson(param3),
                        it.arguments[3].fromJson(param4),
                        it.arguments[4].fromJson(param5),
                        it.arguments[5].fromJson(param6)
                    )
                }
            }
    }

    suspend inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified TResult> onWithResult(
        target: String,
        noinline callback: suspend (T1, T2, T3, T4, T5, T6) -> TResult
    ) where T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, TResult: Any =
        onWithResult(target, T1::class, T2::class, T3::class, T4::class, T5::class, T6::class, TResult::class, callback)

    suspend fun <T1, T2, T3, T4, T5, T6, T7, TResult> onWithResult(
        target: String,
        param1: KClass<T1>,
        param2: KClass<T2>,
        param3: KClass<T3>,
        param4: KClass<T4>,
        param5: KClass<T5>,
        param6: KClass<T6>,
        param7: KClass<T7>,
        resultType: KClass<TResult>,
        callback: suspend (T1, T2, T3, T4, T5, T6, T7) -> TResult
    ) where T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, TResult: Any {
        onWithResult(target)
            .collect {
                handleInvocation(it, resultType) {
                    callback(
                        it.arguments[0].fromJson(param1),
                        it.arguments[1].fromJson(param2),
                        it.arguments[2].fromJson(param3),
                        it.arguments[3].fromJson(param4),
                        it.arguments[4].fromJson(param5),
                        it.arguments[5].fromJson(param6),
                        it.arguments[6].fromJson(param7)
                    )
                }
            }
    }

    suspend inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified TResult> onWithResult(
        target: String,
        noinline callback: suspend (T1, T2, T3, T4, T5, T6, T7) -> TResult
    ) where T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, TResult: Any =
        onWithResult(target, T1::class, T2::class, T3::class, T4::class, T5::class, T6::class, T7::class, TResult::class, callback)

    suspend fun <T1, T2, T3, T4, T5, T6, T7, T8, TResult> onWithResult(
        target: String,
        param1: KClass<T1>,
        param2: KClass<T2>,
        param3: KClass<T3>,
        param4: KClass<T4>,
        param5: KClass<T5>,
        param6: KClass<T6>,
        param7: KClass<T7>,
        param8: KClass<T8>,
        resultType: KClass<TResult>,
        callback: suspend (T1, T2, T3, T4, T5, T6, T7, T8) -> TResult
    ) where T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, TResult: Any {
        onWithResult(target)
            .collect {
                handleInvocation(it, resultType) {
                    callback(
                        it.arguments[0].fromJson(param1),
                        it.arguments[1].fromJson(param2),
                        it.arguments[2].fromJson(param3),
                        it.arguments[3].fromJson(param4),
                        it.arguments[4].fromJson(param5),
                        it.arguments[5].fromJson(param6),
                        it.arguments[6].fromJson(param7),
                        it.arguments[7].fromJson(param8)
                    )
                }
            }
    }

    suspend inline fun <reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified TResult> onWithResult(
        target: String,
        noinline callback: suspend (T1, T2, T3, T4, T5, T6, T7, T8) -> TResult
    ) where T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, TResult: Any =
        onWithResult(target, T1::class, T2::class, T3::class, T4::class, T5::class, T6::class, T7::class, T8::class, TResult::class, callback)

    fun <A : Any> stream(itemType: KClass<A>, method: String): Flow<A> =
        stream(
            itemType = itemType,
            method = method,
            args = emptyList(),
            uploadStreams = emptyList(),
        )

    fun <A : Any, F1 : Any> stream(itemType: KClass<A>, method: String, uploadStream1: Flow<F1>, streamType1: KClass<F1>) =
        stream(
            itemType = itemType,
            method = method,
            args = emptyList(),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )

    fun <A : Any, F1 : Any, F2 : Any> stream(
        itemType: KClass<A>,
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = emptyList(),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )

    fun <A : Any, F1 : Any, F2 : Any, F3 : Any> stream(
        itemType: KClass<A>,
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = emptyList(),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )

    fun <A : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> stream(
        itemType: KClass<A>,
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = emptyList(),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )

    fun <A : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> stream(
        itemType: KClass<A>,
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = emptyList(),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )

    fun <A : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> stream(
        itemType: KClass<A>,
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = emptyList(),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )

    fun <A : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> stream(
        itemType: KClass<A>,
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = emptyList(),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )

    fun <A : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> stream(
        itemType: KClass<A>,
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = emptyList(),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )

    fun <A : Any, T1 : Any> stream(itemType: KClass<A>, method: String, arg1: T1, argType1: KClass<T1>) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = emptyList(),
        )

    fun <A : Any, T1 : Any, F1 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        argType1: KClass<T1>,
        uploadStream1: Flow<F1>,
        streamType1: KClass<F1>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )

    fun <A : Any, T1 : Any, F1 : Any, F2 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        argType1: KClass<T1>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )

    fun <A : Any, T1 : Any, F1 : Any, F2 : Any, F3 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        argType1: KClass<T1>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )

    fun <A : Any, T1 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        argType1: KClass<T1>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )

    fun <A : Any, T1 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        argType1: KClass<T1>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )

    fun <A : Any, T1 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        argType1: KClass<T1>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )

    fun <A : Any, T1 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        argType1: KClass<T1>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )

    fun <A : Any, T1 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        argType1: KClass<T1>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )

    fun <A : Any, T1 : Any, T2 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = emptyList(),
        )

    fun <A : Any, T1 : Any, T2 : Any, F1 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        uploadStream1: Flow<F1>,
        streamType1: KClass<F1>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, F1 : Any, F2 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, F1 : Any, F2 : Any, F3 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = emptyList(),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, F1 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        uploadStream1: Flow<F1>,
        streamType1: KClass<F1>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, F1 : Any, F2 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, F1 : Any, F2 : Any, F3 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = emptyList(),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, F1 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        uploadStream1: Flow<F1>,
        streamType1: KClass<F1>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, F1 : Any, F2 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, F1 : Any, F2 : Any, F3 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(arg1.toJson(argType1), arg2.toJson(argType2), arg3.toJson(argType3), arg4.toJson(argType4)),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = emptyList(),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, F1 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        uploadStream1: Flow<F1>,
        streamType1: KClass<F1>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, F1 : Any, F2 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, F1 : Any, F2 : Any, F3 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = emptyList(),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, F1 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        uploadStream1: Flow<F1>,
        streamType1: KClass<F1>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, F1 : Any, F2 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, F1 : Any, F2 : Any, F3 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = emptyList(),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, F1 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        uploadStream1: Flow<F1>,
        streamType1: KClass<F1>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, F1 : Any, F2 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, F1 : Any, F2 : Any, F3 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = emptyList(),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, F1 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>,
        uploadStream1: Flow<F1>,
        streamType1: KClass<F1>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, F1 : Any, F2 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = listOf(uploadStream1.map { it.toJson(streamType1) }, uploadStream2.map { it.toJson(streamType2) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, F1 : Any, F2 : Any, F3 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) }),
        )

    fun <A : Any, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, T7 : Any, T8 : Any, F1 : Any, F2 : Any, F3 : Any, F4 : Any, F5 : Any, F6 : Any, F7 : Any, F8 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        argType1: KClass<T1>,
        argType2: KClass<T2>,
        argType3: KClass<T3>,
        argType4: KClass<T4>,
        argType5: KClass<T5>,
        argType6: KClass<T6>,
        argType7: KClass<T7>,
        argType8: KClass<T8>,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>,
        streamType1: KClass<F1>,
        streamType2: KClass<F2>,
        streamType3: KClass<F3>,
        streamType4: KClass<F4>,
        streamType5: KClass<F5>,
        streamType6: KClass<F6>,
        streamType7: KClass<F7>,
        streamType8: KClass<F8>
    ) =
        stream(
            itemType = itemType,
            method = method,
            args = listOf(
                arg1.toJson(argType1),
                arg2.toJson(argType2),
                arg3.toJson(argType3),
                arg4.toJson(argType4),
                arg5.toJson(argType5),
                arg6.toJson(argType6),
                arg7.toJson(argType7),
                arg8.toJson(argType8)
            ),
            uploadStreams = listOf(
                uploadStream1.map { it.toJson(streamType1) },
                uploadStream2.map { it.toJson(streamType2) },
                uploadStream3.map { it.toJson(streamType3) },
                uploadStream4.map { it.toJson(streamType4) },
                uploadStream5.map { it.toJson(streamType5) },
                uploadStream6.map { it.toJson(streamType6) },
                uploadStream7.map { it.toJson(streamType7) },
                uploadStream8.map { it.toJson(streamType8) }),
        )

    inline fun <A : Any, reified F1 : Any> stream(itemType: KClass<A>, method: String, uploadStream1: Flow<F1>) =
        stream(
            itemType = itemType,
            method = method,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    inline fun <A : Any, reified F1 : Any, reified F2 : Any> stream(
        itemType: KClass<A>,
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        stream(
            itemType = itemType,
            method = method,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    inline fun <A : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> stream(
        itemType: KClass<A>,
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        stream(
            itemType = itemType,
            method = method,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    inline fun <A : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> stream(
        itemType: KClass<A>,
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        stream(
            itemType = itemType,
            method = method,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    inline fun <A : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> stream(
        itemType: KClass<A>,
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        stream(
            itemType = itemType,
            method = method,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    inline fun <A : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> stream(
        itemType: KClass<A>,
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        stream(
            itemType = itemType,
            method = method,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    inline fun <A : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> stream(
        itemType: KClass<A>,
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        stream(
            itemType = itemType,
            method = method,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    inline fun <A : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> stream(
        itemType: KClass<A>,
        method: String,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        stream(
            itemType = itemType,
            method = method,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    inline fun <A : Any, reified T1 : Any> stream(itemType: KClass<A>, method: String, arg1: T1) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
        )

    inline fun <A : Any, reified T1 : Any, reified F1 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        uploadStream1: Flow<F1>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    inline fun <A : Any, reified T1 : Any, reified F1 : Any, reified F2 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    inline fun <A : Any, reified T1 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    inline fun <A : Any, reified T1 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    inline fun <A : Any, reified T1 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    inline fun <A : Any, reified T1 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    inline fun <A : Any, reified T1 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    inline fun <A : Any, reified T1 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            argType1 = T1::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any> stream(itemType: KClass<A>, method: String, arg1: T1, arg2: T2) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified F1 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        uploadStream1: Flow<F1>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified F1 : Any, reified F2 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            argType1 = T1::class,
            argType2 = T2::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified F1 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        uploadStream1: Flow<F1>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified F1 : Any, reified F2 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified F1 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        uploadStream1: Flow<F1>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified F1 : Any, reified F2 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified F1 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        uploadStream1: Flow<F1>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified F1 : Any, reified F2 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified F1 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        uploadStream1: Flow<F1>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified F1 : Any, reified F2 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified F1 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        uploadStream1: Flow<F1>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified F1 : Any, reified F2 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified F1 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        uploadStream1: Flow<F1>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
            uploadStream1 = uploadStream1,
            streamType1 = F1::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified F1 : Any, reified F2 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            streamType1 = F1::class,
            streamType2 = F2::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
        )

    inline fun <A : Any, reified T1 : Any, reified T2 : Any, reified T3 : Any, reified T4 : Any, reified T5 : Any, reified T6 : Any, reified T7 : Any, reified T8 : Any, reified F1 : Any, reified F2 : Any, reified F3 : Any, reified F4 : Any, reified F5 : Any, reified F6 : Any, reified F7 : Any, reified F8 : Any> stream(
        itemType: KClass<A>,
        method: String,
        arg1: T1,
        arg2: T2,
        arg3: T3,
        arg4: T4,
        arg5: T5,
        arg6: T6,
        arg7: T7,
        arg8: T8,
        uploadStream1: Flow<F1>,
        uploadStream2: Flow<F2>,
        uploadStream3: Flow<F3>,
        uploadStream4: Flow<F4>,
        uploadStream5: Flow<F5>,
        uploadStream6: Flow<F6>,
        uploadStream7: Flow<F7>,
        uploadStream8: Flow<F8>
    ) =
        stream(
            itemType = itemType,
            method = method,
            arg1 = arg1,
            arg2 = arg2,
            arg3 = arg3,
            arg4 = arg4,
            arg5 = arg5,
            arg6 = arg6,
            arg7 = arg7,
            arg8 = arg8,
            argType1 = T1::class,
            argType2 = T2::class,
            argType3 = T3::class,
            argType4 = T4::class,
            argType5 = T5::class,
            argType6 = T6::class,
            argType7 = T7::class,
            argType8 = T8::class,
            uploadStream1 = uploadStream1,
            uploadStream2 = uploadStream2,
            uploadStream3 = uploadStream3,
            uploadStream4 = uploadStream4,
            uploadStream5 = uploadStream5,
            uploadStream6 = uploadStream6,
            uploadStream7 = uploadStream7,
            uploadStream8 = uploadStream8,
            streamType1 = F1::class,
            streamType2 = F2::class,
            streamType3 = F3::class,
            streamType4 = F4::class,
            streamType5 = F5::class,
            streamType6 = F6::class,
            streamType7 = F7::class,
            streamType8 = F8::class,
        )
}