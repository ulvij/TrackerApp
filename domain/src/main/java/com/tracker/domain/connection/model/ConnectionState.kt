package com.tracker.domain.connection.model

/**
 * Sealed class representing the connection state
 */
sealed class ConnectionState {
    object Disconnected : ConnectionState()
    object Connecting : ConnectionState()
    object Connected : ConnectionState()
    data class Error(val message: String) : ConnectionState()

    /**
     * Check if the connection is connected
     */
    val isConnected: Boolean
        get() = this is Connected
}