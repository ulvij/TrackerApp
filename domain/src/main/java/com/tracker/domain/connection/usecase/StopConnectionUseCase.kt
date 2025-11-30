package com.tracker.domain.connection.usecase

import com.tracker.domain.connection.repository.ConnectionRepository

/**
 * Use case for disconnecting from the real-time server
 */
class StopConnectionUseCase(
    private val connectionRepository: ConnectionRepository
) {
    operator fun invoke() {
        connectionRepository.disconnect()
    }
}

