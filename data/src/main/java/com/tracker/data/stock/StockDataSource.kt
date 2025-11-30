package com.tracker.data.stock

/**
 * Data source providing the list of 25 stock symbols to track with their logo URLs
 */
object StockDataSource {

    /**
     * Stock data with symbol, company name, and logo URL
     */
    data class StockData(
        val symbol: String,
        val companyName: String,
        val logoUrl: String
    )

    val stocks = listOf(
        StockData("AAPL", "Apple Inc.", "https://logo.clearbit.com/apple.com"),
        StockData("GOOG", "Alphabet Inc.", "https://logo.clearbit.com/google.com"),
        StockData("TSLA", "Tesla Inc.", "https://logo.clearbit.com/tesla.com"),
        StockData("AMZN", "Amazon.com Inc.", "https://logo.clearbit.com/amazon.com"),
        StockData("MSFT", "Microsoft Corporation", "https://logo.clearbit.com/microsoft.com"),
        StockData("NVDA", "NVIDIA Corporation", "https://logo.clearbit.com/nvidia.com"),
        StockData("META", "Meta Platforms Inc.", "https://logo.clearbit.com/meta.com"),
        StockData("NFLX", "Netflix Inc.", "https://logo.clearbit.com/netflix.com"),
        StockData("AMD", "Advanced Micro Devices", "https://logo.clearbit.com/amd.com"),
        StockData("INTC", "Intel Corporation", "https://logo.clearbit.com/intel.com"),
        StockData("BABA", "Alibaba Group", "https://logo.clearbit.com/alibaba.com"),
        StockData("TSM", "Taiwan Semiconductor", "https://logo.clearbit.com/tsmc.com"),
        StockData("ORCL", "Oracle Corporation", "https://logo.clearbit.com/oracle.com"),
        StockData("CSCO", "Cisco Systems", "https://logo.clearbit.com/cisco.com"),
        StockData("QCOM", "Qualcomm Inc.", "https://logo.clearbit.com/qualcomm.com"),
        StockData("TXN", "Texas Instruments", "https://logo.clearbit.com/ti.com"),
        StockData("AVGO", "Broadcom Inc.", "https://logo.clearbit.com/broadcom.com"),
        StockData("DIS", "The Walt Disney Company", "https://logo.clearbit.com/disney.com"),
        StockData("CRM", "Salesforce Inc.", "https://logo.clearbit.com/salesforce.com"),
        StockData("PYPL", "PayPal Holdings", "https://logo.clearbit.com/paypal.com"),
        StockData("UBER", "Uber Technologies", "https://logo.clearbit.com/uber.com"),
        StockData("SQ", "Block Inc.", "https://logo.clearbit.com/squareup.com"),
        StockData("SHOP", "Shopify Inc.", "https://logo.clearbit.com/shopify.com"),
        StockData("SNAP", "Snap Inc.", "https://logo.clearbit.com/snap.com"),
        StockData("COIN", "Coinbase Global", "https://logo.clearbit.com/coinbase.com")
    )

    /**
     * List of stock symbols
     */
    val symbols: List<String> = stocks.map { it.symbol }

    /**
     * Get logo URL for a stock symbol
     */
    fun getLogoUrl(symbol: String): String? {
        return stocks.find { it.symbol == symbol }?.logoUrl
    }

    /**
     * Generate initial random prices for stocks (between $50 and $500)
     */
    fun generateInitialPrices(): Map<String, Double> {
        return symbols.associateWith {
            (50..500).random().toDouble() + (0..99).random() / 100.0
        }
    }
}

