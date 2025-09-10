# HubConnection

The `HubConnection` class is the main entry point for interacting with a SignalR hub. It provides methods for starting and stopping the connection, sending and receiving messages, and monitoring the connection status.

## Creating a Connection

You don't create a `HubConnection` instance directly. Instead, use the `HubConnectionBuilder` class:

```kotlin
val connection = HubConnectionBuilder.create("https://example.com/chathub")
```

See [HubConnectionBuilder](hub-connection-builder.md) for more details on configuration options.

## Connection Lifecycle

### Starting a Connection

Before you can send or receive messages, you need to start the connection:

```kotlin
// In a coroutine scope
connection.start()
```

This method is suspending and will complete when the connection is established or throw an exception if the connection fails.

### Monitoring Connection State

You can monitor the connection state using the `connectionState` property:

```kotlin
connection.connectionState.collect { state ->
    when (state) {
        HubConnectionState.CONNECTED -> println("Connected to the hub")
        HubConnectionState.CONNECTING -> println("Connecting to the hub")
        HubConnectionState.DISCONNECTED -> println("Disconnected from the hub")
        HubConnectionState.RECONNECTING -> println("Reconnecting to the hub")
    }
}
```

### Stopping a Connection

When you're done with the connection, you should stop it:

```kotlin
connection.stop()
```

You can optionally provide an error message:

```kotlin
connection.stop("Connection closed by user")
```

## Sending Messages

### Send Method

The `send` method sends a message to the hub without expecting a response:

```kotlin
// Send a message with parameters
connection.send("broadcastMessage", "User", "Hello, SignalR!")
```

### Invoke Method

The `invoke` method sends a message to the hub and expects a response:

```kotlin
// Invoke a method and get the result
val result = connection.invoke("echo", String.serializer(), "Hello, SignalR!")
println("Server responded: $result")
```

## Receiving Messages

### On Method

The `on` method registers a handler for a specific hub method:

```kotlin
// Receive a message with two parameters
connection.on("broadcastMessage", String.serializer(), String.serializer()).collect { (user, message) ->
    println("$user says: $message")
}
```

### Receiving Complex Types

You can also receive messages with complex types:

```kotlin
@Serializable
data class ChatMessage(
    val user: String,
    val message: String,
    val timestamp: String
)

connection.on("receiveMessage", ChatMessage.serializer()).collect { (message) ->
    println("${message.user} says: ${message.message} at ${message.timestamp}")
}
```

## Working with Streams

### Receiving a Stream

The `stream` method allows you to receive a stream of data from the hub:

```kotlin
connection.stream("counterStream", Int.serializer(), 10, 1000).collect { count ->
    println("Received count: $count")
}
```

### Sending a Stream

You can send a stream of data to the hub using a Flow:

```kotlin
val dataStream = flow {
    for (i in 1..10) {
        emit(i)
        delay(1000)
    }
}

connection.send("uploadStream", dataStream)
```

## Automatic Reconnection

SignalRKore supports automatic reconnection when the connection is lost. This is configured through the `HubConnectionBuilder`:

```kotlin
val connection = HubConnectionBuilder.create("https://example.com/chathub") {
    automaticReconnect = AutomaticReconnect.Active
}
```

See [Reconnection](../user-guide/reconnection.md) for more details on reconnection options.

## API Reference

### Properties

| Name | Type | Description |
|------|------|-------------|
| `connectionState` | `StateFlow<HubConnectionState>` | The current state of the connection |

### Methods

| Name | Parameters | Return Type | Description |
|------|------------|-------------|-------------|
| `start` | `reconnectionAttempt: Boolean = false` | `Unit` | Starts the connection to the hub |
| `stop` | `errorMessage: String? = null` | `Unit` | Stops the connection to the hub |
| `send` | `method: String, vararg args: Any?` | `Unit` | Sends a message to the hub without expecting a response |
| `invoke` | `method: String, resultType: KClass<T>, vararg args: Any?` | `T` | Sends a message to the hub and expects a response |
| `on` | `method: String, paramType1: KClass<T1>, paramType2: KClass<T2>, ...` | `Flow<Tuple>` | Registers a handler for a specific hub method |
| `stream` | `method: String, itemType: KClass<T>, vararg args: Any?` | `Flow<T>` | Receives a stream of data from the hub |

### Enums

#### HubConnectionState

| Value | Description |
|-------|-------------|
| `CONNECTED` | The connection is connected to the hub |
| `CONNECTING` | The connection is connecting to the hub |
| `DISCONNECTED` | The connection is disconnected from the hub |
| `RECONNECTING` | The connection is reconnecting to the hub |