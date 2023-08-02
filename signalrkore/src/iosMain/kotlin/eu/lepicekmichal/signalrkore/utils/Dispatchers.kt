package eu.lepicekmichal.signalrkore.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

actual val dispatchers: CoroutineDispatchers = object : CoroutineDispatchers {
    override val main: CoroutineDispatcher = try {
        Dispatchers.Main
    } catch (exception: IllegalStateException) {
        Dispatchers.Default
    }
    override val default: CoroutineDispatcher = Dispatchers.Default
    override val io: CoroutineDispatcher = Dispatchers.Default
    override val unconfined: CoroutineDispatcher = Dispatchers.Unconfined
}