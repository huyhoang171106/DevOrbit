package vn.edu.uit.devorbit.mobile.network.stomp

import kotlinx.coroutines.*
import okhttp3.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StompClient @Inject constructor() {

    private var webSocket: WebSocket? = null
    private var listener: StompEventListener? = null
    private var okHttpClient: OkHttpClient? = null
    private var token: String? = null
    private var wsUrl: String? = null
    private val subscriptions = mutableMapOf<String, String>()
    private var reconnectJob: Job? = null
    private var reconnectAttempt = 0
    private var shouldReconnect = false
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var heartbeatJob: Job? = null
    private var connected = false

    fun setListener(listener: StompEventListener) {
        this.listener = listener
    }

    fun connect(url: String, authToken: String) {
        wsUrl = url
        token = authToken
        shouldReconnect = true
        reconnectAttempt = 0
        doConnect()
    }

    private fun doConnect() {
        val url = wsUrl ?: return
        val currentToken = token ?: return

        listener?.onConnecting(reconnectAttempt)

        okHttpClient = OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .pingInterval(15, TimeUnit.SECONDS)
            .build()

        val request = Request.Builder().url(url).build()

        webSocket = okHttpClient!!.newWebSocket(request, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                val connectFrame = StompFrame.connect(currentToken)
                webSocket.send(connectFrame.serialize())
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                val frame = StompFrame.parse(text) ?: return
                when (frame.command) {
                    "CONNECTED" -> {
                        connected = true
                        reconnectAttempt = 0
                        listener?.onConnected()
                        resubscribeAll()
                        startHeartbeat(frame.headers["heart-beat"])
                    }
                    "MESSAGE" -> {
                        val destination = frame.headers["destination"] ?: return
                        val body = frame.body ?: return
                        listener?.onMessage(destination, body)
                    }
                    "ERROR" -> {
                        listener?.onError(frame.body ?: "Unknown STOMP error")
                    }
                    "RECEIPT" -> { }
                }
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                connected = false
                stopHeartbeat()
                listener?.onDisconnected()
                if (shouldReconnect) scheduleReconnect()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                connected = false
                stopHeartbeat()
                listener?.onError(t.message ?: "WebSocket failure")
                if (shouldReconnect) scheduleReconnect()
            }
        })
    }

    private fun scheduleReconnect() {
        reconnectJob?.cancel()
        reconnectJob = scope.launch {
            val delayMs = (1000L * (1 shl reconnectAttempt.coerceAtMost(5))).coerceAtMost(30_000L)
            reconnectAttempt++
            delay(delayMs)
            doConnect()
        }
    }

    private fun resubscribeAll() {
        val ws = webSocket ?: return
        subscriptions.forEach { (id, destination) ->
            ws.send(StompFrame.subscribe(destination, id).serialize())
        }
    }

    private fun startHeartbeat(header: String?) {
        val parts = header?.split(",") ?: return
        if (parts.size < 2) return
        val serverInterval = parts[1].trim().toLongOrNull() ?: return
        if (serverInterval <= 0) return
        heartbeatJob?.cancel()
        heartbeatJob = scope.launch {
            while (isActive && connected) {
                delay(serverInterval)
                try { webSocket?.send("\n") } catch (_: Exception) { break }
            }
        }
    }

    private fun stopHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = null
    }

    fun subscribe(destination: String, subscriptionId: String) {
        subscriptions[subscriptionId] = destination
        if (connected) {
            webSocket?.send(StompFrame.subscribe(destination, subscriptionId).serialize())
        }
    }

    fun unsubscribe(subscriptionId: String) {
        subscriptions.remove(subscriptionId)
        if (connected) {
            webSocket?.send(StompFrame.unsubscribe(subscriptionId).serialize())
        }
    }

    fun send(destination: String, body: String) {
        if (!connected) {
            listener?.onError("Not connected")
            return
        }
        webSocket?.send(StompFrame.send(destination, body).serialize())
    }

    fun disconnect() {
        shouldReconnect = false
        reconnectJob?.cancel()
        stopHeartbeat()
        try {
            webSocket?.send(StompFrame.disconnect().serialize())
        } catch (_: Exception) {}
        webSocket?.close(1000, "Client disconnect")
        webSocket = null
        okHttpClient?.dispatcher?.executorService?.shutdown()
        okHttpClient = null
        connected = false
        scope.cancel()
    }

    fun isConnected(): Boolean = connected
}
