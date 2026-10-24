package eu.lepicekmichal.signalrkore

internal data class Negotiation(
    val transports: List<TransportEnum>,
    val url: String,
    val headers: Map<String, String>,
)