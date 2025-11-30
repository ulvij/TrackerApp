package com.tracker.data.connection.client

import com.tracker.domain.connection.model.ConnectionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager class for WebSocket connection using OkHttp
 */
@Singleton
class WebSocketManager @Inject constructor() {
    private val url: String = "wss://ws.postman-echo.com/raw"

    private val client = OkHttpClient.Builder()
        .readTimeout(30, TimeUnit.SECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .build()

    private var webSocket: WebSocket? = null

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _messages = MutableStateFlow("")
    val messages: StateFlow<String> = _messages.asStateFlow()

    private val listener = object : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            _connectionState.value = ConnectionState.Connected
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            _messages.value = text
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            _connectionState.value = ConnectionState.Disconnected
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            _connectionState.value = ConnectionState.Disconnected
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            _connectionState.value = ConnectionState.Error(
                t.message ?: "Unknown error occurred"
            )
        }
    }

    fun connect() {
        if (_connectionState.value is ConnectionState.Connected) {
            return
        }

        _connectionState.value = ConnectionState.Connecting

        val request = Request.Builder()
            .url(url)
            .build()

        webSocket = client.newWebSocket(request, listener)
    }

    fun disconnect() {
        webSocket?.close(1000, "User disconnected")
        webSocket = null
        _connectionState.value = ConnectionState.Disconnected
    }

    fun sendMessage(message: String) {
        webSocket?.send(message)
    }
}

