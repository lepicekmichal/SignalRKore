package eu.lepicekmichal.signalrkore

import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull

class NegotiateResponseTest {

    private val json = Json { ignoreUnknownKeys = true }

    private fun decode(response: String) = json.decodeFromString<NegotiateResponse>(response)

    @Test
    fun version1() {
        val response = assertIs<NegotiateResponse.Success>(
            decode(
                """{"connectionToken":"token","connectionId":"id","negotiateVersion":1,"availableTransports":[""" +
                    """{"transport":"WebSockets","transferFormats":["Text","Binary"]},""" +
                    """{"transport":"ServerSentEvents","transferFormats":["Text"]},""" +
                    """{"transport":"LongPolling","transferFormats":["Text","Binary"]}]}"""
            )
        )

        assertEquals("token", response.connectionToken)
        assertEquals("id", response.connectionId)
        assertEquals(1, response.negotiateVersion)
        assertEquals(
            listOf(TransportEnum.WebSockets, TransportEnum.ServerSentEvents, TransportEnum.LongPolling),
            response.availableTransports.transports(TransferFormat.Text),
        )
        assertEquals(
            listOf(TransportEnum.WebSockets, TransportEnum.LongPolling),
            response.availableTransports.transports(TransferFormat.Binary),
        )
    }

    @Test
    fun version0WithoutConnectionToken() {
        val response = assertIs<NegotiateResponse.Success>(
            decode("""{"connectionId":"id","negotiateVersion":0,"availableTransports":[{"transport":"LongPolling","transferFormats":["Text"]}]}""")
        )

        assertEquals("id", response.connectionId)
        assertEquals(0, response.negotiateVersion)
    }

    @Test
    fun missingNegotiateVersion() {
        val response = assertIs<NegotiateResponse.Success>(
            decode("""{"connectionId":"id","availableTransports":[{"transport":"WebSockets","transferFormats":["Text"]}]}""")
        )

        assertEquals(0, response.negotiateVersion)
    }

    @Test
    fun redirect() {
        assertEquals(
            NegotiateResponse.Redirect(url = "https://myapp.com/chat", accessToken = "accessToken"),
            decode("""{"url":"https://myapp.com/chat","accessToken":"accessToken"}"""),
        )
    }

    @Test
    fun redirectWithoutAccessToken() {
        val response = assertIs<NegotiateResponse.Redirect>(decode("""{"url":"https://myapp.com/chat"}"""))

        assertNull(response.accessToken)
    }

    @Test
    fun error() {
        assertEquals(NegotiateResponse.Error(error = "This connection is not allowed."), decode("""{"error":"This connection is not allowed."}"""))
    }

    @Test
    fun extraPropertiesAreIgnored() {
        val response = assertIs<NegotiateResponse.Success>(
            decode(
                """{"connectionToken":"token","connectionId":"id","negotiateVersion":1,"useAck":true,""" +
                    """"extra":{"nested":[1,2,{"a":"b"}]},"availableTransports":[{"transport":"WebSockets","transferFormats":["Text"],"extra":1}]}"""
            )
        )

        assertEquals("id", response.connectionId)
    }

    @Test
    fun classicAspNetSignalRServerIsDetected() {
        val ex = assertFailsWith<RuntimeException> { decode("""{"ProtocolVersion":"1.5","ConnectionId":"id"}""") }

        assertEquals(true, ex.message?.contains("ASP.NET Core SignalR"))
    }
}
