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
        StockData("AAPL", "Apple Inc.", "https://www.google.com/s2/favicons?sz=256&domain=apple.com"),
        StockData("GOOG", "Alphabet Inc.", "https://www.google.com/s2/favicons?sz=256&domain=google.com"),
        StockData("TSLA", "Tesla Inc.", "https://www.google.com/s2/favicons?sz=256&domain=tesla.com"),
        StockData("AMZN", "Amazon.com Inc.", "https://www.google.com/s2/favicons?sz=256&domain=amazon.com"),
        StockData("MSFT", "Microsoft Corporation", "https://www.google.com/s2/favicons?sz=256&domain=microsoft.com"),
        StockData("NVDA", "NVIDIA Corporation", "https://www.google.com/s2/favicons?sz=256&domain=nvidia.com"),
        StockData("META", "Meta Platforms Inc.", "https://www.google.com/s2/favicons?sz=256&domain=meta.com"),
        StockData("NFLX", "Netflix Inc.", "https://www.google.com/s2/favicons?sz=256&domain=netflix.com"),
        StockData("AMD", "Advanced Micro Devices", "https://www.google.com/s2/favicons?sz=256&domain=amd.com"),
        StockData("INTC", "Intel Corporation", "https://www.google.com/s2/favicons?sz=256&domain=intel.com"),
        StockData("BABA", "Alibaba Group", "https://www.google.com/s2/favicons?sz=256&domain=alibaba.com"),
        StockData("TSM", "Taiwan Semiconductor", "https://www.google.com/s2/favicons?sz=256&domain=tsmc.com"),
        StockData("ORCL", "Oracle Corporation", "https://www.google.com/s2/favicons?sz=256&domain=oracle.com"),
        StockData("CSCO", "Cisco Systems", "https://www.google.com/s2/favicons?sz=256&domain=cisco.com"),
        StockData("QCOM", "Qualcomm Inc.", "https://www.google.com/s2/favicons?sz=256&domain=qualcomm.com"),
        StockData("TXN", "Texas Instruments", "https://www.google.com/s2/favicons?sz=256&domain=ti.com"),
        StockData("AVGO", "Broadcom Inc.", "https://www.google.com/s2/favicons?sz=256&domain=broadcom.com"),
        StockData("DIS", "The Walt Disney Company", "https://www.google.com/s2/favicons?sz=256&domain=disney.com"),
        StockData("CRM", "Salesforce Inc.", "https://www.google.com/s2/favicons?sz=256&domain=salesforce.com"),
        StockData("PYPL", "PayPal Holdings", "https://www.google.com/s2/favicons?sz=256&domain=paypal.com"),
        StockData("UBER", "Uber Technologies", "https://www.google.com/s2/favicons?sz=256&domain=uber.com"),
        StockData("SQ", "Block Inc.", "https://www.google.com/s2/favicons?sz=256&domain=squareup.com"),
        StockData("SHOP", "Shopify Inc.", "https://www.google.com/s2/favicons?sz=256&domain=shopify.com"),
        StockData("SNAP", "Snap Inc.", "https://www.google.com/s2/favicons?sz=256&domain=snap.com"),
        StockData("COIN", "Coinbase Global", "https://www.google.com/s2/favicons?sz=256&domain=coinbase.com")
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

