package com.tracker.app.ui.model

import androidx.compose.runtime.Immutable
import com.tracker.domain.stock.model.Stock

/**
 * Immutable UI model for Stock
 * This wrapper makes the Stock data stable for Compose, enabling smart recomposition.
 * Only the parts that change will recompose, not the entire UI.
 */
@Immutable
data class StockUIModel(
    val symbol: String,
    val logoUrl: String?,
    val currentPrice: Double,
    val previousPrice: Double,
    val priceChange: Double,
    val priceChangePercentage: Double,
    val isPriceIncreased: Boolean,
    val timestamp: Long
)

/**
 * Extension function to convert domain Stock to UI model
 */
fun Stock.toUIModel(): StockUIModel {
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
 * Extension function to convert list of domain Stocks to UI models
 */
fun List<Stock>.toUIModels(): List<StockUIModel> {
    return map { it.toUIModel() }
}

