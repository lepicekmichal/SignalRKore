# Configuration

This guide explains the various configuration options available in SignalRKore. These options allow you to customize the behavior of the SignalR client to suit your needs.

## HubConnectionBuilder

All configuration options are set through the `HubConnectionBuilder` when creating a connection:

```kotlin
val connection = HubConnectionBuilder.create("https://example.com/chathub") {
    // Configuration options go here
}
```

## Transport Options

### Transport Type

You can specify which transport to use for the connection:

```kotlin
transportEnum = TransportEnum.WebSockets
```

Available transport options:

- `TransportEnum.All` (default): Automatically selects the best available transport
- `TransportEnum.WebSockets`: Uses WebSockets transport
- `TransportEnum.ServerSentEvents`: Uses Server-Sent Events transport
- `TransportEnum.LongPolling`: Uses Long Polling transport

See the [Transport](../api/transport.md) API reference for more details.

### Skip Negotiate

You can skip the negotiate step when using WebSockets:

```kotlin
skipNegotiate = true
```

Note: This option can only be used with WebSockets transport.

## Authentication and Headers

### HTTP Headers

You can add custom HTTP headers to the connection:

```kotlin
headers = mapOf(
    "Authorization" to "Bearer token",
    "Custom-Header" to "Value"
)
```

### Access Token

You can provide an access token for authentication:

```kotlin
accessToken = "your-access-token"
```

This is a convenience property that sets the "Authorization" header with a "Bearer" prefix. It's equivalent to:

```kotlin
headers["Authorization"] = "Bearer your-access-token"
```

This is useful for JWT authentication.

## Timeout and Reconnection

### Handshake Response Timeout

You can configure the timeout for the handshake response:

```kotlin
handshakeResponseTimeout = 30.seconds
```

### Automatic Reconnect

You can configure automatic reconnection when the connection is lost:

```kotlin
automaticReconnect = AutomaticReconnect.Active
```

See the [Reconnection](reconnection.md) guide for more details on reconnection options.

## HTTP Client

### Custom HTTP Client

You can provide a custom Ktor HTTP client:

```kotlin
httpClient = HttpClient {
    install(WebSockets)
    install(SSE)
    install(HttpTimeout)
    install(ContentNegotiation) { json() }
}
```

This is useful if you need to configure the HTTP client with custom settings or plugins.

> **Warning:** If you provide a custom HTTP client, you must install all the necessary plugins. At a minimum, you need to install:
> - `WebSockets` - Required for WebSockets transport
> - `SSE` - Required for Server-Sent Events transport
> - `HttpTimeout` - Required for timeout handling
> - `ContentNegotiation` with JSON - Required for serialization
>
> Failure to install these plugins may result in runtime errors.

### OkHttp Engine

If you're using the JVM or Android platform, you can configure the OkHttp engine:

```kotlin
val okHttpClient = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()

httpClient = HttpClient(OkHttp) {
    engine {
        preconfigured = okHttpClient
    }
    install(WebSockets)
    install(SSE)
    install(HttpTimeout)
    install(ContentNegotiation) { json() }
}
```

## Protocol and Serialization

### Protocol

You can specify a custom hub protocol:

```kotlin
protocol = JsonHubProtocol()
```

Currently, only the `JsonHubProtocol` is supported.

See the [Protocol](../api/protocol.md) API reference for more details.

### JSON Serialization

You can provide a custom JSON serializer:

```kotlin
json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    // Other configuration options
}
```

This is useful if you need to configure the JSON serializer with custom settings or modules.

For example, you can register custom serializers for specific types:

```kotlin
json = Json {
    ignoreUnknownKeys = true
    serializersModule = SerializersModule {
        contextual(Date::class) { DateSerializer }
        contextual(UUID::class) { UUIDSerializer }
    }
}
```

## Logging

### Custom Logger

You can provide a custom logger:

```kotlin
logger = Logger { severity, message, cause ->
    when (severity) {
        Logger.Severity.INFO -> println("INFO: $message")
        Logger.Severity.WARNING -> println("WARNING: $message")
        Logger.Severity.ERROR -> println("ERROR: $message, cause: $cause")
    }
}
```

### Integration with Logging Frameworks

You can integrate with popular logging frameworks:

#### Napier (Multiplatform)

```kotlin
logger = Logger { severity, message, cause ->
    when (severity) {
        Logger.Severity.INFO -> Napier.i(message)
        Logger.Severity.WARNING -> Napier.w(message)
        Logger.Severity.ERROR -> Napier.e(cause, message)
    }
}
```

#### Other Logging Frameworks

For other logging frameworks like SLF4J or Timber, you can use a similar approach:

```kotlin
// SLF4J
logger = Logger { severity, message, cause ->
    val logger = LoggerFactory.getLogger("SignalRKore")
    when (severity) {
        Logger.Severity.INFO -> logger.info(message)
        Logger.Severity.WARNING -> logger.warn(message)
        Logger.Severity.ERROR -> logger.error(message, cause)
    }
}

// Timber (Android)
logger = Logger { severity, message, cause ->
    when (severity) {
        Logger.Severity.INFO -> Timber.i(message)
        Logger.Severity.WARNING -> Timber.w(message)
        Logger.Severity.ERROR -> Timber.e(cause, message)
    }
}
```

## Complete Example

Here's a complete example that demonstrates all configuration options:

```kotlin
val connection = HubConnectionBuilder.create("https://example.com/chathub") {
    // Transport options
    transportEnum = TransportEnum.WebSockets
    skipNegotiate = true

    // Authentication and headers
    headers = mapOf("Custom-Header" to "Value")
    accessToken = "your-access-token"

    // Timeout and reconnection
    handshakeResponseTimeout = 30.seconds
    automaticReconnect = AutomaticReconnect.exponentialBackoff()

    // HTTP client
    httpClient = HttpClient {
        install(WebSockets)
        install(SSE)
        install(HttpTimeout)
        install(ContentNegotiation) { json() }
    }

    // Protocol and serialization
    protocol = JsonHubProtocol()
    json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    // Logging
    logger = Logger { severity, message, cause ->
        when (severity) {
            Logger.Severity.INFO -> println("INFO: $message")
            Logger.Severity.WARNING -> println("WARNING: $message")
            Logger.Severity.ERROR -> println("ERROR: $message, cause: $cause")
        }
    }
}
```

## Platform-Specific Configuration

### Android

On Android, you might want to use the Android-specific HTTP client:

```kotlin
httpClient = HttpClient(Android) {
    install(WebSockets)
    install(SSE)
    install(HttpTimeout)
    install(ContentNegotiation) { json() }
}
```

### iOS

On iOS, you might want to use the Darwin-specific HTTP client:

```kotlin
httpClient = HttpClient(Darwin) {
    install(WebSockets)
    install(SSE)
    install(HttpTimeout)
    install(ContentNegotiation) { json() }
}
```

## Next Steps

Now that you know how to configure SignalRKore, you can learn how to:

- [Send messages](sending-messages.md) to the hub
- [Receive messages](receiving-messages.md) from the hub
- Work with [streams](streams.md)
- Configure [automatic reconnection](reconnection.md)
