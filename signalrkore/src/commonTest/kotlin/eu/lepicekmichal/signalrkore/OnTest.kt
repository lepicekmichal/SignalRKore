package eu.lepicekmichal.signalrkore

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class OnTest : HubTest() {

    @Test
    fun `multiple handlers with one parameter should all be triggered`() = runTest {
        val completable = Completable()
        val value = Single<Int>()
        val add: suspend (Int) -> Unit = {
            value.updateResult { value -> (value ?: 0) + it }
            if (value.result == 20) {
                completable.complete()
            }
        }

        hubConnection.on("add", add)
        hubConnection.on("add", add)
        hubConnection.start()

        transport.receiveMessage("{\"type\":1,\"target\":\"add\",\"arguments\":[10]}$RECORD_SEPARATOR")

        completable.waitForCompletion()
        assertEquals(20, value.result)
    }

    @Test
    fun `handler without parameter should be triggered`() = runTest {
        val completable = Completable()
        hubConnection.on<Unit>("test") { completable.complete() }
        hubConnection.start()

        transport.receiveMessage("{\"type\":1,\"target\":\"test\",\"arguments\":[]}$RECORD_SEPARATOR")
        completable.waitForCompletion()
    }

    @Test
    fun `handler with one parameter should be triggered`() = runTest {
        val completable = Completable()
        var calledWith: String? = null

        hubConnection.on("process",
            resultType = Unit::class,
            paramType1 = String::class,
        ) { p1 ->
            calledWith = p1
            completable.complete()
        }
        hubConnection.start()

        transport.receiveMessage("{\"type\":1,\"target\":\"process\",\"arguments\":[\"10\"]}$RECORD_SEPARATOR")
        completable.waitForCompletion()

        assertEquals("10", calledWith)
    }

    @Test
    fun `handler with two parameters should be triggered`() = runTest {
        val completable = Completable()
        var calledWith: String? = null
        var calledWith2: Int? = null

        hubConnection.on("process",
            resultType = Unit::class,
            paramType1 = String::class,
            paramType2 = Int::class,
        ) { p1, p2 ->
            calledWith = p1
            calledWith2 = p2
            completable.complete()
        }
        hubConnection.start()

        transport.receiveMessage("{\"type\":1,\"target\":\"process\",\"arguments\":[\"10\",11]}$RECORD_SEPARATOR")
        completable.waitForCompletion()

        assertEquals("10", calledWith)
        assertEquals(11, calledWith2)
    }

    @Test
    fun `handler with three parameters should be triggered`() = runTest {
        val completable = Completable()
        var calledWith: String? = null
        var calledWith2: Int? = null
        var calledWith3: IntArray? = null

        hubConnection.on(
            "process",
            resultType = Unit::class,
            paramType1 = String::class,
            paramType2 = Int::class,
            paramType3 = IntArray::class,
        ) { p1, p2, p3 ->
            calledWith = p1
            calledWith2 = p2
            calledWith3 = p3
            completable.complete()
        }
        hubConnection.start()

        transport.receiveMessage("{\"type\":1,\"target\":\"process\",\"arguments\":[\"10\",11,[1,2,3]]}$RECORD_SEPARATOR")
        completable.waitForCompletion()

        assertEquals("10", calledWith)
        assertEquals(11, calledWith2)
        assertContentEquals(intArrayOf(1, 2, 3), calledWith3)
    }

    @Test
    fun `handler with four parameters should be triggered`() = runTest {
        val completable = Completable()
        var calledWith: String? = null
        var calledWith2: Int? = null
        var calledWith3: IntArray? = null
        var calledWith4: Boolean? = null

        hubConnection.on(
            "process",
            resultType = Unit::class,
            paramType1 = String::class,
            paramType2 = Int::class,
            paramType3 = IntArray::class,
            paramType4 = Boolean::class,
        ) { p1, p2, p3, p4 ->
            calledWith = p1
            calledWith2 = p2
            calledWith3 = p3
            calledWith4 = p4
            completable.complete()
        }
        hubConnection.start()

        transport.receiveMessage("{\"type\":1,\"target\":\"process\",\"arguments\":[\"10\",11,[1,2,3],true]}$RECORD_SEPARATOR")
        completable.waitForCompletion()

        assertEquals("10", calledWith)
        assertEquals(11, calledWith2)
        assertContentEquals(intArrayOf(1, 2, 3), calledWith3)
        assertEquals(true, calledWith4)
    }

    @Test
    fun `handler with five parameters should be triggered`() = runTest {
        val completable = Completable()
        var calledWith: String? = null
        var calledWith2: Int? = null
        var calledWith3: IntArray? = null
        var calledWith4: Boolean? = null
        var calledWith5: String? = null

        hubConnection.on(
            "process",
            resultType = Unit::class,
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
            completable.complete()
        }
        hubConnection.start()

        transport.receiveMessage("{\"type\":1,\"target\":\"process\",\"arguments\":[\"10\",11,[1,2,3],true,\"yey\"]}$RECORD_SEPARATOR")
        completable.waitForCompletion()

        assertEquals("10", calledWith)
        assertEquals(11, calledWith2)
        assertContentEquals(intArrayOf(1, 2, 3), calledWith3)
        assertEquals(true, calledWith4)
        assertEquals("yey", calledWith5)
    }

    @Test
    fun `handler with six parameters should be triggered`() = runTest {
        val completable = Completable()
        var calledWith: String? = null
        var calledWith2: Int? = null
        var calledWith3: IntArray? = null
        var calledWith4: Boolean? = null
        var calledWith5: String? = null
        var calledWith6: Double? = null

        hubConnection.on(
            "process",
            resultType = Unit::class,
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
            completable.complete()
        }
        hubConnection.start()

        transport.receiveMessage("{\"type\":1,\"target\":\"process\",\"arguments\":[\"10\",11,[1,2,3],true,\"yey\",1.337]}$RECORD_SEPARATOR")
        completable.waitForCompletion()

        assertEquals("10", calledWith)
        assertEquals(11, calledWith2)
        assertContentEquals(intArrayOf(1, 2, 3), calledWith3)
        assertEquals(true, calledWith4)
        assertEquals("yey", calledWith5)
        assertEquals(1.337, calledWith6)
    }

    @Test
    fun `handler with seven parameters should be triggered`() = runTest {
        val completable = Completable()
        var calledWith: String? = null
        var calledWith2: Int? = null
        var calledWith3: IntArray? = null
        var calledWith4: Boolean? = null
        var calledWith5: String? = null
        var calledWith6: Double? = null
        var calledWith7: String? = null

        hubConnection.on(
            "process",
            resultType = Unit::class,
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
            completable.complete()
        }
        hubConnection.start()

        transport.receiveMessage("{\"type\":1,\"target\":\"process\",\"arguments\":[\"10\",11,[1,2,3],true,\"yey\",1.337,\"hey\"]}$RECORD_SEPARATOR")
        completable.waitForCompletion()

        assertEquals("10", calledWith)
        assertEquals(11, calledWith2)
        assertContentEquals(intArrayOf(1, 2, 3), calledWith3)
        assertEquals(true, calledWith4)
        assertEquals("yey", calledWith5)
        assertEquals(1.337, calledWith6)
        assertEquals("hey", calledWith7)
    }

    @Test
    fun `handler with eight parameters should be triggered`() = runTest {
        val completable = Completable()
        var calledWith: String? = null
        var calledWith2: Int? = null
        var calledWith3: IntArray? = null
        var calledWith4: Boolean? = null
        var calledWith5: String? = null
        var calledWith6: Double? = null
        var calledWith7: String? = null
        var calledWith8: Int? = null

        hubConnection.on(
            "process",
            resultType = Unit::class,
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
            completable.complete()
        }
        hubConnection.start()

        transport.receiveMessage("{\"type\":1,\"target\":\"process\",\"arguments\":[\"10\",11,[1,2,3],true,\"yey\",1.337,\"hey\",123]}$RECORD_SEPARATOR")
        completable.waitForCompletion()

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
    fun `one faulty handler should not stop triggering other handlers`() = runTest {
        val value1 = Single<String>()
        val value2 = Single<String>()

        hubConnection.on("process", resultType = Unit::class, paramType1 = String::class) { p1 ->
            value1.setResult(p1)
            throw RuntimeException("Not ok")
        }
        hubConnection.on("process", resultType = Unit::class, paramType1 = String::class) { p1 ->
            value2.setResult(p1)
        }
        hubConnection.start()

        transport.receiveMessage("{\"type\":1,\"target\":\"process\",\"arguments\":[\"Hello World\"]}$RECORD_SEPARATOR")

        assertEquals("Hello World", value1.waitForResult())
        assertEquals("Hello World", value2.waitForResult())

        val log = logger.assertLogEquals("Getting result for non-blocking invocation of 'process' method has thrown an exception")
        assertEquals(Logger.Severity.ERROR, log.severity)
        assertNotNull(log.cause)
        assertEquals("Not ok", log.cause.message)
    }
}