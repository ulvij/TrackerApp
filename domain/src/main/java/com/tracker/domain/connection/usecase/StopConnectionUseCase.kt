package com.tracker.domain.usecase

import com.tracker.domain.repository.ConnectionRepository

/**
 * Use case for disconnecting from the real-time server
 */
class DisconnectWebSocketUseCase(
    private val connectionRepository: ConnectionRepository
) {
    operator fun invoke() {
        connectionRepository.disconnect()
    }
}

