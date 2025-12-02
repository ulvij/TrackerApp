package com.tracker.domain.connection.usecase

import com.tracker.domain.base.BaseUseCase
import com.tracker.domain.connection.repository.ConnectionRepository
import com.tracker.domain.error.ErrorConverter
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

/**
 * Use case for connecting to the real-time server
 */
class StartConnectionUseCase @Inject constructor(
    context: CoroutineContext,
    converter: ErrorConverter,
    private val connectionRepository: ConnectionRepository
) : BaseUseCase<Unit, Unit>(context, converter) {

    override suspend fun executeOnBackground(params: Unit) {
        connectionRepository.connect()
    }

}

