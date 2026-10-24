package eu.lepicekmichal.signalrkore

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationStrategy

/**
 * A payload of a [HubMessage]: an invocation argument, a stream item or a completion result.
 *
 * The [HubProtocol] owns the wire format of payloads. Outgoing payloads are [Encodable] values that the protocol encodes
 * with their serializer, incoming payloads are [Decodable] values that the protocol decodes once the receiver knows their type.
 */
interface HubValue {

    /**
     * A value to be sent, encoded by the [HubProtocol] with its [serializer].
     */
    class Encodable private constructor(val value: Any?, val serializer: SerializationStrategy<Any?>) : HubValue {

        override fun equals(other: Any?): Boolean =
            other is Encodable && value == other.value && serializer.descriptor == other.serializer.descriptor

        override fun hashCode(): Int = 31 * (value?.hashCode() ?: 0) + serializer.descriptor.hashCode()

        override fun toString(): String = value.toString()

        companion object {
            @Suppress("UNCHECKED_CAST")
            operator fun <T> invoke(value: T, serializer: SerializationStrategy<T>): Encodable =
                Encodable(value, serializer as SerializationStrategy<Any?>)
        }
    }

    /**
     * A received value in the wire format of the [HubProtocol] that parsed it.
     */
    interface Decodable : HubValue {
        /**
         * Decodes the value with the given [deserializer].
         */
        fun <T> decode(deserializer: DeserializationStrategy<T>): T
    }
}
