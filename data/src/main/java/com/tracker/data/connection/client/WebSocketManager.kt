package com.tracker.data.connection.client

import com.tracker.domain.connection.model.ConnectionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

sealed interface WebSocketAction {
    object Connect : WebSocketAction
    object Disconnect : WebSocketAction
    data class SendMessage(val message: String) : WebSocketAction
    object Shutdown : WebSocketAction
}

private sealed interface WebSocketInternalEvent {
    object Opened : WebSocketInternalEvent
    data class Message(val text: String) : WebSocketInternalEvent
    data class Failure(val error: String) : WebSocketInternalEvent
    object Closed : WebSocketInternalEvent
}

@Singleton
class WebSocketManagerV2 @Inject constructor() {

    private val url = "wss://ws.postman-echo.com/raw"

    private val client = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .build()

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _messages = MutableStateFlow("")
    val messages: StateFlow<String> = _messages.asStateFlow()

    private val commandChannel = Channel<WebSocketAction>(Channel.UNLIMITED)
    private val internalEvents = Channel<WebSocketInternalEvent>(Channel.UNLIMITED)

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var processorJob: Job

    private var webSocket: WebSocket? = null

    init {
        processorJob = startProcessor()
    }

    private val listener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            internalEvents.trySend(WebSocketInternalEvent.Opened)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            internalEvents.trySend(WebSocketInternalEvent.Message(text))
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            internalEvents.trySend(WebSocketInternalEvent.Closed)
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            internalEvents.trySend(WebSocketInternalEvent.Failure(t.message ?: "Unknown error"))
        }
    }

    private fun startProcessor() = scope.launch {
        while (isActive) {
            select {
                commandChannel.onReceive { action ->
                    when (action) {
                        WebSocketAction.Connect -> handleConnect()
                        WebSocketAction.Disconnect -> handleDisconnect()
                        is WebSocketAction.SendMessage -> handleSend(action.message)
                        WebSocketAction.Shutdown -> {
                            handleDisconnect()
                            internalEvents.close()
                            commandChannel.close()
                            cancel()
                        }
                    }
                }

                internalEvents.onReceive { event ->
                    when (event) {
                        WebSocketInternalEvent.Opened ->
                            _connectionState.value = ConnectionState.Connected

                        is WebSocketInternalEvent.Message ->
                            _messages.value = event.text

                        is WebSocketInternalEvent.Failure -> {
                            _connectionState.value = ConnectionState.Error(event.error)
                            webSocket = null
                        }

                        WebSocketInternalEvent.Closed -> {
                            _connectionState.value = ConnectionState.Disconnected
                            webSocket = null
                        }
                    }
                }
            }
        }
    }

    suspend fun connect() = commandChannel.send(WebSocketAction.Connect)
    suspend fun disconnect() = commandChannel.send(WebSocketAction.Disconnect)
    suspend fun sendMessage(message: String) =
        commandChannel.send(WebSocketAction.SendMessage(message))

    suspend fun shutdown() {
        if (!commandChannel.isClosedForSend) {
            commandChannel.send(WebSocketAction.Shutdown)
        }
        processorJob.join()
        scope.cancel()
        client.dispatcher.executorService.shutdown()
        client.connectionPool.evictAll()
    }

    private fun handleConnect() {
        if (_connectionState.value is ConnectionState.Connected ||
            _connectionState.value is ConnectionState.Connecting
        ) return

        _connectionState.value = ConnectionState.Connecting

        val request = Request.Builder().url(url).build()
        webSocket = client.newWebSocket(request, listener)
    }

    private fun handleDisconnect() {
        webSocket?.close(1000, "User disconnected")
        webSocket = null
        _connectionState.value = ConnectionState.Disconnected
    }

    private fun handleSend(message: String) {
        val success = webSocket?.send(message) ?: false
        if (!success) {
            _connectionState.value = ConnectionState.Error("Failed to send message")
        }
    }
}

