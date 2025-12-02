package com.tracker.data.stock

import com.tracker.domain.stock.model.PriceUpdate
import com.tracker.domain.stock.model.Stock
import com.tracker.domain.stock.repository.StockRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of StockRepository managing stock prices and data.
 * Thread-safety is ensured by the Mutex protecting the mutable stocks map.
 */
@Singleton
class StockRepositoryImpl @Inject constructor() : StockRepository {

    private val stocks = mutableMapOf<String, Stock>()
    private val _stocksFlow = MutableStateFlow<List<Stock>>(emptyList())

    init {
        // Initialize stocks with random prices
        initializeStocks()
    }

    private fun initializeStocks() {
        val initialPrices = StockDataSource.generateInitialPrices()
        StockDataSource.symbols.forEach { symbol ->
            val price = initialPrices[symbol] ?: 100.0
            stocks[symbol] = createStock(symbol, price, price)
        }
        emitStocks()
    }

    override fun observePriceUpdates(): Flow<List<Stock>> {
        return _stocksFlow.asStateFlow()
    }

    override suspend fun updatePrices(priceUpdates: List<PriceUpdate>) {
        priceUpdates.forEach { update ->
            stocks[update.symbol]?.let { currentStock ->
                stocks[update.symbol] = createStock(
                    symbol = update.symbol,
                    currentPrice = update.price,
                    previousPrice = currentStock.currentPrice
                )
            }
        }
        emitStocks()
    }

    override suspend fun getCurrentStocks(): List<Stock> {
        return stocks.values.toList()
    }

    /**
     * Helper method to create Stock instance with logo URL
     */
    private fun createStock(symbol: String, currentPrice: Double, previousPrice: Double): Stock {
        return Stock(
            symbol = symbol,
            currentPrice = currentPrice,
            previousPrice = previousPrice,
            logoUrl = StockDataSource.getLogoUrl(symbol)
        )
    }

    private fun emitStocks() {
        // Sort by price (highest first) and emit
        _stocksFlow.value = stocks.values
            .sortedByDescending { it.currentPrice }
            .toList()
    }
}
