package com.tracker.data.di

import android.content.Context
import com.tracker.data.connection.client.WebSocketManager
import com.tracker.data.connection.repository.ConnectionRepositoryImpl
import com.tracker.data.stock.StockRepositoryImpl
import com.tracker.data.theme.ThemeRepositoryImpl
import com.tracker.domain.connection.repository.ConnectionRepository
import com.tracker.domain.error.ErrorConverter
import com.tracker.domain.error.ErrorConverterImpl
import com.tracker.domain.stock.repository.StockRepository
import com.tracker.domain.theme.repository.ThemeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

/**
 * Hilt module providing data layer implementations
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    fun provideIOContext(): CoroutineContext {
        return Dispatchers.IO
    }

    @Provides
    fun provideErrorConverter(): ErrorConverter {
        return ErrorConverterImpl()
    }

    @Provides
    @Singleton
    fun provideWebSocketManager(): WebSocketManager {
        return WebSocketManager()
    }

    @Provides
    @Singleton
    fun provideConnectionRepository(
        webSocketManager: WebSocketManager
    ): ConnectionRepository {
        return ConnectionRepositoryImpl(webSocketManager)
    }

    @Provides
    @Singleton
    fun provideStockRepository(): StockRepository {
        return StockRepositoryImpl()
    }

    @Provides
    @Singleton
    fun provideThemeRepository(
        @ApplicationContext context: Context
    ): ThemeRepository {
        return ThemeRepositoryImpl(context)
    }
}