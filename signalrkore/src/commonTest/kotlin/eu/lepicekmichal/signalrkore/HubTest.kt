package eu.lepicekmichal.signalrkore

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.AfterTest

abstract class HubTest {
    protected lateinit var hubConnection: HubConnection
    protected lateinit var transport: MockTransport
    protected lateinit var logger: TestLogger

    @BeforeTest
    fun setup() {
        transport = MockTransport()
        logger = TestLogger()
        hubConnection = createHubConnection(transport) {
            logger = this@HubTest.logger
            // By using Unconfined dispatcher, we assure that on method will start collecting before the connection is started
            dispatcher = Dispatchers.Unconfined
        }
    }

    @AfterTest
    fun cleanup() = runTest {
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