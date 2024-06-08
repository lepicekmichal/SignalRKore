package eu.lepicekmichal.signalrkore

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.test.assertNotNull
import kotlin.time.Duration.Companion.seconds

data class LogEvent(val severity: Logger.Severity, val message: String, val cause: Throwable?)

class TestLogger : Logger {

    private val scope = CoroutineScope(Dispatchers.Unconfined)

    private val logs: MutableSharedFlow<LogEvent> = MutableSharedFlow(replay = 10)

    override fun log(severity: Logger.Severity, message: String, cause: Throwable?) {
        scope.launch {
            logs.emit(LogEvent(severity, message, cause))
        }
    }

    @OptIn(FlowPreview::class)
    suspend fun assertLogEquals(message: String): LogEvent = withContext(Dispatchers.Default) {
        val log = logs.filter { it.message == message }.timeout(5.seconds).catch { }.firstOrNull()

        assertNotNull(log, "Log message '$message' not found")

        log
    }
}