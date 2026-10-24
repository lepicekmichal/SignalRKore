package eu.lepicekmichal.signalrkore.msgpack

internal class MessagePackWriter {

    private var buffer = ByteArray(64)

    var size: Int = 0
        private set

    fun toByteArray(): ByteArray = buffer.copyOf(size)

    private fun ensureCapacity(additional: Int) {
        if (size + additional <= buffer.size) return
        buffer = buffer.copyOf(maxOf(buffer.size * 2, size + additional))
    }

    fun writeByte(value: Int) {
        ensureCapacity(1)
        buffer[size++] = value.toByte()
    }

    fun writeBytes(bytes: ByteArray, offset: Int = 0, length: Int = bytes.size - offset) {
        ensureCapacity(length)
        bytes.copyInto(buffer, size, offset, offset + length)
        size += length
    }

    fun writeBytes(other: MessagePackWriter) = writeBytes(other.buffer, 0, other.size)

    private fun writeBigEndian(value: Long, bytes: Int) {
        ensureCapacity(bytes)
        for (shift in (bytes - 1) * 8 downTo 0 step 8) buffer[size++] = (value ushr shift).toByte()
    }

    fun writeNil() = writeByte(NIL)

    fun writeBoolean(value: Boolean) = writeByte(if (value) TRUE else FALSE)

    fun writeLong(value: Long) = when {
        value >= 0 -> writeUnsignedLong(value)
        value >= -32 -> writeByte(value.toInt())
        value >= Byte.MIN_VALUE -> writeByte(INT8).also { writeBigEndian(value, 1) }
        value >= Short.MIN_VALUE -> writeByte(INT16).also { writeBigEndian(value, 2) }
        value >= Int.MIN_VALUE -> writeByte(INT32).also { writeBigEndian(value, 4) }
        else -> writeByte(INT64).also { writeBigEndian(value, 8) }
    }

    fun writeUnsignedLong(value: Long) = when {
        value in 0..0x7F -> writeByte(value.toInt())
        value in 0..0xFF -> writeByte(UINT8).also { writeBigEndian(value, 1) }
        value in 0..0xFFFF -> writeByte(UINT16).also { writeBigEndian(value, 2) }
        value in 0..0xFFFFFFFFL -> writeByte(UINT32).also { writeBigEndian(value, 4) }
        else -> writeByte(UINT64).also { writeBigEndian(value, 8) }
    }

    fun writeFloat(value: Float) {
        writeByte(FLOAT32)
        writeBigEndian(value.toRawBits().toLong(), 4)
    }

    fun writeDouble(value: Double) {
        writeByte(FLOAT64)
        writeBigEndian(value.toRawBits(), 8)
    }

    fun writeString(value: String) {
        val bytes = value.encodeToByteArray()
        when {
            bytes.size < 32 -> writeByte(FIXSTR or bytes.size)
            bytes.size <= 0xFF -> writeByte(STR8).also { writeBigEndian(bytes.size.toLong(), 1) }
            bytes.size <= 0xFFFF -> writeByte(STR16).also { writeBigEndian(bytes.size.toLong(), 2) }
            else -> writeByte(STR32).also { writeBigEndian(bytes.size.toLong(), 4) }
        }
        writeBytes(bytes)
    }

    fun writeBinary(value: ByteArray) {
        when {
            value.size <= 0xFF -> writeByte(BIN8).also { writeBigEndian(value.size.toLong(), 1) }
            value.size <= 0xFFFF -> writeByte(BIN16).also { writeBigEndian(value.size.toLong(), 2) }
            else -> writeByte(BIN32).also { writeBigEndian(value.size.toLong(), 4) }
        }
        writeBytes(value)
    }

    fun writeArrayHeader(size: Int) = when {
        size < 16 -> writeByte(FIXARRAY or size)
        size <= 0xFFFF -> writeByte(ARRAY16).also { writeBigEndian(size.toLong(), 2) }
        else -> writeByte(ARRAY32).also { writeBigEndian(size.toLong(), 4) }
    }

    fun writeMapHeader(size: Int) = when {
        size < 16 -> writeByte(FIXMAP or size)
        size <= 0xFFFF -> writeByte(MAP16).also { writeBigEndian(size.toLong(), 2) }
        else -> writeByte(MAP32).also { writeBigEndian(size.toLong(), 4) }
    }

    fun writeTimestamp(epochSeconds: Long, nanoseconds: Int) {
        if (epochSeconds ushr 34 == 0L) {
            val data = (nanoseconds.toLong() shl 34) or epochSeconds
            if (data and 0xFFFFFFFF.inv() == 0L) {
                writeByte(FIXEXT4)
                writeByte(TIMESTAMP_TYPE)
                writeBigEndian(data, 4)
            } else {
                writeByte(FIXEXT8)
                writeByte(TIMESTAMP_TYPE)
                writeBigEndian(data, 8)
            }
        } else {
            writeByte(EXT8)
            writeByte(12)
            writeByte(TIMESTAMP_TYPE)
            writeBigEndian(nanoseconds.toLong(), 4)
            writeBigEndian(epochSeconds, 8)
        }
    }

    fun writeVarInt(value: Int) {
        var remaining = value
        do {
            val current = remaining and 0x7F
            remaining = remaining ushr 7
            writeByte(if (remaining > 0) current or 0x80 else current)
        } while (remaining > 0)
    }
}

internal const val FIXMAP = 0x80
internal const val FIXARRAY = 0x90
internal const val FIXSTR = 0xA0
internal const val NIL = 0xC0
internal const val FALSE = 0xC2
internal const val TRUE = 0xC3
internal const val BIN8 = 0xC4
internal const val BIN16 = 0xC5
internal const val BIN32 = 0xC6
internal const val EXT8 = 0xC7
internal const val EXT16 = 0xC8
internal const val EXT32 = 0xC9
internal const val FLOAT32 = 0xCA
internal const val FLOAT64 = 0xCB
internal const val UINT8 = 0xCC
internal const val UINT16 = 0xCD
internal const val UINT32 = 0xCE
internal const val UINT64 = 0xCF
internal const val INT8 = 0xD0
internal const val INT16 = 0xD1
internal const val INT32 = 0xD2
internal const val INT64 = 0xD3
internal const val FIXEXT1 = 0xD4
internal const val FIXEXT2 = 0xD5
internal const val FIXEXT4 = 0xD6
internal const val FIXEXT8 = 0xD7
internal const val FIXEXT16 = 0xD8
internal const val STR8 = 0xD9
internal const val STR16 = 0xDA
internal const val STR32 = 0xDB
internal const val ARRAY16 = 0xDC
internal const val ARRAY32 = 0xDD
internal const val MAP16 = 0xDE
internal const val MAP32 = 0xDF
internal const val TIMESTAMP_TYPE = -1
