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

    /**
     * Update prices for multiple stocks from external source
     * Implementation should handle the conversion from external format
     */
    fun updatePrices(priceUpdates: List<*>)

    /**
     * Get current stock list snapshot
     * @return List of all stocks with current prices
     */
    fun getCurrentStocks(): List<Stock>
}