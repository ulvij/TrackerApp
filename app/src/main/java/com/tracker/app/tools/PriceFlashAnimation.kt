package com.tracker.app.tools

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import com.tracker.app.ui.theme.Green
import com.tracker.app.ui.theme.Red
import kotlinx.coroutines.delay

/**
 * Composable function that returns an animated color based on price changes
 * Flashes green for 1 second on price increase, red on decrease
 *
 * @param currentPrice The current price to monitor
 * @param increaseColor Color to flash when price increases (default: Green)
 * @param decreaseColor Color to flash when price decreases (default: Red)
 * @param flashDurationMillis Duration of the flash in milliseconds (default: 1000)
 * @param animationDurationMillis Duration of the color animation transition (default: 300)
 * @return Animated color that flashes on price changes, or Color.Transparent when not flashing
 */
@Composable
fun rememberPriceFlashColor(
    currentPrice: Double,
    increaseColor: Color = Green,
    decreaseColor: Color = Red,
    flashDurationMillis: Long = 1000,
    animationDurationMillis: Int = 300
): Color {
    // Track previous price to detect changes
    var previousPrice by remember { mutableDoubleStateOf(currentPrice) }
    var shouldFlash by remember { mutableStateOf(false) }
    var flashColor by remember { mutableStateOf<Color?>(null) }

    // Detect price change and trigger flash
    LaunchedEffect(currentPrice) {
        if (previousPrice != currentPrice) {
            // Determine flash color based on price movement
            flashColor = if (currentPrice > previousPrice) {
                increaseColor
            } else if (currentPrice < previousPrice) {
                decreaseColor
            } else {
                null
            }

            if (flashColor != null) {
                shouldFlash = true
                delay(flashDurationMillis)
                shouldFlash = false
            }

            previousPrice = currentPrice
        }
    }

    // Animate the color
    val animatedColor by animateColorAsState(
        targetValue = if (shouldFlash && flashColor != null) flashColor!! else Color.Transparent,
        animationSpec = tween(durationMillis = animationDurationMillis),
        label = "price_flash_animation"
    )

    return animatedColor
}

