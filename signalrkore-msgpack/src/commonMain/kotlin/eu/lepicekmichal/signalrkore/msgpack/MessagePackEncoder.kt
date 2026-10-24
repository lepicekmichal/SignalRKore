package eu.lepicekmichal.signalrkore.msgpack

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import kotlin.time.Instant

@OptIn(ExperimentalSerializationApi::class)
internal open class MessagePackEncoder(
    protected val messagePack: MessagePack,
    protected val writer: MessagePackWriter,
) : AbstractEncoder() {

    override val serializersModule: SerializersModule = messagePack.serializersModule

    override fun encodeNull() = writer.writeNil()
    override fun encodeBoolean(value: Boolean) = writer.writeBoolean(value)
    override fun encodeByte(value: Byte) = writer.writeLong(value.toLong())
    override fun encodeShort(value: Short) = writer.writeLong(value.toLong())
    override fun encodeInt(value: Int) = writer.writeLong(value.toLong())
    override fun encodeLong(value: Long) = writer.writeLong(value)
    override fun encodeFloat(value: Float) = writer.writeFloat(value)
    override fun encodeDouble(value: Double) = writer.writeDouble(value)
    override fun encodeChar(value: Char) = writer.writeLong(value.code.toLong())
    override fun encodeString(value: String) = writer.writeString(value)
    override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) = writer.writeString(enumDescriptor.getElementName(index))

    override fun encodeInline(descriptor: SerialDescriptor): Encoder =
        if (descriptor in unsignedDescriptors) UnsignedEncoder(writer, serializersModule) else this

    override fun <T> encodeSerializableValue(serializer: SerializationStrategy<T>, value: T) = when {
        serializer.descriptor.isByteArray -> writer.writeBinary(value as ByteArray)
        serializer.descriptor.isInstant -> (value as Instant).let { writer.writeTimestamp(it.epochSeconds, it.nanosecondsOfSecond) }
        else -> super.encodeSerializableValue(serializer, value)
    }

    override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder {
        if (descriptor.kind == StructureKind.MAP) writer.writeMapHeader(collectionSize) else writer.writeArrayHeader(collectionSize)
        return MessagePackEncoder(messagePack, writer)
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder = BufferedEncoder(messagePack, writer, descriptor)
}

@OptIn(ExperimentalSerializationApi::class)
private class BufferedEncoder(
    messagePack: MessagePack,
    private val parent: MessagePackWriter,
    descriptor: SerialDescriptor,
) : MessagePackEncoder(messagePack, MessagePackWriter()) {

    private val kind = descriptor.kind

    private var count = 0

    override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
        if (kind != StructureKind.LIST && kind != StructureKind.MAP) writer.writeString(descriptor.getElementName(index))
        count++
        return true
    }

    override fun shouldEncodeElementDefault(descriptor: SerialDescriptor, index: Int): Boolean = messagePack.configuration.encodeDefaults

    override fun endStructure(descriptor: SerialDescriptor) {
        when (kind) {
            StructureKind.LIST -> parent.writeArrayHeader(count)
            StructureKind.MAP -> parent.writeMapHeader(count / 2)
            else -> parent.writeMapHeader(count)
        }
        parent.writeBytes(writer)
    }
}

private class UnsignedEncoder(
    private val writer: MessagePackWriter,
    override val serializersModule: SerializersModule,
) : AbstractEncoder() {
    override fun encodeByte(value: Byte) = writer.writeUnsignedLong(value.toLong() and 0xFF)
    override fun encodeShort(value: Short) = writer.writeUnsignedLong(value.toLong() and 0xFFFF)
    override fun encodeInt(value: Int) = writer.writeUnsignedLong(value.toLong() and 0xFFFFFFFF)
    override fun encodeLong(value: Long) = writer.writeUnsignedLong(value)
}

internal val unsignedDescriptors: Set<SerialDescriptor> = setOf(
    UByte.serializer().descriptor,
    UShort.serializer().descriptor,
    UInt.serializer().descriptor,
    ULong.serializer().descriptor,
)

private val byteArrayDescriptor = ByteArraySerializer().descriptor

internal val SerialDescriptor.isByteArray: Boolean
    get() = serialName == byteArrayDescriptor.serialName

@OptIn(ExperimentalSerializationApi::class)
internal val SerialDescriptor.isInstant: Boolean
    get() = serialName == "kotlin.time.Instant" && kind == PrimitiveKind.STRING
