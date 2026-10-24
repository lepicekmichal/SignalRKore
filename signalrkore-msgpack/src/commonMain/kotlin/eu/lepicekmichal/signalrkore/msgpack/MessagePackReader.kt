package eu.lepicekmichal.signalrkore.msgpack

import kotlinx.serialization.SerializationException
import kotlin.time.Instant

internal class MessagePackReader(
    private val bytes: ByteArray,
    var position: Int = 0,
    private val end: Int = bytes.size,
) {

    val hasMore: Boolean
        get() = position < end

    fun peek(): Int {
        if (position >= end) throw SerializationException("Unexpected end of MessagePack data")
        return bytes[position].toInt() and 0xFF
    }

    private fun readByte(): Int = peek().also { position++ }

    private inline fun <T> afterType(block: () -> T): T {
        position++
        return block()
    }

    private fun readBigEndian(count: Int): Long {
        if (end - position < count) throw SerializationException("Unexpected end of MessagePack data")
        var value = 0L
        repeat(count) { value = (value shl 8) or (bytes[position++].toLong() and 0xFF) }
        return value
    }

    private fun readLength(count: Int): Int = readBigEndian(count).also { ensureRemaining(it) }.toInt()

    private fun ensureRemaining(length: Long) {
        if (length > end - position) throw SerializationException("MessagePack length $length exceeds the remaining ${end - position} bytes")
    }

    val isNil: Boolean
        get() = peek() == NIL

    val isString: Boolean
        get() = peek().let { it and 0xE0 == FIXSTR || it == STR8 || it == STR16 || it == STR32 }

    val isArray: Boolean
        get() = peek().let { it and 0xF0 == FIXARRAY || it == ARRAY16 || it == ARRAY32 }

    val isMap: Boolean
        get() = peek().let { it and 0xF0 == FIXMAP || it == MAP16 || it == MAP32 }

    val isExtension: Boolean
        get() = peek().let { it in FIXEXT1..FIXEXT16 || it in EXT8..EXT32 }

    fun readNil() {
        if (peek() != NIL) unexpected("nil")
        position++
    }

    fun readBoolean(): Boolean = when (peek()) {
        TRUE -> true.also { position++ }
        FALSE -> false.also { position++ }
        else -> unexpected("boolean")
    }

    fun readLong(): Long = when (peek()) {
        in 0x00..0x7F -> readByte().toLong()
        in 0xE0..0xFF -> readByte().toByte().toLong()
        UINT8 -> afterType { readBigEndian(1) }
        UINT16 -> afterType { readBigEndian(2) }
        UINT32 -> afterType { readBigEndian(4) }
        UINT64 -> afterType { readBigEndian(8) }.also { if (it < 0) throw SerializationException("Integer ${it.toULong()} does not fit into Long") }
        INT8 -> afterType { readBigEndian(1).toByte().toLong() }
        INT16 -> afterType { readBigEndian(2).toShort().toLong() }
        INT32 -> afterType { readBigEndian(4).toInt().toLong() }
        INT64 -> afterType { readBigEndian(8) }
        else -> unexpected("integer")
    }

    fun readUnsignedLong(): Long = if (peek() == UINT64) afterType { readBigEndian(8) } else readLong()

    fun readDouble(): Double = when (peek()) {
        FLOAT32 -> afterType { Float.fromBits(readBigEndian(4).toInt()).toDouble() }
        FLOAT64 -> afterType { Double.fromBits(readBigEndian(8)) }
        else -> if (isInteger(peek())) readLong().toDouble() else unexpected("float")
    }

    fun readFloat(): Float = if (peek() == FLOAT32) afterType { Float.fromBits(readBigEndian(4).toInt()) } else readDouble().toFloat()

    fun readString(): String {
        val type = peek()
        val length = when {
            type and 0xE0 == FIXSTR -> afterType { (type and 0x1F).also { ensureRemaining(it.toLong()) } }
            type == STR8 -> afterType { readLength(1) }
            type == STR16 -> afterType { readLength(2) }
            type == STR32 -> afterType { readLength(4) }
            else -> unexpected("string")
        }
        return bytes.decodeToString(position, position + length).also { position += length }
    }

    fun readNullableString(): String? = if (isNil) null.also { position++ } else readString()

    fun readBinary(): ByteArray {
        val length = when (peek()) {
            BIN8 -> afterType { readLength(1) }
            BIN16 -> afterType { readLength(2) }
            BIN32 -> afterType { readLength(4) }
            else -> unexpected("binary")
        }
        return bytes.copyOfRange(position, position + length).also { position += length }
    }

    fun readArrayHeader(): Int {
        val type = peek()
        return when {
            type and 0xF0 == FIXARRAY -> afterType { type and 0x0F }
            type == ARRAY16 -> afterType { readBigEndian(2).toInt() }
            type == ARRAY32 -> afterType { readBigEndian(4) }.toCollectionSize()
            else -> unexpected("array")
        }
    }

    fun readMapHeader(): Int {
        val type = peek()
        return when {
            type and 0xF0 == FIXMAP -> afterType { type and 0x0F }
            type == MAP16 -> afterType { readBigEndian(2).toInt() }
            type == MAP32 -> afterType { readBigEndian(4) }.toCollectionSize()
            else -> unexpected("map")
        }
    }

    private fun Long.toCollectionSize(): Int =
        if (this > end - position) throw SerializationException("MessagePack collection size $this exceeds the remaining ${end - position} bytes")
        else toInt()

    private fun readExtensionHeader(): Pair<Int, Int> {
        val length = when (peek()) {
            FIXEXT1 -> afterType { 1 }
            FIXEXT2 -> afterType { 2 }
            FIXEXT4 -> afterType { 4 }
            FIXEXT8 -> afterType { 8 }
            FIXEXT16 -> afterType { 16 }
            EXT8 -> afterType { readBigEndian(1).toInt() }
            EXT16 -> afterType { readBigEndian(2).toInt() }
            EXT32 -> afterType { readBigEndian(4).toInt() }
            else -> unexpected("extension")
        }
        val type = readByte().toByte().toInt()
        ensureRemaining(length.toLong())
        return type to length
    }

    fun readTimestamp(): Instant {
        val (type, length) = readExtensionHeader()
        if (type != TIMESTAMP_TYPE) throw SerializationException("Expected a timestamp extension but found extension type $type")
        return when (length) {
            4 -> Instant.fromEpochSeconds(readBigEndian(4))
            8 -> readBigEndian(8).let { Instant.fromEpochSeconds(it and 0x3FFFFFFFFL, it ushr 34) }
            12 -> readBigEndian(4).let { nanoseconds -> Instant.fromEpochSeconds(readBigEndian(8), nanoseconds) }
            else -> throw SerializationException("Invalid timestamp extension length $length")
        }
    }

    fun skip() {
        val type = peek()
        when {
            type <= 0x7F || type >= 0xE0 || type == NIL || type == TRUE || type == FALSE -> position++
            type and 0xF0 == FIXMAP || type == MAP16 || type == MAP32 -> repeat(readMapHeader() * 2) { skip() }
            type and 0xF0 == FIXARRAY || type == ARRAY16 || type == ARRAY32 -> repeat(readArrayHeader()) { skip() }
            isString -> readString()
            type == BIN8 || type == BIN16 || type == BIN32 -> readBinary()
            isExtension -> readExtensionHeader().let { (_, length) -> position += length }
            isInteger(type) -> readUnsignedLong()
            type == FLOAT32 -> position += 5
            type == FLOAT64 -> position += 9
            else -> throw SerializationException("Invalid MessagePack type 0x${type.toString(16)}")
        }
        if (position > end) throw SerializationException("Unexpected end of MessagePack data")
    }

    fun readRaw(): ByteArray {
        val start = position
        skip()
        return bytes.copyOfRange(start, position)
    }

    fun render(): String = buildString { renderValue(this) }

    private fun renderValue(builder: StringBuilder) {
        val type = peek()
        when {
            type == NIL -> builder.append("null").also { position++ }
            type == TRUE || type == FALSE -> builder.append(readBoolean())
            type == UINT64 -> builder.append(readUnsignedLong().toULong())
            isInteger(type) -> builder.append(readLong())
            type == FLOAT32 || type == FLOAT64 -> builder.append(readDouble())
            isString -> builder.append('"').append(readString().replace("\\", "\\\\").replace("\"", "\\\"")).append('"')
            type == BIN8 || type == BIN16 || type == BIN32 -> builder.append("bin(").append(readBinary().toHex()).append(')')
            isArray -> {
                builder.append('[')
                repeat(readArrayHeader()) { index ->
                    if (index > 0) builder.append(',')
                    renderValue(builder)
                }
                builder.append(']')
            }

            isMap -> {
                builder.append('{')
                repeat(readMapHeader()) { index ->
                    if (index > 0) builder.append(',')
                    renderValue(builder)
                    builder.append(':')
                    renderValue(builder)
                }
                builder.append('}')
            }

            else -> {
                val start = position
                val (extensionType, length) = readExtensionHeader()
                if (extensionType == TIMESTAMP_TYPE) {
                    position = start
                    builder.append(readTimestamp())
                } else {
                    builder.append("ext(").append(extensionType).append(',').append(bytes.copyOfRange(position, position + length).toHex()).append(')')
                    position += length
                }
            }
        }
    }

    private fun isInteger(type: Int): Boolean = type <= 0x7F || type >= 0xE0 || type in UINT8..INT64

    private fun unexpected(expected: String): Nothing =
        throw SerializationException("Expected MessagePack $expected but found 0x${peek().toString(16).padStart(2, '0')} at position $position")
}

internal fun ByteArray.toHex(): String = joinToString("") { (it.toInt() and 0xFF).toString(16).padStart(2, '0') }
