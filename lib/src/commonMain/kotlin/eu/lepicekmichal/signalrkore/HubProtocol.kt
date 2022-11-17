// Licensed to the .NET Foundation under one or more agreements.
// The .NET Foundation licenses this file to you under the MIT license.
package eu.lepicekmichal.signalrkore

/**
 * A protocol abstraction for communicating with SignalR hubs.
 */
interface HubProtocol {
    val name: String
    val version: Int

    /**
     * Creates a new list of [HubMessage]s.
     * @param payload A ByteBuffer representation of one or more [HubMessage]s.
     * @param binder The [InvocationBinder] to use for this Protocol instance.
     * @return A list of [HubMessage]s.
     */
    fun parseMessages(payload: ByteArray): List<HubMessage>

    /**
     * Writes the specified [HubMessage] to a String.
     * @param message The message to write.
     * @return A ByteBuffer representation of the message.
     */
    fun writeMessage(message: HubMessage): ByteArray
}