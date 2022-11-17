package eu.lepicekmichal.signalrkore

import kotlinx.serialization.Serializable

@Serializable
internal class AvailableTransport(
    val transport: TransportEnumResponse,
    val transferFormats: List<TransferFormat>,
)