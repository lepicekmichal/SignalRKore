# Protocol

The `HubProtocol` interface defines the contract for protocols used by SignalRKore to serialize and deserialize messages sent between the client and the server. SignalRKore provides two implementations of this interface:

- `JsonHubProtocol`, the default, part of the `signalrkore` artifact
- `MessagePackHubProtocol`, a binary protocol in the separate `signalrkore-msgpack` artifact

Like in the official SignalR clients, the protocol owns the serialization of payloads (arguments, stream items and results): the same `@Serializable` models are written as JSON by `JsonHubProtocol` and as MessagePack by `MessagePackHubProtocol`.

## HubProtocol Interface

The `HubProtocol` interface defines the following properties and methods:

```kotlin
interface HubProtocol {
    val name: String
    val version: Int
    val transferFormat: TransferFormat

    fun parseMessages(payload: ByteArray, logger: Logger): List<HubMessage>
    fun writeMessage(message: HubMessage): ByteArray
}
```

Protocols don't hold a logger. The connection passes its [logger](../user-guide/configuration.md#logging) to `parseMessages`, so a protocol can log decoded messages and the ones it had to skip.

## JsonHubProtocol

The `JsonHubProtocol` class is the default implementation of the `HubProtocol` interface. It uses JSON for serialization and deserialization of messages.

```kotlin
val connection = HubConnectionBuilder.create("https://example.com/chathub") {
    protocol = JsonHubProtocol()  // Default
}
```

By default, payloads are decoded with a `Json` that ignores unknown keys, like the official SignalR clients do, so server objects may have more properties than your models. To encode payloads with your own `Json` instance, pass it to the protocol. It replaces the default, so set `ignoreUnknownKeys = true` in it if you still want that:

```kotlin
protocol = JsonHubProtocol(json = Json { ignoreUnknownKeys = true; isLenient = true })
```

## MessagePackHubProtocol

The `MessagePackHubProtocol` class implements the [MessagePack encoding](https://github.com/dotnet/aspnetcore/blob/main/src/SignalR/docs/specs/HubProtocol.md#messagepack-msgpack-encoding) of the SignalR hub protocol. Messages are smaller and faster to process than JSON, and binary data is sent as raw bytes instead of Base64 strings.

It lives in a separate artifact, so the core library does not depend on any MessagePack code:

```kotlin
dependencies {
    implementation("eu.lepicekmichal.signalrkore:signalrkore:${signalrkoreVersion}")
    implementation("eu.lepicekmichal.signalrkore:signalrkore-msgpack:${signalrkoreVersion}")
}
```

```kotlin
val connection = HubConnectionBuilder.create("https://example.com/chathub") {
    protocol = MessagePackHubProtocol()
}
```

The server has to enable the protocol too, with `AddMessagePackProtocol()` on ASP.NET Core:

```csharp
builder.Services.AddSignalR().AddMessagePackProtocol();
```

MessagePack is a binary format, so it cannot be sent over Server-Sent Events. With this protocol the connection only uses WebSockets and Long Polling, and it skips any transport whose negotiated transfer formats don't include `Binary`.

### Serialization

`MessagePackHubProtocol` encodes payloads with `MessagePack`, a kotlinx.serialization format written for SignalRKore. It follows the conventions of [MessagePack-CSharp](https://github.com/MessagePack-CSharp/MessagePack-CSharp), the library used by ASP.NET Core:

| Kotlin                         | MessagePack                                | .NET                      |
|--------------------------------|--------------------------------------------|---------------------------|
| `@Serializable` class          | map keyed by property name                 | class or record           |
| `enum class`                   | string with the constant's name            | `enum`                    |
| `ByteArray`                    | `bin`                                      | `byte[]`                  |
| `kotlin.time.Instant`          | timestamp extension (type -1)              | `DateTime` (UTC)          |
| `List`, `Set`, arrays          | array                                      | arrays, lists             |
| `Map`                          | map                                        | `Dictionary`              |
| `Int`, `Long`, unsigned types  | the smallest integer format that fits      | numeric types             |
| `Float`, `Double`              | `float 32`, `float 64`                     | `float`, `double`         |
| `Char`                         | integer                                    | `char`                    |

!!! warning "Property names are case sensitive"
    Unlike the JSON protocol, the MessagePack protocol of ASP.NET Core matches property names exactly. .NET properties are usually PascalCase, so name your Kotlin properties the same way or map them with `@SerialName`:

    ```kotlin
    @Serializable
    data class Message(
        @SerialName("User") val user: String,
        @SerialName("Text") val text: String,
        @SerialName("SentAt") val sentAt: Instant,
    )
    ```

Classes that .NET encodes as arrays (with `[MessagePackObject]` and `[Key(index)]` attributes) are decoded by position. `JsonElement` payloads only work with `JsonHubProtocol`.

### Configuration

Pass a configured `MessagePack` instance to change how payloads are encoded:

```kotlin
protocol = MessagePackHubProtocol(
    messagePack = MessagePack {
        ignoreUnknownKeys = false  // default true: keys without a matching property are skipped
        encodeDefaults = false     // default true: properties equal to their default values are written
        serializersModule = SerializersModule {
            contextual(UUID::class, UUIDSerializer)
        }
    },
)
```

`MessagePack` is a regular kotlinx.serialization `BinaryFormat`, so you can also use it on its own with `encodeToByteArray` and `decodeFromByteArray`.

## Custom Protocol

You can implement your own protocol by implementing the `HubProtocol` interface. This is an advanced use case and is not typically needed.

```kotlin
class MyHubProtocol : HubProtocol {
    override val name: String = "my-protocol"
    override val version: Int = 1
    override val transferFormat: TransferFormat = TransferFormat.Text

    override fun parseMessages(payload: ByteArray, logger: Logger): List<HubMessage> {
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

A protocol encodes payloads and decodes them later, once the receiver asks for a type:

- When writing a message, each payload is a `HubValue.Encodable` holding the value and its `SerializationStrategy`.
- When parsing a message, the protocol returns payloads as its own implementation of `HubValue.Decodable`, which keeps the raw value and decodes it with the `DeserializationStrategy` passed to `decode`.

## HubMessage

The `HubMessage` sealed class represents messages sent between the client and the server. It has several subclasses for different types of messages:

- `HubMessage.Invocation`: Represents an invocation of a hub method
- `HubMessage.StreamInvocation`: Represents an invocation of a streaming hub method
- `HubMessage.StreamItem`: Represents an item in a stream
- `HubMessage.Completion`: Represents the completion of a hub method invocation
- `HubMessage.Ping`: Represents a ping message
- `HubMessage.Close`: Represents a close message
- `HubMessage.CancelInvocation`: Represents a cancellation of a streaming hub method invocation

Arguments, stream items and results are `HubValue`s.

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
| `parseMessages` | `payload: ByteArray`, `logger: Logger` | `List<HubMessage>` | Parses messages from a byte array |
| `writeMessage` | `message: HubMessage` | `ByteArray` | Writes a message to a byte array |

### JsonHubProtocol

#### Constructor

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `json` | `Json` | `Json { ignoreUnknownKeys = true }` | The Json instance for payloads |

#### Properties

| Name | Type | Value | Description |
|------|------|-------|-------------|
| `name` | `String` | `"json"` | The name of the protocol |
| `version` | `Int` | `1` | The version of the protocol |
| `transferFormat` | `TransferFormat` | `TransferFormat.Text` | The format in which messages are transferred |

### MessagePackHubProtocol

#### Constructor

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `messagePack` | `MessagePack` | `MessagePack` | The MessagePack format for payloads |

#### Properties

| Name | Type | Value | Description |
|------|------|-------|-------------|
| `name` | `String` | `"messagepack"` | The name of the protocol |
| `version` | `Int` | `1` | The version of the protocol |
| `transferFormat` | `TransferFormat` | `TransferFormat.Binary` | The format in which messages are transferred |

### HubValue

| Type | Description |
|------|-------------|
| `HubValue.Encodable` | An outgoing payload: `value` and its `serializer` |
| `HubValue.Decodable` | An incoming payload, decoded by `decode(deserializer)` |

### TransferFormat

| Value | Description |
|-------|-------------|
| `Text` | Messages are transferred as text |
| `Binary` | Messages are transferred as binary data |

## Implementation Details

Both protocols handle the following message types:

- Invocation
- StreamInvocation
- StreamItem
- Completion
- Ping
- Close
- CancelInvocation

`JsonHubProtocol` separates messages with the record separator character (`0x1E`). `MessagePackHubProtocol` encodes each message as a MessagePack array prefixed with its length as a VarInt. Message headers sent by the server are ignored. Messages that cannot be decoded are logged as errors and skipped.

For more details on the SignalR protocol, see the [official documentation](https://github.com/dotnet/aspnetcore/tree/main/src/SignalR/docs/specs).
