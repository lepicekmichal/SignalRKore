package eu.lepicekmichal.signalrkore

import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class HubConnectionReturnResultTest {

    @Test
    fun `handler with no params should return a value`() = runTest {
        val mockTransport = MockTransport()
        val hubConnection = createHubConnection(mockTransport)

        var called = false
        val job = launch {
            hubConnection.onWithResult<Int>("inc") {
                called = true
                10
            }
        }

        testScheduler.advanceUntilIdle()

        hubConnection.start()
        val sentMessage = mockTransport.nextSentMessage
        mockTransport.receiveMessage("{\"type\":1,\"invocationId\":\"1\",\"target\":\"inc\",\"arguments\":[]}$RECORD_SEPARATOR")
        val response = sentMessage.waitForResult()
        val expected = "{\"type\":3,\"invocationId\":\"1\",\"result\":10}$RECORD_SEPARATOR"

        assertEquals(expected, response)
        assertEquals(true, called)

        job.cancel()
        hubConnection.stop()
    }

    @Test
    fun `handler with no return value should report an error`() = runTest {
        val mockTransport = MockTransport()
        val hubConnection = createHubConnection(mockTransport)
        val nonResultCalled = CompletableSubject()
        val job = launch {
            hubConnection.on("inc") {
                nonResultCalled.complete()
            }
        }

        testScheduler.advanceUntilIdle()

        hubConnection.start()
        val sentMessage = mockTransport.nextSentMessage
        mockTransport.receiveMessage("{\"type\":1,\"invocationId\":\"1\",\"target\":\"inc\",\"arguments\":[]}$RECORD_SEPARATOR")
        val response = sentMessage.waitForResult()
        val expected = "{\"type\":3,\"invocationId\":\"1\",\"error\":\"Client did not provide a result.\"}$RECORD_SEPARATOR"

        assertEquals(expected, response)
        nonResultCalled.waitForCompletion()

        job.cancel()
        hubConnection.stop()
    }

    @Test
    fun `missing handler with return value should report an error`() = runTest {
        val mockTransport = MockTransport()
        val hubConnection = createHubConnection(mockTransport)

        hubConnection.start()
        val sentMessage = mockTransport.nextSentMessage
        mockTransport.receiveMessage("{\"type\":1,\"invocationId\":\"1\",\"target\":\"inc\",\"arguments\":[]}$RECORD_SEPARATOR")
        val response = sentMessage.waitForResult()
        val expected = "{\"type\":3,\"invocationId\":\"1\",\"error\":\"Client did not provide a result.\"}$RECORD_SEPARATOR"

        assertEquals(expected, response)

        hubConnection.stop()
    }

    @Test
    fun `faulty handler should return an error`() = runTest {
        val mockTransport = MockTransport()
        val hubConnection = createHubConnection(mockTransport)

        val job = launch {
            hubConnection.onWithResult<Int>("inc") {
                throw RuntimeException("Custom error.")
            }
        }

        testScheduler.advanceUntilIdle()

        hubConnection.start()
        val sentMessage = mockTransport.nextSentMessage
        mockTransport.receiveMessage("{\"type\":1,\"invocationId\":\"1\",\"target\":\"inc\",\"arguments\":[]}$RECORD_SEPARATOR")
        val response = sentMessage.waitForResult()
        val expected = "{\"type\":3,\"invocationId\":\"1\",\"error\":\"Custom error.\"}$RECORD_SEPARATOR"

        assertEquals(expected, response)

        job.cancel()
        hubConnection.stop()
    }

    @Test
    fun `registering multiple handler should raise an exception`() = runTest {
        val mockTransport = MockTransport()
        val hubConnection = createHubConnection(mockTransport)

        val job = launch {
            hubConnection.onWithResult<String>("inc") { "value" }
        }

        testScheduler.advanceUntilIdle()

        val exception = assertFailsWith<RuntimeException> {
            hubConnection.onWithResult<String>("inc") { "value2" }
        }
        assertEquals("'inc' already has a value returning handler. Multiple return values are not supported.", exception.message)

        job.cancel()
    }


    @Test
    fun `handler with result should log if server does not expect a return value`() = runTest {
        val mockTransport = MockTransport()
        val testLogger = TestLogger()
        val hubConnection = createHubConnection(mockTransport) {
            logger = testLogger
        }
        val nonResultCalled = CompletableSubject()

        val job = launch {
            hubConnection.onWithResult<Int>("m") { 42 }
        }

        val job2 = launch {
            hubConnection.on("fin") {
                nonResultCalled.complete()
            }
        }

        testScheduler.advanceUntilIdle()

        hubConnection.start()
        mockTransport.receiveMessage("{\"type\":1,\"target\":\"m\",\"arguments\":[]}$RECORD_SEPARATOR")
        // send another invocation message and wait for it to be processed to make sure the first invocation was processed
        mockTransport.receiveMessage("{\"type\":1,\"target\":\"fin\",\"arguments\":[]}$RECORD_SEPARATOR")

        nonResultCalled.waitForCompletion()

        testLogger.assertLogEquals("Result given for 'm' method but server is not expecting a result.")

        job.cancel()
        job2.cancel()
        hubConnection.stop()
    }

    @Test
    fun `handler with one param should return a value`() = runTest {
        val mockTransport = MockTransport()
        val hubConnection = createHubConnection(mockTransport)

        var calledWith: String? = null
        val job = launch {
            hubConnection.onWithResult<String, Int>("inc") {
                calledWith = it
                10
            }
        }

        testScheduler.advanceUntilIdle()

        hubConnection.start()
        val sentMessage = mockTransport.nextSentMessage
        mockTransport.receiveMessage("{\"type\":1,\"invocationId\":\"1\",\"target\":\"inc\",\"arguments\":[\"1\"]}$RECORD_SEPARATOR")
        val response = sentMessage.waitForResult()
        val expected = "{\"type\":3,\"invocationId\":\"1\",\"result\":10}$RECORD_SEPARATOR"

        assertEquals(expected, response)
        assertEquals("1", calledWith)

        job.cancel()
        hubConnection.stop()
    }

    @Test
    fun `handler with two params should return a value`() = runTest {
        val mockTransport = MockTransport()
        val hubConnection = createHubConnection(mockTransport)

        var calledWith: String? = null
        var calledWith2: Int? = null
        val job = launch {
            hubConnection.onWithResult<String, Int, String>("inc") { p1, p2 ->
                calledWith = p1
                calledWith2 = p2
                "bob"
            }
        }

        testScheduler.advanceUntilIdle()

        hubConnection.start()
        val sentMessage = mockTransport.nextSentMessage
        mockTransport.receiveMessage("{\"type\":1,\"invocationId\":\"1\",\"target\":\"inc\",\"arguments\":[\"1\", 13]}$RECORD_SEPARATOR")
        val response = sentMessage.waitForResult()
        val expected = "{\"type\":3,\"invocationId\":\"1\",\"result\":\"bob\"}$RECORD_SEPARATOR"

        assertEquals(expected, response)
        assertEquals("1", calledWith)
        assertEquals(13, calledWith2)

        job.cancel()
        hubConnection.stop()
    }

    @Test
    fun `handler with three params should return a value`() = runTest {
        val mockTransport = MockTransport()
        val hubConnection = createHubConnection(mockTransport)

        var calledWith: String? = null
        var calledWith2: Int? = null
        var calledWith3: IntArray? = null
        val job = launch {
            hubConnection.onWithResult<String, Int, IntArray, String>("inc") { p1, p2, p3 ->
                calledWith = p1
                calledWith2 = p2
                calledWith3 = p3
                "bob"
            }
        }

        testScheduler.advanceUntilIdle()

        hubConnection.start()
        val sentMessage = mockTransport.nextSentMessage
        mockTransport.receiveMessage("{\"type\":1,\"invocationId\":\"1\",\"target\":\"inc\",\"arguments\":[\"1\",13,[1,2,3]]}$RECORD_SEPARATOR")
        val response = sentMessage.waitForResult()
        val expected = "{\"type\":3,\"invocationId\":\"1\",\"result\":\"bob\"}$RECORD_SEPARATOR"

        assertEquals(expected, response)
        assertEquals("1", calledWith)
        assertEquals(13, calledWith2)
        assertContentEquals(intArrayOf(1, 2, 3), calledWith3)

        job.cancel()
        hubConnection.stop()
    }

    @Test
    fun `handler with four params should return a value`() = runTest {
        val mockTransport = MockTransport()
        val hubConnection = createHubConnection(mockTransport)

        var calledWith: String? = null
        var calledWith2: Int? = null
        var calledWith3: IntArray? = null
        var calledWith4: Boolean? = null
        val job = launch {
            hubConnection.onWithResult<String, Int, IntArray, Boolean, String>("inc") { p1, p2, p3, p4 ->
                calledWith = p1
                calledWith2 = p2
                calledWith3 = p3
                calledWith4 = p4
                "bob"
            }
        }

        testScheduler.advanceUntilIdle()

        hubConnection.start()
        val sentMessage = mockTransport.nextSentMessage
        mockTransport.receiveMessage("{\"type\":1,\"invocationId\":\"1\",\"target\":\"inc\",\"arguments\":[\"1\",13,[1,2,3],true]}$RECORD_SEPARATOR")
        val response = sentMessage.waitForResult()
        val expected = "{\"type\":3,\"invocationId\":\"1\",\"result\":\"bob\"}$RECORD_SEPARATOR"

        assertEquals(expected, response)
        assertEquals("1", calledWith)
        assertEquals(13, calledWith2)
        assertContentEquals(intArrayOf(1, 2, 3), calledWith3)
        assertEquals(true, calledWith4)

        job.cancel()
        hubConnection.stop()
    }

    @Test
    fun `handler with five params should return a value`() = runTest {
        val mockTransport = MockTransport()
        val hubConnection = createHubConnection(mockTransport)

        var calledWith: String? = null
        var calledWith2: Int? = null
        var calledWith3: IntArray? = null
        var calledWith4: Boolean? = null
        var calledWith5: String? = null
        val job = launch {
            hubConnection.onWithResult<String, Int, IntArray, Boolean, String, String>("inc") { p1, p2, p3, p4, p5 ->
                calledWith = p1
                calledWith2 = p2
                calledWith3 = p3
                calledWith4 = p4
                calledWith5 = p5
                "bob"
            }
        }

        testScheduler.advanceUntilIdle()

        hubConnection.start()
        val sentMessage = mockTransport.nextSentMessage
        mockTransport.receiveMessage("{\"type\":1,\"invocationId\":\"1\",\"target\":\"inc\",\"arguments\":[\"1\",13,[1,2,3],true,\"t\"]}$RECORD_SEPARATOR")
        val response = sentMessage.waitForResult()
        val expected = "{\"type\":3,\"invocationId\":\"1\",\"result\":\"bob\"}$RECORD_SEPARATOR"

        assertEquals(expected, response)
        assertEquals("1", calledWith)
        assertEquals(13, calledWith2)
        assertContentEquals(intArrayOf(1, 2, 3), calledWith3)
        assertEquals(true, calledWith4)
        assertEquals("t", calledWith5)

        job.cancel()
        hubConnection.stop()
    }

    @Test
    fun `handler with six params should return a value`() = runTest {
        val mockTransport = MockTransport()
        val hubConnection = createHubConnection(mockTransport)

        var calledWith: String? = null
        var calledWith2: Int? = null
        var calledWith3: IntArray? = null
        var calledWith4: Boolean? = null
        var calledWith5: String? = null
        var calledWith6: Double? = null
        val job = launch {
            hubConnection.onWithResult<String, Int, IntArray, Boolean, String, Double, String>("inc") { p1, p2, p3, p4, p5, p6 ->
                calledWith = p1
                calledWith2 = p2
                calledWith3 = p3
                calledWith4 = p4
                calledWith5 = p5
                calledWith6 = p6
                "bob"
            }
        }

        testScheduler.advanceUntilIdle()

        hubConnection.start()
        val sentMessage = mockTransport.nextSentMessage
        mockTransport.receiveMessage("{\"type\":1,\"invocationId\":\"1\",\"target\":\"inc\",\"arguments\":[\"1\",13,[1,2,3],true,\"t\",1.5]}$RECORD_SEPARATOR")
        val response = sentMessage.waitForResult()
        val expected = "{\"type\":3,\"invocationId\":\"1\",\"result\":\"bob\"}$RECORD_SEPARATOR"

        assertEquals(expected, response)
        assertEquals("1", calledWith)
        assertEquals(13, calledWith2)
        assertContentEquals(intArrayOf(1, 2, 3), calledWith3)
        assertEquals(true, calledWith4)
        assertEquals("t", calledWith5)
        assertEquals(1.5, calledWith6)

        job.cancel()
        hubConnection.stop()
    }

    @Test
    fun `handler with seven params should return a value`() = runTest {
        val mockTransport = MockTransport()
        val hubConnection = createHubConnection(mockTransport)

        var calledWith: String? = null
        var calledWith2: Int? = null
        var calledWith3: IntArray? = null
        var calledWith4: Boolean? = null
        var calledWith5: String? = null
        var calledWith6: Double? = null
        var calledWith7: String? = null
        val job = launch {
            hubConnection.onWithResult<String, Int, IntArray, Boolean, String, Double, String, String>("inc") { p1, p2, p3, p4, p5, p6, p7 ->
                calledWith = p1
                calledWith2 = p2
                calledWith3 = p3
                calledWith4 = p4
                calledWith5 = p5
                calledWith6 = p6
                calledWith7 = p7
                "bob"
            }
        }

        testScheduler.advanceUntilIdle()

        hubConnection.start()
        val sentMessage = mockTransport.nextSentMessage
        mockTransport.receiveMessage("{\"type\":1,\"invocationId\":\"1\",\"target\":\"inc\",\"arguments\":[\"1\",13,[1,2,3],true,\"t\",1.5,\"h\"]}$RECORD_SEPARATOR")
        val response = sentMessage.waitForResult()
        val expected = "{\"type\":3,\"invocationId\":\"1\",\"result\":\"bob\"}$RECORD_SEPARATOR"

        assertEquals(expected, response)
        assertEquals("1", calledWith)
        assertEquals(13, calledWith2)
        assertContentEquals(intArrayOf(1, 2, 3), calledWith3)
        assertEquals(true, calledWith4)
        assertEquals("t", calledWith5)
        assertEquals(1.5, calledWith6)
        assertEquals("h", calledWith7)

        job.cancel()
        hubConnection.stop()
    }

    @Test
    fun `handler with eight params should return a value`() = runTest {
        val mockTransport = MockTransport()
        val hubConnection = createHubConnection(mockTransport)

        var calledWith: String? = null
        var calledWith2: Int? = null
        var calledWith3: IntArray? = null
        var calledWith4: Boolean? = null
        var calledWith5: String? = null
        var calledWith6: Double? = null
        var calledWith7: String? = null
        var calledWith8: Int? = null
        val job = launch {
            hubConnection.onWithResult<String, Int, IntArray, Boolean, String, Double, String, Int, String>("inc") { p1, p2, p3, p4, p5, p6, p7, p8 ->
                calledWith = p1
                calledWith2 = p2
                calledWith3 = p3
                calledWith4 = p4
                calledWith5 = p5
                calledWith6 = p6
                calledWith7 = p7
                calledWith8 = p8
                "bob"
            }
        }

        testScheduler.advanceUntilIdle()

        hubConnection.start()
        val sentMessage = mockTransport.nextSentMessage
        mockTransport.receiveMessage("{\"type\":1,\"invocationId\":\"1\",\"target\":\"inc\",\"arguments\":[\"1\",13,[1,2,3],true,\"t\",1.5,\"h\",33]}$RECORD_SEPARATOR")
        val response = sentMessage.waitForResult()
        val expected = "{\"type\":3,\"invocationId\":\"1\",\"result\":\"bob\"}$RECORD_SEPARATOR"

        assertEquals(expected, response)
        assertEquals("1", calledWith)
        assertEquals(13, calledWith2)
        assertContentEquals(intArrayOf(1, 2, 3), calledWith3)
        assertEquals(true, calledWith4)
        assertEquals("t", calledWith5)
        assertEquals(1.5, calledWith6)
        assertEquals("h", calledWith7)
        assertEquals(33, calledWith8)

        job.cancel()
        hubConnection.stop()
    }

    @Test
    fun `handler with return value should not block other handlers`() = runTest {
        val mockTransport = MockTransport()
        val hubConnection = createHubConnection(mockTransport)
        val resultCalled = CompletableSubject()
        val nonResultCalled = CompletableSubject()
        val completeResult = CompletableSubject()

        val job = launch {
            hubConnection.onWithResult<String>("inc") {
                resultCalled.complete()
                completeResult.waitForCompletion()
                "bob"
            }
        }

        val job2 = launch {
            hubConnection.on("inc2") {
                nonResultCalled.complete()
            }
        }

        testScheduler.advanceUntilIdle()

        hubConnection.start()
        val sentMessage = mockTransport.nextSentMessage
        mockTransport.receiveMessage("{\"type\":1,\"invocationId\":\"1\",\"target\":\"inc\",\"arguments\":[\"1\"]}$RECORD_SEPARATOR")
        resultCalled.waitForCompletion()

        // Send an non-result invocation and make sure it's processed even with a blocking result invocation
        mockTransport.receiveMessage("{\"type\":1,\"target\":\"inc2\",\"arguments\":[\"1\"]}$RECORD_SEPARATOR")
        nonResultCalled.waitForCompletion()

        completeResult.complete()

        val response = sentMessage.waitForResult()
        val expected = "{\"type\":3,\"invocationId\":\"1\",\"result\":\"bob\"}$RECORD_SEPARATOR"
        assertEquals(expected, response)

        job.cancel()
        job2.cancel()
        hubConnection.stop()
    }

    @Test
    fun `handler should return an error if cannot parse argument`() = runTest {
        val mockTransport = MockTransport()
        val hubConnection = createHubConnection(mockTransport)

        val job = launch {
            hubConnection.onWithResult<Int, String>("inc") { "bob" }
        }

        testScheduler.advanceUntilIdle()

        hubConnection.start()
        val sentMessage = mockTransport.nextSentMessage
        mockTransport.receiveMessage("{\"type\":1,\"invocationId\":\"1\",\"target\":\"inc\",\"arguments\":[\"not int\"]}$RECORD_SEPARATOR")

        val response = sentMessage.waitForResult()
        val expected = "{\"type\":3,\"invocationId\":\"1\",\"error\":\"Failed to parse literal as 'int' value\\nJSON input: \\\"not int\\\"\"}$RECORD_SEPARATOR"
        assertEquals(expected, response)

        job.cancel()
        hubConnection.stop()
    }

    private fun createHubConnection(customTransport: Transport, block: HttpHubConnectionBuilder.() -> Unit = {}): HubConnection {
        val builder = HttpHubConnectionBuilder("http://example.com").apply {
            transport = customTransport
            skipNegotiate = true
            transportEnum = TransportEnum.WebSockets
        }
        builder.apply(block)

        return builder.build()
    }
}