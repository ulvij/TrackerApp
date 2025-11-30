package com.tracker.domain.stock.usecase

import com.tracker.domain.stock.model.Stock
import com.tracker.domain.stock.repository.StockRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing real-time prices of Stocks
 */
class ObserveStockPricesUseCase(
    private val stockRepository: StockRepository
) {
    operator fun invoke(): Flow<List<Stock>> {
        return stockRepository.observePriceUpdates()
    }
}

