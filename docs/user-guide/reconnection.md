# Reconnection

This guide explains how to configure automatic reconnection in SignalRKore. Automatic reconnection allows the client to reconnect to the server when the connection is lost.

## Prerequisites

Before you can configure reconnection, you need to:

1. Create a connection using `HubConnectionBuilder`
2. Configure the reconnection policy
3. Start the connection using `connection.start()`

See the [Connection](connection.md) guide for details.

## Reconnection Policies

SignalRKore provides several reconnection policies:

### Inactive (Default)

By default, automatic reconnection is disabled:

```kotlin
val connection = HubConnectionBuilder.create("https://example.com/chathub") {
    automaticReconnect = AutomaticReconnect.Inactive
}
```

With this policy, when the connection is lost, the client will not attempt to reconnect automatically. You'll need to manually call `connection.start()` to reconnect.

### Active (Basic)

The basic reconnection policy attempts to reconnect with a fixed delay pattern:

```kotlin
val connection = HubConnectionBuilder.create("https://example.com/chathub") {
    automaticReconnect = AutomaticReconnect.Active
}
```

With this policy, when the connection is lost, the client will attempt to reconnect with the following delays:

1. 0 seconds (immediate)
2. 2 seconds
3. 10 seconds
4. 30 seconds

If all four attempts fail, the client will stop trying to reconnect, and the connection state will change to `DISCONNECTED`.

### Exponential Backoff

The exponential backoff policy attempts to reconnect with increasing delays:

```kotlin
val connection = HubConnectionBuilder.create("https://example.com/chathub") {
    automaticReconnect = AutomaticReconnect.exponentialBackoff()
}
```

With this policy, when the connection is lost, the client will attempt to reconnect with exponentially increasing delays. By default:

- Initial delay: 1 second
- Multiplier: 1.5 (each delay is 1.5 times longer than the previous one)
- Maximum delay: 60 seconds
- Maximum attempts: 15

You can customize these parameters:

```kotlin
val connection = HubConnectionBuilder.create("https://example.com/chathub") {
    automaticReconnect = AutomaticReconnect.exponentialBackoff(
        initialDelayMillis = 2000,  // 2 seconds
        maxDelayMillis = 30000,     // 30 seconds
        multiplier = 2.0,           // Double the delay each time
        maxAttempts = 10            // Maximum 10 attempts
    )
}
```

### Custom

You can create a custom reconnection policy by implementing the `AutomaticReconnect.Custom` class:

```kotlin
val connection = HubConnectionBuilder.create("https://example.com/chathub") {
    automaticReconnect = AutomaticReconnect.Custom { previousRetryCount, elapsedTime ->
        when (previousRetryCount) {
            0 -> 0L                 // Immediate retry
            1 -> 1000L              // 1 second
            2 -> 5000L              // 5 seconds
            3 -> 10000L             // 10 seconds
            else -> null            // Stop retrying
        }
    }
}
```

The custom policy function takes two parameters:

- `previousRetryCount`: The number of previous retry attempts (0-based)
- `elapsedTime`: The time elapsed since the first reconnection attempt

The function should return:

- A delay in milliseconds for the next reconnection attempt
- `null` to stop retrying

## Monitoring Reconnection

You can monitor the connection state to track reconnection attempts:

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

When the connection is lost and automatic reconnection is enabled, the connection state will change to `RECONNECTING`. If reconnection is successful, the state will change to `CONNECTED`. If all reconnection attempts fail, the state will change to `DISCONNECTED`.

## Server-Initiated Reconnection

SignalR Core servers can send a close message with `allowReconnect` set to `true`. When this happens, SignalRKore will attempt to reconnect according to the configured reconnection policy.

## Manual Reconnection

If automatic reconnection is disabled or all reconnection attempts fail, you can manually reconnect:

```kotlin
if (connection.connectionState.value == HubConnectionState.DISCONNECTED) {
    try {
        connection.start()
        println("Reconnected successfully")
    } catch (ex: Exception) {
        println("Failed to reconnect: ${ex.message}")
    }
}
```

## Handling Reconnection Events

You can handle reconnection events by monitoring the connection state:

```kotlin
var previousState = HubConnectionState.DISCONNECTED

connection.connectionState.collect { state ->
    when (state) {
        HubConnectionState.CONNECTED -> {
            if (previousState == HubConnectionState.RECONNECTING) {
                println("Reconnected successfully")
                // Re-subscribe to hub methods or perform other initialization
            } else {
                println("Connected to the hub")
            }
        }
        HubConnectionState.RECONNECTING -> {
            println("Connection lost, attempting to reconnect")
            // Handle reconnection attempt
        }
        HubConnectionState.DISCONNECTED -> {
            if (previousState == HubConnectionState.RECONNECTING) {
                println("Failed to reconnect")
                // Handle reconnection failure
            } else {
                println("Disconnected from the hub")
            }
        }
        else -> println("Connection state: $state")
    }
    previousState = state
}
```

## Complete Example

Here's a complete example that demonstrates how to configure and handle reconnection:

```kotlin
val scope = CoroutineScope(Dispatchers.Main)

// Create a connection with automatic reconnection
val connection = HubConnectionBuilder.create("https://example.com/chathub") {
    automaticReconnect = AutomaticReconnect.exponentialBackoff(
        initialDelayMillis = 1000,  // 1 second
        maxDelayMillis = 30000,     // 30 seconds
        multiplier = 1.5,           // 1.5x multiplier
        maxAttempts = 10            // Maximum 10 attempts
    )
}

// Monitor connection state
var previousState = HubConnectionState.DISCONNECTED
scope.launch {
    connection.connectionState.collect { state ->
        when (state) {
            HubConnectionState.CONNECTED -> {
                if (previousState == HubConnectionState.RECONNECTING) {
                    println("Reconnected successfully")
                    // Re-subscribe to hub methods or perform other initialization
                } else {
                    println("Connected to the hub")
                }
            }
            HubConnectionState.RECONNECTING -> {
                println("Connection lost, attempting to reconnect")
                // Handle reconnection attempt
            }
            HubConnectionState.DISCONNECTED -> {
                if (previousState == HubConnectionState.RECONNECTING) {
                    println("Failed to reconnect")
                    // Handle reconnection failure
                } else {
                    println("Disconnected from the hub")
                }
            }
            else -> println("Connection state: $state")
        }
        previousState = state
    }
}

// Start the connection
scope.launch {
    try {
        connection.start()
        println("Connection started successfully")
        
        // Keep the connection open
        delay(Long.MAX_VALUE)
    } catch (ex: Exception) {
        println("Error: ${ex.message}")
    }
}
```

## Next Steps

Now that you know how to configure reconnection, you can learn how to:

- [Send messages](sending-messages.md) to the hub
- [Receive messages](receiving-messages.md) from the hub
- Work with [streams](streams.md)
- Explore advanced [configuration options](configuration.md)