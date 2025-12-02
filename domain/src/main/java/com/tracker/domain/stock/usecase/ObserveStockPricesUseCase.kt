package com.tracker.domain.stock.usecase

import com.tracker.domain.base.BaseFlowUseCase
import com.tracker.domain.error.ErrorConverter
import com.tracker.domain.stock.model.Stock
import com.tracker.domain.stock.repository.StockRepository
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

/**
 * Use case for observing real-time prices of Stocks
 */
class ObserveStockPricesUseCase(
    context: CoroutineContext,
    converter: ErrorConverter,
    private val stockRepository: StockRepository
) : BaseFlowUseCase<Unit, List<Stock>>(context, converter) {

    override fun createFlow(params: Unit): Flow<List<Stock>> {
        return stockRepository.observePriceUpdates()
    }
}

