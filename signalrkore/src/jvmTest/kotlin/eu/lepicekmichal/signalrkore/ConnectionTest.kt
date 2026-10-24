package eu.lepicekmichal.signalrkore

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.install
import io.ktor.server.request.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.websocket.CloseReason
import io.ktor.websocket.close
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class ConnectionTest : HubTest() {

    @Test
    fun `start connects and stop disconnects`() = runBlocking<Unit> {
        val port = startHub { ignoreMessages() }
        val connection = connection(port)
        assertEquals(HubConnectionState.DISCONNECTED, connection.connectionState.value)

        connection.start()
        assertEquals(HubConnectionState.CONNECTED, connection.connectionState.value)
        assertEquals("abc", connection.connectionId.value)

        connection.stop()
        assertEquals(HubConnectionState.DISCONNECTED, connection.connectionState.value)
    }

    @Test
    fun `starting a started connection does nothing`() = runBlocking<Unit> {
        val negotiations = AtomicInteger(0)
        val port = serve {
            install(WebSockets)
            routing {
                post("/hub/negotiate") {
                    negotiations.incrementAndGet()
                    call.respondText(negotiateResponse(), ContentType.Application.Json)
                }
                hub { ignoreMessages() }
            }
        }
        val connection = connection(port)

        connection.start()
        connection.start()

        assertEquals(1, negotiations.get())
        assertEquals(HubConnectionState.CONNECTED, connection.connectionState.value)
        connection.stop()
    }

    @Test
    fun `connection can be started again after stop`() = runBlocking<Unit> {
        val handshakes = AtomicInteger(0)
        val port = startHub {
            handshakes.incrementAndGet()
            ignoreMessages()
        }
        val connection = connection(port)

        connection.start()
        connection.stop()
        connection.start()

        assertEquals(HubConnectionState.CONNECTED, connection.connectionState.value)
        withTimeout(5.seconds) { while (handshakes.get() < 2) delay(10) }
        connection.stop()
    }

    @Test
    fun `stop while connecting fails start`() = runBlocking<Unit> {
        val port = startHub(handshakeResponse = null) { ignoreMessages() }
        val connection = connection(port)

        val start = async { runCatching { connection.start() } }
        connection.awaitState(HubConnectionState.CONNECTING)
        delay(200)
        connection.stop()

        assertTrue(withTimeout(5.seconds) { start.await() }.isFailure)
        assertEquals(HubConnectionState.DISCONNECTED, connection.connectionState.value)
    }

    @Test
    fun `negotiate version is sent once`() = runBlocking<Unit> {
        val versions = Collections.synchronizedList(mutableListOf<List<String>?>())
        val port = serve {
            install(WebSockets)
            routing {
                post("/hub/negotiate") {
                    versions += call.request.queryParameters.getAll("negotiateVersion")
                    call.respondText(negotiateResponse(), ContentType.Application.Json)
                }
                hub { ignoreMessages() }
            }
        }

        connection(port).apply { start(); stop() }
        connection(port, path = "/hub?negotiateVersion=1").apply { start(); stop() }

        assertEquals(listOf(listOf("1"), listOf("1")), versions)
    }

    @Test
    fun `connection token is used as id`() = runBlocking<Unit> {
        val id = CompletableDeferred<String?>()
        val port = startHub {
            id.complete(call.request.queryParameters["id"])
            ignoreMessages()
        }
        val connection = connection(port)

        connection.start()

        assertEquals("token", id.await())
        assertEquals("abc", connection.connectionId.value)
        connection.stop()
    }

    @Test
    fun `connection token is ignored with negotiate version 0`() = runBlocking<Unit> {
        val id = CompletableDeferred<String?>()
        val port = serve {
            install(WebSockets)
            routing {
                negotiate(response = """{"negotiateVersion":0,"connectionId":"abc","connectionToken":"token","availableTransports":[{"transport":"WebSockets","transferFormats":["Text"]}]}""")
                hub {
                    id.complete(call.request.queryParameters["id"])
                    ignoreMessages()
                }
            }
        }
        val connection = connection(port)

        connection.start()

        assertEquals("abc", id.await())
        connection.stop()
    }

    @Test
    fun `negotiate version 1 without connection token fails start`() = runBlocking<Unit> {
        val port = serve {
            routing {
                negotiate(response = """{"negotiateVersion":1,"connectionId":"abc","availableTransports":[{"transport":"WebSockets","transferFormats":["Text"]}]}""")
            }
        }
        val connection = connection(port)

        val ex = assertFailsWith<RuntimeException> { connection.start() }

        assertEquals("Negotiate response is missing the connection token.", ex.message)
        assertEquals(HubConnectionState.DISCONNECTED, connection.connectionState.value)
    }

    @Test
    fun `negotiate redirect is followed with its access token`() = runBlocking<Unit> {
        val authorization = CompletableDeferred<String?>()
        val port = serve {
            install(WebSockets)
            routing {
                post("/hub/negotiate") {
                    call.respondText(
                        """{"url":"http://${call.request.header("Host")}/redirected","accessToken":"redirected-token"}""",
                        ContentType.Application.Json,
                    )
                }
                negotiate(path = "/redirected/negotiate")
                hub(path = "/redirected") {
                    authorization.complete(call.request.header("Authorization"))
                    ignoreMessages()
                }
            }
        }
        val connection = connection(port) { accessTokenProvider = { "original-token" } }

        connection.start()

        assertEquals("Bearer redirected-token", authorization.await())
        connection.stop()
    }

    @Test
    fun `negotiate redirecting forever fails after 100 attempts`() = runBlocking<Unit> {
        val negotiations = AtomicInteger(0)
        val port = serve {
            routing {
                post("/hub/negotiate") {
                    negotiations.incrementAndGet()
                    call.respondText("""{"url":"http://${call.request.header("Host")}/hub"}""", ContentType.Application.Json)
                }
            }
        }
        val connection = connection(port)

        val ex = assertFailsWith<RuntimeException> { connection.start() }

        assertEquals("Negotiate redirection limit exceeded.", ex.message)
        assertEquals(101, negotiations.get())
    }

    @Test
    fun `negotiate error fails start`() = runBlocking<Unit> {
        val port = serve { routing { negotiate(response = """{"error":"This connection is not allowed."}""") } }
        val connection = connection(port)

        val ex = assertFailsWith<RuntimeException> { connection.start() }

        assertEquals("This connection is not allowed.", ex.message)
        assertEquals(HubConnectionState.DISCONNECTED, connection.connectionState.value)
    }

    @Test
    fun `negotiate failure throws HttpRequestException`() = runBlocking<Unit> {
        val port = serve { routing { post("/hub/negotiate") { call.respond(HttpStatusCode.InternalServerError, "") } } }
        val connection = connection(port)

        val ex = assertFailsWith<HttpRequestException> { connection.start() }

        assertEquals(500, ex.statusCode)
        assertEquals(HubConnectionState.DISCONNECTED, connection.connectionState.value)
    }

    @Test
    fun `classic SignalR server is detected`() = runBlocking<Unit> {
        val port = serve { routing { negotiate(response = """{"ProtocolVersion":"1.5","ConnectionId":"abc"}""") } }
        val connection = connection(port)

        val ex = assertFailsWith<Exception> { connection.start() }

        assertTrue(ex.message.orEmpty().contains("ASP.NET Core SignalR"), ex.message)
    }

    @Test
    fun `skipping negotiate connects to websocket directly`() = runBlocking<Unit> {
        val negotiations = AtomicInteger(0)
        val port = serve {
            install(WebSockets)
            routing {
                post("/hub/negotiate") {
                    negotiations.incrementAndGet()
                    call.respond(HttpStatusCode.InternalServerError, "")
                }
                hub { ignoreMessages() }
            }
        }
        val connection = connection(port) {
            skipNegotiate = true
            transports = setOf(TransportEnum.WebSockets)
        }

        connection.start()

        assertEquals(HubConnectionState.CONNECTED, connection.connectionState.value)
        assertEquals(0, negotiations.get())
        assertNull(connection.connectionId.value)
        connection.stop()
    }

    @Test
    fun `skipping negotiate requires websockets`() = runBlocking<Unit> {
        val connection = connection(port = 1) { skipNegotiate = true }

        assertFailsWith<RuntimeException> { connection.start() }
        assertEquals(HubConnectionState.DISCONNECTED, connection.connectionState.value)
    }

    @Test
    fun `headers and access token are sent with every request`() = runBlocking<Unit> {
        val negotiateHeaders = Collections.synchronizedList(mutableListOf<Pair<String?, String?>>())
        val websocketHeaders = Collections.synchronizedList(mutableListOf<Pair<String?, String?>>())
        val port = serve {
            install(WebSockets)
            routing {
                post("/hub/negotiate") {
                    negotiateHeaders += call.request.header("X-Test") to call.request.header("Authorization")
                    call.respondText(negotiateResponse(), ContentType.Application.Json)
                }
                hub {
                    websocketHeaders += call.request.header("X-Test") to call.request.header("Authorization")
                    ignoreMessages()
                }
            }
        }
        val tokens = AtomicInteger(0)
        val connection = connection(port) {
            headers["X-Test"] = "value"
            accessTokenProvider = { "token-${tokens.incrementAndGet()}" }
        }

        connection.start()
        connection.stop()
        connection.start()
        connection.stop()

        assertEquals(listOf("value" to "Bearer token-1", "value" to "Bearer token-2"), negotiateHeaders)
        assertEquals(listOf("value" to "Bearer token-1", "value" to "Bearer token-2"), websocketHeaders)
    }

    @Test
    fun `access token provider failure fails start`() = runBlocking<Unit> {
        val connection = connection(port = 1) { accessTokenProvider = { throw IllegalStateException("no token") } }

        val ex = assertFailsWith<IllegalStateException> { connection.start() }

        assertEquals("no token", ex.message)
        assertEquals(HubConnectionState.DISCONNECTED, connection.connectionState.value)
    }

    @Test
    fun `handshake request uses json protocol`() = runBlocking<Unit> {
        val handshake = CompletableDeferred<String>()
        val port = startHub {
            handshake.complete(this.handshake.toString())
            ignoreMessages()
        }
        val connection = connection(port)

        connection.start()

        assertEquals(Json.parseToJsonElement("""{"protocol":"json","version":1}"""), Json.parseToJsonElement(handshake.await()))
        connection.stop()
    }

    @Test
    fun `handshake response with error fails start`() = runBlocking<Unit> {
        val port = startHub(handshakeResponse = """{"error":"Requested protocol 'json' is not available."}""") { ignoreMessages() }
        val connection = connection(port)

        val ex = assertFailsWith<RuntimeException> { connection.start() }

        assertEquals("Error in handshake Requested protocol 'json' is not available.", ex.message)
        assertEquals(HubConnectionState.DISCONNECTED, connection.connectionState.value)
    }

    @Test
    fun `invalid handshake response fails start`() = runBlocking<Unit> {
        val port = startHub(handshakeResponse = """{"error":42""") { ignoreMessages() }
        val connection = connection(port)

        val ex = assertFailsWith<RuntimeException> { connection.start() }

        assertEquals("An invalid handshake response was received from the server.", ex.message)
        assertEquals(HubConnectionState.DISCONNECTED, connection.connectionState.value)
    }

    @Test
    fun `missing handshake response times out`() = runBlocking<Unit> {
        val port = startHub(handshakeResponse = null) { ignoreMessages() }
        val connection = connection(port) { handshakeResponseTimeout = 300.milliseconds }

        val ex = assertFailsWith<RuntimeException> { withTimeout(5.seconds) { connection.start() } }

        assertEquals("Timed out waiting for the handshake response from the server.", ex.message)
        assertEquals(HubConnectionState.DISCONNECTED, connection.connectionState.value)
    }

    @Test
    fun `message sent together with handshake response is received`() = runBlocking<Unit> {
        val port = startHub(handshakeResponse = null) {
            sendMessage("{}", """{"type":1,"target":"Hello","arguments":["world"]}""")
            ignoreMessages()
        }
        val connection = connection(port)
        val received = CompletableDeferred<String>()
        val collecting = launchCollecting(connection.on("Hello", String.serializer())) { received.complete(it.arg1) }

        connection.start()

        assertEquals("world", withTimeout(5.seconds) { received.await() })
        collecting.cancel()
        connection.stop()
    }

    @Test
    fun `pings are sent when connection is idle`() = runBlocking<Unit> {
        val ping = CompletableDeferred<Int>()
        val port = startHub {
            ping.complete(receiveMessage(skipPings = false).type)
            ignoreMessages()
        }
        val connection = connection(port) { keepAliveInterval = 200.milliseconds }

        connection.start()

        assertEquals(HubMessageType.PING.value, withTimeout(5.seconds) { ping.await() })
        connection.stop()
    }

    @Test
    fun `server timeout closes the connection`() = runBlocking<Unit> {
        val port = startHub { ignoreMessages() }
        val connection = connection(port) { serverTimeout = 300.milliseconds }

        connection.start()
        connection.awaitState(HubConnectionState.DISCONNECTED)

        val error = awaitLog { it.severity == Logger.Severity.ERROR }
        assertEquals("Server timeout elapsed without receiving a message from the server.", error.cause?.message)
    }

    @Test
    fun `server pings keep the connection alive`() = runBlocking<Unit> {
        val port = startHub {
            repeat(15) {
                sendMessage("""{"type":6}""")
                delay(100)
            }
            ignoreMessages()
        }
        val connection = connection(port) { serverTimeout = 500.milliseconds }

        connection.start()
        delay(1_200)

        assertEquals(HubConnectionState.CONNECTED, connection.connectionState.value, logsText())
        connection.stop()
    }

    @Test
    fun `stop sends close message to the server`() = runBlocking<Unit> {
        val received = CompletableDeferred<Int>()
        val port = startHub {
            received.complete(receiveMessage().type)
            ignoreMessages()
        }
        val connection = connection(port)
        connection.start()

        connection.stop()

        assertEquals(HubMessageType.CLOSE.value, withTimeout(5.seconds) { received.await() })
        assertEquals(HubConnectionState.DISCONNECTED, connection.connectionState.value)
    }

    @Test
    fun `close message from server closes the connection`() = runBlocking<Unit> {
        val port = startHub {
            sendMessage("""{"type":7}""")
            ignoreMessages()
        }
        val connection = connection(port)

        connection.start()
        connection.awaitState(HubConnectionState.DISCONNECTED)
        delay(100)

        assertTrue(logs.none { it.severity == Logger.Severity.ERROR }, logsText())
    }

    @Test
    fun `close message with error is logged as error with cause`() = runBlocking<Unit> {
        val port = startHub {
            sendMessage("""{"type":7,"error":"Server is shutting down"}""")
            ignoreMessages()
        }
        val connection = connection(port)

        connection.start()
        connection.awaitState(HubConnectionState.DISCONNECTED)

        val error = awaitLog { it.severity == Logger.Severity.ERROR }
        assertTrue(error.cause?.message.orEmpty().contains("Server is shutting down"))
    }

    @Test
    fun `graceful websocket close disconnects without waiting for server timeout`() = runBlocking<Unit> {
        val port = startHub {
            delay(200)
            close(CloseReason(CloseReason.Codes.NORMAL, "bye"))
        }
        val connection = connection(port)

        connection.start()
        connection.awaitState(HubConnectionState.DISCONNECTED)
        delay(100)

        assertTrue(logs.none { it.severity == Logger.Severity.ERROR }, logsText())
    }

    @Test
    fun `websocket error close is logged as error with cause`() = runBlocking<Unit> {
        val port = startHub {
            delay(200)
            close(CloseReason(CloseReason.Codes.INTERNAL_ERROR, "boom"))
        }
        val connection = connection(port)

        connection.start()
        connection.awaitState(HubConnectionState.DISCONNECTED)

        awaitLog { it.severity == Logger.Severity.ERROR && it.cause != null }
    }
}
