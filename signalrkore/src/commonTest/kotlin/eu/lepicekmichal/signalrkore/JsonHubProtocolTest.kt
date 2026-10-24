package eu.lepicekmichal.signalrkore

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.add
import kotlinx.serialization.json.addJsonArray
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertTrue

class JsonHubProtocolTest {

    private val logs = mutableListOf<Logger.Severity>()
    private val protocol = JsonHubProtocol(logger = { severity, _, _ -> logs += severity })

    private fun parse(vararg messages: String) =
        protocol.parseMessages(messages.joinToString("") { it + RECORD_SEPARATOR }.encodeToByteArray())

    private fun value(element: JsonElement) = JsonValue(element, Json)

    private fun assertWritten(expected: String, message: HubMessage) {
        val written = protocol.writeMessage(message).decodeToString()
        assertTrue(written.endsWith(RECORD_SEPARATOR), "Message must end with the record separator: $written")
        assertEquals(Json.parseToJsonElement(expected), Json.parseToJsonElement(written.dropLast(1)))
    }

    @Test
    fun nameAndVersion() {
        assertEquals("json", protocol.name)
        assertEquals(1, protocol.version)
    }

    @Test
    fun writeBlockingInvocation() = assertWritten(
        """{"type":1,"invocationId":"123","target":"Send","arguments":[42,"Test Message"]}""",
        HubMessage.Invocation.Blocking(
            target = "Send",
            arguments = listOf(HubValue.Encodable(42, Int.serializer()), HubValue.Encodable("Test Message", String.serializer())),
            invocationId = "123",
        ),
    )

    @Test
    fun writeNonBlockingInvocation() = assertWritten(
        """{"type":1,"target":"Send","arguments":[42,"Test Message"]}""",
        HubMessage.Invocation.NonBlocking(target = "Send", arguments = listOf(HubValue.Encodable(42, Int.serializer()), HubValue.Encodable("Test Message", String.serializer()))),
    )

    @Test
    fun writeInvocationWithStreamIds() = assertWritten(
        """{"type":1,"target":"Upload","arguments":[],"streamIds":["1","2"]}""",
        HubMessage.Invocation.NonBlocking(target = "Upload", arguments = emptyList(), streamIds = listOf("1", "2")),
    )

    @Test
    fun writeStreamInvocation() = assertWritten(
        """{"type":4,"invocationId":"123","target":"Send","arguments":[42]}""",
        HubMessage.StreamInvocation(target = "Send", arguments = listOf(HubValue.Encodable(42, Int.serializer())), invocationId = "123"),
    )

    @Test
    fun writeStreamItem() = assertWritten(
        """{"type":2,"invocationId":"123","item":42}""",
        HubMessage.StreamItem(invocationId = "123", item = HubValue.Encodable(42, Int.serializer())),
    )

    @Test
    fun writeCompletions() {
        assertWritten("""{"type":3,"invocationId":"123"}""", HubMessage.Completion.Simple(invocationId = "123"))
        assertWritten("""{"type":3,"invocationId":"123","result":42}""", HubMessage.Completion.Resulted(invocationId = "123", result = HubValue.Encodable(42, Int.serializer())))
        assertWritten("""{"type":3,"invocationId":"123","error":"It didn't work!"}""", HubMessage.Completion.Error(invocationId = "123", error = "It didn't work!"))
    }

    @Test
    fun writeCancelInvocation() = assertWritten("""{"type":5,"invocationId":"123"}""", HubMessage.CancelInvocation(invocationId = "123"))

    @Test
    fun writePing() = assertWritten("""{"type":6}""", HubMessage.Ping())

    @Test
    fun writeClose() = assertWritten("""{"type":7}""", HubMessage.Close())

    @Test
    fun parseEmptyPayload() {
        assertEquals(emptyList(), protocol.parseMessages(ByteArray(0)))
    }

    @Test
    fun parsePing() {
        assertIs<HubMessage.Ping>(parse("""{"type":6}""").single())
    }

    @Test
    fun parseClose() {
        assertEquals(HubMessage.Close(), parse("""{"type":7}""").single())
        assertEquals(HubMessage.Close(error = "Connection closed because of an error!"), parse("""{"type":7,"error":"Connection closed because of an error!"}""").single())
        assertEquals(
            HubMessage.Close(allowReconnect = true, error = "Connection closed because of an error!"),
            parse("""{"type":7,"error":"Connection closed because of an error!","allowReconnect":true}""").single(),
        )
    }

    @Test
    fun parseInvocations() {
        assertEquals(
            HubMessage.Invocation.Blocking(target = "Send", arguments = listOf(value(JsonPrimitive(42)), value(JsonPrimitive("Test Message"))), invocationId = "123"),
            parse("""{"type":1,"invocationId":"123","target":"Send","arguments":[42,"Test Message"]}""").single(),
        )
        assertEquals(
            HubMessage.Invocation.NonBlocking(target = "Send", arguments = listOf(value(JsonPrimitive(42)))),
            parse("""{"type":1,"target":"Send","arguments":[42]}""").single(),
        )
    }

    @Test
    fun parseNestedCollectionArgument() {
        val message = assertIs<HubMessage.Invocation.NonBlocking>(parse("""{"type":1,"target":"Send","arguments":[[[1,2],[3]]]}""").single())

        assertEquals(
            buildJsonArray {
                addJsonArray { add(1); add(2) }
                addJsonArray { add(3) }
            },
            (message.arguments.single() as JsonValue).element,
        )
    }

    @Test
    fun parseMessageWithPropertiesOutOfOrder() {
        assertEquals(
            HubMessage.Invocation.Blocking(target = "Send", arguments = listOf(value(JsonPrimitive(42))), invocationId = "123"),
            parse("""{"arguments":[42],"target":"Send","invocationId":"123","type":1}""").single(),
        )
        assertEquals(
            HubMessage.Completion.Resulted(invocationId = "123", result = value(JsonPrimitive(42))),
            parse("""{"result":42,"invocationId":"123","type":3}""").single(),
        )
    }

    @Test
    fun parseMessagesWithHeaders() {
        assertEquals(
            HubMessage.Invocation.Blocking(target = "Send", arguments = listOf(value(JsonPrimitive(42))), invocationId = "123"),
            parse("""{"type":1,"headers":{"Foo":"Bar"},"invocationId":"123","target":"Send","arguments":[42]}""").single(),
        )
        assertEquals(
            HubMessage.Completion.Simple(invocationId = "123"),
            parse("""{"type":3,"invocationId":"123","headers":{}}""").single(),
        )
        assertEquals(
            HubMessage.StreamItem(invocationId = "123", item = value(JsonPrimitive(42))),
            parse("""{"headers":{"Foo":"Bar"},"type":2,"invocationId":"123","item":42}""").single(),
        )
    }

    @Test
    fun parseCompletions() {
        assertEquals(HubMessage.Completion.Simple(invocationId = "123"), parse("""{"type":3,"invocationId":"123"}""").single())
        assertEquals(HubMessage.Completion.Simple(invocationId = "123"), parse("""{"type":3,"invocationId":"123","result":null}""").single())
        assertEquals(HubMessage.Completion.Resulted(invocationId = "123", result = value(JsonPrimitive(42))), parse("""{"type":3,"invocationId":"123","result":42}""").single())
        assertEquals(HubMessage.Completion.Error(invocationId = "123", error = "It didn't work!"), parse("""{"type":3,"invocationId":"123","error":"It didn't work!"}""").single())
    }

    @Test
    fun parseCompletionWithResultAndErrorAsError() {
        assertEquals(
            HubMessage.Completion.Error(invocationId = "123", error = "It didn't work!"),
            parse("""{"type":3,"invocationId":"123","result":42,"error":"It didn't work!"}""").single(),
        )
    }

    @Test
    fun parseStreamMessages() {
        assertEquals(HubMessage.StreamItem(invocationId = "123", item = value(JsonNull)), parse("""{"type":2,"invocationId":"123","item":null}""").single())
        assertEquals(HubMessage.CancelInvocation(invocationId = "123"), parse("""{"type":5,"invocationId":"123"}""").single())
        assertEquals(
            HubMessage.StreamInvocation(target = "Count", arguments = listOf(value(JsonPrimitive(10))), invocationId = "123"),
            parse("""{"type":4,"invocationId":"123","target":"Count","arguments":[10]}""").single(),
        )
    }

    @Test
    fun parseTwoMessagesInOnePayload() {
        val messages = parse("""{"type":6}""", """{"type":3,"invocationId":"1","result":"done"}""")

        assertEquals(2, messages.size)
        assertIs<HubMessage.Ping>(messages[0])
        assertEquals(HubMessage.Completion.Resulted(invocationId = "1", result = value(JsonPrimitive("done"))), messages[1])
    }

    @Test
    fun incompleteMessageThrows() {
        assertFailsWith<RuntimeException> { protocol.parseMessages("""{"type":6}""".encodeToByteArray()) }
    }

    @Test
    fun unknownMessageTypeIsSkippedAndLogged() {
        val messages = parse("""{"type":99}""", """{"type":6}""")

        assertIs<HubMessage.Ping>(messages.single())
        assertTrue(Logger.Severity.ERROR in logs)
    }

    @Serializable
    data class Person(val firstName: String, val lastName: String, val age: Int? = null)

    @Test
    fun writeArgumentsWithTheirSerializers() = assertWritten(
        """{"type":1,"target":"Send","arguments":[{"firstName":"John","lastName":"Doe"},[1,2]]}""",
        HubMessage.Invocation.NonBlocking(
            target = "Send",
            arguments = listOf(
                HubValue.Encodable(Person("John", "Doe"), Person.serializer()),
                HubValue.Encodable(listOf(1, 2), ListSerializer(Int.serializer())),
            ),
        ),
    )

    @Test
    fun writeArgumentsWithTheConfiguredJson() {
        val protocol = JsonHubProtocol(logger = { _, _, _ -> }, json = Json { encodeDefaults = true })
        val message = HubMessage.StreamItem(invocationId = "1", item = HubValue.Encodable(Person("John", "Doe"), Person.serializer()))

        assertEquals(
            Json.parseToJsonElement("""{"type":2,"invocationId":"1","item":{"firstName":"John","lastName":"Doe","age":null}}"""),
            Json.parseToJsonElement(protocol.writeMessage(message).decodeToString().dropLast(1)),
        )
    }

    @Test
    fun decodeParsedValues() {
        val message = assertIs<HubMessage.Invocation.NonBlocking>(
            parse("""{"type":1,"target":"Send","arguments":[{"firstName":"John","lastName":"Doe","age":30},[1,2],"text"]}""").single()
        )
        val arguments = message.arguments.map { assertIs<HubValue.Decodable>(it) }

        assertEquals(Person("John", "Doe", 30), arguments[0].decode(Person.serializer()))
        assertEquals(listOf(1, 2), arguments[1].decode(ListSerializer(Int.serializer())))
        assertEquals("text", arguments[2].decode(String.serializer()))
    }

    @Test
    fun decodeParsedValuesWithTheConfiguredJson() {
        val protocol = JsonHubProtocol(logger = { _, _, _ -> }, json = Json { ignoreUnknownKeys = true })
        val payload = """{"type":2,"invocationId":"1","item":{"firstName":"John","lastName":"Doe","nickname":"JD"}}""" + RECORD_SEPARATOR
        val message = assertIs<HubMessage.StreamItem>(protocol.parseMessages(payload.encodeToByteArray()).single())

        assertEquals(Person("John", "Doe"), assertIs<HubValue.Decodable>(message.item).decode(Person.serializer()))
    }

    @Test
    fun parsedMessageCanBeWrittenBack() {
        val message = parse("""{"type":1,"invocationId":"1","target":"Send","arguments":[{"a":[1,null]}],"streamIds":["2"]}""").single()

        assertWritten("""{"type":1,"invocationId":"1","target":"Send","arguments":[{"a":[1,null]}],"streamIds":["2"]}""", message)
    }

    @Test
    fun textTransferFormat() {
        assertEquals(TransferFormat.Text, protocol.transferFormat)
    }
}
