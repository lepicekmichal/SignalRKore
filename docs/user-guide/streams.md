# Streams

This guide explains how to work with streams in SignalRKore. Streams allow you to send and receive a sequence of data over time, rather than a single message.

## Prerequisites

Before you can work with streams, you need to:

1. Create a connection using `HubConnectionBuilder`
2. Start the connection using `connection.start()`

See the [Connection](connection.md) guide for details.

## Receiving Streams

The `stream` method allows you to receive a stream of data from the hub. It returns a Flow that emits items from the stream as they arrive.

### Basic Usage

```kotlin
// Receive a stream of integers
connection.stream("counter", Int.serializer()).collect { count ->
    println("Received count: $count")
}
```

The first parameter is the name of the hub method to call, and the second parameter is the type of items in the stream. This is the most basic form of receiving a stream from the hub.

### Stream Parameters

You can pass parameters to the stream method:

```kotlin
// Stream with parameters
connection.stream("generateNumbers", Int.serializer(), 1, 10, 500).collect { number ->
    println("Received number: $number")
}
```

In this example, we're calling a hub method named "generateNumbers" that returns a stream of integers. We're passing three parameters: 1 (the start value), 10 (the end value), and 500 (the delay between items in milliseconds).

### Receiving Complex Types

You can receive streams of complex types:

```kotlin
@Serializable
data class StockPrice(
    val symbol: String,
    val price: Double,
    val timestamp: String
)

// Receive a stream of stock prices
connection.stream("stockPrices", StockPrice.serializer(), "AAPL", "MSFT", "GOOG").collect { price ->
    println("${price.symbol}: ${price.price} at ${price.timestamp}")
}
```

Note that complex types must be annotated with `@Serializable` from the Kotlinx Serialization library.

### Handling Stream Completion

The stream completes when the server completes the stream or when an error occurs:

```kotlin
connection.stream("counter", Int.serializer(), 10, 1000)
    .catch { ex ->
        println("Stream error: ${ex.message}")
    }
    .collect { count ->
        println("Received count: $count")
    }
```

### Cancelling a Stream

You can cancel a stream by cancelling the coroutine job:

```kotlin
val job = scope.launch {
    connection.stream("counter", Int.serializer(), 10, 1000).collect { count ->
        println("Received count: $count")
    }
}

// Later, when you want to cancel the stream
job.cancel()
```

## Sending Streams

You can send a stream of data to the hub using a Flow:

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

### Sending Complex Types

You can send streams of complex types:

```kotlin
@Serializable
data class DataPoint(
    val value: Double,
    val timestamp: String
)

// Create a flow of data points
val dataStream = flow {
    for (i in 1..10) {
        val dataPoint = DataPoint(
            value = Random.nextDouble(0.0, 100.0),
            timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date())
        )
        emit(dataPoint)
        delay(1000)
    }
}

// Send the stream to the hub
connection.send("uploadDataPoints", dataStream)
```

### Stream Completion

The stream completes when the Flow completes:

```kotlin
val dataStream = flow {
    for (i in 1..10) {
        emit(i)
        delay(1000)
    }
}

connection.send("uploadStream", dataStream)
```

## Error Handling

When working with streams, you should handle potential errors:

```kotlin
// Receiving streams
connection.stream("counter", Int.serializer(), 10, 1000)
    .catch { ex ->
        println("Stream error: ${ex.message}")
    }
    .collect { count ->
        println("Received count: $count")
    }

// Sending streams
val dataStream = flow {
    try {
        for (i in 1..10) {
            emit(i)
            delay(1000)
        }
    } catch (ex: Exception) {
        println("Stream error: ${ex.message}")
    }
}

try {
    connection.send("uploadStream", dataStream)
} catch (ex: Exception) {
    println("Failed to send stream: ${ex.message}")
}
```

Common errors include:

- Connection lost
- Stream cancelled
- Parameter type mismatch
- Serialization errors

## Complete Example

Here's a complete example that demonstrates how to work with streams:

```kotlin
val scope = CoroutineScope(Dispatchers.Main)

// Create a connection
val connection = HubConnectionBuilder.create("https://example.com/chathub")

// Start the connection
scope.launch {
    try {
        connection.start()
        println("Connection started successfully")

        // Receive a stream
        launch {
            connection.stream("counter", Int.serializer(), 10, 1000)
                .catch { ex ->
                    println("Stream error: ${ex.message}")
                }
                .collect { count ->
                    println("Received count: $count")
                }
        }

        // Send a stream
        val dataStream = flow {
            for (i in 1..10) {
                emit(i)
                delay(1000)
            }
        }

        connection.send("uploadStream", dataStream)

        // Keep the connection open
        delay(Long.MAX_VALUE)
    } catch (ex: Exception) {
        println("Error: ${ex.message}")
    }
}
```

## Next Steps

Now that you know how to work with streams, you can learn how to:

- [Send messages](sending-messages.md) to the hub
- [Receive messages](receiving-messages.md) from the hub
- Configure [automatic reconnection](reconnection.md)
- Explore advanced [configuration options](configuration.md)
