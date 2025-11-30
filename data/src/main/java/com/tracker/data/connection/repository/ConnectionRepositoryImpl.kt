package com.tracker.data.connection.repository

import com.tracker.data.connection.client.WebSocketManager
import com.tracker.domain.connection.model.ConnectionState
import com.tracker.domain.connection.repository.ConnectionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ConnectionRepository using WebSocket
 */
@Singleton
class ConnectionRepositoryImpl @Inject constructor(
    private val webSocketManager: WebSocketManager
) : ConnectionRepository {

    override fun connect() {
        webSocketManager.connect()
    }

    override fun disconnect() {
        webSocketManager.disconnect()
    }

    override fun sendMessage(message: String) {
        webSocketManager.sendMessage(message)
    }

    override fun observeMessages(): Flow<String> {
        return webSocketManager.messages
    }

    override fun observeConnectionState(): Flow<ConnectionState> {
        return webSocketManager.connectionState
    }
}

