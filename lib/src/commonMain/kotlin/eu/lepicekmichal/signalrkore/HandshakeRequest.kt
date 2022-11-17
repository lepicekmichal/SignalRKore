package eu.lepicekmichal.signalrkore

import kotlinx.serialization.Serializable

@Serializable
internal class Handshake(val protocol: String, val version: Int)