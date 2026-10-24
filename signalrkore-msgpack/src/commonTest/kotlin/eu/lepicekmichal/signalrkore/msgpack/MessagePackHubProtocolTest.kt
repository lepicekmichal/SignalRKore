package eu.lepicekmichal.signalrkore.msgpack

import eu.lepicekmichal.signalrkore.HubMessage
import eu.lepicekmichal.signalrkore.HubValue
import eu.lepicekmichal.signalrkore.Logger
import eu.lepicekmichal.signalrkore.TransferFormat
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ByteArraySerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Instant

class MessagePackHubProtocolTest {

    @Serializable
    data class PersonPojo<T>(val firstName: String, val lastName: String, val age: Int, val t: T)

    private val logs = mutableListOf<Logger.Severity>()
    private val protocol = MessagePackHubProtocol(logger = { severity, _, _ -> logs += severity })

    private fun framed(body: String): ByteArray = hex(body).let { byteArrayOf(it.size.toByte()) + it }

    private fun parse(body: String): HubMessage = protocol.parseMessages(framed(body)).single()

    private fun assertWritten(expected: String, message: HubMessage) = assertEquals(hex(expected).toHex(), protocol.writeMessage(message).toHex())

    private fun <T> HubValue.decode(deserializer: DeserializationStrategy<T>): T = assertIs<HubValue.Decodable>(this).decode(deserializer)

    private val value42 = HubValue.Encodable(42, Int.serializer())

    @Test
    fun nameVersionAndTransferFormat() {
        assertEquals("messagepack", protocol.name)
        assertEquals(1, protocol.version)
        assertEquals(TransferFormat.Binary, protocol.transferFormat)
    }

    @Test
    fun writeBlockingInvocation() = assertWritten(
        "11 96 01 80 a3 78 79 7a a6 6d 65 74 68 6f 64 91 2a 90",
        HubMessage.Invocation.Blocking(target = "method", arguments = listOf(value42), invocationId = "xyz"),
    )

    @Test
    fun writeNonBlockingInvocation() = assertWritten(
        "0c 96 01 80 c0 a4 74 65 73 74 91 2a 90",
        HubMessage.Invocation.NonBlocking(target = "test", arguments = listOf(value42)),
    )

    @Test
    fun writeStreamInvocationWithStreamIds() = assertWritten(
        "15 96 04 80 a2 69 64 a4 74 65 73 74 91 2a 91 a6 73 74 72 65 61 6d",
        HubMessage.StreamInvocation(target = "test", arguments = listOf(value42), invocationId = "id", streamIds = listOf("stream")),
    )

    @Test
    fun writeStreamItem() = assertWritten("07 94 02 80 a2 69 64 2a", HubMessage.StreamItem(invocationId = "id", item = value42))

    @Test
    fun writeCompletions() {
        assertWritten("08 95 03 80 a2 69 64 03 2a", HubMessage.Completion.Resulted(invocationId = "id", result = value42))
        assertWritten("07 94 03 80 a2 69 64 02", HubMessage.Completion.Simple(invocationId = "id"))
        assertWritten("0d 95 03 80 a2 69 64 01 a5 65 72 72 6f 72", HubMessage.Completion.Error(invocationId = "id", error = "error"))
    }

    @Test
    fun writeCancelInvocation() = assertWritten("06 93 05 80 a2 69 64", HubMessage.CancelInvocation(invocationId = "id"))

    @Test
    fun writePing() = assertWritten("02 91 06", HubMessage.Ping())

    @Test
    fun writeClose() {
        assertWritten("04 93 07 c0 c2", HubMessage.Close())
        assertWritten("09 93 07 a5 45 72 72 6f 72 c3", HubMessage.Close(allowReconnect = true, error = "Error"))
    }

    @Test
    fun writePrimitiveArguments() = assertWritten(
        "1d 96 01 80 c0 a4 74 65 73 74 96 01 cb 40 04 00 00 00 00 00 00 c3 11 63 ce c6 ae a1 55 90",
        HubMessage.Invocation.NonBlocking(
            target = "test",
            arguments = listOf(
                HubValue.Encodable(1, Int.serializer()),
                HubValue.Encodable(2.5, Double.serializer()),
                HubValue.Encodable(true, Boolean.serializer()),
                HubValue.Encodable(0x11.toByte(), Byte.serializer()),
                HubValue.Encodable('c', Char.serializer()),
                HubValue.Encodable(3333333333, Long.serializer()),
            ),
        ),
    )

    @Test
    fun writeObjectArgument() = assertWritten(
        "32 96 01 80 c0 a4 74 65 73 74 91 84 a9 66 69 72 73 74 4e 61 6d 65 a4 4a 6f 68 6e a8 6c 61 73 74 4e 61 6d 65 a3 44 6f 65 " +
            "a3 61 67 65 1e a1 74 92 05 08 90",
        HubMessage.Invocation.NonBlocking(
            target = "test",
            arguments = listOf(
                HubValue.Encodable(PersonPojo("John", "Doe", 30, listOf<Short>(5, 8)), PersonPojo.serializer(ListSerializer(Short.serializer()))),
            ),
        ),
    )

    @Test
    fun writeMapArgument() = assertWritten(
        "23 96 01 80 c0 a4 74 65 73 74 91 82 a5 61 70 70 6c 65 a6 62 61 6e 61 6e 61 a3 6b 65 79 a5 76 61 6c 75 65 90",
        HubMessage.Invocation.NonBlocking(
            target = "test",
            arguments = listOf(HubValue.Encodable(mapOf("apple" to "banana", "key" to "value"), MapSerializer(String.serializer(), String.serializer()))),
        ),
    )

    @Test
    fun writeBinaryAndDateArguments() = assertWritten(
        "16 96 01 80 c0 a4 74 65 73 74 92 c4 03 01 02 03 d6 ff 5f 5e 10 00 90",
        HubMessage.Invocation.NonBlocking(
            target = "test",
            arguments = listOf(
                HubValue.Encodable(byteArrayOf(1, 2, 3), ByteArraySerializer()),
                HubValue.Encodable(Instant.fromEpochSeconds(1600000000), Instant.serializer()),
            ),
        ),
    )

    @Test
    fun writeLongMessageWithTwoByteLengthHeader() {
        val written = protocol.writeMessage(HubMessage.Invocation.NonBlocking(target = "test", arguments = listOf(HubValue.Encodable("a".repeat(122), String.serializer()))))

        assertEquals("8701", written.copyOfRange(0, 2).toHex())
        assertEquals(135, written.size - 2)
    }

    @Test
    fun parseBlockingInvocation() {
        val message = assertIs<HubMessage.Invocation.Blocking>(parse("96 01 80 a3 78 79 7a a6 6d 65 74 68 6f 64 91 2a 90"))

        assertEquals("xyz", message.invocationId)
        assertEquals("method", message.target)
        assertEquals(42, message.arguments.single().decode(Int.serializer()))
        assertNull(message.streamIds)
    }

    @Test
    fun parseNonBlockingInvocation() {
        val message = assertIs<HubMessage.Invocation.NonBlocking>(parse("96 01 80 c0 a6 6d 65 74 68 6f 64 91 2a 90"))

        assertEquals("method", message.target)
        assertEquals(42, message.arguments.single().decode(Int.serializer()))
    }

    @Test
    fun parseInvocationWithEmptyInvocationIdAsNonBlocking() {
        assertIs<HubMessage.Invocation.NonBlocking>(parse("96 01 80 a0 a6 6d 65 74 68 6f 64 91 2a 90"))
    }

    @Test
    fun parseInvocationWithoutStreamIdsFromOlderServers() {
        val message = assertIs<HubMessage.Invocation.Blocking>(parse("95 01 80 a3 78 79 7a a6 6d 65 74 68 6f 64 91 2a"))

        assertEquals("xyz", message.invocationId)
        assertNull(message.streamIds)
    }

    @Test
    fun parseInvocationWithHeaders() {
        val message = assertIs<HubMessage.Invocation.Blocking>(
            parse("96 01 82 a1 78 a1 79 a1 7a a1 7a a3 78 79 7a a6 6d 65 74 68 6f 64 91 2a 90")
        )

        assertEquals("xyz", message.invocationId)
        assertEquals("method", message.target)
        assertEquals(42, message.arguments.single().decode(Int.serializer()))
    }

    @Test
    fun parseStreamInvocation() {
        val message = assertIs<HubMessage.StreamInvocation>(parse("96 04 80 a3 78 79 7a a6 6d 65 74 68 6f 64 91 2a 91 a1 73"))

        assertEquals("xyz", message.invocationId)
        assertEquals("method", message.target)
        assertEquals(listOf("s"), message.streamIds)
    }

    @Test
    fun parseStreamItem() {
        val message = assertIs<HubMessage.StreamItem>(parse("94 02 80 a3 78 79 7a 2a"))

        assertEquals("xyz", message.invocationId)
        assertEquals(42, message.item.decode(Int.serializer()))
    }

    @Test
    fun parseCompletions() {
        assertEquals(HubMessage.Completion.Error(invocationId = "xyz", error = "Error"), parse("95 03 80 a3 78 79 7a 01 a5 45 72 72 6f 72"))
        assertEquals(HubMessage.Completion.Simple(invocationId = "xyz"), parse("94 03 80 a3 78 79 7a 02"))
        assertEquals(HubMessage.Completion.Simple(invocationId = "xyz"), parse("95 03 80 a3 78 79 7a 03 c0"))

        val resulted = assertIs<HubMessage.Completion.Resulted>(parse("95 03 80 a3 78 79 7a 03 2a"))
        assertEquals("xyz", resulted.invocationId)
        assertEquals(42, resulted.result.decode(Int.serializer()))
    }

    @Test
    fun parseCancelInvocation() {
        assertEquals(HubMessage.CancelInvocation(invocationId = "xyz"), parse("93 05 80 a3 78 79 7a"))
    }

    @Test
    fun parsePing() {
        assertIs<HubMessage.Ping>(parse("91 06"))
    }

    @Test
    fun parseClose() {
        assertEquals(HubMessage.Close(error = "xyz"), parse("92 07 a3 78 79 7a"))
        assertEquals(HubMessage.Close(allowReconnect = true, error = "xyz"), parse("93 07 a3 78 79 7a c3"))
        assertEquals(HubMessage.Close(), parse("93 07 c0 c2"))
    }

    @Test
    fun parseTwoMessages() {
        val messages = protocol.parseMessages(
            hex("0b 96 01 80 c0 a3 6f 6e 65 91 2a 90 0b 96 01 80 c0 a3 74 77 6f 91 2b 90")
        )

        assertEquals(listOf("one", "two"), messages.map { assertIs<HubMessage.Invocation.NonBlocking>(it).target })
        assertEquals(43, (messages[1] as HubMessage.Invocation).arguments.single().decode(Int.serializer()))
    }

    @Test
    fun parseObjectArgument() {
        val message = assertIs<HubMessage.Invocation.NonBlocking>(
            parse(
                "96 01 80 c0 a4 74 65 73 74 91 84 a9 66 69 72 73 74 4e 61 6d 65 a4 4a 6f 68 6e a8 6c 61 73 74 4e 61 6d 65 a3 44 6f 65 " +
                    "a3 61 67 65 1e a1 74 92 05 08 90"
            )
        )

        assertEquals(
            PersonPojo("John", "Doe", 30, listOf<Short>(5, 8)),
            message.arguments.single().decode(PersonPojo.serializer(ListSerializer(Short.serializer()))),
        )
    }

    @Test
    fun parsePrimitiveArgumentsWrittenByTheJavaClient() {
        val message = assertIs<HubMessage.Invocation.NonBlocking>(
            parse("96 01 80 c0 a4 74 65 73 74 96 01 cb 40 04 00 00 00 00 00 00 c3 11 a1 63 ce c6 ae a1 55 90")
        )
        val arguments = message.arguments

        assertEquals(1, arguments[0].decode(Int.serializer()))
        assertEquals(2.5, arguments[1].decode(Double.serializer()))
        assertEquals(true, arguments[2].decode(Boolean.serializer()))
        assertEquals(0x11, arguments[3].decode(Byte.serializer()))
        assertEquals('c', arguments[4].decode(Char.serializer()))
        assertEquals(3333333333, arguments[5].decode(Long.serializer()))
    }

    @Test
    fun parsedValuesDecodeAsDifferentTypes() {
        val argument = assertIs<HubMessage.Invocation>(parse("96 01 80 c0 a4 74 65 73 74 91 94 01 02 03 04 90")).arguments.single()

        assertEquals(listOf(1, 2, 3, 4), argument.decode(ListSerializer(Int.serializer())))
        assertEquals(listOf(1L, 2L, 3L, 4L), argument.decode(ListSerializer(Long.serializer())))
        assertContentEquals(byteArrayOf(1, 2, 3, 4), argument.decode(ByteArraySerializer()))
        assertEquals("[1,2,3,4]", argument.toString())
    }

    @Test
    fun parsedMessageCanBeWrittenBack() {
        val bytes = framed("96 01 80 a3 78 79 7a a6 6d 65 74 68 6f 64 92 2a 81 a1 61 c4 01 ff 91 a1 73")

        assertContentEquals(bytes, protocol.writeMessage(protocol.parseMessages(bytes).single()))
    }

    @Test
    fun parseMessageWithTwoByteLengthHeader() {
        val body = "96 01 80 c0 a4 74 65 73 74 91 d9 7a " + "61 ".repeat(122) + "90"
        val message = assertIs<HubMessage.Invocation.NonBlocking>(protocol.parseMessages(hex("87 01 $body")).single())

        assertEquals("a".repeat(122), message.arguments.single().decode(String.serializer()))
    }

    @Test
    fun lengthHeaderLongerThanPayloadFails() {
        val ex = assertFailsWith<RuntimeException> { protocol.parseMessages(hex("0d 96 01 80 c0 a4 74 65 73 74 91 2a 90")) }

        assertEquals("MessagePack message was length 12 but claimed to be length 13.", ex.message)
    }

    @Test
    fun lengthHeaderShorterThanMessageFails() {
        assertFailsWith<RuntimeException> { protocol.parseMessages(hex("0b 96 01 80 c0 a4 74 65 73 74 91 2a 90")) }
        assertTrue(Logger.Severity.ERROR in logs)
    }

    @Test
    fun incompleteOrOversizedLengthHeaderFails() {
        assertEquals("The length header was incomplete", assertFailsWith<RuntimeException> { protocol.parseMessages(hex("80")) }.message)
        assertEquals(
            "Messages over 2GB in size are not supported",
            assertFailsWith<RuntimeException> { protocol.parseMessages(hex("ff ff ff ff 08")) }.message,
        )
    }

    @Test
    fun emptyPayloadHasNoMessages() {
        assertTrue(protocol.parseMessages(ByteArray(0)).isEmpty())
    }

    @Test
    fun unknownMessageTypeIsSkippedAndLogged() {
        val messages = protocol.parseMessages(hex("04 92 08 cc 24 02 91 06"))

        assertIs<HubMessage.Ping>(messages.single())
        assertTrue(Logger.Severity.ERROR in logs)
    }
}
