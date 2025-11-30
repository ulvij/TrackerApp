package com.tracker.domain.stock.repository

import com.tracker.domain.stock.model.Stock
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for stock price operations
 */
interface StockRepository {

    /**
     * Observe real-time price updates for all stocks
     */
    fun observePriceUpdates(): Flow<List<Stock>>
}