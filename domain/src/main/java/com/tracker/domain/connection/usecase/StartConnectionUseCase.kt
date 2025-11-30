package com.tracker.domain.connection.usecase

import com.tracker.domain.connection.repository.ConnectionRepository

/**
 * Use case for connecting to the real-time server
 */
class StartConnectionUseCase(
    private val connectionRepository: ConnectionRepository
) {
    operator fun invoke() {
        connectionRepository.connect()
    }
}

