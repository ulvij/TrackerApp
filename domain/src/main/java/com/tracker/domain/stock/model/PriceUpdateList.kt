package com.tracker.domain.stock.model

/**
 * DTO for a list of price updates sent/received via WebSocket
 */
data class PriceUpdateList(
    val updates: List<PriceUpdate>
)