package com.tracker.data.connection.repository

import com.tracker.data.connection.client.WebSocketManagerV2
import com.tracker.domain.connection.model.ConnectionState
import com.tracker.domain.connection.repository.ConnectionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ConnectionRepository using WebSocket
 *
 * Note: Thread-safety is handled by WebSocketManagerV2's internal actor pattern.
 * This repository is just a thin adapter layer.
 */
@Singleton
class ConnectionRepositoryImpl @Inject constructor(
    private val webSocketManager: WebSocketManagerV2
) : ConnectionRepository {

    override suspend fun connect() {
        webSocketManager.connect()
    }

    override suspend fun disconnect() {
        webSocketManager.disconnect()
    }

    override suspend fun sendMessage(message: String) {
        webSocketManager.sendMessage(message)
    }

    override fun observeMessages(): Flow<String> {
        return webSocketManager.messages
    }

    override fun observeConnectionState(): Flow<ConnectionState> {
        return webSocketManager.connectionState
    }
}

