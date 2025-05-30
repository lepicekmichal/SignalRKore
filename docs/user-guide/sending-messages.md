# Sending Messages

This guide explains how to send messages to a SignalR hub using SignalRKore.

## Prerequisites

Before you can send messages, you need to:

1. Create a connection using `HubConnectionBuilder`
2. Start the connection using `connection.start()`

See the [Connection](connection.md) guide for details.

## Send Method

The `send` method sends a message to the hub without expecting a response:

```kotlin
// Send a message with no parameters
connection.send("ping")

// Send a message with parameters
connection.send("broadcastMessage", "User", "Hello, SignalR!")

// Send a message with multiple parameters
connection.send("sendCoordinates", "User", 10.5, 20.3, "Location")
```

The first parameter is the name of the hub method to call, followed by any parameters to pass to the method.

## Invoke Method

The `invoke` method sends a message to the hub and expects a response:

```kotlin
// Invoke a method and get the result
val result = connection.invoke("echo", String::class, "Hello, SignalR!")
println("Server responded: $result")

// Invoke a method with multiple parameters
val sum = connection.invoke("add", Int::class, 5, 10)
println("Sum: $sum")
```

The first parameter is the name of the hub method to call, the second parameter is the expected return type, followed by any parameters to pass to the method.

## Sending Complex Types

You can send complex types as parameters:

```kotlin
@Serializable
data class ChatMessage(
    val user: String,
    val message: String,
    val timestamp: String
)

val message = ChatMessage(
    user = "User",
    message = "Hello, SignalR!",
    timestamp = "2023-01-01T12:00:00Z"
)

// Send a complex type
connection.send("sendMessage", message)

// Invoke a method with a complex type
val response = connection.invoke("echoMessage", ChatMessage::class, message)
println("Server echoed: ${response.message}")
```

Note that complex types must be annotated with `@Serializable` from the Kotlinx Serialization library.

## Sending Collections

You can send collections as parameters:

```kotlin
// Send a list
val users = listOf("User1", "User2", "User3")
connection.send("addUsers", users)

// Send a map
val userRoles = mapOf("User1" to "Admin", "User2" to "User")
connection.send("setUserRoles", userRoles)
```

## Sending Streams

You can send streams of data to the hub using Kotlin Flows:

```kotlin
// Create a flow that emits 10 integers with a 1-second delay between each
val dataStream = flow {
    for (i in 1..10) {
        emit(i)
        delay(1000)
    }
}

// Send the stream to the hub
connection.send("uploadStream", dataStream)
```

See the [Streams](streams.md) guide for more details on working with streams.

## Error Handling

When sending messages, you should handle potential errors:

```kotlin
try {
    connection.send("broadcastMessage", "User", "Hello, SignalR!")
} catch (ex: Exception) {
    println("Failed to send message: ${ex.message}")
}
```

Common errors include:

- Connection not established
- Hub method not found
- Parameter type mismatch
- Server-side errors

## Connection State Check

The `send` and `invoke` methods automatically check if the connection is in the `CONNECTED` state before sending a message. If the connection is not connected, they will throw an exception.

You can manually check the connection state:

```kotlin
if (connection.connectionState.value == HubConnectionState.CONNECTED) {
    connection.send("broadcastMessage", "User", "Hello, SignalR!")
} else {
    println("Cannot send message: Connection is not connected")
}
```

## Complete Example

Here's a complete example that demonstrates how to send messages:

```kotlin
val scope = CoroutineScope(Dispatchers.Main)

// Create a connection
val connection = HubConnectionBuilder.create("https://example.com/chathub")

// Start the connection
scope.launch {
    try {
        connection.start()
        println("Connection started successfully")

        // Send a message
        connection.send("broadcastMessage", "User", "Hello, SignalR!")

        // Invoke a method and get the result
        val result = connection.invoke("echo", String::class, "Hello, SignalR!")
        println("Server responded: $result")

        // Send a complex type
        val message = ChatMessage(
            user = "User",
            message = "Hello, SignalR!",
            timestamp = "2023-01-01T12:00:00Z"
        )
        connection.send("sendMessage", message)

        // Send a stream
        val dataStream = flow {
            for (i in 1..10) {
                emit(i)
                delay(1000)
            }
        }
        connection.send("uploadStream", dataStream)

        // Stop the connection when done
        connection.stop()
    } catch (ex: Exception) {
        println("Error: ${ex.message}")
    }
}
```

## Next Steps

Now that you know how to send messages, you can learn how to:

- [Receive messages](receiving-messages.md) from the hub
- Work with [streams](streams.md)
- Configure [automatic reconnection](reconnection.md)
- Explore advanced [configuration options](configuration.md)
