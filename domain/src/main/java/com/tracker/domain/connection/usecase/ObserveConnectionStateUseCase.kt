package com.tracker.domain.connection.usecase

import com.tracker.domain.connection.model.ConnectionState
import com.tracker.domain.connection.repository.ConnectionRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing connection state
 */
class ObserveConnectionStateUseCase(
    private val connectionRepository: ConnectionRepository
) {
    operator fun invoke(): Flow<ConnectionState> {
        return connectionRepository.observeConnectionState()
    }
}

