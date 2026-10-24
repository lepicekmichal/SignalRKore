package eu.lepicekmichal.signalrkore

/**
 * Thrown when an invocation or a stream could not complete because the connection was closed.
 *
 * @param cause The error that closed the connection, null if it was closed gracefully
 */
class ConnectionClosedException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
