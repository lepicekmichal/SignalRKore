package eu.lepicekmichal.signalrkore

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.withContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Completable {
    private val stateFlow = MutableStateFlow(false)

    @OptIn(FlowPreview::class)
    suspend fun waitForCompletion(timeout: Duration = 5.seconds) = withContext(Dispatchers.Default) {
        stateFlow.filter { it }.timeout(timeout).first()
    }

    fun reset() {
        stateFlow.value = false
    }

    fun complete() {
        stateFlow.value = true
    }
}