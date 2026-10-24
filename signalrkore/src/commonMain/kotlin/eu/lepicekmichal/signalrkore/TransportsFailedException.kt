package eu.lepicekmichal.signalrkore

/**
 * Thrown when none of the transports offered by the server and allowed by the client could be started.
 *
 * @property failures The error of each transport that was tried, in the order they were tried
 */
class TransportsFailedException(val failures: Map<TransportEnum, Throwable>) : RuntimeException(
    "Unable to connect to the server with any of the available transports. " +
        failures.entries.joinToString(" ") { (transport, ex) -> "$transport failed: ${ex.message}" },
    failures.values.lastOrNull(),
)
