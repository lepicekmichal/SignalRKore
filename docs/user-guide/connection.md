# Connection

This guide explains how to establish and manage connections to a SignalR hub using SignalRKore.

## Creating a Connection

To connect to a SignalR hub, you first need to create a `HubConnection` instance using the `HubConnectionBuilder`:

```kotlin
val connection = HubConnectionBuilder.create("https://example.com/chathub")
```

The URL should point to your SignalR hub endpoint. This is typically the base URL of your web application followed by the hub path (e.g., `/chathub`).

## Connection Lifecycle

### Starting a Connection

Before you can send or receive messages, you need to start the connection:

```kotlin
// In a coroutine scope
scope.launch {
    try {
        connection.start()
        println("Connection started successfully")
    } catch (ex: Exception) {
        println("Failed to start connection: ${ex.message}")
    }
}
```

The `start` method is suspending and will complete when the connection is established or throw an exception if the connection fails.

### Monitoring Connection State

You can monitor the connection state using the `connectionState` property:

```kotlin
scope.launch {
    connection.connectionState.collect { state ->
        when (state) {
            HubConnectionState.CONNECTED -> println("Connected to the hub")
            HubConnectionState.CONNECTING -> println("Connecting to the hub")
            HubConnectionState.DISCONNECTED -> println("Disconnected from the hub")
            HubConnectionState.RECONNECTING -> println("Reconnecting to the hub")
        }
    }
}
```

This is useful for updating your UI based on the connection state or for implementing custom reconnection logic.

### Stopping a Connection

When you're done with the connection, you should stop it to release resources:

```kotlin
scope.launch {
    connection.stop()
    println("Connection stopped")
}
```

You can optionally provide an error message:

```kotlin
scope.launch {
    connection.stop("Connection closed by user")
}
```

## Connection Options

When creating a connection, you can configure various options:

```kotlin
val connection = HubConnectionBuilder.create("https://example.com/chathub") {
    // Configure connection options here
}
```

### Transport

You can specify which transport to use for the connection:

```kotlin
transportEnum = TransportEnum.WebSockets
```

Available transport options:

- `TransportEnum.All` (default): Automatically selects the best available transport
- `TransportEnum.WebSockets`: Uses WebSockets transport
- `TransportEnum.ServerSentEvents`: Uses Server-Sent Events transport
- `TransportEnum.LongPolling`: Uses Long Polling transport

### Skip Negotiate

You can skip the negotiate step when using WebSockets:

```kotlin
skipNegotiate = true
```

Note: This option can only be used with WebSockets transport.

### Automatic Reconnect

You can configure automatic reconnection when the connection is lost:

```kotlin
automaticReconnect = AutomaticReconnect.Active
```

See [Reconnection](reconnection.md) for more details on reconnection options.

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

### Handshake Response Timeout

You can configure the timeout for the handshake response:

```kotlin
handshakeResponseTimeout = 30.seconds
```

### HTTP Client

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

### Protocol

You can specify a custom hub protocol:

```kotlin
protocol = JsonHubProtocol()
```

Currently, only the `JsonHubProtocol` is supported.

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

### Logger

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

## Complete Example

Here's a complete example that demonstrates how to create, start, monitor, and stop a connection:

```kotlin
val scope = CoroutineScope(Dispatchers.Main)

// Create a connection
val connection = HubConnectionBuilder.create("https://example.com/chathub") {
    transportEnum = TransportEnum.WebSockets
    automaticReconnect = AutomaticReconnect.Active
    headers = mapOf("Authorization" to "Bearer token")
}

// Monitor connection state
scope.launch {
    connection.connectionState.collect { state ->
        println("Connection state: $state")
    }
}

// Start the connection
scope.launch {
    try {
        connection.start()
        println("Connection started successfully")

        // Do something with the connection

        // Stop the connection when done
        connection.stop()
    } catch (ex: Exception) {
        println("Failed to start connection: ${ex.message}")
    }
}
```

## Next Steps

Now that you know how to establish and manage connections, you can learn how to:

- [Send messages](sending-messages.md) to the hub
- [Receive messages](receiving-messages.md) from the hub
- Work with [streams](streams.md)
- Configure [automatic reconnection](reconnection.md)
- Explore advanced [configuration options](configuration.md)
