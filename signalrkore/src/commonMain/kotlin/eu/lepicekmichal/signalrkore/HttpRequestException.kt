package eu.lepicekmichal.signalrkore

/**
 * Thrown when an HTTP request to the server fails with an unexpected status code.
 *
 * @property statusCode The HTTP status code returned by the server
 */
class HttpRequestException(message: String, val statusCode: Int) : RuntimeException(message)
