package com.tracker.domain.connection.repository

import com.tracker.domain.connection.model.ConnectionState
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for real-time connection operations
 */
interface ConnectionRepository {
    /**
     * Connect to the real-time server
     */
    suspend fun connect()

    /**
     * Disconnect from the real-time server
     */
    suspend fun disconnect()

    /**
     * Send a message through the connection
     */
    suspend fun sendMessage(message: String)

    /**
     * Observe incoming messages from the connection
     */
    fun observeMessages(): Flow<String>

    /**
     * Observe the connection state
     */
    fun observeConnectionState(): Flow<ConnectionState>
}