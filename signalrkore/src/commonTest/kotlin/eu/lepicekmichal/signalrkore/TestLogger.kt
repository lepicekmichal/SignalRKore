package eu.lepicekmichal.signalrkore

import kotlin.test.assertNotNull

data class LogEvent(val level: Logger.Level, val message: String)

class TestLogger : Logger {

    private val logs: MutableList<LogEvent> = mutableListOf()

    override fun log(level: Logger.Level, message: String) {
        logs.add(LogEvent(level, message))
    }

    fun assertLogEquals(message: String): LogEvent {
        val log = logs.firstOrNull { it.message == message }

        assertNotNull(log, "Log message '$message' not found")

        return log
    }

    fun assertLogContains(message: String): LogEvent {
        val log = logs.firstOrNull { it.message.contains(message) }

        assertNotNull(log, "Log message containing '$message' not found")

        return log
    }
}