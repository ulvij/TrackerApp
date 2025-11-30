package com.tracker.domain.usecase

import com.tracker.domain.model.StockSymbol
import com.tracker.domain.repository.StockRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing real-time price updates
 */
class ObservePriceUpdatesUseCase(
    private val stockRepository: StockRepository
) {
    operator fun invoke(): Flow<List<StockSymbol>> {
        return stockRepository.observePriceUpdates()
    }
}

