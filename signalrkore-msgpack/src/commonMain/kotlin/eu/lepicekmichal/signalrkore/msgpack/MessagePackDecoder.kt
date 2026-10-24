package eu.lepicekmichal.signalrkore.msgpack

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
internal open class MessagePackDecoder(
    protected val messagePack: MessagePack,
    protected val reader: MessagePackReader,
) : AbstractDecoder() {

    override val serializersModule: SerializersModule = messagePack.serializersModule

    override fun decodeNotNullMark(): Boolean = !reader.isNil
    override fun decodeNull(): Nothing? = reader.readNil().let { null }
    override fun decodeBoolean(): Boolean = reader.readBoolean()
    override fun decodeByte(): Byte = reader.readLong().checkRange(Byte.MIN_VALUE.toLong(), Byte.MAX_VALUE.toLong(), "Byte").toByte()
    override fun decodeShort(): Short = reader.readLong().checkRange(Short.MIN_VALUE.toLong(), Short.MAX_VALUE.toLong(), "Short").toShort()
    override fun decodeInt(): Int = reader.readLong().checkRange(Int.MIN_VALUE.toLong(), Int.MAX_VALUE.toLong(), "Int").toInt()
    override fun decodeLong(): Long = reader.readLong()
    override fun decodeFloat(): Float = reader.readFloat()
    override fun decodeDouble(): Double = reader.readDouble()
    override fun decodeString(): String = reader.readString()

    override fun decodeChar(): Char =
        if (reader.isString) reader.readString().singleOrNull() ?: throw SerializationException("Expected a single character string")
        else reader.readLong().checkRange(Char.MIN_VALUE.code.toLong(), Char.MAX_VALUE.code.toLong(), "Char").toInt().toChar()

    override fun decodeEnum(enumDescriptor: SerialDescriptor): Int {
        val name = reader.readString()
        val index = enumDescriptor.getElementIndex(name)
        if (index == CompositeDecoder.UNKNOWN_NAME) throw SerializationException("${enumDescriptor.serialName} does not contain element with name '$name'")
        return index
    }

    override fun decodeInline(descriptor: SerialDescriptor): Decoder =
        if (descriptor in unsignedDescriptors) UnsignedDecoder(reader, serializersModule) else this

    @Suppress("UNCHECKED_CAST")
    override fun <T> decodeSerializableValue(deserializer: DeserializationStrategy<T>): T = when {
        deserializer.descriptor.isByteArray && !reader.isArray -> reader.readBinary() as T
        deserializer.descriptor.isInstant && reader.isExtension -> reader.readTimestamp() as T
        else -> super.decodeSerializableValue(deserializer)
    }

    override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder = when (descriptor.kind) {
        StructureKind.LIST -> SequenceDecoder(messagePack, reader, reader.readArrayHeader(), descriptor)
        StructureKind.MAP -> SequenceDecoder(messagePack, reader, reader.readMapHeader() * 2, descriptor)
        StructureKind.CLASS, StructureKind.OBJECT, is PolymorphicKind ->
            if (reader.isArray) SequenceDecoder(messagePack, reader, reader.readArrayHeader(), descriptor)
            else ClassDecoder(messagePack, reader, reader.readMapHeader())

        else -> throw SerializationException("${descriptor.kind} ${descriptor.serialName} cannot be decoded from MessagePack")
    }

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int =
        throw SerializationException("${descriptor.serialName} is not a structure")

    private fun Long.checkRange(min: Long, max: Long, type: String): Long =
        if (this in min..max) this else throw SerializationException("Integer $this does not fit into $type")
}

@OptIn(ExperimentalSerializationApi::class)
private class SequenceDecoder(
    messagePack: MessagePack,
    reader: MessagePackReader,
    private val size: Int,
    descriptor: SerialDescriptor,
) : MessagePackDecoder(messagePack, reader) {

    private val positional = descriptor.kind != StructureKind.LIST && descriptor.kind != StructureKind.MAP

    private var index = 0

    override fun decodeCollectionSize(descriptor: SerialDescriptor): Int = if (descriptor.kind == StructureKind.MAP) size / 2 else size

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        while (index < size) {
            val current = index++
            if (!positional || current < descriptor.elementsCount) return current
            reader.skip()
        }
        return CompositeDecoder.DECODE_DONE
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        while (index < size) {
            index++
            reader.skip()
        }
    }
}

private class ClassDecoder(
    messagePack: MessagePack,
    reader: MessagePackReader,
    private val size: Int,
) : MessagePackDecoder(messagePack, reader) {

    private var index = 0

    override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        while (index < size) {
            index++
            if (!reader.isString) {
                reader.skip()
                reader.skip()
                continue
            }
            val name = reader.readString()
            val elementIndex = descriptor.getElementIndex(name)
            if (elementIndex != CompositeDecoder.UNKNOWN_NAME) return elementIndex
            if (!messagePack.configuration.ignoreUnknownKeys) throw SerializationException("Encountered an unknown key '$name' in ${descriptor.serialName}")
            reader.skip()
        }
        return CompositeDecoder.DECODE_DONE
    }

    override fun endStructure(descriptor: SerialDescriptor) {
        while (index < size) {
            index++
            reader.skip()
            reader.skip()
        }
    }
}

private class UnsignedDecoder(
    private val reader: MessagePackReader,
    override val serializersModule: SerializersModule,
) : AbstractDecoder() {
    override fun decodeByte(): Byte = reader.readUnsignedLong().toByte()
    override fun decodeShort(): Short = reader.readUnsignedLong().toShort()
    override fun decodeInt(): Int = reader.readUnsignedLong().toInt()
    override fun decodeLong(): Long = reader.readUnsignedLong()
    override fun decodeElementIndex(descriptor: SerialDescriptor): Int = CompositeDecoder.DECODE_DONE
}
