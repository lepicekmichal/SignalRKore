package eu.lepicekmichal.signalrkore

sealed class HubMessage {
    abstract val type: Int

    sealed class Invocation : HubMessage() {

        abstract val target: String
        abstract val arguments: List<HubValue>
        abstract val streamIds: List<String>?

        override val type: Int = HubMessageType.INVOCATION.value

        data class Blocking(
            override val target: String,
            override val arguments: List<HubValue>,
            val invocationId: String,
            override val streamIds: List<String>? = null,
        ) : Invocation()

        data class NonBlocking(
            override val target: String,
            override val arguments: List<HubValue>,
            override val streamIds: List<String>? = null,
        ) : Invocation()
    }

    data class StreamInvocation(
        val target: String,
        val arguments: List<HubValue>,
        val invocationId: String,
        val streamIds: List<String>? = null,
    ) : HubMessage() {
        override val type: Int = HubMessageType.STREAM_INVOCATION.value
    }

    sealed class Completion : HubMessage() {

        abstract val invocationId: String

        override val type: Int = HubMessageType.COMPLETION.value

        data class Simple(override val invocationId: String) : Completion()

        data class Resulted(override val invocationId: String, val result: HubValue) : Completion()

        data class Error(override val invocationId: String, val error: String) : Completion()
    }

    class Ping : HubMessage() {
        override val type: Int = HubMessageType.PING.value
    }

    data class Close(
        val allowReconnect: Boolean = false,
        val error: String? = null,
    ) : HubMessage() {
        override val type: Int = HubMessageType.CLOSE.value
    }

    data class StreamItem(
        val invocationId: String,
        val item: HubValue,
    ) : HubMessage() {
        override val type: Int = HubMessageType.STREAM_ITEM.value
    }

    data class CancelInvocation(
        val invocationId: String,
    ) : HubMessage() {
        override val type: Int = HubMessageType.CANCEL_INVOCATION.value
    }
}
