# Module SignalRKore

SignalRKore is a Kotlin Multiplatform client library for ASP.NET Core SignalR. It enables real-time communication between clients and servers, allowing server-side code to push content to clients and vice-versa instantly.

## Features

- **Kotlin Multiplatform**: Supports Android, JVM, and iOS platforms
- **Coroutines**: Uses Kotlin Coroutines for asynchronous operations
- **Ktor**: Built on top of Ktor for networking
- **Kotlinx Serialization**: Uses Kotlinx Serialization for JSON serialization
- **Multiple Transports**: Supports WebSockets, ServerSentEvents, and LongPolling
- **Automatic Reconnect**: Provides automatic reconnection functionality
- **Streams**: Supports streaming data between client and server
- **Connection Status**: Provides connection status monitoring

## Main Components

### HubConnection

The main class for connecting to a SignalR hub. It provides methods for starting and stopping the connection, sending and receiving messages, and monitoring the connection status.

### HubConnectionBuilder

A builder class for creating HubConnection instances with various configuration options.

### Transport

An interface for different transport mechanisms (WebSockets, ServerSentEvents, LongPolling).

### HubProtocol

An interface for hub protocols, with JsonHubProtocol as the default implementation.