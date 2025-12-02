package com.tracker.domain.stock.model

/**
 * DTO for a single price update
 */
data class PriceUpdate(
    val symbol: String,
    val price: Double,
    val timestamp: Long
)



