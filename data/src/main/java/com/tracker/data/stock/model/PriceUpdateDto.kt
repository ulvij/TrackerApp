package com.tracker.data.stock.model

/**
 * DTO for a single price update
 */
data class PriceUpdateDto(
    val symbol: String,
    val price: Double,
    val timestamp: Long
)



