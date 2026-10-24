// Licensed to the .NET Foundation under one or more agreements.
// The .NET Foundation licenses this file to you under the MIT license.
package eu.lepicekmichal.signalrkore

/**
 * A protocol abstraction for communicating with SignalR hubs.
 *
 * The protocol owns the encoding of [HubValue] payloads: it encodes [HubValue.Encodable] values when writing a message
 * and parses payloads into [HubValue.Decodable] values that are decoded once the receiver asks for a type.
 */
interface HubProtocol {
    val name: String
    val version: Int

    /**
     * The format of the data the protocol produces. Binary protocols cannot use transports that only carry text.
     */
    val transferFormat: TransferFormat

    /**
     * Creates a new list of [HubMessage]s.
     * @param payload A ByteArray representation of one or more [HubMessage]s.
     * @return A list of [HubMessage]s.
     */
    fun parseMessages(payload: ByteArray): List<HubMessage>

    /**
     * Writes the specified [HubMessage] to a ByteArray.
     * @param message The message to write.
     * @return A ByteArray representation of the message.
     */
    fun writeMessage(message: HubMessage): ByteArray
}
