package com.tracker.data.stock.model

/**
 * DTO for a list of price updates sent/received via WebSocket
 */
data class PriceUpdateListDto(
    val updates: List<PriceUpdateDto>
)