package eu.lepicekmichal.signalrkore

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class OnWithResultTest : HubTest() {

    @Test
    fun `handler with no parameters should return a value`() = runTest {
        var called = false

        hubConnection.on("process", resultType = Boolean::class) {
            called = true
            true
        }
        hubConnection.start()

        val sentMessage = transport.nextSentMessage
        transport.receiveMessage("{\"type\":1,\"invocationId\":\"1\",\"target\":\"process\",\"arguments\":[]}$RECORD_SEPARATOR")
        val response = sentMessage.waitForResult()
        val expected = "{\"type\":3,\"invocationId\":\"1\",\"result\":true}$RECORD_SEPARATOR"

        assertEquals(expected, response)
        assertEquals(true, called)
    }

    @Test
    fun `handler with no return value should report an error`() = runTest {
        val nonResultCalled = Completable()

        hubConnection.on<Unit>("process") { nonResultCalled.complete() }
        hubConnection.start()

        val sentMessage = transport.nextSentMessage
        transport.receiveMessage("{\"type\":1,\"invocationId\":\"1\",\"target\":\"process\",\"arguments\":[]}$RECORD_SEPARATOR")
        val response = sentMessage.waitForResult()
        val expected = "{\"type\":3,\"invocationId\":\"1\",\"error\":\"Client did not provide a result.\"}$RECORD_SEPARATOR"

        assertEquals(expected, response)
        nonResultCalled.waitForCompletion()
    }

    @Test
    fun `missing handler with return value should report an error`() = runTest {
        hubConnection.start()

        val sentMessage = transport.nextSentMessage
        transport.receiveMessage("{\"type\":1,\"invocationId\":\"1\",\"target\":\"process\",\"arguments\":[]}$RECORD_SEPARATOR")
        val response = sentMessage.waitForResult()
        val expected = "{\"type\":3,\"invocationId\":\"1\",\"error\":\"Client did not provide a result.\"}$RECORD_SEPARATOR"

        assertEquals(expected, response)
    }

    @Test
    fun `faulty handler should return an error`() = runTest {
        hubConnection.on("process", resultType = Int::class) {
            throw RuntimeException("Custom error.")
        }
        hubConnection.start()

        val sentMessage = transport.nextSentMessage
        transport.receiveMessage("{\"type\":1,\"invocationId\":\"1\",\"target\":\"process\",\"arguments\":[]}$RECORD_SEPARATOR")
        val response = sentMessage.waitForResult()
        val expected = "{\"type\":3,\"invocationId\":\"1\",\"error\":\"Custom error.\"}$RECORD_SEPARATOR"

        assertEquals(expected, response)
    }

    @Test
    fun `registering multiple handlers for same target should raise an exception`() = runTest {
        hubConnection.on("process", resultType = Int::class) { 1 }

        val exception = assertFailsWith<RuntimeException> {
            hubConnection.on("process", resultType = Int::class) { 2 }
        }

        assertEquals("There can be only one function for returning result on blocking invocation (method: process)", exception.message)
    }

    @Test
    fun `handler with result should log if server does not expect a return value`() = runTest {
        val value = Single<Int>()

        hubConnection.on("process", resultType = Int::class) { value.updateResult { 42 }!! }
        hubConnection.start()

        transport.receiveMessage("{\"type\":1,\"target\":\"process\",\"arguments\":[]}$RECORD_SEPARATOR")
        value.waitForResult()

        logger.assertLogEquals("Result was returned for 'process' method but server is not expecting any result.")
    }

    @Test
    fun `handler with one param should return a value`() = runTest {
        var calledWith: String? = null

        hubConnection.on("ping", resultType = String::class, paramType1 = String::class) { p ->
            calledWith = p
            "pong"
        }
        hubConnection.start()

        val sentMessage = transport.nextSentMessage
        transport.receiveMessage("{\"type\":1,\"invocationId\":\"1\",\"target\":\"ping\",\"arguments\":[\"10\"]}$RECORD_SEPARATOR")
        val response = sentMessage.waitForResult()
        val expected = "{\"type\":3,\"invocationId\":\"1\",\"result\":\"pong\"}$RECORD_SEPARATOR"

        assertEquals(expected, response)
        assertEquals("10", calledWith)
    }

    @Test
    fun `handler with two params should return a value`() = runTest {
        var calledWith: String? = null
        var calledWith2: Int? = null

        hubConnection.on(
            "ping",
            resultType = String::class,
            paramType1 = String::class,
            paramType2 = Int::class,
        ) { p1, p2 ->
            calledWith = p1
            calledWith2 = p2
            "pong"
        }
        hubConnection.start()

        val sentMessage = transport.nextSentMessage
        transport.receiveMessage("{\"type\":1,\"invocationId\":\"1\",\"target\":\"ping\",\"arguments\":[\"10\",11]}$RECORD_SEPARATOR")
        val response = sentMessage.waitForResult()
        val expected = "{\"type\":3,\"invocationId\":\"1\",\"result\":\"pong\"}$RECORD_SEPARATOR"

        assertEquals(expected, response)
        assertEquals("10", calledWith)
        assertEquals(11, calledWith2)
    }

    @Test
    fun `handler with three params should return a value`() = runTest {
        var calledWith: String? = null
        var calledWith2: Int? = null
        var calledWith3: IntArray? = null

        hubConnection.on(
            "ping",
            resultType = String::class,
            paramType1 = String::class,
            paramType2 = Int::class,
            paramType3 = IntArray::class,
        ) { p1, p2, p3 ->
            calledWith = p1
            calledWith2 = p2
            calledWith3 = p3
            "pong"
        }
        hubConnection.start()

        val sentMessage = transport.nextSentMessage
        transport.receiveMessage("{\"type\":1,\"invocationId\":\"1\",\"target\":\"ping\",\"arguments\":[\"10\",11,[1,2,3]]}$RECORD_SEPARATOR")
        val response = sentMessage.waitForResult()
        val expected = "{\"type\":3,\"invocationId\":\"1\",\"result\":\"pong\"}$RECORD_SEPARATOR"

        assertEquals(expected, response)
        assertEquals("10", calledWith)
        assertEquals(11, calledWith2)
        assertContentEquals(intArrayOf(1, 2, 3), calledWith3)
    }

    @Test
    fun `handler with four params should return a value`() = runTest {
        var calledWith: String? = null
        var calledWith2: Int? = null
        var calledWith3: IntArray? = null
        var calledWith4: Boolean? = null

        hubConnection.on(
            "ping",
            resultType = String::class,
            paramType1 = String::class,
            paramType2 = Int::class,
            paramType3 = IntArray::class,
            paramType4 = Boolean::class,
        ) { p1, p2, p3, p4 ->
            calledWith = p1
            calledWith2 = p2
            calledWith3 = p3
            calledWith4 = p4
            "pong"
        }
        hubConnection.start()

        val sentMessage = transport.nextSentMessage
        transport.receiveMessage("{\"type\":1,\"invocationId\":\"1\",\"target\":\"ping\",\"arguments\":[\"10\",11,[1,2,3],true]}$RECORD_SEPARATOR")
        val response = sentMessage.waitForResult()
        val expected = "{\"type\":3,\"invocationId\":\"1\",\"result\":\"pong\"}$RECORD_SEPARATOR"

        assertEquals(expected, response)
        assertEquals("10", calledWith)
        assertEquals(11, calledWith2)
        assertContentEquals(intArrayOf(1, 2, 3), calledWith3)
        assertEquals(true, calledWith4)
    }

    @Test
    fun `handler with five params should return a value`() = runTest {
        var calledWith: String? = null
        var calledWith2: Int? = null
        var calledWith3: IntArray? = null
        var calledWith4: Boolean? = null
        var calledWith5: String? = null

        hubConnection.on(
            "ping",
            resultType = String::class,
            paramType1 = String::class,
            paramType2 = Int::class,
            paramType3 = IntArray::class,
            paramType4 = Boolean::class,
            paramType5 = String::class,
        ) { p1, p2, p3, p4, p5 ->
            calledWith = p1
            calledWith2 = p2
            calledWith3 = p3
            calledWith4 = p4
            calledWith5 = p5
            "pong"
        }
        hubConnection.start()

        val sentMessage = transport.nextSentMessage
        transport.receiveMessage("{\"type\":1,\"invocationId\":\"1\",\"target\":\"ping\",\"arguments\":[\"10\",11,[1,2,3],true,\"yey\"]}$RECORD_SEPARATOR")
        val response = sentMessage.waitForResult()
        val expected = "{\"type\":3,\"invocationId\":\"1\",\"result\":\"pong\"}$RECORD_SEPARATOR"

        assertEquals(expected, response)
        assertEquals("10", calledWith)
        assertEquals(11, calledWith2)
        assertContentEquals(intArrayOf(1, 2, 3), calledWith3)
        assertEquals(true, calledWith4)
        assertEquals("yey", calledWith5)
    }

    @Test
    fun `handler with six params should return a value`() = runTest {
        var calledWith: String? = null
        var calledWith2: Int? = null
        var calledWith3: IntArray? = null
        var calledWith4: Boolean? = null
        var calledWith5: String? = null
        var calledWith6: Double? = null

        hubConnection.on(
            "ping",
            resultType = String::class,
            paramType1 = String::class,
            paramType2 = Int::class,
            paramType3 = IntArray::class,
            paramType4 = Boolean::class,
            paramType5 = String::class,
            paramType6 = Double::class,
        ) { p1, p2, p3, p4, p5, p6 ->
            calledWith = p1
            calledWith2 = p2
            calledWith3 = p3
            calledWith4 = p4
            calledWith5 = p5
            calledWith6 = p6
            "pong"
        }
        hubConnection.start()

        val sentMessage = transport.nextSentMessage
        transport.receiveMessage("{\"type\":1,\"invocationId\":\"1\",\"target\":\"ping\",\"arguments\":[\"10\",11,[1,2,3],true,\"yey\",1.337]}$RECORD_SEPARATOR")
        val response = sentMessage.waitForResult()
        val expected = "{\"type\":3,\"invocationId\":\"1\",\"result\":\"pong\"}$RECORD_SEPARATOR"

        assertEquals(expected, response)
        assertEquals("10", calledWith)
        assertEquals(11, calledWith2)
        assertContentEquals(intArrayOf(1, 2, 3), calledWith3)
        assertEquals(true, calledWith4)
        assertEquals("yey", calledWith5)
        assertEquals(1.337, calledWith6)
    }

    @Test
    fun `handler with seven params should return a value`() = runTest {
        var calledWith: String? = null
        var calledWith2: Int? = null
        var calledWith3: IntArray? = null
        var calledWith4: Boolean? = null
        var calledWith5: String? = null
        var calledWith6: Double? = null
        var calledWith7: String? = null

        hubConnection.on(
            "ping",
            resultType = String::class,
            paramType1 = String::class,
            paramType2 = Int::class,
            paramType3 = IntArray::class,
            paramType4 = Boolean::class,
            paramType5 = String::class,
            paramType6 = Double::class,
            paramType7 = String::class,
        ) { p1, p2, p3, p4, p5, p6, p7 ->
            calledWith = p1
            calledWith2 = p2
            calledWith3 = p3
            calledWith4 = p4
            calledWith5 = p5
            calledWith6 = p6
            calledWith7 = p7
            "pong"
        }
        hubConnection.start()

        val sentMessage = transport.nextSentMessage
        transport.receiveMessage("{\"type\":1,\"invocationId\":\"1\",\"target\":\"ping\",\"arguments\":[\"10\",11,[1,2,3],true,\"yey\",1.337,\"hey\"]}$RECORD_SEPARATOR")
        val response = sentMessage.waitForResult()
        val expected = "{\"type\":3,\"invocationId\":\"1\",\"result\":\"pong\"}$RECORD_SEPARATOR"

        assertEquals(expected, response)
        assertEquals("10", calledWith)
        assertEquals(11, calledWith2)
        assertContentEquals(intArrayOf(1, 2, 3), calledWith3)
        assertEquals(true, calledWith4)
        assertEquals("yey", calledWith5)
        assertEquals(1.337, calledWith6)
        assertEquals("hey", calledWith7)
    }

    @Test
    fun `handler with eight params should return a value`() = runTest {
        var calledWith: String? = null
        var calledWith2: Int? = null
        var calledWith3: IntArray? = null
        var calledWith4: Boolean? = null
        var calledWith5: String? = null
        var calledWith6: Double? = null
        var calledWith7: String? = null
        var calledWith8: Int? = null

        hubConnection.on(
            "ping",
            resultType = String::class,
            paramType1 = String::class,
            paramType2 = Int::class,
            paramType3 = IntArray::class,
            paramType4 = Boolean::class,
            paramType5 = String::class,
            paramType6 = Double::class,
            paramType7 = String::class,
            paramType8 = Int::class,
        ) { p1, p2, p3, p4, p5, p6, p7, p8 ->
            calledWith = p1
            calledWith2 = p2
            calledWith3 = p3
            calledWith4 = p4
            calledWith5 = p5
            calledWith6 = p6
            calledWith7 = p7
            calledWith8 = p8
            "pong"
        }
        hubConnection.start()

        val sentMessage = transport.nextSentMessage
        transport.receiveMessage("{\"type\":1,\"invocationId\":\"1\",\"target\":\"ping\",\"arguments\":[\"10\",11,[1,2,3],true,\"yey\",1.337,\"hey\",123]}$RECORD_SEPARATOR")
        val response = sentMessage.waitForResult()
        val expected = "{\"type\":3,\"invocationId\":\"1\",\"result\":\"pong\"}$RECORD_SEPARATOR"

        assertEquals(expected, response)
        assertEquals("10", calledWith)
        assertEquals(11, calledWith2)
        assertContentEquals(intArrayOf(1, 2, 3), calledWith3)
        assertEquals(true, calledWith4)
        assertEquals("yey", calledWith5)
        assertEquals(1.337, calledWith6)
        assertEquals("hey", calledWith7)
        assertEquals(123, calledWith8)
    }

    @Test
    fun `handler with return value should not block other handlers`() = runTest {
        val resultCalled = Completable()
        val nonResultCalled = Completable()
        val completeResult = Completable()

        hubConnection.on("ping", resultType = String::class) {
            resultCalled.complete()
            completeResult.waitForCompletion()
            "pong"
        }
        hubConnection.on<Unit>("process") {
            nonResultCalled.complete()
        }
        hubConnection.start()

        val sentMessage = transport.nextSentMessage
        transport.receiveMessage("{\"type\":1,\"invocationId\":\"1\",\"target\":\"ping\",\"arguments\":[\"1\"]}$RECORD_SEPARATOR")
        resultCalled.waitForCompletion()

        // Send an non-result invocation and make sure it's processed even with a blocking result invocation
        transport.receiveMessage("{\"type\":1,\"target\":\"process\",\"arguments\":[\"1\"]}$RECORD_SEPARATOR")
        nonResultCalled.waitForCompletion()

        completeResult.complete()

        val response = sentMessage.waitForResult()
        val expected = "{\"type\":3,\"invocationId\":\"1\",\"result\":\"pong\"}$RECORD_SEPARATOR"
        assertEquals(expected, response)
    }

    @Test
    fun `handler should return an error if cannot parse argument`() = runTest {
        hubConnection.on("process", resultType = String::class, paramType1 = Int::class) { _ -> "bob" }
        hubConnection.start()

        val sentMessage = transport.nextSentMessage
        transport.receiveMessage("{\"type\":1,\"invocationId\":\"1\",\"target\":\"process\",\"arguments\":[\"corrupt int\"]}$RECORD_SEPARATOR")

        val response = sentMessage.waitForResult()
        val expected = "{\"type\":3,\"invocationId\":\"1\",\"error\":\"Failed to parse literal as 'int' value\\nJSON input: \\\"corrupt int\\\"\"}$RECORD_SEPARATOR"
        assertEquals(expected, response)
    }
}