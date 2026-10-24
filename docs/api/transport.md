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
    transports = setOf(TransportEnum.WebSockets)
}
```

### Server-Sent Events (SSE)

Server-Sent Events is a technology where a browser receives automatic updates from a server via HTTP connection. It's a one-way communication channel from the server to the client.

```kotlin
val connection = HubConnectionBuilder.create("https://example.com/chathub") {
    transports = setOf(TransportEnum.ServerSentEvents)
}
```

### Long Polling

Long Polling is a technique where the client makes an HTTP request to the server, and the server keeps the connection open until it has new data to send. Once the server sends a response, the client immediately makes a new request.

```kotlin
val connection = HubConnectionBuilder.create("https://example.com/chathub") {
    transports = setOf(TransportEnum.LongPolling)
}
```

## Transport Selection and Fallback

By default, all transports are allowed. SignalRKore uses the transports the server offers, in the order the server offers them (by default WebSockets, Server-Sent Events, Long Polling), skipping the ones not allowed by `transports`.

If a transport fails to start, for example because a proxy blocks WebSockets, SignalRKore negotiates again and tries the next one. If none of them can be started, `start` throws `TransportsFailedException`, which contains the error of each transport that was tried. When only one transport could be tried, its error is thrown directly.

```kotlin
val connection = HubConnectionBuilder.create("https://example.com/chathub") {
    transports = setOf(TransportEnum.WebSockets, TransportEnum.LongPolling)
}
```

## Custom Transport

You can implement your own transport by implementing the `Transport` interface. However, this is an advanced use case and is not typically needed.

## TransportEnum

The `TransportEnum` enum defines the available transport types:

```kotlin
enum class TransportEnum {
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
| `WebSockets` | Uses WebSockets transport |
| `ServerSentEvents` | Uses Server-Sent Events transport |
| `LongPolling` | Uses Long Polling transport |

## Implementation Details

SignalRKore provides three implementations of the `Transport` interface:

- `WebSocketTransport`: Uses Ktor's WebSockets client
- `ServerSentEventsTransport`: Uses Ktor's SSE client
- `LongPollingTransport`: Uses Ktor's HTTP client for long polling

These implementations are internal to the library and are not meant to be used directly. Instead, you should use the `HubConnectionBuilder` to create a connection with the desired transport type.