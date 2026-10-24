package eu.lepicekmichal.signalrkore

/**
 * Thrown when the server fails to invoke a hub method and returns an error.
 */
class HubException(message: String) : RuntimeException(message)
