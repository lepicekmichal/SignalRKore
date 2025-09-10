# SignalRKore

[![Maven Central](https://img.shields.io/maven-central/v/eu.lepicekmichal.signalrkore/signalrkore)](https://mvnrepository.com/artifact/eu.lepicekmichal.signalrkore)
[![Kotlin](https://img.shields.io/badge/kotlin-2.1.10-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![GitHub License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

![badge-android](http://img.shields.io/badge/platform-android-6EDB8D.svg?style=flat)
![badge-jvm](http://img.shields.io/badge/platform-jvm-DB413D.svg?style=flat)
![badge-ios](http://img.shields.io/badge/platform-ios-lightgray?style=flat)

## Overview

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

## Quick Example

```kotlin
// Create a connection
val connection = HubConnectionBuilder.create("http://localhost:5000/chat")

// Start the connection
connection.start()

// Send a message to the server
connection.send("broadcastMessage", "User", "Hello, SignalR!")

// Receive messages from the server
connection.on("broadcastMessage", String.serializer(), String.serializer()).collect { (user, message) ->
    println("$user says: $message")
}

// Don't forget to stop the connection when done
connection.stop()
```

## Getting Started

Check out the [Getting Started](getting-started/installation.md) guide to learn how to add SignalRKore to your project and start using it.

## License

SignalRKore is released under the [Apache 2.0 license](https://github.com/lepicekmichal/SignalRKore/blob/main/LICENSE.txt).

```
Copyright 2023 Michal Lepicek

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```