package com.tracker.domain.stock.model

/**
 * Domain model representing a stock with its current price information
 * Computed properties are cached for better performance
 */
data class Stock(
    val symbol: String,
    val currentPrice: Double,
    val previousPrice: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val logoUrl: String? = null
) {
    // Cache computed properties for performance
    val priceChange: Double = currentPrice - previousPrice

    val priceChangePercentage: Double = if (previousPrice != 0.0) {
        ((currentPrice - previousPrice) / previousPrice) * 100
    } else {
        0.0
    }

    val isPriceIncreased: Boolean = currentPrice > previousPrice

    val isPriceDecreased: Boolean = currentPrice < previousPrice
}

