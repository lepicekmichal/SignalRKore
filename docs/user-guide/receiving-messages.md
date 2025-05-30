# Receiving Messages

This guide explains how to receive messages from a SignalR hub using SignalRKore.

## Prerequisites

Before you can receive messages, you need to:

1. Create a connection using `HubConnectionBuilder`
2. Start the connection using `connection.start()`

See the [Connection](connection.md) guide for details.

## On Method

The `on` method registers a handler for a specific hub method. It returns a Flow that emits the parameters of the hub method when it's invoked by the server.

### Basic Usage

```kotlin
// Register a handler for a hub method with no parameters
connection.on("ping").collect {
    println("Received ping from server")
}

// Register a handler for a hub method with one parameter
connection.on("receiveMessage", String::class).collect { (message) ->
    println("Received message: $message")
}

// Register a handler for a hub method with multiple parameters
connection.on("broadcastMessage", String::class, String::class).collect { (user, message) ->
    println("$user says: $message")
}
```

The first parameter is the name of the hub method to handle, followed by the parameter types of the hub method.

### Type Parameters

You need to specify the type of each parameter that the hub method will receive:

```kotlin
// One parameter of type String
connection.on("receiveMessage", String::class)

// Two parameters: String and Int
connection.on("receiveScore", String::class, Int::class)

// Three parameters: String, Double, Double
connection.on("receiveLocation", String::class, Double::class, Double::class)
```

### Receiving Complex Types

You can receive complex types as parameters:

```kotlin
@Serializable
data class ChatMessage(
    val user: String,
    val message: String,
    val timestamp: String
)

// Register a handler for a hub method that receives a complex type
connection.on("receiveMessage", ChatMessage::class).collect { (message) ->
    println("${message.user} says: ${message.message} at ${message.timestamp}")
}
```

Note that complex types must be annotated with `@Serializable` from the Kotlinx Serialization library.

### Receiving Collections

You can receive collections as parameters:

```kotlin
// Receive a list of strings
connection.on("receiveUsers", List::class).collect { (users) ->
    val userList = users as List<String>
    println("Received users: ${userList.joinToString(", ")}")
}

// Receive a map
connection.on("receiveUserRoles", Map::class).collect { (userRoles) ->
    val roleMap = userRoles as Map<String, String>
    println("Received user roles: $roleMap")
}
```

## Returning Results

Some hub methods may expect a result from the client. You can return a result using the `on` method with a result type:

```kotlin
// Register a handler that returns a result
connection.on("getClientTime", resultType = String::class) {
    // Return the current time as a string
    val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
    currentTime
}

// Register a handler with parameters that returns a result
connection.on("add", Int::class, Int::class, resultType = Int::class) { a, b ->
    // Return the sum of the two parameters
    a + b
}
```

The `resultType` parameter specifies the type of the result that the hub method expects.

## Handling Errors

When receiving messages, you should handle potential errors:

```kotlin
connection.on("receiveMessage", String::class)
    .catch { ex ->
        println("Error receiving message: ${ex.message}")
    }
    .collect { (message) ->
        println("Received message: $message")
    }
```

Common errors include:

- Connection lost
- Parameter type mismatch
- Serialization errors

## Unregistering Handlers

The `on` method returns a Flow that you can collect. When you're done collecting, the handler is automatically unregistered.

If you need to manually unregister a handler, you can use the `cancel` method on the coroutine job:

```kotlin
val job = scope.launch {
    connection.on("receiveMessage", String::class).collect { (message) ->
        println("Received message: $message")
    }
}

// Later, when you want to unregister the handler
job.cancel()
```

## Complete Example

Here's a complete example that demonstrates how to receive messages:

```kotlin
val scope = CoroutineScope(Dispatchers.Main)

// Create a connection
val connection = HubConnectionBuilder.create("https://example.com/chathub")

// Start the connection
scope.launch {
    try {
        connection.start()
        println("Connection started successfully")
        
        // Register a handler for a hub method with multiple parameters
        launch {
            connection.on("broadcastMessage", String::class, String::class)
                .catch { ex ->
                    println("Error receiving message: ${ex.message}")
                }
                .collect { (user, message) ->
                    println("$user says: $message")
                }
        }
        
        // Register a handler for a hub method that receives a complex type
        launch {
            connection.on("receiveMessage", ChatMessage::class).collect { (message) ->
                println("${message.user} says: ${message.message} at ${message.timestamp}")
            }
        }
        
        // Register a handler that returns a result
        connection.on("getClientTime", resultType = String::class) {
            val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
            currentTime
        }
        
        // Keep the connection open
        delay(Long.MAX_VALUE)
    } catch (ex: Exception) {
        println("Error: ${ex.message}")
    }
}
```

## Next Steps

Now that you know how to receive messages, you can learn how to:

- [Send messages](sending-messages.md) to the hub
- Work with [streams](streams.md)
- Configure [automatic reconnection](reconnection.md)
- Explore advanced [configuration options](configuration.md)