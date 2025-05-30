# SignalRKore

[![Maven Central](https://img.shields.io/maven-central/v/eu.lepicekmichal.signalrkore/signalrkore)](https://mvnrepository.com/artifact/eu.lepicekmichal.signalrkore)
[![Kotlin](https://img.shields.io/badge/kotlin-2.1.10-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![GitHub License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)
[![Kotlin Weekly](https://img.shields.io/badge/Kotlin%20Weekly-%23416-purple?style=flat)](https://mailchi.mp/kotlinweekly/kotlin-weekly-416)

![badge-android](http://img.shields.io/badge/platform-android-6EDB8D.svg?style=flat)
![badge-jvm](http://img.shields.io/badge/platform-jvm-DB413D.svg?style=flat)
![badge-ios](http://img.shields.io/badge/platform-ios-lightgray?style=flat)

## Overview

SignalRKore is a Kotlin Multiplatform client library for ASP.NET Core SignalR. It enables real-time communication between clients and servers, allowing server-side code to push content to clients and vice-versa instantly.

## Why should you use **this** library

|                            | [Official client library](https://learn.microsoft.com/en-us/aspnet/core/signalr/java-client) |             SignalRKore             |
|:---------------------------|:--------------------------------------------------------------------------------------------:|:-----------------------------------:|
| Written in                 |                                             Java                                             |               Kotlin                |
| KMM / KMP                  |                                   :heavy_multiplication_x:                                   |          Android, JVM, iOS          |
| Network                    |                                         OkHttp only                                          |                Ktor                 |
| Async                      |                                            RxJava                                            |             Coroutines              |
| Serialization              |                                   Gson (non-customizable)                                    | Kotlinx Serializable (customizable) |
| Streams                    |                                      :heavy_check_mark:                                      |         :heavy_check_mark:          |
| Transport fallback         |                                   :heavy_multiplication_x:                                   |      :heavy_multiplication_x:       |
| Automatic reconnect        |                                   :heavy_multiplication_x:                                   |         :heavy_check_mark:          |
| SSE                        |                                   :heavy_multiplication_x:                                   |         :heavy_check_mark:          |
| Connection status          |                                   :heavy_multiplication_x:                                   |         :heavy_check_mark:          |
| Logging                    |                                            SLF4J                                             |          Custom interface           |
| MsgPack                    |                                      :heavy_check_mark:                                      |      :heavy_multiplication_x:       |
| Tested by time & community |                                      :heavy_check_mark:                                      |      :heavy_multiplication_x:       |

## Quick Example

```kotlin
// Create a connection
val connection = HubConnectionBuilder.create("http://localhost:5000/chat")

// Start the connection
connection.start()

// Send a message to the server
connection.send("broadcastMessage", "User", "Hello, SignalR!")

// Receive messages from the server
connection.on("broadcastMessage", String::class, String::class).collect { (user, message) ->
    println("$user says: $message")
}

// Don't forget to stop the connection when done
connection.stop()
```

## How to use

Please see the [documentation](https://lepicekmichal.github.io/SignalRKore/).

## Acknowledgments

All functionality was possible to implement only thanks to [AzureSignalR ChatRoomLocal sample](https://github.
com/aspnet/AzureSignalR-samples/tree/main/samples/ChatRoomLocal).

## License

SignalRKore is released under the [Apache 2.0 license](https://github.com/lepicekmichal/SignalRKore/blob/main/LICENSE.txt).

## TODO list

- [x] Readme
- [x] Documentation
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

## Documentation

The documentation is available on GitHub Pages. To view it:

1. **Trigger the GitHub Actions workflow to create the gh-pages branch:**
   - The gh-pages branch is created automatically by the GitHub Actions workflow
   - The workflow runs when changes are pushed to the main branch
   - You can check the status of the workflow in the "Actions" tab of your repository
   - If the workflow hasn't run yet, you can trigger it manually by:
     - Making a small change to any file and pushing it to the main branch, or
     - Going to the "Actions" tab, selecting the "Deploy MkDocs to GitHub Pages" workflow, and clicking "Run workflow"

2. **Enable GitHub Pages for your repository:**
   - After the workflow has run successfully and created the gh-pages branch
   - Go to your repository on GitHub
   - Click on "Settings"
   - Scroll down to the "GitHub Pages" section
   - Under "Source", select "Deploy from a branch"
   - Under "Branch", select "gh-pages" and "/ (root)" folder
   - Click "Save"

3. **Access the documentation:**
   - Once GitHub Pages is enabled, the documentation will be available at:
   - `https://[your-github-username].github.io/SignalRKore/`
   - For example: `https://lepicekmichal.github.io/SignalRKore/`
