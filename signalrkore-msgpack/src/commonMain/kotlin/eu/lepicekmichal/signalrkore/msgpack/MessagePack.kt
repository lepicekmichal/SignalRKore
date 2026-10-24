package eu.lepicekmichal.signalrkore.msgpack

import kotlinx.serialization.BinaryFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule

/**
 * A kotlinx.serialization format for [MessagePack](https://msgpack.org), compatible with the MessagePack-CSharp
 * contractless resolver used by ASP.NET Core SignalR servers:
 *
 * - classes are maps keyed by their serial names, which are case sensitive, so use `@SerialName` to match .NET property names
 *   (classes encoded as arrays by `[Key(index)]` attributes are decoded by position),
 * - enums are strings with their serial names,
 * - [ByteArray] is the `bin` format,
 * - [kotlin.time.Instant] is the timestamp extension (type -1), the format of .NET `DateTime`,
 * - [Char] is an integer, numbers use the smallest format that fits them.
 *
 * Use [MessagePack.Default] or create a configured instance with the `MessagePack { }` builder function.
 */
sealed class MessagePack(
    val configuration: MessagePackConfiguration,
    override val serializersModule: SerializersModule,
) : BinaryFormat {

    /**
     * The default instance: unknown keys are ignored and default values are encoded.
     */
    companion object Default : MessagePack(MessagePackConfiguration(), EmptySerializersModule())

    override fun <T> encodeToByteArray(serializer: SerializationStrategy<T>, value: T): ByteArray =
        MessagePackWriter().also { encodeTo(it, serializer, value) }.toByteArray()

    override fun <T> decodeFromByteArray(deserializer: DeserializationStrategy<T>, bytes: ByteArray): T {
        val reader = MessagePackReader(bytes)
        val value = decodeFrom(reader, deserializer)
        if (reader.hasMore) throw SerializationException("Unexpected trailing data at position ${reader.position}")
        return value
    }

    internal fun <T> encodeTo(writer: MessagePackWriter, serializer: SerializationStrategy<T>, value: T) =
        MessagePackEncoder(this, writer).encodeSerializableValue(serializer, value)

    internal fun <T> decodeFrom(reader: MessagePackReader, deserializer: DeserializationStrategy<T>): T =
        MessagePackDecoder(this, reader).decodeSerializableValue(deserializer)
}

/**
 * Creates a [MessagePack] instance configured by [builderAction], starting from the configuration of [from].
 */
fun MessagePack(from: MessagePack = MessagePack.Default, builderAction: MessagePackBuilder.() -> Unit): MessagePack =
    MessagePackBuilder(from).apply(builderAction).run {
        MessagePackImpl(MessagePackConfiguration(ignoreUnknownKeys = ignoreUnknownKeys, encodeDefaults = encodeDefaults), serializersModule)
    }

/**
 * The builder of a [MessagePack] instance.
 */
class MessagePackBuilder internal constructor(from: MessagePack) {
    /**
     * Whether keys of a map that have no matching property in the decoded class are skipped. Defaults to `true`.
     */
    var ignoreUnknownKeys: Boolean = from.configuration.ignoreUnknownKeys

    /**
     * Whether properties equal to their default values are encoded. Defaults to `true`, as MessagePack-CSharp does.
     */
    var encodeDefaults: Boolean = from.configuration.encodeDefaults

    /**
     * The module with contextual and polymorphic serializers.
     */
    var serializersModule: SerializersModule = from.serializersModule
}

/**
 * The configuration of a [MessagePack] instance, set by [MessagePackBuilder].
 */
class MessagePackConfiguration internal constructor(
    val ignoreUnknownKeys: Boolean = true,
    val encodeDefaults: Boolean = true,
) {
    override fun toString(): String = "MessagePackConfiguration(ignoreUnknownKeys=$ignoreUnknownKeys, encodeDefaults=$encodeDefaults)"
}

private class MessagePackImpl(configuration: MessagePackConfiguration, serializersModule: SerializersModule) :
    MessagePack(configuration, serializersModule)
