package eu.lepicekmichal.signalrkore

internal data class Negotiation(
    val transport: TransportEnum,
    val url: String,
    val headers: Map<String, String>,
)