# Transport

The `Transport` interface defines the contract for different transport mechanisms used by SignalRKore to communicate with a SignalR hub. SignalRKore supports three transport types: WebSockets, Server-Sent Events (SSE), and Long Polling.

## Transport Interface

The `Transport` interface defines the following methods:

```kotlin
interface Transport {
    suspend fun start(url: String)
    suspend fun send(data: ByteArray)
    fun receive(): Flow<ByteArray>
    suspend fun stop()
}
```

## Transport Types

### WebSockets

WebSockets is a protocol providing full-duplex communication channels over a single TCP connection. It's the preferred transport for SignalR as it provides the most efficient communication.

```kotlin
val connection = HubConnectionBuilder.create("https://example.com/chathub") {
    transportEnum = TransportEnum.WebSockets
}
```

### Server-Sent Events (SSE)

Server-Sent Events is a technology where a browser receives automatic updates from a server via HTTP connection. It's a one-way communication channel from the server to the client.

```kotlin
val connection = HubConnectionBuilder.create("https://example.com/chathub") {
    transportEnum = TransportEnum.ServerSentEvents
}
```

### Long Polling

Long Polling is a technique where the client makes an HTTP request to the server, and the server keeps the connection open until it has new data to send. Once the server sends a response, the client immediately makes a new request.

```kotlin
val connection = HubConnectionBuilder.create("https://example.com/chathub") {
    transportEnum = TransportEnum.LongPolling
}
```

## Automatic Transport Selection

By default, SignalRKore will automatically select the best available transport based on what the server supports. The order of preference is:

1. WebSockets
2. Server-Sent Events
3. Long Polling

```kotlin
val connection = HubConnectionBuilder.create("https://example.com/chathub") {
    transportEnum = TransportEnum.All  // Default
}
```

## Transport Fallback

Currently, SignalRKore does not support automatic transport fallback. If a transport fails to connect, the connection will fail. You need to handle reconnection manually or use the automatic reconnect feature.

## Custom Transport

You can implement your own transport by implementing the `Transport` interface. However, this is an advanced use case and is not typically needed.

## TransportEnum

The `TransportEnum` enum defines the available transport types:

```kotlin
enum class TransportEnum {
    All,
    WebSockets,
    ServerSentEvents,
    LongPolling
}
```

## API Reference

### Transport Interface

| Method | Parameters | Return Type | Description |
|--------|------------|-------------|-------------|
| `start` | `url: String` | `Unit` | Starts the transport with the specified URL |
| `send` | `data: ByteArray` | `Unit` | Sends data to the server |
| `receive` | None | `Flow<ByteArray>` | Receives data from the server as a flow |
| `stop` | None | `Unit` | Stops the transport |

### TransportEnum

| Value | Description |
|-------|-------------|
| `All` | Automatically selects the best available transport |
| `WebSockets` | Uses WebSockets transport |
| `ServerSentEvents` | Uses Server-Sent Events transport |
| `LongPolling` | Uses Long Polling transport |

## Implementation Details

SignalRKore provides three implementations of the `Transport` interface:

- `WebSocketTransport`: Uses Ktor's WebSockets client
- `ServerSentEventsTransport`: Uses Ktor's SSE client
- `LongPollingTransport`: Uses Ktor's HTTP client for long polling

These implementations are internal to the library and are not meant to be used directly. Instead, you should use the `HubConnectionBuilder` to create a connection with the desired transport type.