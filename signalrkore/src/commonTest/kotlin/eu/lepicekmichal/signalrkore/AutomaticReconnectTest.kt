package eu.lepicekmichal.signalrkore

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration

class AutomaticReconnectTest {

    private suspend fun AutomaticReconnect.delays(attempts: Int) =
        (this as AutomaticReconnect.Custom).let { policy -> List(attempts) { policy.invoke(previousRetryCount = it, elapsedTime = Duration.ZERO) } }

    @Test
    fun activeRetriesFourTimes() = runTest {
        assertEquals(listOf(0L, 2_000L, 10_000L, 30_000L, null), AutomaticReconnect.Active.delays(5))
    }

    @Test
    fun exponentialBackoffGrowsUntilMaxDelay() = runTest {
        val policy = AutomaticReconnect.exponentialBackoff(times = 4, initialDelay = 1_000, factor = 2.0, maxDelay = 5_000)

        assertEquals(listOf(1_000L, 2_000L, 4_000L, 5_000L, 5_000L, null), policy.delays(6))
    }
}
