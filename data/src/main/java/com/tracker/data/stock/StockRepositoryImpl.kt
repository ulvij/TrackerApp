package com.tracker.data.stock

import com.google.gson.Gson
import com.tracker.data.stock.model.PriceUpdateDto
import com.tracker.data.stock.model.PriceUpdateListDto
import com.tracker.domain.connection.model.ConnectionState
import com.tracker.domain.connection.repository.ConnectionRepository
import com.tracker.domain.stock.model.Stock
import com.tracker.domain.stock.repository.StockRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

/**
 * Implementation of StockRepository managing stock prices, updates, and price generation
 * Automatically manages price generation based on connection state
 */
@Singleton
class StockRepositoryImpl @Inject constructor(
    private val connectionRepository: ConnectionRepository
) : StockRepository {

    companion object {
        private const val PRICE_UPDATE_INTERVAL_MS = 2000L
        private const val MIN_PRICE = 1.0
        private const val MAX_PRICE_CHANGE_PERCENT = 5.0
    }


    private val stocks = mutableMapOf<String, Stock>()
    private val _stocksFlow = MutableStateFlow<List<Stock>>(emptyList())

    private var generationJob: Job? = null
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val gson = Gson()

    init {
        // Step 1: Initialize stocks with random prices
        initializeStocks()

        // Step 2: Observe connection state and manage price generation/updates
        observeConnectionStateAndManageGeneration()

        // Step 3: Observe WebSocket messages and update local prices
        observeWebSocketMessages()
    }


    private fun initializeStocks() {
        val initialPrices = StockDataSource.generateInitialPrices()
        StockDataSource.symbols.forEach { symbol ->
            val price = initialPrices[symbol] ?: 100.0
            stocks[symbol] = createStock(symbol, price, price)
        }
        emitStocks()
    }

    private fun observeConnectionStateAndManageGeneration() {
        connectionRepository.observeConnectionState()
            .onEach { connectionState ->
                when (connectionState) {
                    is ConnectionState.Connected -> startPriceGeneration()
                    else -> stopPriceGeneration()
                }
            }
            .launchIn(repositoryScope)
    }

    private fun observeWebSocketMessages() {
        connectionRepository.observeMessages()
            .onEach { message ->
                try {
                    val priceUpdates = gson.fromJson(message, PriceUpdateListDto::class.java)
                    updateLocalPrices(priceUpdates.updates)
                } catch (_: Exception) {
                    // Ignore malformed messages
                }
            }
            .launchIn(repositoryScope)
    }

    override fun observePriceUpdates(): Flow<List<Stock>> {
        return _stocksFlow.asStateFlow()
    }

    /**
     * Start generating prices and sending them via WebSocket
     */
    private fun startPriceGeneration() {
        if (generationJob?.isActive == true) return

        generationJob = repositoryScope.launch {
            while (true) {
                // Generate new prices for all stocks
                val priceUpdates = stocks.map { (symbol, stock) ->
                    PriceUpdateDto(
                        symbol = symbol,
                        price = generateNextPrice(stock.currentPrice),
                        timestamp = System.currentTimeMillis()
                    )
                }

                // Send all price updates as a single list via WebSocket
                val priceUpdateList = PriceUpdateListDto(updates = priceUpdates)
                val message = gson.toJson(priceUpdateList)
                connectionRepository.sendMessage(message)

                delay(PRICE_UPDATE_INTERVAL_MS)
            }
        }
    }

    /**
     * Generate next price with random change between -5% and +5%
     */
    private fun generateNextPrice(currentPrice: Double): Double {
        val changePercentage = Random.nextDouble(-MAX_PRICE_CHANGE_PERCENT, MAX_PRICE_CHANGE_PERCENT)
        val priceChange = currentPrice * (changePercentage / 100)
        return (currentPrice + priceChange).coerceAtLeast(MIN_PRICE)
    }

    /**
     * Stop price generation
     */
    private fun stopPriceGeneration() {
        generationJob?.cancel()
        generationJob = null
    }

    // ...existing code...

    /**
     * Update multiple stock prices at once (called when receiving WebSocket message list)
     */
    private fun updateLocalPrices(priceUpdates: List<PriceUpdateDto>) {
        priceUpdates.forEach { update ->
            stocks[update.symbol]?.let { currentStock ->
                stocks[update.symbol] = createStock(update.symbol, update.price, currentStock.currentPrice)
            }
        }
        emitStocks()
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
