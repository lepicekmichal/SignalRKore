# SignalRKore

[![Maven Central](https://img.shields.io/maven-central/v/eu.lepicekmichal.signalrkore/signalrkore)](https://mvnrepository.com/artifact/eu.lepicekmichal.signalrkore)
[![Kotlin](https://img.shields.io/badge/kotlin-1.9.23-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![GitHub License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

![badge-android](http://img.shields.io/badge/platform-android-6EDB8D.svg?style=flat)
![badge-jvm](http://img.shields.io/badge/platform-jvm-DB413D.svg?style=flat)
![badge-ios](http://img.shields.io/badge/platform-ios-lightgray?style=flat)

SignalRKore is a client library connecting to ASP.NET Core server for real-time functionality. Enables server-side code to push content to
clients and vice-versa. Instantly.

## Why should you use **this** library

|                            | [Official client library](https://learn.microsoft.com/en-us/aspnet/core/signalr/java-client) |             SignalRKore             |
|:---------------------------|:--------------------------------------------------------------------------------------------:|:-----------------------------------:|
| Written in                 |                                             Java                                             |               Kotlin                |
| KMM / KMP                  |                                   :heavy_multiplication_x:                                   |          Android, JVM, iOS          |
| Network                    |                                         OkHttp only                                          |    Ktor (any engine pluggable*)     |
| Async                      |                                            RxJava                                            |             Coroutines              |
| Serialization              |                                   Gson (non-customizable)                                    | Kotlinx Serializable (customizable) |
| Streams                    |                                      :heavy_check_mark:                                      |         :heavy_check_mark:          |
| Transport fallback         |                                   :heavy_multiplication_x:                                   |      :heavy_multiplication_x:       |
| Automatic reconnect        |                                   :heavy_multiplication_x:                                   |         :heavy_check_mark:          |
| SSE                        |                                   :heavy_multiplication_x:                                   |        :heavy_check_mark: **        |
| Connection status          |                                   :heavy_multiplication_x:                                   |         :heavy_check_mark:          |
| Logging                    |                                            SLF4J                                             |          Custom interface           |
| MsgPack                    |                                      :heavy_check_mark:                                      |      :heavy_multiplication_x:       |
| Tested by time & community |                                      :heavy_check_mark:                                      |      :heavy_multiplication_x:       |

_* Except for SSE which uses only OkHttp at the moment_   
_** Only for android and jvm for now_

## Install

```kotlin
implementation("eu.lepicekmichal.signalrkore:signalrkore:${signalrkoreVersion}")
```

## Usage

### Create your hub connection

```kotlin
private val connection: HubConnection = HubConnectionBuilder.create("http://localhost:5000/chat")
```

### Start your connection

```kotlin
connection.start()
```

### Send to server

```kotlin
connection.send("broadcastMessage", "Michal", "Hello")
// or 
connection.invoke("broadcastMessage", "Michal", "Hello")
```

### Receive from server

```kotlin
connection.on("broadcastMessage", paramType1 = String::class, paramType2 = String::class).collect { (user, message) ->
    println("User $user is saying: $message")
}
```

### Receive from server and return result

```kotlin
connection.on("sneakAttack", paramType1 = Boolean::class, resultType = String::class) { surprise ->
    if (surprise) "Wow, you got me"
    else "I saw you waaay from over there"
}
```

maximizing shorthand style

```kotlin
connection.on("sneakAttack") { surprise: Boolean ->
    if (surprise) "Wow, you got me"
    else "I saw you waaay from over there"
}
```

### Don't forget to stop the connection

```kotlin
connection.stop()
```

## Send and Receive complex data types

```kotlin
// Serializable class
@Serializable
data class Message(
    val id: String,
    val author: String,
    val date: String,
    val text: String,
)

// Sending message
val message = Message(
    id = UUID.next(),
    author = "Michal",
    date = "2022-11-30T21:37:11Z",
    text = "Hello",
)
connection.send("broadcastMessage", message)

// Receiving messages
connection.on("broadcastMessage", Message::class).collect { (message) ->
    println(message.toString())
}
```

## We got streams too

```kotlin
// Receiving
connection.stream(
    method = "Counter",
    itemType = Int::class,
    arg1 = 10, // up to
    arg2 = 500, // delay
).collect {
    println("Countdown: ${10 - it}")
}

// Uploading
// send, invoke or stream methods
connection.send("UploadStream", flow<Int> {
    var data = 0
    while (data < 10) {
        emit(++data)
        delay(500)
    }
})

Receiving stream invocation and responding to it is in
```

## Keep up with connection status

```kotlin
connection.connectionState.collect {
    when (it) {
        HubConnectionState.CONNECTED -> println("Yay, we online")
        HubConnectionState.DISCONNECTED -> println("Shut off!")
        HubConnectionState.CONNECTING -> println("Almost there")
        HubConnectionState.RECONNECTING -> println("Down again")
    }
} 
```

## Connection configuration

```kotlin
HubConnectionBuilder
    .create(url) {
        transportEnum = ...
        httpClient = ...
        protocol = ...
        skipNegotiate = ...
        automaticReconnect = ...
        accessToken = ...
        handshakeResponseTimeout = ...
        headers = ...
        json = ...
        logger = ...
    }
```

### Supported transports

1. TransportEnum.All _(default, automatic choice based on availability)_
2. TransportEnum.WebSockets
3. TransportEnum.ServerSentEvents _(only for android and jvm, only okhttp engine)_
4. TransportEnum.LongPolling

### Add your own ktor http client

For example one with okhttp engine and its builder containing interceptors

```kotlin
httpClient = HttpClient(OkHttp) {
    engine {
        preconfigured = okHttpBuilder.build()
    }
}
```

But if you do opt-in to pass the http client, make sure it has WebSockets and other plugins installed

```kotlin
HttpClient {
    install(WebSockets)
    install(HttpTimeout)
    install(ContentNegotiation) { json() }
}
```

### You may pass your own HubProtocol

With custom parsing and encoding

```kotlin
class MyHubProtocol : HubProtocol {
    fun parseMessages(payload: ByteArray): List<HubMessage> {
        // your implementation
    }

    fun writeMessage(message: HubMessage): ByteArray {
        // your implementation
    }
}
```

### Logs are available

Just decide what to do with the message

```kotlin
logger = Logger { severity, msg, cause ->
    Napier.v("SignalRKore is saying: $ms")
}
```

### Do not forget your own instance of Json

If your kotlinx-serialization Json is customized or it has modules registered in it, then don't forget to pass it.

### Reconnect if server wants you to

SignalR Core server can send close message with allowReconnect property set to true.   
Automatic reconnect is not default behaviour, but can be simply set up.

```kotlin

import kotlin.random.Random

/** Default, reconnect turned off **/
automaticReconnect = AutomaticReconnect.Inactive

/**
 * Basic reconnect policy
 * waits 0、2、10 and 30 seconds before each attempt to reconnect
 * If all four attempts are unsucessful, reconnect is aborted
 */
automaticReconnect = AutomaticReconnect.Active

/**
 * Extra reconnect policy
 * Each attempt to reconnect is suspended with delay of exponential backoff time
 * In default settings
 *      initially waits 1 second, then 1.5 times more seconds each time
 *      at most 15 tries with highest delay of 60 seconds
 *      all can be adjusted
 */
automaticReconnect = AutomaticReconnect.exponentialBackoff()

/**
 * Custom reconnect policy
 * You can implement anything you find plausible
 */
automaticReconnect = AutomaticReconnect.Custom { previousRetryCount, elapsedTime ->
    // before each attempt wait random time 
    // but at most only the time that we are already trying to reconnect
    delay(Random.nextLong(elapsedTime.inWholeMilliseconds))
}
```

## TODO list

- [x] Readme
- [ ] Documentation
- [ ] Add example project
- [x] Fix up ServerSentEvents' http client
- [x] Add logging
- [ ] Error handling
- [ ] Add tests
- [x] Implement streams
- [x] Extend to JVM
- [x] Extend to iOS
- [ ] Implement transport fallback
- [x] Implement automatic reconnect
- [ ] Reacting to stream invocation from server

> All functionality was possible to implement only thanks
> to [AzureSignalR ChatRoomLocal sample](https://github.com/aspnet/AzureSignalR-samples/tree/main/samples/ChatRoomLocal)
