# Quick Start

This guide will help you get started with SignalRKore by walking you through the basic steps to connect to a SignalR hub, send and receive messages.

## Creating a Connection

The first step is to create a connection to a SignalR hub using the `HubConnectionBuilder`:

```kotlin
val connection = HubConnectionBuilder.create("https://example.com/chathub")
```

## Starting the Connection

Before you can send or receive messages, you need to start the connection:

```kotlin
// In a coroutine scope
connection.start()
```

You can also check the connection state:

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

## Sending Messages

Once connected, you can send messages to the hub using the `send` or `invoke` methods:

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

To receive messages from the hub, use the `on` method:

```kotlin
// Receive a message with two parameters
connection.on("broadcastMessage", String.serializer(), String.serializer()).collect { (user, message) ->
    println("$user says: $message")
}
```

### Receiving Messages with Complex Types

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

SignalRKore supports streaming data between client and server:

### Receiving a Stream

```kotlin
connection.stream("counterStream", Int.serializer(), 10, 1000).collect { count ->
    println("Received count: $count")
}
```

### Sending a Stream

```kotlin
// Create a flow that emits 10 integers with a 1-second delay between each
val dataStream = flow {
    for (i in 1..10) {
        emit(i)
        delay(1000)
    }
}

connection.send("uploadStream", dataStream)
```

## Stopping the Connection

When you're done, don't forget to stop the connection:

```kotlin
connection.stop()
```

## Complete Example

Here's a complete example that demonstrates the basic usage of SignalRKore:

```kotlin
// Define a serializable message class
@Serializable
data class ChatMessage(
    val user: String,
    val message: String,
    val timestamp: String
)

fun main() {
    // Create a coroutine scope
    val scope = CoroutineScope(Dispatchers.Main)

    // Create a connection
    val connection = HubConnectionBuilder.create("https://example.com/chathub")

    // Monitor connection state
    scope.launch {
        connection.connectionState.collect { state ->
            println("Connection state: $state")
        }
    }

    // Start the connection
    scope.launch {
        connection.start()

        // Send a message
        connection.send("broadcastMessage", "User", "Hello, SignalR!")

        // Receive messages
        connection.on("broadcastMessage", String.serializer(), String.serializer()).collect { (user, message) ->
            println("$user says: $message")
        }

        // Receive messages with complex types
        connection.on("receiveMessage", ChatMessage.serializer()).collect { (message) ->
            println("${message.user} says: ${message.message} at ${message.timestamp}")
        }

        // Stop the connection when done
        connection.stop()
    }
}
```

## Next Steps

Now that you have a basic understanding of how to use SignalRKore, you can explore the [User Guide](../user-guide/connection.md) for more detailed information on each feature.
