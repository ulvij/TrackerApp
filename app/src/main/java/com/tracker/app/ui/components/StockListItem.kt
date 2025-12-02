package com.tracker.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tracker.app.tools.rememberPriceFlashColor
import com.tracker.app.ui.TestTags
import com.tracker.app.ui.model.StockUIModel
import com.tracker.app.ui.theme.Green
import com.tracker.app.ui.theme.Red
import com.tracker.app.ui.theme.TrackerAppTheme

/**
 * List item component displaying stock information
 * Optimized for minimal recomposition - only price section recomposes on updates
 */
@Composable
fun StockListItem(
    stock: StockUIModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .testTag("${TestTags.STOCK_ITEM_PREFIX}${stock.symbol}"),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            StockIdentity(
                symbol = stock.symbol,
                logoUrl = stock.logoUrl
            )

            StockPriceSection(
                symbol = stock.symbol,
                currentPrice = stock.currentPrice,
                priceChange = stock.priceChange,
                priceChangePercentage = stock.priceChangePercentage,
                isPriceIncreased = stock.isPriceIncreased
            )
        }
    }
}

/**
 * Static identity section (logo + symbol)
 * Won't recompose when only price changes
 */
@Composable
private fun StockIdentity(
    symbol: String,
    logoUrl: String?,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
    ) {
        // Stock logo
        AsyncImage(
            model = logoUrl,
            contentDescription = "$symbol logo",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Fit
        )

        // Symbol
        Text(
            text = symbol,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * Dynamic price section
 * This is the only part that recomposes when price changes
 */
@Composable
private fun StockPriceSection(
    symbol: String,
    currentPrice: Double,
    priceChange: Double,
    priceChangePercentage: Double,
    isPriceIncreased: Boolean,
    modifier: Modifier = Modifier
) {
    // Get the animated flash color for price changes
    val priceFlashColor = rememberPriceFlashColor(currentPrice = currentPrice)

    Column(
        horizontalAlignment = Alignment.End,
        modifier = modifier
    ) {
        // Current price
        Text(
            text = "$%.2f".format(currentPrice),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (priceFlashColor != Color.Transparent) priceFlashColor else MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.testTag("${TestTags.STOCK_PRICE_PREFIX}$symbol")
        )

        // Price change with arrow
        if (priceChange != 0.0) {
            PriceChangeIndicator(
                symbol = symbol,
                priceChange = priceChange,
                priceChangePercentage = priceChangePercentage,
                isPriceIncreased = isPriceIncreased
            )
        }
    }
}

/**
 * Price change indicator (arrow + change amount)
 * Separated for even finer-grained recomposition control
 */
@Composable
private fun PriceChangeIndicator(
    symbol: String,
    priceChange: Double,
    priceChangePercentage: Double,
    isPriceIncreased: Boolean,
    modifier: Modifier = Modifier
) {
    val changeColor = if (isPriceIncreased) Green else Red
    val arrowSymbol = if (isPriceIncreased) "▲" else "▼"

    Text(
        text = "$arrowSymbol $%.2f (%.2f%%)".format(
            kotlin.math.abs(priceChange),
            kotlin.math.abs(priceChangePercentage)
        ),
        style = MaterialTheme.typography.bodyMedium,
        color = changeColor,
        modifier = modifier.testTag("${TestTags.STOCK_CHANGE_PREFIX}$symbol")
    )
}

@Preview(showBackground = true, name = "Light Theme")
@Composable
fun StockListItemPreview() {
    TrackerAppTheme(isDarkTheme = false) {
        Column {
            // Stock with price increase
            StockListItem(
                stock = StockUIModel(
                    symbol = "AAPL",
                    currentPrice = 185.50,
                    previousPrice = 182.30,
                    priceChange = 3.20,
                    priceChangePercentage = 1.76,
                    isPriceIncreased = true,
                    logoUrl = null,
                    timestamp = System.currentTimeMillis()
                )
            )

            // Stock with price decrease
            StockListItem(
                stock = StockUIModel(
                    symbol = "GOOG",
                    currentPrice = 142.75,
                    previousPrice = 148.20,
                    priceChange = -5.45,
                    priceChangePercentage = -3.68,
                    isPriceIncreased = false,
                    logoUrl = null,
                    timestamp = System.currentTimeMillis()
                )
            )

            // Stock with no change
            StockListItem(
                stock = StockUIModel(
                    symbol = "TSLA",
                    currentPrice = 245.00,
                    previousPrice = 245.00,
                    priceChange = 0.0,
                    priceChangePercentage = 0.0,
                    isPriceIncreased = false,
                    logoUrl = null,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }
}

@Preview(showBackground = true, name = "Dark Theme")
@Composable
fun StockListItemDarkPreview() {
    TrackerAppTheme(isDarkTheme = true) {
        Column {
            // Stock with price increase
            StockListItem(
                stock = StockUIModel(
                    symbol = "AAPL",
                    currentPrice = 185.50,
                    previousPrice = 182.30,
                    priceChange = 3.20,
                    priceChangePercentage = 1.76,
                    isPriceIncreased = true,
                    logoUrl = null,
                    timestamp = System.currentTimeMillis()
                )
            )

            // Stock with price decrease
            StockListItem(
                stock = StockUIModel(
                    symbol = "GOOG",
                    currentPrice = 142.75,
                    previousPrice = 148.20,
                    priceChange = -5.45,
                    priceChangePercentage = -3.68,
                    isPriceIncreased = false,
                    logoUrl = null,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun StockListItemIncreasedPreview() {
    TrackerAppTheme {
        StockListItem(
            stock = StockUIModel(
                symbol = "NVDA",
                currentPrice = 495.85,
                previousPrice = 472.10,
                priceChange = 23.75,
                priceChangePercentage = 5.03,
                isPriceIncreased = true,
                logoUrl = null,
                timestamp = System.currentTimeMillis()
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StockListItemDecreasedPreview() {
    TrackerAppTheme {
        StockListItem(
            stock = StockUIModel(
                symbol = "META",
                currentPrice = 328.40,
                previousPrice = 345.75,
                priceChange = -17.35,
                priceChangePercentage = -5.02,
                isPriceIncreased = false,
                logoUrl = null,
                timestamp = System.currentTimeMillis()
            )
        )
    }
}
