package com.tracker.domain.stock.model

/**
 * Domain model representing a stock with its current price information
 */
data class Stock(
    val symbol: String,
    val currentPrice: Double,
    val previousPrice: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val logoUrl: String? = null
) {
    val priceChange: Double
        get() = currentPrice - previousPrice

    val priceChangePercentage: Double
        get() = if (previousPrice != 0.0) {
            ((currentPrice - previousPrice) / previousPrice) * 100
        } else {
            0.0
        }

    val isPriceIncreased: Boolean
        get() = currentPrice > previousPrice

    val isPriceDecreased: Boolean
        get() = currentPrice < previousPrice
}

