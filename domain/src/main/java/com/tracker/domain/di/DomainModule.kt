package com.tracker.domain.di

import com.tracker.domain.connection.repository.ConnectionRepository
import com.tracker.domain.connection.usecase.ObserveConnectionStateUseCase
import com.tracker.domain.connection.usecase.StartConnectionUseCase
import com.tracker.domain.connection.usecase.StopConnectionUseCase
import com.tracker.domain.stock.repository.StockRepository
import com.tracker.domain.stock.usecase.ObserveStockPricesUseCase
import com.tracker.domain.theme.repository.ThemeRepository
import com.tracker.domain.theme.usecase.ObserveIsDarkThemeUseCase
import com.tracker.domain.theme.usecase.ToggleThemeUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing domain layer use cases
 */
@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    // Connection Use Cases
    @Provides
    @Singleton
    fun provideStartConnectionUseCase(
        connectionRepository: ConnectionRepository
    ): StartConnectionUseCase {
        return StartConnectionUseCase(connectionRepository)
    }

    @Provides
    @Singleton
    fun provideStopConnectionUseCase(
        connectionRepository: ConnectionRepository
    ): StopConnectionUseCase {
        return StopConnectionUseCase(connectionRepository)
    }

    @Provides
    @Singleton
    fun provideObserveConnectionStateUseCase(
        connectionRepository: ConnectionRepository
    ): ObserveConnectionStateUseCase {
        return ObserveConnectionStateUseCase(connectionRepository)
    }

    // Stock Use Cases
    @Provides
    @Singleton
    fun provideObserveStockPricesUseCase(
        stockRepository: StockRepository
    ): ObserveStockPricesUseCase {
        return ObserveStockPricesUseCase(stockRepository)
    }

    // Theme Use Cases
    @Provides
    @Singleton
    fun provideObserveThemeUseCase(
        themeRepository: ThemeRepository
    ): ObserveIsDarkThemeUseCase {
        return ObserveIsDarkThemeUseCase(themeRepository)
    }

    @Provides
    @Singleton
    fun provideToggleThemeUseCase(
        themeRepository: ThemeRepository
    ): ToggleThemeUseCase {
        return ToggleThemeUseCase(themeRepository)
    }
}

