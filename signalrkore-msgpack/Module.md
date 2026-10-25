# Module SignalRKore MessagePack

The MessagePack hub protocol for SignalRKore. It ships `MessagePackHubProtocol` and `MessagePack`, a kotlinx.serialization
format compatible with the MessagePack-CSharp library used by ASP.NET Core SignalR servers.

```kotlin
val connection = HubConnectionBuilder.create("https://example.com/chathub") {
    protocol = MessagePackHubProtocol()
}
```
