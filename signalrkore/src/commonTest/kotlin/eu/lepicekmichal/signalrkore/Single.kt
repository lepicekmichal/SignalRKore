package eu.lepicekmichal.signalrkore

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Single<T> {
    private val stateFlow = MutableStateFlow<T?>(null)
    private val mutex = Mutex()

    val result get() = stateFlow.value

    @Suppress("UNCHECKED_CAST")
    @OptIn(FlowPreview::class)
    suspend fun waitForResult(timeout: Duration = 5.seconds) : T =
        withContext(Dispatchers.Default) {
            stateFlow.filter { it != null }.timeout(timeout).first() as T
        }

    fun reset() {
        stateFlow.value = null
    }

    fun setResult(value: T) {
        stateFlow.value = value
    }

    suspend fun updateResult(updater: (T?) -> T?): T? = mutex.withLock {
        stateFlow.value = updater(stateFlow.value)
        stateFlow.value
    }
}