package eu.lepicekmichal.signalrkore

fun interface Logger {
    /**
     * @param severity specifies message's severity, info/warning/error
     * @param message lazily produces the text representation of the log; only invoked if the logger decides to use it,
     * so callers don't pay the cost of building the message when it will be discarded
     * @param cause contains original throwable if there is any
     */
    fun log(severity: Severity, message: () -> String, cause: Throwable?)

    enum class Severity {
        INFO,
        WARNING,
        ERROR,
    }
}