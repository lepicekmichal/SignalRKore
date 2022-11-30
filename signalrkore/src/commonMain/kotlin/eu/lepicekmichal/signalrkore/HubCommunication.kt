package eu.lepicekmichal.signalrkore

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.JsonElement
import kotlin.reflect.KClass

abstract class HubCommunication {

    protected abstract val receivedInvocations: SharedFlow<HubMessage.Invocation>

    protected abstract fun <T : Any> T.toJson(kClass: KClass<T>): JsonElement

    protected abstract fun <T : Any> JsonElement.fromJson(kClass: KClass<T>): T

    abstract fun send(method: String, args: List<JsonElement>)

    abstract suspend fun invoke(method: String, args: List<JsonElement>)

    abstract suspend fun <T : Any> invoke(result: KClass<T>, method: String, args: List<JsonElement>): T

    fun <S : Any> send(method: String, arg: S, argType: KClass<S>) =
        send(method, listOf(arg.toJson(argType)))

    fun <S : Any, T : Any> send(method: String, arg1: S, arg2: T, arg1Type: KClass<S>, arg2Type: KClass<T>) =
        send(method, listOf(arg1.toJson(arg1Type), arg2.toJson(arg2Type)))

    fun <S : Any, T : Any, U : Any> send(
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg1Type: KClass<S>,
        arg2Type: KClass<T>,
        arg3Type: KClass<U>
    ) = send(method, listOf(arg1.toJson(arg1Type), arg2.toJson(arg2Type), arg3.toJson(arg3Type)))

    fun <S : Any, T : Any, U : Any, V : Any> send(
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg4: V,
        arg1Type: KClass<S>,
        arg2Type: KClass<T>,
        arg3Type: KClass<U>,
        arg4Type: KClass<V>
    ) = send(method, listOf(arg1.toJson(arg1Type), arg2.toJson(arg2Type), arg3.toJson(arg3Type), arg4.toJson(arg4Type)))

    fun <S : Any, T : Any, U : Any, V : Any, W : Any> send(
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg4: V,
        arg5: W,
        arg1Type: KClass<S>,
        arg2Type: KClass<T>,
        arg3Type: KClass<U>,
        arg4Type: KClass<V>,
        arg5Type: KClass<W>,
    ) = send(
        method,
        listOf(arg1.toJson(arg1Type), arg2.toJson(arg2Type), arg3.toJson(arg3Type), arg4.toJson(arg4Type), arg5.toJson(arg5Type))
    )

    fun <S : Any, T : Any, U : Any, V : Any, W : Any, X : Any> send(
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg4: V,
        arg5: W,
        arg6: X,
        arg1Type: KClass<S>,
        arg2Type: KClass<T>,
        arg3Type: KClass<U>,
        arg4Type: KClass<V>,
        arg5Type: KClass<W>,
        arg6Type: KClass<X>,
    ) = send(
        method,
        listOf(
            arg1.toJson(arg1Type),
            arg2.toJson(arg2Type),
            arg3.toJson(arg3Type),
            arg4.toJson(arg4Type),
            arg5.toJson(arg5Type),
            arg6.toJson(arg6Type)
        )
    )

    fun <S : Any, T : Any, U : Any, V : Any, W : Any, X : Any, Y : Any> send(
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg4: V,
        arg5: W,
        arg6: X,
        arg7: Y,
        arg1Type: KClass<S>,
        arg2Type: KClass<T>,
        arg3Type: KClass<U>,
        arg4Type: KClass<V>,
        arg5Type: KClass<W>,
        arg6Type: KClass<X>,
        arg7Type: KClass<Y>,
    ) = send(
        method,
        listOf(
            arg1.toJson(arg1Type),
            arg2.toJson(arg2Type),
            arg3.toJson(arg3Type),
            arg4.toJson(arg4Type),
            arg5.toJson(arg5Type),
            arg6.toJson(arg6Type),
            arg7.toJson(arg7Type)
        )
    )

    fun <S : Any, T : Any, U : Any, V : Any, W : Any, X : Any, Y : Any, Z : Any> send(
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg4: V,
        arg5: W,
        arg6: X,
        arg7: Y,
        arg8: Z,
        arg1Type: KClass<S>,
        arg2Type: KClass<T>,
        arg3Type: KClass<U>,
        arg4Type: KClass<V>,
        arg5Type: KClass<W>,
        arg6Type: KClass<X>,
        arg7Type: KClass<Y>,
        arg8Type: KClass<Z>,
    ) = send(
        method,
        listOf(
            arg1.toJson(arg1Type),
            arg2.toJson(arg2Type),
            arg3.toJson(arg3Type),
            arg4.toJson(arg4Type),
            arg5.toJson(arg5Type),
            arg6.toJson(arg6Type),
            arg7.toJson(arg7Type),
            arg8.toJson(arg8Type)
        ),
    )

    inline fun <reified S : Any> send(method: String, arg: S) =
        send(method, arg, S::class)

    inline fun <reified S : Any, reified T : Any> send(method: String, arg1: S, arg2: T) =
        send(method, arg1, arg2, S::class, T::class)

    inline fun <reified S : Any, reified T : Any, reified U : Any> send(method: String, arg1: S, arg2: T, arg3: U) =
        send(method, arg1, arg2, arg3, S::class, T::class, U::class)

    inline fun <reified S : Any, reified T : Any, reified U : Any, reified V : Any> send(
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg4: V
    ) = send(method, arg1, arg2, arg3, arg4, S::class, T::class, U::class, V::class)

    inline fun <reified S : Any, reified T : Any, reified U : Any, reified V : Any, reified W : Any> send(
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg4: V,
        arg5: W
    ) = send(method, arg1, arg2, arg3, arg4, arg5, S::class, T::class, U::class, V::class, W::class)

    inline fun <reified S : Any, reified T : Any, reified U : Any, reified V : Any, reified W : Any, reified X : Any> send(
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg4: V,
        arg5: W,
        arg6: X,
    ) = send(method, arg1, arg2, arg3, arg4, arg5, arg6, S::class, T::class, U::class, V::class, W::class, X::class)

    inline fun <reified S : Any, reified T : Any, reified U : Any, reified V : Any, reified W : Any, reified X : Any, reified Y : Any> send(
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg4: V,
        arg5: W,
        arg6: X,
        arg7: Y,
    ) = send(method, arg1, arg2, arg3, arg4, arg5, arg6, arg7, S::class, T::class, U::class, V::class, W::class, X::class, Y::class)

    inline fun <reified S : Any, reified T : Any, reified U : Any, reified V : Any, reified W : Any, reified X : Any, reified Y : Any, reified Z : Any> send(
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg4: V,
        arg5: W,
        arg6: X,
        arg7: Y,
        arg8: Z,
    ) = send(
        method,
        arg1,
        arg2,
        arg3,
        arg4,
        arg5,
        arg6,
        arg7,
        arg8,
        S::class,
        T::class,
        U::class,
        V::class,
        W::class,
        X::class,
        Y::class,
        Z::class,
    )

    suspend fun <S : Any> invoke(method: String, arg: S, argType: KClass<S>) {
        invoke(method, listOf(arg.toJson(argType)))
    }

    suspend fun <S : Any, T : Any> invoke(method: String, arg1: S, arg2: T, arg1Type: KClass<S>, arg2Type: KClass<T>) =
        invoke(method, listOf(arg1.toJson(arg1Type), arg2.toJson(arg2Type)))

    suspend fun <S : Any, T : Any, U : Any> invoke(
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg1Type: KClass<S>,
        arg2Type: KClass<T>,
        arg3Type: KClass<U>
    ) = invoke(method, listOf(arg1.toJson(arg1Type), arg2.toJson(arg2Type), arg3.toJson(arg3Type)))

    suspend fun <S : Any, T : Any, U : Any, V : Any> invoke(
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg4: V,
        arg1Type: KClass<S>,
        arg2Type: KClass<T>,
        arg3Type: KClass<U>,
        arg4Type: KClass<V>
    ) = invoke(method, listOf(arg1.toJson(arg1Type), arg2.toJson(arg2Type), arg3.toJson(arg3Type), arg4.toJson(arg4Type)))

    suspend fun <S : Any, T : Any, U : Any, V : Any, W : Any> invoke(
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg4: V,
        arg5: W,
        arg1Type: KClass<S>,
        arg2Type: KClass<T>,
        arg3Type: KClass<U>,
        arg4Type: KClass<V>,
        arg5Type: KClass<W>,
    ) = invoke(
        method,
        listOf(arg1.toJson(arg1Type), arg2.toJson(arg2Type), arg3.toJson(arg3Type), arg4.toJson(arg4Type), arg5.toJson(arg5Type))
    )

    suspend fun <S : Any, T : Any, U : Any, V : Any, W : Any, X : Any> invoke(
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg4: V,
        arg5: W,
        arg6: X,
        arg1Type: KClass<S>,
        arg2Type: KClass<T>,
        arg3Type: KClass<U>,
        arg4Type: KClass<V>,
        arg5Type: KClass<W>,
        arg6Type: KClass<X>,
    ) = invoke(
        method,
        listOf(
            arg1.toJson(arg1Type),
            arg2.toJson(arg2Type),
            arg3.toJson(arg3Type),
            arg4.toJson(arg4Type),
            arg5.toJson(arg5Type),
            arg6.toJson(arg6Type)
        )
    )

    suspend fun <S : Any, T : Any, U : Any, V : Any, W : Any, X : Any, Y : Any> invoke(
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg4: V,
        arg5: W,
        arg6: X,
        arg7: Y,
        arg1Type: KClass<S>,
        arg2Type: KClass<T>,
        arg3Type: KClass<U>,
        arg4Type: KClass<V>,
        arg5Type: KClass<W>,
        arg6Type: KClass<X>,
        arg7Type: KClass<Y>,
    ) = invoke(
        method,
        listOf(
            arg1.toJson(arg1Type),
            arg2.toJson(arg2Type),
            arg3.toJson(arg3Type),
            arg4.toJson(arg4Type),
            arg5.toJson(arg5Type),
            arg6.toJson(arg6Type),
            arg7.toJson(arg7Type)
        )
    )

    suspend fun <S : Any, T : Any, U : Any, V : Any, W : Any, X : Any, Y : Any, Z : Any> invoke(
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg4: V,
        arg5: W,
        arg6: X,
        arg7: Y,
        arg8: Z,
        arg1Type: KClass<S>,
        arg2Type: KClass<T>,
        arg3Type: KClass<U>,
        arg4Type: KClass<V>,
        arg5Type: KClass<W>,
        arg6Type: KClass<X>,
        arg7Type: KClass<Y>,
        arg8Type: KClass<Z>,
    ) = invoke(
        method,
        listOf(
            arg1.toJson(arg1Type),
            arg2.toJson(arg2Type),
            arg3.toJson(arg3Type),
            arg4.toJson(arg4Type),
            arg5.toJson(arg5Type),
            arg6.toJson(arg6Type),
            arg7.toJson(arg7Type),
            arg8.toJson(arg8Type)
        ),
    )

    suspend inline fun <reified S : Any> invoke(method: String, arg: S) =
        invoke(method, arg, S::class)

    suspend inline fun <reified S : Any, reified T : Any> invoke(method: String, arg1: S, arg2: T) =
        invoke(method, arg1, arg2, S::class, T::class)

    suspend inline fun <reified S : Any, reified T : Any, reified U : Any> invoke(method: String, arg1: S, arg2: T, arg3: U) =
        invoke(method, arg1, arg2, arg3, S::class, T::class, U::class)

    suspend inline fun <reified S : Any, reified T : Any, reified U : Any, reified V : Any> invoke(
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg4: V
    ) = invoke(method, arg1, arg2, arg3, arg4, S::class, T::class, U::class, V::class)

    suspend inline fun <reified S : Any, reified T : Any, reified U : Any, reified V : Any, reified W : Any> invoke(
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg4: V,
        arg5: W
    ) = invoke(method, arg1, arg2, arg3, arg4, arg5, S::class, T::class, U::class, V::class, W::class)

    suspend inline fun <reified S : Any, reified T : Any, reified U : Any, reified V : Any, reified W : Any, reified X : Any> invoke(
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg4: V,
        arg5: W,
        arg6: X,
    ) = invoke(method, arg1, arg2, arg3, arg4, arg5, arg6, S::class, T::class, U::class, V::class, W::class, X::class)

    suspend inline fun <reified S : Any, reified T : Any, reified U : Any, reified V : Any, reified W : Any, reified X : Any, reified Y : Any> invoke(
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg4: V,
        arg5: W,
        arg6: X,
        arg7: Y,
    ) = invoke(method, arg1, arg2, arg3, arg4, arg5, arg6, arg7, S::class, T::class, U::class, V::class, W::class, X::class, Y::class)

    suspend inline fun <reified S : Any, reified T : Any, reified U : Any, reified V : Any, reified W : Any, reified X : Any, reified Y : Any, reified Z : Any> invoke(
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg4: V,
        arg5: W,
        arg6: X,
        arg7: Y,
        arg8: Z,
    ) = invoke(
        method,
        arg1,
        arg2,
        arg3,
        arg4,
        arg5,
        arg6,
        arg7,
        arg8,
        S::class,
        T::class,
        U::class,
        V::class,
        W::class,
        X::class,
        Y::class,
        Z::class,
    )

    suspend inline fun <A : Any, reified S : Any> invoke(result: KClass<A>, method: String, arg: S): A =
        invoke(result, method, arg, S::class)

    suspend inline fun <A : Any, reified S : Any, reified T : Any> invoke(result: KClass<A>, method: String, arg1: S, arg2: T): A =
        invoke(result, method, arg1, arg2, S::class, T::class)

    suspend inline fun <A : Any, reified S : Any, reified T : Any, reified U : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: S,
        arg2: T,
        arg3: U
    ): A = invoke(result, method, arg1, arg2, arg3, S::class, T::class, U::class)

    suspend inline fun <A : Any, reified S : Any, reified T : Any, reified U : Any, reified V : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg4: V,
    ): A = invoke(result, method, arg1, arg2, arg3, arg4, S::class, T::class, U::class, V::class)

    suspend inline fun <A : Any, reified S : Any, reified T : Any, reified U : Any, reified V : Any, reified W : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg4: V,
        arg5: W,
    ): A = invoke(result, method, arg1, arg2, arg3, arg4, arg5, S::class, T::class, U::class, V::class, W::class)

    suspend inline fun <A : Any, reified S : Any, reified T : Any, reified U : Any, reified V : Any, reified W : Any, reified X : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg4: V,
        arg5: W,
        arg6: X,
    ): A = invoke(result, method, arg1, arg2, arg3, arg4, arg5, arg6, S::class, T::class, U::class, V::class, W::class, X::class)

    suspend inline fun <A : Any, reified S : Any, reified T : Any, reified U : Any, reified V : Any, reified W : Any, reified X : Any, reified Y : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg4: V,
        arg5: W,
        arg6: X,
        arg7: Y,
    ): A = invoke(
        result,
        method,
        arg1,
        arg2,
        arg3,
        arg4,
        arg5,
        arg6,
        arg7,
        S::class,
        T::class,
        U::class,
        V::class,
        W::class,
        X::class,
        Y::class,
    )

    suspend inline fun <A : Any, reified S : Any, reified T : Any, reified U : Any, reified V : Any, reified W : Any, reified X : Any, reified Y : Any, reified Z : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg4: V,
        arg5: W,
        arg6: X,
        arg7: Y,
        arg8: Z,
    ) = invoke(
        result,
        method,
        arg1,
        arg2,
        arg3,
        arg4,
        arg5,
        arg6,
        arg7,
        arg8,
        S::class,
        T::class,
        U::class,
        V::class,
        W::class,
        X::class,
        Y::class,
        Z::class,
    )

    suspend fun <A : Any, S : Any> invoke(result: KClass<A>, method: String, arg: S, argType: KClass<S>): A =
        invoke(result, method, listOf(arg.toJson(argType)))

    suspend fun <A : Any, S : Any, T : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: S,
        arg2: T,
        arg1Type: KClass<S>,
        arg2Type: KClass<T>,
    ): A = invoke(result, method, listOf(arg1.toJson(arg1Type), arg2.toJson(arg2Type)))

    suspend fun <A : Any, S : Any, T : Any, U : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg1Type: KClass<S>,
        arg2Type: KClass<T>,
        arg3Type: KClass<U>,
    ): A = invoke(result, method, listOf(arg1.toJson(arg1Type), arg2.toJson(arg2Type), arg3.toJson(arg3Type)))

    suspend fun <A : Any, S : Any, T : Any, U : Any, V : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg4: V,
        arg1Type: KClass<S>,
        arg2Type: KClass<T>,
        arg3Type: KClass<U>,
        arg4Type: KClass<V>,
    ): A = invoke(result, method, listOf(arg1.toJson(arg1Type), arg2.toJson(arg2Type), arg3.toJson(arg3Type), arg4.toJson(arg4Type)))

    suspend fun <A : Any, S : Any, T : Any, U : Any, V : Any, W : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg4: V,
        arg5: W,
        arg1Type: KClass<S>,
        arg2Type: KClass<T>,
        arg3Type: KClass<U>,
        arg4Type: KClass<V>,
        arg5Type: KClass<W>,
    ): A = invoke(
        result,
        method,
        listOf(arg1.toJson(arg1Type), arg2.toJson(arg2Type), arg3.toJson(arg3Type), arg4.toJson(arg4Type), arg5.toJson(arg5Type)),
    )

    suspend fun <A : Any, S : Any, T : Any, U : Any, V : Any, W : Any, X : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg4: V,
        arg5: W,
        arg6: X,
        arg1Type: KClass<S>,
        arg2Type: KClass<T>,
        arg3Type: KClass<U>,
        arg4Type: KClass<V>,
        arg5Type: KClass<W>,
        arg6Type: KClass<X>,
    ): A = invoke(
        result,
        method,
        listOf(
            arg1.toJson(arg1Type),
            arg2.toJson(arg2Type),
            arg3.toJson(arg3Type),
            arg4.toJson(arg4Type),
            arg5.toJson(arg5Type),
            arg6.toJson(arg6Type),
        ),
    )

    suspend fun <A : Any, S : Any, T : Any, U : Any, V : Any, W : Any, X : Any, Y : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg4: V,
        arg5: W,
        arg6: X,
        arg7: Y,
        arg1Type: KClass<S>,
        arg2Type: KClass<T>,
        arg3Type: KClass<U>,
        arg4Type: KClass<V>,
        arg5Type: KClass<W>,
        arg6Type: KClass<X>,
        arg7Type: KClass<Y>,
    ): A = invoke(
        result,
        method,
        listOf(
            arg1.toJson(arg1Type),
            arg2.toJson(arg2Type),
            arg3.toJson(arg3Type),
            arg4.toJson(arg4Type),
            arg5.toJson(arg5Type),
            arg6.toJson(arg6Type),
            arg7.toJson(arg7Type),
        ),
    )

    suspend fun <A : Any, S : Any, T : Any, U : Any, V : Any, W : Any, X : Any, Y : Any, Z : Any> invoke(
        result: KClass<A>,
        method: String,
        arg1: S,
        arg2: T,
        arg3: U,
        arg4: V,
        arg5: W,
        arg6: X,
        arg7: Y,
        arg8: Z,
        arg1Type: KClass<S>,
        arg2Type: KClass<T>,
        arg3Type: KClass<U>,
        arg4Type: KClass<V>,
        arg5Type: KClass<W>,
        arg6Type: KClass<X>,
        arg7Type: KClass<Y>,
        arg8Type: KClass<Z>,
    ): A = invoke(
        result,
        method,
        listOf(
            arg1.toJson(arg1Type),
            arg2.toJson(arg2Type),
            arg3.toJson(arg3Type),
            arg4.toJson(arg4Type),
            arg5.toJson(arg5Type),
            arg6.toJson(arg6Type),
            arg7.toJson(arg7Type),
            arg8.toJson(arg8Type),
        ),
    )

    fun <T1> on(target: String, param1: KClass<T1>): Flow<T1> where T1 : Any =
        receivedInvocations
            .filter { it.target == target }
            .map { it.arguments[0].fromJson(param1) }

    fun <T1, T2> on(target: String, param1: KClass<T1>, param2: KClass<T2>): Flow<OnResult2<T1, T2>> where T1 : Any, T2 : Any =
        receivedInvocations
            .filter { it.target == target }
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
        receivedInvocations
            .filter { it.target == target }
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
        receivedInvocations
            .filter { it.target == target }
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
        receivedInvocations
            .filter { it.target == target }
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
        receivedInvocations
            .filter { it.target == target }
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
        receivedInvocations
            .filter { it.target == target }
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
        receivedInvocations
            .filter { it.target == target }
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

    suspend fun <T1> on(target: String, param1: KClass<T1>, callback: (T1) -> Unit) where T1 : Any {
        receivedInvocations
            .filter { it.target == target }
            .collect { callback(it.arguments[0].fromJson(param1)) }
    }

    suspend fun <T1, T2> on(target: String, param1: KClass<T1>, param2: KClass<T2>, callback: (T1, T2) -> Unit) where T1 : Any, T2 : Any {
        receivedInvocations
            .filter { it.target == target }
            .collect {
                callback(
                    it.arguments[0].fromJson(param1),
                    it.arguments[1].fromJson(param2),
                )
            }
    }

    suspend fun <T1, T2, T3> on(
        target: String,
        param1: KClass<T1>,
        param2: KClass<T2>,
        param3: KClass<T3>,
        callback: (T1, T2, T3) -> Unit
    ) where T1 : Any, T2 : Any, T3 : Any {
        receivedInvocations
            .filter { it.target == target }
            .collect {
                callback(
                    it.arguments[0].fromJson(param1),
                    it.arguments[1].fromJson(param2),
                    it.arguments[2].fromJson(param3),
                )
            }
    }

    suspend fun <T1, T2, T3, T4> on(
        target: String,
        param1: KClass<T1>,
        param2: KClass<T2>,
        param3: KClass<T3>,
        param4: KClass<T4>,
        callback: (T1, T2, T3, T4) -> Unit
    ) where T1 : Any, T2 : Any, T3 : Any, T4 : Any {
        receivedInvocations
            .filter { it.target == target }
            .collect {
                callback(
                    it.arguments[0].fromJson(param1),
                    it.arguments[1].fromJson(param2),
                    it.arguments[2].fromJson(param3),
                    it.arguments[3].fromJson(param4),
                )
            }
    }

    suspend fun <T1, T2, T3, T4, T5> on(
        target: String,
        param1: KClass<T1>,
        param2: KClass<T2>,
        param3: KClass<T3>,
        param4: KClass<T4>,
        param5: KClass<T5>,
        callback: (T1, T2, T3, T4, T5) -> Unit
    ) where T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any {
        receivedInvocations
            .filter { it.target == target }
            .collect {
                callback(
                    it.arguments[0].fromJson(param1),
                    it.arguments[1].fromJson(param2),
                    it.arguments[2].fromJson(param3),
                    it.arguments[3].fromJson(param4),
                    it.arguments[4].fromJson(param5),
                )
            }
    }

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
        receivedInvocations
            .filter { it.target == target }
            .collect {
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
        receivedInvocations
            .filter { it.target == target }
            .collect {
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
        receivedInvocations
            .filter { it.target == target }
            .collect {
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