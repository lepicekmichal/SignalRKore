package eu.lepicekmichal.signalrkore

import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class HubConnectionTest {

    @Test
    fun `registering multiple handlers with parameter should all be triggered`() = runTest {
        val mockTransport = MockTransport()
        val hubConnection = createHubConnection(mockTransport)
        val completable = CompletableSubject()
        val value = SingleSubject<Double>()

        val action: (Double) -> Unit = {
            value.setResult((value.result ?: 0.0) + it)
            if (value.result == 24.0) {
                completable.complete()
            }
        }
        val job = launch { hubConnection.on("add", action) }
        val job2 = launch { hubConnection.on("add", action) }

        testScheduler.advanceUntilIdle()

        hubConnection.start()
        mockTransport.receiveMessage("{\"type\":1,\"target\":\"add\",\"arguments\":[12]}$RECORD_SEPARATOR")

        // Confirming that our handler was called and the correct message was passed in.
        completable.waitForCompletion()
        assertEquals(24.0, value.result)

        job.cancel()
        job2.cancel()
        hubConnection.stop()
    }

    @Test
    fun `handler without param should be triggered`() = runTest {
        val mockTransport = MockTransport()
        val hubConnection = createHubConnection(mockTransport)

        val completable = CompletableSubject()
        val job = launch {
            hubConnection.on("inc") { completable.complete() }
        }

        testScheduler.advanceUntilIdle()

        hubConnection.start()
        mockTransport.receiveMessage("{\"type\":1,\"target\":\"inc\",\"arguments\":[]}$RECORD_SEPARATOR")
        completable.waitForCompletion()

        job.cancel()
        hubConnection.stop()
    }

    @Test
    fun `handler with one param should be triggered`() = runTest {
        val mockTransport = MockTransport()
        val hubConnection = createHubConnection(mockTransport)
        val completable = CompletableSubject()

        var calledWith: String? = null
        val job = launch {
            hubConnection.on("inc", param1 = String::class) { p1 ->
                calledWith = p1
                completable.complete()
            }
        }

        testScheduler.advanceUntilIdle()

        hubConnection.start()
        mockTransport.receiveMessage("{\"type\":1,\"target\":\"inc\",\"arguments\":[\"1\"]}$RECORD_SEPARATOR")
        completable.waitForCompletion()

        assertEquals("1", calledWith)

        job.cancel()
        hubConnection.stop()
    }

    @Test
    fun `handler with two params should be triggered`() = runTest {
        val mockTransport = MockTransport()
        val hubConnection = createHubConnection(mockTransport)
        val completable = CompletableSubject()

        var calledWith: String? = null
        var calledWith2: Int? = null
        val job = launch {
            hubConnection.on("inc", param1 = String::class, param2 = Int::class) { p1, p2 ->
                calledWith = p1
                calledWith2 = p2
                completable.complete()
            }
        }

        testScheduler.advanceUntilIdle()

        hubConnection.start()
        mockTransport.receiveMessage("{\"type\":1,\"target\":\"inc\",\"arguments\":[\"1\",13]}$RECORD_SEPARATOR")
        completable.waitForCompletion()

        assertEquals("1", calledWith)
        assertEquals(13, calledWith2)

        job.cancel()
        hubConnection.stop()
    }

    @Test
    fun `handler with three params should be triggered`() = runTest {
        val mockTransport = MockTransport()
        val hubConnection = createHubConnection(mockTransport)
        val completable = CompletableSubject()

        var calledWith: String? = null
        var calledWith2: Int? = null
        var calledWith3: IntArray? = null
        val job = launch {
            hubConnection.on(
                "inc",
                param1 = String::class,
                param2 = Int::class,
                param3 = IntArray::class,
            ) { p1, p2, p3 ->
                calledWith = p1
                calledWith2 = p2
                calledWith3 = p3
                completable.complete()
            }
        }

        testScheduler.advanceUntilIdle()

        hubConnection.start()
        mockTransport.receiveMessage("{\"type\":1,\"target\":\"inc\",\"arguments\":[\"1\",13,[1,2,3]]}$RECORD_SEPARATOR")
        completable.waitForCompletion()

        assertEquals("1", calledWith)
        assertEquals(13, calledWith2)
        assertContentEquals(intArrayOf(1, 2, 3), calledWith3)

        job.cancel()
        hubConnection.stop()
    }

    @Test
    fun `handler with four params should be triggered`() = runTest {
        val mockTransport = MockTransport()
        val hubConnection = createHubConnection(mockTransport)
        val completable = CompletableSubject()

        var calledWith: String? = null
        var calledWith2: Int? = null
        var calledWith3: IntArray? = null
        var calledWith4: Boolean? = null
        val job = launch {
            hubConnection.on(
                "inc",
                param1 = String::class,
                param2 = Int::class,
                param3 = IntArray::class,
                param4 = Boolean::class,
            ) { p1, p2, p3, p4 ->
                calledWith = p1
                calledWith2 = p2
                calledWith3 = p3
                calledWith4 = p4
                completable.complete()
            }
        }

        testScheduler.advanceUntilIdle()

        hubConnection.start()
        mockTransport.receiveMessage("{\"type\":1,\"target\":\"inc\",\"arguments\":[\"1\",13,[1,2,3],true]}$RECORD_SEPARATOR")
        completable.waitForCompletion()

        assertEquals("1", calledWith)
        assertEquals(13, calledWith2)
        assertContentEquals(intArrayOf(1, 2, 3), calledWith3)
        assertEquals(true, calledWith4)

        job.cancel()
        hubConnection.stop()
    }

    @Test
    fun `handler with five params should be triggered`() = runTest {
        val mockTransport = MockTransport()
        val hubConnection = createHubConnection(mockTransport)
        val completable = CompletableSubject()

        var calledWith: String? = null
        var calledWith2: Int? = null
        var calledWith3: IntArray? = null
        var calledWith4: Boolean? = null
        var calledWith5: String? = null
        val job = launch {
            hubConnection.on(
                "inc",
                param1 = String::class,
                param2 = Int::class,
                param3 = IntArray::class,
                param4 = Boolean::class,
                param5 = String::class,
            ) { p1, p2, p3, p4, p5 ->
                calledWith = p1
                calledWith2 = p2
                calledWith3 = p3
                calledWith4 = p4
                calledWith5 = p5
                completable.complete()
            }
        }

        testScheduler.advanceUntilIdle()

        hubConnection.start()
        mockTransport.receiveMessage("{\"type\":1,\"target\":\"inc\",\"arguments\":[\"1\",13,[1,2,3],true,\"t\"]}$RECORD_SEPARATOR")
        completable.waitForCompletion()

        assertEquals("1", calledWith)
        assertEquals(13, calledWith2)
        assertContentEquals(intArrayOf(1, 2, 3), calledWith3)
        assertEquals(true, calledWith4)
        assertEquals("t", calledWith5)

        job.cancel()
        hubConnection.stop()
    }

    @Test
    fun `handler with six params should be triggered`() = runTest {
        val mockTransport = MockTransport()
        val hubConnection = createHubConnection(mockTransport)
        val completable = CompletableSubject()

        var calledWith: String? = null
        var calledWith2: Int? = null
        var calledWith3: IntArray? = null
        var calledWith4: Boolean? = null
        var calledWith5: String? = null
        var calledWith6: Double? = null
        val job = launch {
            hubConnection.on(
                "inc",
                param1 = String::class,
                param2 = Int::class,
                param3 = IntArray::class,
                param4 = Boolean::class,
                param5 = String::class,
                param6 = Double::class,
            ) { p1, p2, p3, p4, p5, p6 ->
                calledWith = p1
                calledWith2 = p2
                calledWith3 = p3
                calledWith4 = p4
                calledWith5 = p5
                calledWith6 = p6
                completable.complete()
            }
        }

        testScheduler.advanceUntilIdle()

        hubConnection.start()
        mockTransport.receiveMessage("{\"type\":1,\"target\":\"inc\",\"arguments\":[\"1\",13,[1,2,3],true,\"t\",1.5]}$RECORD_SEPARATOR")
        completable.waitForCompletion()

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
    fun `handler with seven params should be triggered`() = runTest {
        val mockTransport = MockTransport()
        val hubConnection = createHubConnection(mockTransport)
        val completable = CompletableSubject()

        var calledWith: String? = null
        var calledWith2: Int? = null
        var calledWith3: IntArray? = null
        var calledWith4: Boolean? = null
        var calledWith5: String? = null
        var calledWith6: Double? = null
        var calledWith7: String? = null
        val job = launch {
            hubConnection.on(
                "inc",
                param1 = String::class,
                param2 = Int::class,
                param3 = IntArray::class,
                param4 = Boolean::class,
                param5 = String::class,
                param6 = Double::class,
                param7 = String::class,
            ) { p1, p2, p3, p4, p5, p6, p7 ->
                calledWith = p1
                calledWith2 = p2
                calledWith3 = p3
                calledWith4 = p4
                calledWith5 = p5
                calledWith6 = p6
                calledWith7 = p7
                completable.complete()
            }
        }

        testScheduler.advanceUntilIdle()

        hubConnection.start()
        mockTransport.receiveMessage("{\"type\":1,\"target\":\"inc\",\"arguments\":[\"1\",13,[1,2,3],true,\"t\",1.5,\"h\"]}$RECORD_SEPARATOR")
        completable.waitForCompletion()

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
    fun `handler with eight params should be triggered`() = runTest {
        val mockTransport = MockTransport()
        val hubConnection = createHubConnection(mockTransport)
        val completable = CompletableSubject()

        var calledWith: String? = null
        var calledWith2: Int? = null
        var calledWith3: IntArray? = null
        var calledWith4: Boolean? = null
        var calledWith5: String? = null
        var calledWith6: Double? = null
        var calledWith7: String? = null
        var calledWith8: Int? = null
        val job = launch {
            hubConnection.on(
                "inc",
                param1 = String::class,
                param2 = Int::class,
                param3 = IntArray::class,
                param4 = Boolean::class,
                param5 = String::class,
                param6 = Double::class,
                param7 = String::class,
                param8 = Int::class,
            ) { p1, p2, p3, p4, p5, p6, p7, p8 ->
                calledWith = p1
                calledWith2 = p2
                calledWith3 = p3
                calledWith4 = p4
                calledWith5 = p5
                calledWith6 = p6
                calledWith7 = p7
                calledWith8 = p8
                completable.complete()
            }
        }

        testScheduler.advanceUntilIdle()

        hubConnection.start()
        mockTransport.receiveMessage("{\"type\":1,\"target\":\"inc\",\"arguments\":[\"1\",13,[1,2,3],true,\"t\",1.5,\"h\",33]}$RECORD_SEPARATOR")
        completable.waitForCompletion()

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
    fun `throwing from one handler should not stop triggering other handlers`() = runTest {
        val mockTransport = MockTransport()
        val testLogger = TestLogger()
        val hubConnection = createHubConnection(mockTransport) {
            logger = testLogger
        }
        val value1 = SingleSubject<String>()
        val value2 = SingleSubject<String>()

        val job = launch {
            hubConnection.on("inc", param1 = String::class) { p1 ->
                value1.setResult(p1)
                throw RuntimeException("throw from on handler")
            }
        }

        val job2 = launch {
            hubConnection.on("inc", param1 = String::class) { p1 ->
                value2.setResult(p1)
            }
        }

        testScheduler.advanceUntilIdle()

        hubConnection.start()
        mockTransport.receiveMessage("{\"type\":1,\"target\":\"inc\",\"arguments\":[\"Hello World\"]}$RECORD_SEPARATOR")

        // Confirming that our handler was called and the correct message was passed in.
        assertEquals("Hello World", value1.waitForResult())
        assertEquals("Hello World", value2.waitForResult())

        val log = testLogger.assertLogEquals("Invoking client side method 'inc' failed: java.lang.RuntimeException: throw from on handler")
        assertEquals(Logger.Level.ERROR, log.level)

        job.cancel()
        job2.cancel()
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