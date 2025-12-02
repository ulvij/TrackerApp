package com.tracker.domain.connection.usecase

import com.tracker.domain.base.BaseFlowUseCase
import com.tracker.domain.connection.model.ConnectionState
import com.tracker.domain.connection.repository.ConnectionRepository
import com.tracker.domain.error.ErrorConverter
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

/**
 * Use case for observing connection state
 */
class ObserveConnectionStateUseCase(
    context: CoroutineContext,
    converter: ErrorConverter,
    private val connectionRepository: ConnectionRepository
) : BaseFlowUseCase<Unit, ConnectionState>(context, converter) {

    override fun createFlow(params: Unit): Flow<ConnectionState> {
        return connectionRepository.observeConnectionState()
    }
}

