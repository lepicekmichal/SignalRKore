// Licensed to the .NET Foundation under one or more agreements.
// The .NET Foundation licenses this file to you under the MIT license.
package eu.lepicekmichal.signalrkore

/**
 * Used to specify the transport the client will use.
 */
enum class TransportEnum {
    All,
    ServerSentEvents,
    LongPolling,
    WebSockets,
}

internal val List<AvailableTransport>.transports: List<TransportEnum>
    get() = mapNotNull {
        when (it.transport) {
            TransportEnumResponse.ServerSentEvents -> TransportEnum.ServerSentEvents
            TransportEnumResponse.LongPolling -> TransportEnum.LongPolling
            TransportEnumResponse.WebSockets -> TransportEnum.WebSockets
        }
    }