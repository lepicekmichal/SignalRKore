# SignalR Kore

[![Maven Central](https://img.shields.io/maven-central/v/eu.lepicekmichal.signalrkore/signalrkore)](https://mvnrepository.com/artifact/eu.lepicekmichal.signalrkore)
[![Kotlin](https://img.shields.io/badge/kotlin-1.7.20-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![GitHub License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

SignalR Kore is a library written in kotlin and coroutines connecting to ASP.NET Core server for real-time functionality brought to apps.
Enables server-side code to push content to clients and vice-versa.Instantly.

## KMP - why use **this** library

* kotlin
* coroutines
* kotlinx-serializable
* ktor

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
connection.on("broadcastMessage", String::class, String::class) { user, message ->
    println("User $user is saying: $message")
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
connection.on("broadcastMessage", Message::class) { message ->
    println(message.toString())
}
```

## Keep up with connection status

```kotlin
connection.connectionState.collect {
    when (it) {
        is CONNECTED -> prinln("Yay, we online")
        is DISCONNECTED -> prinln("Shut off!")
        is CONNECTING -> prinln("Almost there")
        is RECONNECTING -> prinln("Down again")
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
3. TransportEnum.ServerSentEvents _(only for android, only okhttp engine)_
4. TransportEnum.LongPolling

### Add your own ktor http client

For example with okhttp engine and its builder containing interceptors

```kotlin
httpClient = HttpClient(OkHttp) {
    engine {
        preconfigured = okHttpBuilder.build()
    }
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
logger = Logger {
    Napier.v("SignalR Kore is saying: $it")
}
```

### Do not forget your own instance of Json

If your kotlinx-serialization Json is cusomized or it has modules registered in it, then don't forget to pass it.

## TODO list

- [x] Readme
- [ ] Documentation
- [ ] Add example project
- [ ] Fix up ServerSentEvents' http client
- [x] Add logging
- [ ] Error handling
- [ ] Add tests
- [ ] Implement streams