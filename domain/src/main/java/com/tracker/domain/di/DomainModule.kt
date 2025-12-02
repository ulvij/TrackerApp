package com.tracker.domain.di

import com.tracker.domain.connection.repository.ConnectionRepository
import com.tracker.domain.connection.usecase.ObserveConnectionStateUseCase
import com.tracker.domain.connection.usecase.StartConnectionUseCase
import com.tracker.domain.connection.usecase.StopConnectionUseCase
import com.tracker.domain.error.ErrorConverter
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
import kotlin.coroutines.CoroutineContext

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
        context: CoroutineContext,
        converter: ErrorConverter,
        connectionRepository: ConnectionRepository
    ): StartConnectionUseCase {
        return StartConnectionUseCase(
            context = context,
            converter = converter,
            connectionRepository = connectionRepository
        )
    }

    @Provides
    @Singleton
    fun provideStopConnectionUseCase(
        context: CoroutineContext,
        converter: ErrorConverter,
        connectionRepository: ConnectionRepository
    ): StopConnectionUseCase {
        return StopConnectionUseCase(
            context = context,
            converter = converter,
            connectionRepository = connectionRepository
        )
    }

    @Provides
    @Singleton
    fun provideObserveConnectionStateUseCase(
        context: CoroutineContext,
        converter: ErrorConverter,
        connectionRepository: ConnectionRepository
    ): ObserveConnectionStateUseCase {
        return ObserveConnectionStateUseCase(
            context = context,
            converter = converter,
            connectionRepository = connectionRepository
        )
    }

    // Stock Use Cases
    @Provides
    @Singleton
    fun provideObserveStockPricesUseCase(
        context: CoroutineContext,
        converter: ErrorConverter,
        stockRepository: StockRepository
    ): ObserveStockPricesUseCase {
        return ObserveStockPricesUseCase(
            context = context,
            converter = converter,
            stockRepository = stockRepository
        )
    }

    // Theme Use Cases
    @Provides
    @Singleton
    fun provideObserveThemeUseCase(
        context: CoroutineContext,
        converter: ErrorConverter,
        themeRepository: ThemeRepository
    ): ObserveIsDarkThemeUseCase {
        return ObserveIsDarkThemeUseCase(
            context = context,
            converter = converter,
            themeRepository = themeRepository
        )
    }

    @Provides
    @Singleton
    fun provideToggleThemeUseCase(
        context: CoroutineContext,
        converter: ErrorConverter,
        themeRepository: ThemeRepository
    ): ToggleThemeUseCase {
        return ToggleThemeUseCase(
            context = context,
            converter = converter,
            themeRepository = themeRepository
        )
    }
}

