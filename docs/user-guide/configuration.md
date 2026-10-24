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

You can limit which transports the connection may use:

```kotlin
transports = setOf(TransportEnum.WebSockets, TransportEnum.LongPolling)
```

Available transports:

- `TransportEnum.WebSockets`
- `TransportEnum.ServerSentEvents`
- `TransportEnum.LongPolling`

By default, all of them are allowed. The allowed transports are tried in the order the server offers them, and if one fails to start, the next one is tried. If none of them can be started, `start` throws `TransportsFailedException` with the error of each transport that was tried.

See the [Transport](../api/transport.md) API reference for more details.

### Skip Negotiate

You can skip the negotiate step when using WebSockets:

```kotlin
transports = setOf(TransportEnum.WebSockets)
skipNegotiate = true
```

Note: This option can only be used when WebSockets is the only allowed transport, and there is no transport fallback.

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
accessTokenProvider = { "your-access-token" }
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

### Server Timeout

You can configure the maximum duration the connection will wait for a message from the server before closing:

```kotlin
serverTimeout = 30.seconds // default is 30 seconds
```

### Keep Alive Interval

You can configure how often the client sends keep-alive pings to the server:

```kotlin
keepAliveInterval = 15.seconds // default is 15 seconds
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

You can specify the hub protocol:

```kotlin
protocol = JsonHubProtocol(logger)
```

Two protocols are available:

- `JsonHubProtocol` (default) - JSON messages, part of the core `signalrkore` artifact
- `MessagePackHubProtocol` - binary MessagePack messages, in the `signalrkore-msgpack` artifact

See the [Protocol](../api/protocol.md) API reference for more details.

### MessagePack

To use the MessagePack protocol, add the `signalrkore-msgpack` artifact:

```kotlin
implementation("eu.lepicekmichal.signalrkore:signalrkore-msgpack:${signalrkoreVersion}")
```

and set the protocol:

```kotlin
protocol = MessagePackHubProtocol(logger)
```

The server has to enable it with `AddMessagePackProtocol()`. Keep in mind that:

- Server-Sent Events cannot carry binary data, so the connection uses WebSockets or Long Polling.
- Property names are case sensitive. Map Kotlin properties to PascalCase .NET properties with `@SerialName`.
- Enums are sent as strings, `ByteArray` as binary data and `kotlin.time.Instant` as a MessagePack timestamp, which .NET reads as a `DateTime`.

Payloads are encoded by the `MessagePack` format, which you can configure and pass to the protocol:

```kotlin
protocol = MessagePackHubProtocol(
    logger = logger,
    messagePack = MessagePack {
        ignoreUnknownKeys = true
        encodeDefaults = true
        serializersModule = SerializersModule {
            contextual(UUID::class) { UUIDSerializer }
        }
    },
)
```

### JSON Serialization

You can provide a custom JSON serializer for the payloads of the default `JsonHubProtocol`:

```kotlin
json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    // Other configuration options
}
```

This is useful if you need to configure the JSON serializer with custom settings or modules. The `json` instance is also used to read the negotiate response with the default HTTP client. When you set the `protocol` yourself, pass the `Json` instance to the protocol: `JsonHubProtocol(logger, json)`.

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
        Logger.Severity.INFO -> println("INFO: ${message()}")
        Logger.Severity.WARNING -> println("WARNING: ${message()}")
        Logger.Severity.ERROR -> println("ERROR: ${message()}, cause: $cause")
    }
}
```

### Integration with Logging Frameworks

You can integrate with popular logging frameworks:

#### Napier (Multiplatform)

```kotlin
logger = Logger { severity, message, cause ->
    when (severity) {
        Logger.Severity.INFO -> Napier.i(message())
        Logger.Severity.WARNING -> Napier.w(message())
        Logger.Severity.ERROR -> Napier.e(cause, message())
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
        Logger.Severity.INFO -> logger.info(message())
        Logger.Severity.WARNING -> logger.warn(message())
        Logger.Severity.ERROR -> logger.error(message(), cause)
    }
}

// Timber (Android)
logger = Logger { severity, message, cause ->
    when (severity) {
        Logger.Severity.INFO -> Timber.i(message())
        Logger.Severity.WARNING -> Timber.w(message())
        Logger.Severity.ERROR -> Timber.e(cause, message())
    }
}
```

## Complete Example

Here's a complete example that demonstrates all configuration options:

```kotlin
val connection = HubConnectionBuilder.create("https://example.com/chathub") {
    // Transport options
    transports = setOf(TransportEnum.WebSockets)
    skipNegotiate = true

    // Authentication and headers
    headers = mapOf("Custom-Header" to "Value")
    accessTokenProvider = { "your-access-token" }

    // Timeout and reconnection
    handshakeResponseTimeout = 30.seconds
    serverTimeout = 30.seconds // default
    keepAliveInterval = 15.seconds // default
    automaticReconnect = AutomaticReconnect.exponentialBackoff()

    // HTTP client
    httpClient = HttpClient {
        install(WebSockets)
        install(SSE)
        install(HttpTimeout)
        install(ContentNegotiation) { json() }
    }

    // Protocol and serialization
    json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    // Logging
    logger = Logger { severity, message, cause ->
        when (severity) {
            Logger.Severity.INFO -> println("INFO: ${message()}")
            Logger.Severity.WARNING -> println("WARNING: ${message()}")
            Logger.Severity.ERROR -> println("ERROR: ${message()}, cause: $cause")
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
