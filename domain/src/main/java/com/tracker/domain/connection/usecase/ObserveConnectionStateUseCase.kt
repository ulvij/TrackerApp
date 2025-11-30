package com.tracker.domain.usecase

import com.tracker.domain.model.ConnectionState
import com.tracker.domain.repository.ConnectionRepository
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

