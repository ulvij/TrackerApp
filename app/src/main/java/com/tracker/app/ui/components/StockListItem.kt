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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tracker.app.ui.theme.Green
import com.tracker.app.ui.theme.Red
import com.tracker.app.ui.theme.TrackerAppTheme
import com.tracker.app.tools.rememberPriceFlashColor
import com.tracker.domain.stock.model.Stock

/**
 * List item component displaying stock information
 */
@Composable
fun StockListItem(
    stock: Stock,
    modifier: Modifier = Modifier
) {
    // Get the animated flash color for price changes
    val priceFlashColor = rememberPriceFlashColor(currentPrice = stock.currentPrice)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo and Symbol
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Stock logo
                AsyncImage(
                    model = stock.logoUrl,
                    contentDescription = "${stock.symbol} logo",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Fit
                )

                // Symbol
                Text(
                    text = stock.symbol,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Price and change
            Column(
                horizontalAlignment = Alignment.End
            ) {
                // Current price
                Text(
                    text = "$%.2f".format(stock.currentPrice),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (priceFlashColor != Color.Transparent) priceFlashColor else MaterialTheme.colorScheme.onSurface
                )

                // Price change with arrow
                if (stock.priceChange != 0.0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        // Use text arrow instead of icon
                        Text(
                            text = if (stock.isPriceIncreased) "▲" else "▼",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (stock.isPriceIncreased) Green else Red,
                            modifier = Modifier.padding(end = 2.dp)
                        )
                        Text(
                            text = "$%.2f (%.2f%%)".format(
                                kotlin.math.abs(stock.priceChange),
                                kotlin.math.abs(stock.priceChangePercentage)
                            ),
                            style = MaterialTheme.typography.bodySmall,
                            color = if (stock.isPriceIncreased) Green else Red
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Light Theme")
@Composable
fun StockListItemPreview() {
    TrackerAppTheme(isDarkTheme = false) {
        Column {
            // Stock with price increase
            StockListItem(
                stock = Stock(
                    symbol = "AAPL",
                    currentPrice = 185.50,
                    previousPrice = 182.30
                )
            )

            // Stock with price decrease
            StockListItem(
                stock = Stock(
                    symbol = "GOOG",
                    currentPrice = 142.75,
                    previousPrice = 148.20
                )
            )

            // Stock with no change
            StockListItem(
                stock = Stock(
                    symbol = "TSLA",
                    currentPrice = 245.00,
                    previousPrice = 245.00
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
                stock = Stock(
                    symbol = "AAPL",
                    currentPrice = 185.50,
                    previousPrice = 182.30
                )
            )

            // Stock with price decrease
            StockListItem(
                stock = Stock(
                    symbol = "GOOG",
                    currentPrice = 142.75,
                    previousPrice = 148.20
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
            stock = Stock(
                symbol = "NVDA",
                currentPrice = 495.85,
                previousPrice = 472.10
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun StockListItemDecreasedPreview() {
    TrackerAppTheme {
        StockListItem(
            stock = Stock(
                symbol = "META",
                currentPrice = 328.40,
                previousPrice = 345.75
            )
        )
    }
}
