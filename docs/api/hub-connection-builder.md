# HubConnectionBuilder

The `HubConnectionBuilder` class is used to create and configure instances of `HubConnection`. It provides a fluent API for setting various connection options.

## Creating a Connection

The simplest way to create a connection is to use the `create` method:

```kotlin
val connection = HubConnectionBuilder.create("https://example.com/chathub")
```

## Configuration Options

You can configure the connection by passing a lambda to the `create` method:

```kotlin
val connection = HubConnectionBuilder.create("https://example.com/chathub") {
    transportEnum = TransportEnum.WebSockets
    skipNegotiate = true
    automaticReconnect = AutomaticReconnect.Active
    handshakeResponseTimeout = 30.seconds
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

Available reconnect options:

- `AutomaticReconnect.Inactive` (default): No automatic reconnection
- `AutomaticReconnect.Active`: Basic reconnect policy (waits 0, 2, 10, and 30 seconds before each attempt)
- `AutomaticReconnect.exponentialBackoff()`: Exponential backoff policy
- `AutomaticReconnect.Custom`: Custom reconnect policy

See [Reconnection](../user-guide/reconnection.md) for more details on reconnection options.

### Handshake Response Timeout

You can configure the timeout for the handshake response:

```kotlin
handshakeResponseTimeout = 30.seconds
```

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

This is a convenience property that sets the "Authorization" header with a "Bearer" prefix.

### HTTP Client

You can provide a custom Ktor HTTP client:

```kotlin
httpClient = HttpClient(OkHttp) {
    engine {
        preconfigured = okHttpBuilder.build()
    }
}
```

Note: If you provide a custom HTTP client, make sure it has the necessary plugins installed:

```kotlin
HttpClient {
    install(WebSockets)
    install(SSE)
    install(HttpTimeout)
    install(ContentNegotiation) { json() }
}
```

### Protocol

You can specify a custom hub protocol:

```kotlin
protocol = JsonHubProtocol()
```

### JSON Serialization

You can provide a custom JSON serializer:

```kotlin
json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    // Other configuration options
}
```

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

Here's a complete example that demonstrates all configuration options:

```kotlin
val connection = HubConnectionBuilder.create("https://example.com/chathub") {
    transportEnum = TransportEnum.WebSockets
    skipNegotiate = true
    automaticReconnect = AutomaticReconnect.Active
    handshakeResponseTimeout = 30.seconds
    headers = mapOf("Custom-Header" to "Value")
    accessTokenProvider = { "your-access-token" }
    httpClient = HttpClient {
        install(WebSockets)
        install(SSE)
        install(HttpTimeout)
        install(ContentNegotiation) { json() }
    }
    protocol = JsonHubProtocol()
    json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }
    logger = Logger { severity, message, cause ->
        when (severity) {
            Logger.Severity.INFO -> println("INFO: $message")
            Logger.Severity.WARNING -> println("WARNING: $message")
            Logger.Severity.ERROR -> println("ERROR: $message, cause: $cause")
        }
    }
}
```

## API Reference

### Methods

| Name | Parameters | Return Type | Description |
|------|------------|-------------|-------------|
| `create` | `url: String, configure: (HttpHubConnectionBuilder.() -> Unit)? = null` | `HubConnection` | Creates a new HubConnection with the specified URL and configuration |

### HttpHubConnectionBuilder Properties

| Name                       | Type                    | Default | Description |
|----------------------------|-------------------------|---------|-------------|
| `transportEnum`            | `TransportEnum`         | `TransportEnum.All` | The transport type to use for the connection |
| `skipNegotiate`            | `Boolean`               | `false` | Whether to skip the negotiate step (WebSockets only) |
| `automaticReconnect`       | `AutomaticReconnect`    | `AutomaticReconnect.Inactive` | The automatic reconnect policy |
| `handshakeResponseTimeout` | `Duration`              | `15.seconds` | The timeout for the handshake response |
| `headers`                  | `Map<String, String>`   | `emptyMap()` | HTTP headers to include in requests |
| `accessTokenProvider`      | `(suspend () -> String)?` | `null` | Access token for authentication (sets the "Authorization" header with "Bearer" prefix) |
| `httpClient`               | `HttpClient?`           | `null` | Custom Ktor HTTP client |
| `protocol`                 | `HubProtocol`           | `JsonHubProtocol()` | The protocol used for communication with the hub |
| `json`                     | `Json`                  | `Json { ignoreUnknownKeys = true }` | JSON serializer |
| `logger`                   | `Logger`                | `Logger.Empty` | Logger for logging messages |
