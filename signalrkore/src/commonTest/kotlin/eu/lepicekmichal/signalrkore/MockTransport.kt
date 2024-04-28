package eu.lepicekmichal.signalrkore

import io.ktor.utils.io.core.toByteArray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MockTransport(
    private val ignorePings: Boolean = true,
    private val autoHandshake: Boolean = true
) : Transport {

    private val receivedMessages: MutableSharedFlow<String> = MutableSharedFlow()

    var nextSentMessage: SingleSubject<String> = SingleSubject()
        private set

    lateinit var url: String
        private set

    override suspend fun start(url: String) {
        this.url = url
        if (autoHandshake) {
            CoroutineScope(Dispatchers.IO).launch {
                receivedMessages.subscriptionCount.filter { it > 0 }.first()
                receivedMessages.emit("{}$RECORD_SEPARATOR")
            }
        }
    }

    override suspend fun send(message: ByteArray) {
        if (!ignorePings || !isPing(message)) {
            nextSentMessage.setResult(io.ktor.utils.io.core.String(message))
            nextSentMessage = SingleSubject()
        }
    }

    override fun receive(): Flow<ByteArray> = receivedMessages.map { it.toByteArray() }

    override suspend fun stop() { }

    suspend fun receiveMessage(message: String, waitForSubscriber: Boolean = true) {
        if (waitForSubscriber) {
            receivedMessages.subscriptionCount.filter { it > 0 }.first()
        }

        receivedMessages.emit(message)
    }

    private fun isPing(message: ByteArray): Boolean {
        return io.ktor.utils.io.core.String(message) == "{\"type\":6}$RECORD_SEPARATOR" ||
                (message[0].toInt() == 2 && message[1].toInt() == -111 && message[2].toInt() == 6)
    }
}