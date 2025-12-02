package com.tracker.app.ui.tool

import com.tracker.app.ui.model.StockUIModel
import com.tracker.app.ui.screen.TrackerUIState
import com.tracker.domain.connection.model.ConnectionState

/**
 * Helper functions for creating test data
 */

/**
 * Creates a test StockUIModel with default values
 */
fun createTestStockUIModel(
    symbol: String = "AAPL",
    logoUrl: String? = null,
    currentPrice: Double = 100.0,
    previousPrice: Double = 95.0,
    priceChange: Double = 5.0,
    priceChangePercentage: Double = 5.26,
    isPriceIncreased: Boolean = true,
    timestamp: Long = System.currentTimeMillis()
): StockUIModel {
    return StockUIModel(
        symbol = symbol,
        logoUrl = logoUrl,
        currentPrice = currentPrice,
        previousPrice = previousPrice,
        priceChange = priceChange,
        priceChangePercentage = priceChangePercentage,
        isPriceIncreased = isPriceIncreased,
        timestamp = timestamp
    )
}

/**
 * Creates a test TrackerUIState with default values
 */
fun createTestTrackerUIState(
    stocks: List<StockUIModel> = emptyList(),
    connectionState: ConnectionState = ConnectionState.Disconnected
): TrackerUIState {
    return TrackerUIState(
        stocks = stocks,
        connectionState = connectionState
    )
}

/**
 * Creates a stock with price increase
 */
fun createStockWithIncrease(symbol: String = "AAPL"): StockUIModel {
    return createTestStockUIModel(
        symbol = symbol,
        currentPrice = 185.50,
        previousPrice = 182.30,
        priceChange = 3.20,
        priceChangePercentage = 1.76,
        isPriceIncreased = true
    )
}

/**
 * Creates a stock with price decrease
 */
fun createStockWithDecrease(symbol: String = "GOOG"): StockUIModel {
    return createTestStockUIModel(
        symbol = symbol,
        currentPrice = 142.75,
        previousPrice = 148.20,
        priceChange = -5.45,
        priceChangePercentage = -3.68,
        isPriceIncreased = false
    )
}

/**
 * Creates a stock with no price change
 */
fun createStockWithNoChange(symbol: String = "TSLA"): StockUIModel {
    return createTestStockUIModel(
        symbol = symbol,
        currentPrice = 245.00,
        previousPrice = 245.00,
        priceChange = 0.0,
        priceChangePercentage = 0.0,
        isPriceIncreased = false
    )
}

/**
 * Creates multiple test stocks
 */
fun createMultipleTestStocks(count: Int = 3): List<StockUIModel> {
    return List(count) { index ->
        createTestStockUIModel(
            symbol = "STOCK$index",
            currentPrice = 100.0 + index * 10,
            previousPrice = 95.0 + index * 10,
            priceChange = 5.0,
            priceChangePercentage = 5.26,
            isPriceIncreased = index % 2 == 0
        )
    }
}

