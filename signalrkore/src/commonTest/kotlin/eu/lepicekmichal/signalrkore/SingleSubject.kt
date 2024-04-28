package eu.lepicekmichal.signalrkore

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.withContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class SingleSubject<T> {
    private val stateFlow = MutableStateFlow<T?>(null)

    val result get() = stateFlow.value

    suspend fun waitForResult(timeout: Duration = 30.seconds) : T =
        withContext(Dispatchers.Default) {
            stateFlow.filter { it != null }.timeout(timeout).first() as T
        }

    fun reset() {
        stateFlow.value = null
    }

    fun setResult(value: T) {
        stateFlow.value = value
    }
}