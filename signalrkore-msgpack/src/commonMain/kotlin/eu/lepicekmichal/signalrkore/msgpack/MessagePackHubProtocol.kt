package eu.lepicekmichal.signalrkore.msgpack

import eu.lepicekmichal.signalrkore.HubMessage
import eu.lepicekmichal.signalrkore.HubMessageType
import eu.lepicekmichal.signalrkore.HubProtocol
import eu.lepicekmichal.signalrkore.HubValue
import eu.lepicekmichal.signalrkore.Logger
import eu.lepicekmichal.signalrkore.TransferFormat
import kotlinx.serialization.DeserializationStrategy

/**
 * The MessagePack hub protocol (`messagepack`, version 1), a binary protocol producing smaller messages than JSON.
 *
 * Payloads are encoded by the [messagePack] format, which follows the conventions of the MessagePack-CSharp library used by
 * ASP.NET Core servers. The server has to enable the protocol with `AddMessagePackProtocol()`.
 *
 * Binary data cannot be sent over server-sent events, so the connection uses WebSockets or long polling with this protocol.
 *
 * @param logger The logger for decoded and encoded messages
 * @param messagePack The MessagePack format that encodes and decodes the payloads of messages (arguments, stream items and results)
 */
class MessagePackHubProtocol(
    private val logger: Logger,
    private val messagePack: MessagePack = MessagePack,
) : HubProtocol {

    override val name: String = PROTOCOL_NAME
    override val version: Int = PROTOCOL_VERSION
    override val transferFormat: TransferFormat = TransferFormat.Binary

    override fun parseMessages(payload: ByteArray): List<HubMessage> {
        val messages = mutableListOf<HubMessage>()
        var position = 0
        while (position < payload.size) {
            val (length, headerLength) = readLengthHeader(payload, position)
            val start = position + headerLength
            if (payload.size - start < length) {
                throw RuntimeException("MessagePack message was length ${payload.size - start} but claimed to be length $length.")
            }
            val end = start + length
            try {
                logger.log(Logger.Severity.INFO, { "Decoding message: ${payload.render(start, end)}" }, null)
                messages += MessagePackReader(payload, start, end).readHubMessage()
            } catch (ex: Exception) {
                logger.log(Logger.Severity.ERROR, { "Failed to decode message: ${payload.copyOfRange(start, end).toHex()}" }, ex)
            }
            position = end
        }
        return messages
    }

    override fun writeMessage(message: HubMessage): ByteArray {
        val body = MessagePackWriter().apply { writeHubMessage(message) }
        logger.log(Logger.Severity.INFO, { "Encoded message: ${body.toByteArray().let { it.render(0, it.size) }}" }, null)
        return MessagePackWriter().apply {
            writeVarInt(body.size)
            writeBytes(body)
        }.toByteArray()
    }

    private fun ByteArray.render(start: Int, end: Int): String =
        try {
            MessagePackReader(this, start, end).render()
        } catch (ex: Exception) {
            copyOfRange(start, end).toHex()
        }

    private fun readLengthHeader(payload: ByteArray, position: Int): Pair<Int, Int> {
        var length = 0L
        var count = 0
        var current: Int
        do {
            if (position + count >= payload.size) throw RuntimeException("The length header was incomplete")
            current = payload[position + count].toInt() and 0xFF
            length = length or ((current and 0x7F).toLong() shl (count * 7))
            count++
        } while (count < MAX_LENGTH_HEADER_SIZE && current and 0x80 != 0)

        if (current and 0x80 != 0 || length > Int.MAX_VALUE) throw RuntimeException("Messages over 2GB in size are not supported")
        return length.toInt() to count
    }

    private fun MessagePackReader.readHubMessage(): HubMessage {
        val itemCount = readArrayHeader()
        return when (val type = readLong().toInt()) {
            HubMessageType.INVOCATION.value -> {
                skipHeaders()
                val invocationId = readNullableString()?.takeIf { it.isNotEmpty() }
                val target = readString()
                val arguments = readArguments()
                val streamIds = if (itemCount > 5) readStreamIds() else null
                if (invocationId == null) HubMessage.Invocation.NonBlocking(target = target, arguments = arguments, streamIds = streamIds)
                else HubMessage.Invocation.Blocking(target = target, arguments = arguments, invocationId = invocationId, streamIds = streamIds)
            }

            HubMessageType.STREAM_INVOCATION.value -> {
                skipHeaders()
                val invocationId = readString()
                val target = readString()
                val arguments = readArguments()
                val streamIds = if (itemCount > 5) readStreamIds() else null
                HubMessage.StreamInvocation(target = target, arguments = arguments, invocationId = invocationId, streamIds = streamIds)
            }

            HubMessageType.STREAM_ITEM.value -> {
                skipHeaders()
                HubMessage.StreamItem(invocationId = readString(), item = readValue())
            }

            HubMessageType.COMPLETION.value -> {
                skipHeaders()
                val invocationId = readString()
                when (val resultKind = readLong().toInt()) {
                    ERROR_RESULT -> HubMessage.Completion.Error(invocationId = invocationId, error = readString())
                    VOID_RESULT -> HubMessage.Completion.Simple(invocationId = invocationId)
                    NON_VOID_RESULT ->
                        if (isNil) HubMessage.Completion.Simple(invocationId = invocationId).also { readNil() }
                        else HubMessage.Completion.Resulted(invocationId = invocationId, result = readValue())

                    else -> throw RuntimeException("Invalid invocation result kind $resultKind.")
                }
            }

            HubMessageType.CANCEL_INVOCATION.value -> {
                skipHeaders()
                HubMessage.CancelInvocation(invocationId = readString())
            }

            HubMessageType.PING.value -> HubMessage.Ping()
            HubMessageType.CLOSE.value -> {
                val error = readNullableString()
                HubMessage.Close(allowReconnect = if (itemCount > 2) readBoolean() else false, error = error)
            }

            else -> error("Unknown type ($type) when deserializing SignalR structure")
        }
    }

    private fun MessagePackReader.skipHeaders() = repeat(readMapHeader() * 2) { skip() }

    private fun MessagePackReader.readArguments(): List<HubValue> = List(readArrayHeader()) { readValue() }

    private fun MessagePackReader.readStreamIds(): List<String>? = List(readArrayHeader()) { readString() }.takeIf { it.isNotEmpty() }

    private fun MessagePackReader.readValue(): HubValue = MessagePackValue(readRaw(), messagePack)

    private fun MessagePackWriter.writeHubMessage(message: HubMessage) {
        when (message) {
            is HubMessage.Invocation.Blocking -> writeInvocation(message.type, message.invocationId, message.target, message.arguments, message.streamIds)
            is HubMessage.Invocation.NonBlocking -> writeInvocation(message.type, null, message.target, message.arguments, message.streamIds)
            is HubMessage.StreamInvocation -> writeInvocation(message.type, message.invocationId, message.target, message.arguments, message.streamIds)
            is HubMessage.StreamItem -> {
                writeArrayHeader(4)
                writeLong(message.type.toLong())
                writeMapHeader(0)
                writeString(message.invocationId)
                writeValue(message.item)
            }

            is HubMessage.Completion -> {
                writeArrayHeader(if (message is HubMessage.Completion.Simple) 4 else 5)
                writeLong(message.type.toLong())
                writeMapHeader(0)
                writeString(message.invocationId)
                when (message) {
                    is HubMessage.Completion.Error -> writeLong(ERROR_RESULT.toLong()).also { writeString(message.error) }
                    is HubMessage.Completion.Simple -> writeLong(VOID_RESULT.toLong())
                    is HubMessage.Completion.Resulted -> writeLong(NON_VOID_RESULT.toLong()).also { writeValue(message.result) }
                }
            }

            is HubMessage.CancelInvocation -> {
                writeArrayHeader(3)
                writeLong(message.type.toLong())
                writeMapHeader(0)
                writeString(message.invocationId)
            }

            is HubMessage.Ping -> {
                writeArrayHeader(1)
                writeLong(message.type.toLong())
            }

            is HubMessage.Close -> {
                writeArrayHeader(3)
                writeLong(message.type.toLong())
                message.error?.takeIf { it.isNotEmpty() }?.let { writeString(it) } ?: writeNil()
                writeBoolean(message.allowReconnect)
            }
        }
    }

    private fun MessagePackWriter.writeInvocation(type: Int, invocationId: String?, target: String, arguments: List<HubValue>, streamIds: List<String>?) {
        writeArrayHeader(6)
        writeLong(type.toLong())
        writeMapHeader(0)
        invocationId?.takeIf { it.isNotEmpty() }?.let { writeString(it) } ?: writeNil()
        writeString(target)
        writeArrayHeader(arguments.size)
        arguments.forEach { writeValue(it) }
        writeArrayHeader(streamIds.orEmpty().size)
        streamIds.orEmpty().forEach { writeString(it) }
    }

    private fun MessagePackWriter.writeValue(value: HubValue) = when (value) {
        is HubValue.Encodable -> messagePack.encodeTo(this, value.serializer, value.value)
        is MessagePackValue -> writeBytes(value.bytes)
        else -> throw IllegalArgumentException("$value cannot be written by the MessagePack protocol")
    }

    companion object {
        private const val PROTOCOL_NAME = "messagepack"
        private const val PROTOCOL_VERSION = 1
        private const val MAX_LENGTH_HEADER_SIZE = 5
        private const val ERROR_RESULT = 1
        private const val VOID_RESULT = 2
        private const val NON_VOID_RESULT = 3
    }
}

internal class MessagePackValue(val bytes: ByteArray, private val messagePack: MessagePack) : HubValue.Decodable {

    override fun <T> decode(deserializer: DeserializationStrategy<T>): T = messagePack.decodeFromByteArray(deserializer, bytes)

    override fun equals(other: Any?): Boolean = other is MessagePackValue && bytes.contentEquals(other.bytes)

    override fun hashCode(): Int = bytes.contentHashCode()

    override fun toString(): String = MessagePackReader(bytes).render()
}
