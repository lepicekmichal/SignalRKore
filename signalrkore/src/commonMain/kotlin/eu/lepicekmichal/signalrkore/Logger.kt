package eu.lepicekmichal.signalrkore

fun interface Logger {
    /**
     * @param severity specifies message's severity, info/warning/error
     * @param message is text representation of the log
     * @param cause contains original throwable if there is any
     */
    fun log(severity: Severity, message: String, cause: Throwable?)

    enum class Severity {
        INFO,
        WARNING,
        ERROR,
    }
}