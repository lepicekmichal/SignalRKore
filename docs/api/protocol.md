# Protocol

The `HubProtocol` interface defines the contract for protocols used by SignalRKore to serialize and deserialize messages sent between the client and the server. SignalRKore currently provides one implementation of this interface: `JsonHubProtocol`.

## HubProtocol Interface

The `HubProtocol` interface defines the following properties and methods:

```kotlin
interface HubProtocol {
    val name: String
    val version: Int
    val transferFormat: TransferFormat

    fun parseMessages(payload: ByteArray): List<HubMessage>
    fun writeMessage(message: HubMessage): ByteArray
}
```

## JsonHubProtocol

The `JsonHubProtocol` class is the default implementation of the `HubProtocol` interface. It uses JSON for serialization and deserialization of messages.

```kotlin
val connection = HubConnectionBuilder.create("https://example.com/chathub") {
    protocol = JsonHubProtocol()  // Default
}
```

## Custom Protocol

You can implement your own protocol by implementing the `HubProtocol` interface. This is an advanced use case and is not typically needed.

```kotlin
class MyHubProtocol : HubProtocol {
    override val name: String = "my-protocol"
    override val version: Int = 1
    override val transferFormat: TransferFormat = TransferFormat.Text

    override fun parseMessages(payload: ByteArray): List<HubMessage> {
        // Your implementation
    }

    override fun writeMessage(message: HubMessage): ByteArray {
        // Your implementation
    }
}

val connection = HubConnectionBuilder.create("https://example.com/chathub") {
    protocol = MyHubProtocol()
}
```

## HubMessage

The `HubMessage` sealed class represents messages sent between the client and the server. It has several subclasses for different types of messages:

- `HubMessage.Invocation`: Represents an invocation of a hub method
- `HubMessage.StreamInvocation`: Represents an invocation of a streaming hub method
- `HubMessage.StreamItem`: Represents an item in a stream
- `HubMessage.Completion`: Represents the completion of a hub method invocation
- `HubMessage.Ping`: Represents a ping message
- `HubMessage.Close`: Represents a close message
- `HubMessage.CancelInvocation`: Represents a cancellation of a streaming hub method invocation

## TransferFormat

The `TransferFormat` enum defines the format in which messages are transferred:

```kotlin
enum class TransferFormat {
    Text,
    Binary
}
```

## API Reference

### HubProtocol Interface

#### Properties

| Name | Type | Description |
|------|------|-------------|
| `name` | `String` | The name of the protocol |
| `version` | `Int` | The version of the protocol |
| `transferFormat` | `TransferFormat` | The format in which messages are transferred |

#### Methods

| Name | Parameters | Return Type | Description |
|------|------------|-------------|-------------|
| `parseMessages` | `payload: ByteArray` | `List<HubMessage>` | Parses messages from a byte array |
| `writeMessage` | `message: HubMessage` | `ByteArray` | Writes a message to a byte array |

### JsonHubProtocol

The `JsonHubProtocol` class is the default implementation of the `HubProtocol` interface. It uses JSON for serialization and deserialization of messages.

#### Properties

| Name | Type | Value | Description |
|------|------|-------|-------------|
| `name` | `String` | `"json"` | The name of the protocol |
| `version` | `Int` | `1` | The version of the protocol |
| `transferFormat` | `TransferFormat` | `TransferFormat.Text` | The format in which messages are transferred |

### TransferFormat

| Value | Description |
|-------|-------------|
| `Text` | Messages are transferred as text |
| `Binary` | Messages are transferred as binary data |

## Implementation Details

The `JsonHubProtocol` class uses Kotlinx Serialization for JSON serialization and deserialization. It handles the following message types:

- Invocation
- StreamInvocation
- StreamItem
- Completion
- Ping
- Close
- CancelInvocation

Each message type has a specific format defined by the SignalR protocol. The `JsonHubProtocol` class handles the serialization and deserialization of these messages according to the protocol specification.

For more details on the SignalR protocol, see the [official documentation](https://github.com/dotnet/aspnetcore/tree/main/src/SignalR/docs/specs).