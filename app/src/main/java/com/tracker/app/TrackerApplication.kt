package com.tracker.app

import android.app.Application
import com.tracker.data.stock.StockPriceCoordinator
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Application class with Hilt initialization
 */
@HiltAndroidApp
class TrackerApplication : Application() {

    @Inject
    lateinit var stockPriceCoordinator: StockPriceCoordinator

    override fun onCreate() {
        super.onCreate()
        // Initialize the coordinator - this triggers the init block which starts observing
        stockPriceCoordinator
    }
}

