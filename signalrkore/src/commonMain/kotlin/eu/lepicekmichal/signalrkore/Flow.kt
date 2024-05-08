package eu.lepicekmichal.signalrkore

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration

internal fun tickerFlow(period: Duration, initialDelay: Duration = Duration.ZERO) = flow {
    delay(initialDelay)
    while (true) {
        emit(Unit)
        delay(period)
    }
}

internal fun <T> Flow<T>.collectInScope(scope: CoroutineScope, action: suspend (T) -> Unit): Job {
    var cancellationFlag = false
    return scope.launch {
        collect {
            if (isActive && !cancellationFlag) action(it)
        }
    }.also {
        it.invokeOnCompletion { cancellationFlag = true }
    }
}