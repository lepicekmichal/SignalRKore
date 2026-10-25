# SignalRKore

[![Maven Central](https://img.shields.io/maven-central/v/eu.lepicekmichal.signalrkore/signalrkore)](https://mvnrepository.com/artifact/eu.lepicekmichal.signalrkore)
[![Documentation](https://img.shields.io/badge/docs-lepicekmichal.github.io-blue.svg?style=flat)](https://lepicekmichal.github.io/SignalRKore/)
[![Kotlin](https://img.shields.io/badge/kotlin-2.4.20-blue.svg?logo=kotlin)](https://kotlinlang.org)
[![GitHub License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](https://www.apache.org/licenses/LICENSE-2.0)
[![Kotlin Weekly](https://img.shields.io/badge/Kotlin%20Weekly-%23416-purple?style=flat)](https://mailchi.mp/kotlinweekly/kotlin-weekly-416)

[![Tests](https://github.com/lepicekmichal/SignalRKore/actions/workflows/tests.yml/badge.svg)](https://github.com/lepicekmichal/SignalRKore/actions/workflows/tests.yml)

![badge-android](https://img.shields.io/badge/platform-android-6EDB8D.svg?style=flat)
![badge-jvm](https://img.shields.io/badge/platform-jvm-DB413D.svg?style=flat)
![badge-ios](https://img.shields.io/badge/platform-ios-lightgray?style=flat)

## Overview

SignalRKore is a Kotlin Multiplatform client library for ASP.NET Core SignalR. It enables real-time communication between clients and servers, allowing server-side code to push content to clients and vice-versa instantly.

## Why should you use **this** library

|                         | [Official client library](https://learn.microsoft.com/en-us/aspnet/core/signalr/java-client) |             **SignalRKore**             |
|:------------------------|:--------------------------------------------------------------------------------------------:|:---------------------------------------:|
| **Technology**          |                                                                                              |                                         |
| Written in              |                                             Java                                             |                  Kotlin                 |
| Kotlin Multiplatform    |                                             :x:                                              |          **Android, JVM, iOS**          |
| Network                 |                                         OkHttp only                                          |                 **Ktor**                |
| Async                   |                                            RxJava                                            |              **Coroutines**             |
| Serialization           |                                   Gson (non-customizable)                                    | **Kotlinx Serializable (customizable)** |
| Logging                 |                                            SLF4J                                             |           **Custom interface**          |
| **Features**            |                                                                                              |                                         |
| Streams                 |                                      :white_check_mark:                                      |            :white_check_mark:           |
| Client results          |                                      :white_check_mark:                                      |            :white_check_mark:           |
| MsgPack                 |                                      :white_check_mark:                                      |            :white_check_mark:           |
| Transport fallback      |                                             :x:                                              |            :white_check_mark:           |
| Automatic reconnect     |                                             :x:                                              |            :white_check_mark:           |
| SSE                     |                                             :x:                                              |            :white_check_mark:           |
| Connection state stream |                                             :x:                                              |            :white_check_mark:           |
| **Community**           |                                                                                              |                                         |
| Maintained by Microsoft |                                      :white_check_mark:                                      |                   :x:                   |

## Installation

SignalRKore is available on Maven Central:

```kotlin
dependencies {
    implementation("eu.lepicekmichal.signalrkore:signalrkore:$signalrkoreVersion")

    // Optional: MessagePack hub protocol
    implementation("eu.lepicekmichal.signalrkore:signalrkore-msgpack:$signalrkoreVersion")
}
```

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

## How to use

Please see the [documentation](https://lepicekmichal.github.io/SignalRKore/).

## Acknowledgments

All functionality was possible to implement only thanks to [AzureSignalR ChatRoomLocal sample](https://github.com/aspnet/AzureSignalR-samples/tree/main/samples/ChatRoomLocal).

## License

SignalRKore is released under the [Apache 2.0 license](https://github.com/lepicekmichal/SignalRKore/blob/main/LICENSE.txt).

## Roadmap

- [ ] Reacting to stream invocation from server

<details>
<summary>Done</summary>

- [x] Readme
- [x] Documentation
- [x] Fix up ServerSentEvents' http client
- [x] Add logging
- [x] Error handling
- [x] Add tests
- [x] Implement streams
- [x] Extend to JVM
- [x] Extend to iOS
- [x] Implement transport fallback
- [x] Implement automatic reconnect
- [x] MessagePack protocol

</details>
