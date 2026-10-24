package eu.lepicekmichal.signalrkore

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HandshakeTest {

    @Test
    fun handshakeRequest() {
        assertEquals(
            Json.parseToJsonElement("""{"protocol":"json","version":1}"""),
            Json.encodeToJsonElement(Handshake.serializer(), Handshake(protocol = "json", version = 1)),
        )
    }

    @Test
    fun emptyHandshakeResponse() {
        assertNull(Json.decodeFromString<HandshakeResponse>("{}").error)
    }

    @Test
    fun handshakeResponseWithError() {
        assertEquals(
            "Requested protocol 'messagepack' is not available.",
            Json.decodeFromString<HandshakeResponse>("""{"error":"Requested protocol 'messagepack' is not available."}""").error,
        )
    }
}
