package com.tracker.domain.usecase

import com.tracker.domain.repository.ConnectionRepository

/**
 * Use case for connecting to the real-time server
 */
class ConnectWebSocketUseCase(
    private val connectionRepository: ConnectionRepository
) {
    operator fun invoke() {
        connectionRepository.connect()
    }
}

