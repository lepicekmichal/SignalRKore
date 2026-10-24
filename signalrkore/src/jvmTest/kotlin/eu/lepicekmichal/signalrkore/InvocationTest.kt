package eu.lepicekmichal.signalrkore

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class InvocationTest : HubTest() {

    @Serializable
    data class Person(val firstName: String, val lastName: String, val age: Int)

    private val received = Channel<JsonObject>(Channel.UNLIMITED)

    private fun startRecordingHub(respond: suspend HubSession.(JsonObject) -> Unit = {}) = startHub {
        while (true) {
            val message = receiveMessage()
            received.send(message)
            respond(message)
        }
    }

    private suspend fun nextMessage() = withTimeout(5.seconds) { received.receive() }

    @Test
    fun `send without arguments`() = runBlocking<Unit> {
        val connection = connection(startRecordingHub()).apply { start() }

        connection.send("Ping")

        assertEquals(Json.parseToJsonElement("""{"type":1,"target":"Ping","arguments":[]}"""), nextMessage())
        connection.stop()
    }

    @Test
    fun `send with arguments`() = runBlocking<Unit> {
        val connection = connection(startRecordingHub()).apply { start() }

        connection.send("Send", "text", 42)

        assertEquals(Json.parseToJsonElement("""{"type":1,"target":"Send","arguments":["text",42]}"""), nextMessage())
        connection.stop()
    }

    @Test
    fun `send with custom object`() = runBlocking<Unit> {
        val connection = connection(startRecordingHub()).apply { start() }

        connection.send("Person", Person("John", "Doe", 30))

        assertEquals(
            Json.parseToJsonElement("""{"type":1,"target":"Person","arguments":[{"firstName":"John","lastName":"Doe","age":30}]}"""),
            nextMessage(),
        )
        connection.stop()
    }

    @Test
    fun `messages are sent in order`() = runBlocking<Unit> {
        val connection = connection(startRecordingHub()).apply { start() }

        repeat(200) { connection.send("Number", it) }

        assertEquals(List(200) { it }, List(200) { nextMessage().arguments.single().jsonPrimitive.content.toInt() })
        connection.stop()
    }

    @Test
    fun `invoke waits for completion`() = runBlocking<Unit> {
        val connection = connection(startRecordingHub { message ->
            delay(200)
            sendMessage("""{"type":3,"invocationId":"${message.invocationId}"}""")
        }).apply { start() }

        connection.invoke("Void")

        val message = nextMessage()
        assertEquals("Void", message.target)
        assertTrue("invocationId" in message)
        connection.stop()
    }

    @Test
    fun `invoke returns result`() = runBlocking<Unit> {
        val connection = connection(startRecordingHub { message ->
            sendMessage("""{"type":3,"invocationId":"${message.invocationId}","result":${message.arguments[0].jsonPrimitive.content.toInt() * 2}}""")
        }).apply { start() }

        assertEquals(42, connection.invoke("Double", 21, Int.serializer(), Int.serializer()))
        connection.stop()
    }

    @Test
    fun `invoke returns custom object`() = runBlocking<Unit> {
        val connection = connection(startRecordingHub { message ->
            sendMessage("""{"type":3,"invocationId":"${message.invocationId}","result":{"firstName":"John","lastName":"Doe","age":30}}""")
        }).apply { start() }

        assertEquals(Person("John", "Doe", 30), connection.invoke("Person", Person.serializer()))
        connection.stop()
    }

    @Test
    fun `invoke without expected result fails on result missing`() = runBlocking<Unit> {
        val connection = connection(startRecordingHub { message ->
            sendMessage("""{"type":3,"invocationId":"${message.invocationId}"}""")
        }).apply { start() }

        assertFailsWith<RuntimeException> { connection.invoke("Nothing", Int.serializer()) }
        connection.stop()
    }

    @Test
    fun `multiple invokes wait for their own completion`() = runBlocking<Unit> {
        val port = startHub {
            val first = receiveMessage()
            val second = receiveMessage()
            sendMessage("""{"type":3,"invocationId":"${second.invocationId}","result":"${second.target}"}""")
            sendMessage("""{"type":3,"invocationId":"${first.invocationId}","result":"${first.target}"}""")
            ignoreMessages()
        }
        val connection = connection(port).apply { start() }

        val first = async { connection.invoke("First", String.serializer()) }
        delay(100)
        val second = async { connection.invoke("Second", String.serializer()) }

        assertEquals("First", withTimeout(5.seconds) { first.await() })
        assertEquals("Second", withTimeout(5.seconds) { second.await() })
        connection.stop()
    }

    @Test
    fun `many concurrent invokes get their completion`() = runBlocking<Unit> {
        val connection = connection(startRecordingHub { message ->
            sendMessage("""{"type":3,"invocationId":"${message.invocationId}","result":42}""")
        }).apply { start() }

        val results = List(50) { async { withTimeout(5.seconds) { connection.invoke("Answer", Int.serializer()) } } }.map { it.await() }

        assertEquals(List(50) { 42 }, results)
        connection.stop()
    }

    @Test
    fun `invoke throws HubException when server returns an error`() = runBlocking<Unit> {
        val connection = connection(startRecordingHub { message ->
            sendMessage("""{"type":3,"invocationId":"${message.invocationId}","error":"It didn't work!"}""")
        }).apply { start() }

        val ex = assertFailsWith<HubException> { connection.invoke("Fail", Int.serializer()) }

        assertEquals("It didn't work!", ex.message)
        assertEquals(HubConnectionState.CONNECTED, connection.connectionState.value)
        connection.stop()
    }

    @Test
    fun `completion with result and error throws HubException`() = runBlocking<Unit> {
        val connection = connection(startRecordingHub { message ->
            sendMessage("""{"type":3,"invocationId":"${message.invocationId}","result":42,"error":"It didn't work!"}""")
        }).apply { start() }

        assertFailsWith<HubException> { connection.invoke("Fail", Int.serializer()) }
        connection.stop()
    }

    @Test
    fun `stop cancels pending invokes`() = runBlocking<Unit> {
        val connection = connection(startRecordingHub()).apply { start() }

        val pending = async { runCatching { connection.invoke("Never", Int.serializer()) }.exceptionOrNull() }
        nextMessage()
        connection.stop()

        val ex = withTimeout(5.seconds) { pending.await() }
        assertTrue(ex is ConnectionClosedException, "Got $ex")
        assertNull(ex.cause)
    }

    @Test
    fun `pending invoke fails when connection drops`() = runBlocking<Unit> {
        val port = startHub {
            receiveMessage()
            cancel()
        }
        val connection = connection(port).apply { start() }

        assertFailsWith<ConnectionClosedException> { withTimeout(5.seconds) { connection.invoke("Never", Int.serializer()) } }

        assertEquals(HubConnectionState.DISCONNECTED, connection.connectionState.value)
    }

    @Test
    fun `methods cannot be called before start`() = runBlocking<Unit> {
        val connection = connection(port = 1)

        assertFailsWith<RuntimeException> { connection.send("Send") }
        assertFailsWith<RuntimeException> { connection.invoke("Invoke") }
        assertFailsWith<RuntimeException> { connection.stream("Stream", Int.serializer()) }
    }

    @Test
    fun `on receives invocations with arguments`() = runBlocking<Unit> {
        val port = startHub {
            sendMessage("""{"type":1,"target":"Nothing","arguments":[]}""")
            sendMessage("""{"type":1,"target":"One","arguments":["a"]}""")
            sendMessage("""{"type":1,"target":"Two","arguments":["a",2]}""")
            sendMessage("""{"type":1,"target":"Person","arguments":[{"firstName":"John","lastName":"Doe","age":30}]}""")
            ignoreMessages()
        }
        val connection = connection(port)
        val nothing = CompletableDeferred<Unit>()
        val one = CompletableDeferred<String>()
        val two = CompletableDeferred<Pair<String, Int>>()
        val person = CompletableDeferred<Person>()
        val jobs = listOf(
            launchCollecting(connection.on("Nothing")) { nothing.complete(Unit) },
            launchCollecting(connection.on("One", String.serializer())) { one.complete(it.arg1) },
            launchCollecting(connection.on("Two", String.serializer(), Int.serializer())) { two.complete(it.arg1 to it.arg2) },
            launchCollecting(connection.on("Person", Person.serializer())) { person.complete(it.arg1) },
        )

        connection.start()

        withTimeout(5.seconds) {
            nothing.await()
            assertEquals("a", one.await())
            assertEquals("a" to 2, two.await())
            assertEquals(Person("John", "Doe", 30), person.await())
        }
        jobs.forEach { it.cancel() }
        connection.stop()
    }

    @Test
    fun `multiple handlers for the same method are all triggered`() = runBlocking<Unit> {
        val port = startHub {
            sendMessage("""{"type":1,"target":"Hello","arguments":["world"]}""")
            ignoreMessages()
        }
        val connection = connection(port)
        val first = CompletableDeferred<String>()
        val second = CompletableDeferred<String>()
        val jobs = listOf(
            launchCollecting(connection.on("Hello", String.serializer())) { first.complete(it.arg1) },
            launchCollecting(connection.on("Hello", String.serializer())) { second.complete(it.arg1) },
        )

        connection.start()

        withTimeout(5.seconds) {
            assertEquals("world", first.await())
            assertEquals("world", second.await())
        }
        jobs.forEach { it.cancel() }
        connection.stop()
    }

    @Test
    fun `invocation with wrong arguments is skipped`() = runBlocking<Unit> {
        val port = startHub {
            sendMessage("""{"type":1,"target":"Number","arguments":["not a number"]}""")
            sendMessage("""{"type":1,"target":"Number","arguments":[]}""")
            sendMessage("""{"type":1,"target":"Number","arguments":[42]}""")
            ignoreMessages()
        }
        val connection = connection(port)
        val numbers = Channel<Int>(Channel.UNLIMITED)
        val job = launchCollecting(connection.on("Number", Int.serializer())) { numbers.send(it.arg1) }

        connection.start()

        assertEquals(42, withTimeout(5.seconds) { numbers.receive() })
        assertTrue(numbers.isEmpty)
        assertTrue(logs.any { it.severity == Logger.Severity.ERROR }, logsText())
        assertEquals(HubConnectionState.CONNECTED, connection.connectionState.value)
        job.cancel()
        connection.stop()
    }

    @Test
    fun `stream receives items until completion`() = runBlocking<Unit> {
        val connection = connection(startRecordingHub { message ->
            if (message.type != HubMessageType.STREAM_INVOCATION.value) return@startRecordingHub
            val count = message.arguments[0].jsonPrimitive.content.toInt()
            repeat(count) { sendMessage("""{"type":2,"invocationId":"${message.invocationId}","item":$it}""") }
            sendMessage("""{"type":3,"invocationId":"${message.invocationId}"}""")
        }).apply { start() }

        val items = withTimeout(5.seconds) { connection.stream("Count", Int.serializer(), 3, Int.serializer()).toList() }

        assertEquals(listOf(0, 1, 2), items)
        assertEquals(HubMessageType.STREAM_INVOCATION.value, nextMessage().type)
        connection.stop()
    }

    @Test
    fun `stream of custom objects`() = runBlocking<Unit> {
        val connection = connection(startRecordingHub { message ->
            sendMessage("""{"type":2,"invocationId":"${message.invocationId}","item":{"firstName":"John","lastName":"Doe","age":30}}""")
            sendMessage("""{"type":3,"invocationId":"${message.invocationId}"}""")
        }).apply { start() }

        assertEquals(listOf(Person("John", "Doe", 30)), withTimeout(5.seconds) { connection.stream("People", Person.serializer()).toList() })
        connection.stop()
    }

    @Test
    fun `stream completed with error throws HubException after delivering items`() = runBlocking<Unit> {
        val connection = connection(startRecordingHub { message ->
            sendMessage("""{"type":2,"invocationId":"${message.invocationId}","item":1}""")
            sendMessage("""{"type":3,"invocationId":"${message.invocationId}","error":"Stream failed"}""")
        }).apply { start() }

        val items = mutableListOf<Int>()
        val ex = assertFailsWith<HubException> { connection.stream("Numbers", Int.serializer()).collect { items += it } }

        assertEquals("Stream failed", ex.message)
        assertEquals(listOf(1), items)
        connection.stop()
    }

    @Test
    fun `stream completed with result is an error`() = runBlocking<Unit> {
        val connection = connection(startRecordingHub { message ->
            sendMessage("""{"type":3,"invocationId":"${message.invocationId}","result":42}""")
        }).apply { start() }

        assertFailsWith<IllegalStateException> { connection.stream("Numbers", Int.serializer()).toList() }
        connection.stop()
    }

    @Test
    fun `cancelling stream collection sends cancel invocation`() = runBlocking<Unit> {
        val connection = connection(startRecordingHub { message ->
            if (message.type != HubMessageType.STREAM_INVOCATION.value) return@startRecordingHub
            repeat(5) { sendMessage("""{"type":2,"invocationId":"${message.invocationId}","item":$it}""") }
        }).apply { start() }

        val items = withTimeout(5.seconds) { connection.stream("Numbers", Int.serializer()).take(2).toList() }

        assertEquals(listOf(0, 1), items)
        val invocation = nextMessage()
        val cancel = nextMessage()
        assertEquals(HubMessageType.CANCEL_INVOCATION.value, cancel.type)
        assertEquals(invocation.invocationId, cancel.invocationId)
        connection.stop()
    }

    @Test
    fun `stream item that cannot be parsed fails the stream and cancels it`() = runBlocking<Unit> {
        val connection = connection(startRecordingHub { message ->
            if (message.type != HubMessageType.STREAM_INVOCATION.value) return@startRecordingHub
            sendMessage("""{"type":2,"invocationId":"${message.invocationId}","item":"not a number"}""")
        }).apply { start() }

        assertFailsWith<RuntimeException> { withTimeout(5.seconds) { connection.stream("Numbers", Int.serializer()).toList() } }

        nextMessage()
        assertEquals(HubMessageType.CANCEL_INVOCATION.value, nextMessage().type)
        connection.stop()
    }

    @Test
    fun `stream fails when connection drops`() = runBlocking<Unit> {
        val port = startHub {
            val invocation = receiveMessage()
            sendMessage("""{"type":2,"invocationId":"${invocation.invocationId}","item":1}""")
            delay(200)
            cancel()
        }
        val connection = connection(port).apply { start() }

        val items = mutableListOf<Int>()
        val ex = runCatching { withTimeout(5.seconds) { connection.stream("Numbers", Int.serializer()).toList(items) } }.exceptionOrNull()

        assertTrue(ex is ConnectionClosedException, "Got $ex\n${logsText()}")
        assertEquals(listOf(1), items)
    }

    @Test
    fun `stream collected twice invokes the server twice`() = runBlocking<Unit> {
        val connection = connection(startRecordingHub { message ->
            sendMessage("""{"type":2,"invocationId":"${message.invocationId}","item":1}""")
            sendMessage("""{"type":3,"invocationId":"${message.invocationId}"}""")
        }).apply { start() }
        val stream = connection.stream("Numbers", Int.serializer())

        assertEquals(listOf(1), withTimeout(5.seconds) { stream.toList() })
        assertEquals(listOf(1), withTimeout(5.seconds) { stream.toList() })
        assertFalse(nextMessage().invocationId == nextMessage().invocationId)
        connection.stop()
    }

    @Test
    fun `upload stream through send`() = runBlocking<Unit> {
        val connection = connection(startRecordingHub()).apply { start() }

        connection.send("Upload", flowOf(1, 2))

        val invocation = nextMessage()
        val streamId = invocation.getValue("streamIds").jsonArray.single().jsonPrimitive.content
        assertEquals("Upload", invocation.target)
        assertFalse("invocationId" in invocation)
        assertEquals(Json.parseToJsonElement("""{"type":2,"invocationId":"$streamId","item":1}"""), nextMessage())
        assertEquals(Json.parseToJsonElement("""{"type":2,"invocationId":"$streamId","item":2}"""), nextMessage())
        assertEquals(Json.parseToJsonElement("""{"type":3,"invocationId":"$streamId"}"""), nextMessage())
        connection.stop()
    }

    @Test
    fun `upload stream with argument through invoke`() = runBlocking<Unit> {
        val connection = connection(startRecordingHub { message ->
            if (message.type == HubMessageType.COMPLETION.value) {
                sendMessage("""{"type":3,"invocationId":"$invocationIdOfUpload","result":3}""")
            } else if (message.type == HubMessageType.INVOCATION.value) {
                invocationIdOfUpload = message.invocationId
            }
        }).apply { start() }

        val sum = withTimeout(5.seconds) {
            connection.invoke("Sum", "numbers", String.serializer(), flowOf(1, 2), Int.serializer(), Int.serializer())
        }

        assertEquals(3, sum)
        val invocation = nextMessage()
        assertEquals(Json.parseToJsonElement("""["numbers"]"""), invocation.arguments)
        assertEquals(1, invocation.getValue("streamIds").jsonArray.size)
        connection.stop()
    }

    private var invocationIdOfUpload = ""

    @Test
    fun `upload stream through stream`() = runBlocking<Unit> {
        val connection = connection(startRecordingHub { message ->
            if (message.type == HubMessageType.STREAM_INVOCATION.value) invocationIdOfUpload = message.invocationId
            if (message.type == HubMessageType.STREAM_ITEM.value) {
                sendMessage("""{"type":2,"invocationId":"$invocationIdOfUpload","item":${message.getValue("item").jsonPrimitive.content.toInt() * 10}}""")
            }
            if (message.type == HubMessageType.COMPLETION.value) {
                sendMessage("""{"type":3,"invocationId":"$invocationIdOfUpload"}""")
            }
        }).apply { start() }

        val items = withTimeout(5.seconds) {
            connection.stream("Multiply", Int.serializer(), flowOf(1, 2, 3), Int.serializer()).toList()
        }

        assertEquals(listOf(10, 20, 30), items)
        connection.stop()
    }

    @Test
    fun `multiple upload streams`() = runBlocking<Unit> {
        val connection = connection(startRecordingHub()).apply { start() }

        connection.send("Upload", flowOf("a"), flowOf(1))

        val streamIds = nextMessage().getValue("streamIds").jsonArray.map { it.jsonPrimitive.content }
        val messages = List(4) { nextMessage() }
        assertEquals(2, streamIds.toSet().size)
        assertEquals(streamIds.toSet(), messages.map { it.invocationId }.toSet())
        assertEquals(2, messages.count { it.type == HubMessageType.COMPLETION.value })
        connection.stop()
    }

    @Test
    fun `failing upload stream sends completion with error`() = runBlocking<Unit> {
        val connection = connection(startRecordingHub()).apply { start() }

        connection.send("Upload", flow<Int> {
            emit(1)
            throw IllegalStateException("upload failed")
        })

        val streamId = nextMessage().getValue("streamIds").jsonArray.single().jsonPrimitive.content
        assertEquals(HubMessageType.STREAM_ITEM.value, nextMessage().type)
        assertEquals(Json.parseToJsonElement("""{"type":3,"invocationId":"$streamId","error":"upload failed"}"""), nextMessage())
        connection.stop()
    }

    @Test
    fun `list arguments and results`() = runBlocking<Unit> {
        val connection = connection(startRecordingHub { message ->
            sendMessage("""{"type":3,"invocationId":"${message.invocationId}","result":${message.arguments[0]}}""")
        }).apply { start() }

        val echoed = connection.invoke("Echo", listOf(1, 2, 3), ListSerializer(Int.serializer()), ListSerializer(Int.serializer()))

        assertEquals(listOf(1, 2, 3), echoed)
        connection.stop()
    }
}
