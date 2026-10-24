package eu.lepicekmichal.signalrkore

import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.cio.CIO
import io.ktor.server.engine.EmbeddedServer
import io.ktor.server.engine.embeddedServer
import io.ktor.server.request.header
import io.ktor.server.request.receiveText
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytesWriter
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import io.ktor.server.websocket.DefaultWebSocketServerSession
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.webSocket
import io.ktor.utils.io.writeStringUtf8
import io.ktor.websocket.Frame
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.Collections
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.AfterTest
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

abstract class HubTest {

    private var server: EmbeddedServer<*, *>? = null

    private val uncaught: MutableList<Throwable> = Collections.synchronizedList(mutableListOf())
    private val previousHandler = Thread.getDefaultUncaughtExceptionHandler()

    protected val logs: MutableList<Log> = Collections.synchronizedList(mutableListOf())

    data class Log(val severity: Logger.Severity, val message: String, val cause: Throwable?)

    init {
        Thread.setDefaultUncaughtExceptionHandler { _, throwable -> uncaught += throwable }
    }

    @AfterTest
    fun tearDownServer() {
        stopServer()
        Thread.setDefaultUncaughtExceptionHandler(previousHandler)
        assertTrue(uncaught.isEmpty(), "Uncaught exceptions: $uncaught\nLogs:\n${logsText()}")
    }

    protected suspend fun awaitLog(predicate: (Log) -> Boolean): Log = withTimeout(5.seconds) {
        var log = logs.toList().firstOrNull(predicate)
        while (log == null) {
            delay(10)
            log = logs.toList().firstOrNull(predicate)
        }
        log
    }

    protected fun logsText() = logs.joinToString("\n") { (severity, message, cause) -> "$severity $message ${cause ?: ""}" }

    protected fun stopServer() {
        server?.stop(0, 0)
    }

    protected fun serve(module: Application.() -> Unit): Int {
        val server = embeddedServer(CIO, port = 0, host = "127.0.0.1", module = module).start(wait = false)
        this.server = server
        return runBlocking { server.engine.resolvedConnectors().first().port }
    }

    protected fun Route.negotiate(
        vararg transports: String = arrayOf("WebSockets"),
        path: String = "/hub/negotiate",
        response: String = negotiateResponse(*transports),
    ) {
        post(path) { call.respondText(response, ContentType.Application.Json) }
    }

    protected fun negotiateResponse(vararg transports: String = arrayOf("WebSockets")) =
        """{"negotiateVersion":1,"connectionId":"abc","connectionToken":"token","availableTransports":[""" +
            transports.joinToString(",") { """{"transport":"$it","transferFormats":["Text","Binary"]}""" } + "]}"

    protected fun Route.hub(
        path: String = "/hub",
        handshakeResponse: String? = "{}",
        handler: suspend HubSession.() -> Unit,
    ) {
        webSocket(path) {
            val session = HubSession(this)
            session.handshake = session.receiveMessage(skipPings = false)
            if (handshakeResponse != null) session.sendMessage(handshakeResponse)
            session.handler()
        }
    }

    protected fun startHub(
        handshakeResponse: String? = "{}",
        handler: suspend HubSession.() -> Unit,
    ) = serve {
        install(WebSockets)
        routing {
            negotiate()
            hub(handshakeResponse = handshakeResponse, handler = handler)
        }
    }

    protected fun startHttpHub(
        transport: String,
        hub: HttpHub = HttpHub(),
        handshakeResponse: String? = "{}",
        offered: List<String> = listOf(transport),
    ) = serve {
        routing {
            post("/hub/negotiate") {
                hub.negotiations.incrementAndGet()
                call.respondText(negotiateResponse(*offered.toTypedArray()), ContentType.Application.Json)
            }
            post("/hub") {
                hub.requests += call.request.headers.copy()
                call.receiveText().split(RECORD_SEPARATOR).filter { it.isNotBlank() }.forEach {
                    val message = Json.parseToJsonElement(it).jsonObject
                    if ("protocol" in message) {
                        if (handshakeResponse != null) hub.sendMessage(handshakeResponse)
                    } else {
                        hub.fromClient.send(message)
                    }
                }
                call.respond(hub.sendStatus, "")
            }
            delete("/hub") {
                hub.deletes.incrementAndGet()
                hub.toClient.close()
                call.respond(HttpStatusCode.Accepted, "")
            }
            if (transport == "LongPolling") {
                get("/hub") {
                    if (call.request.header("Upgrade") != null) return@get call.respond(HttpStatusCode.NotFound, "")
                    hub.requests += call.request.headers.copy()
                    if (hub.polls.getAndIncrement() == 0) return@get call.respond(HttpStatusCode.OK, "")
                    val payload = withTimeoutOrNull(2.seconds) { hub.toClient.receiveCatching() }
                    when {
                        payload == null -> call.respond(HttpStatusCode.OK, "")
                        payload.isClosed -> call.respond(HttpStatusCode.NoContent)
                        else -> call.respondText(payload.getOrThrow())
                    }
                }
            } else {
                get("/hub") {
                    if (call.request.header("Upgrade") != null) return@get call.respond(HttpStatusCode.NotFound, "")
                    hub.requests += call.request.headers.copy()
                    call.respondBytesWriter(ContentType.Text.EventStream) {
                        for (payload in hub.toClient) {
                            writeStringUtf8("data: $payload\n\n")
                            flush()
                        }
                    }
                }
            }
        }
    }

    protected fun connection(port: Int, path: String = "/hub", block: HttpHubConnectionBuilder.() -> Unit = {}) =
        HubConnectionBuilder.create("http://127.0.0.1:$port$path") {
            logger = Logger { severity, message, cause -> logs += Log(severity, message(), cause) }
            block()
        }

    class HubSession(private val session: DefaultWebSocketServerSession) : DefaultWebSocketServerSession by session {

        lateinit var handshake: JsonObject

        private val received = ArrayDeque<JsonObject>()

        suspend fun receiveMessage(skipPings: Boolean = true): JsonObject {
            while (true) {
                while (received.isEmpty()) {
                    incoming.receive().data.decodeToString()
                        .split(RECORD_SEPARATOR)
                        .filter { it.isNotBlank() }
                        .mapTo(received) { Json.parseToJsonElement(it).jsonObject }
                }
                val message = received.removeFirst()
                if (!skipPings || message.type != HubMessageType.PING.value) return message
            }
        }

        suspend fun sendMessage(vararg messages: String) {
            send(Frame.Text(messages.joinToString("") { it + RECORD_SEPARATOR }))
        }

        suspend fun ignoreMessages() {
            for (frame in incoming) Unit
        }
    }
}

class HttpHub {
    val toClient = Channel<String>(Channel.UNLIMITED)
    val fromClient = Channel<JsonObject>(Channel.UNLIMITED)
    val negotiations = AtomicInteger(0)
    val polls = AtomicInteger(0)
    val deletes = AtomicInteger(0)
    val requests: MutableList<Headers> = Collections.synchronizedList(mutableListOf())

    @Volatile
    var sendStatus: HttpStatusCode = HttpStatusCode.OK

    suspend fun sendMessage(vararg messages: String) {
        toClient.send(messages.joinToString("") { it + RECORD_SEPARATOR })
    }

    suspend fun receiveMessage(skipPings: Boolean = true): JsonObject {
        while (true) {
            val message = fromClient.receive()
            if (!skipPings || message.type != HubMessageType.PING.value) return message
        }
    }
}

fun <T> CoroutineScope.launchCollecting(flow: Flow<T>, action: suspend (T) -> Unit) =
    launch(start = CoroutineStart.UNDISPATCHED) { flow.collect(action) }

fun Headers.copy(): Headers = HeadersBuilder().apply { appendAll(this@copy) }.build()

suspend fun HubConnection.awaitState(state: HubConnectionState) {
    withTimeout(5.seconds) { connectionState.first { it == state } }
}

val JsonObject.type: Int
    get() = getValue("type").jsonPrimitive.content.toInt()

val JsonObject.invocationId: String
    get() = getValue("invocationId").jsonPrimitive.content

val JsonObject.target: String
    get() = getValue("target").jsonPrimitive.content

val JsonObject.arguments: JsonArray
    get() = getValue("arguments").jsonArray
