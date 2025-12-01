package com.tracker.data.di

import android.content.Context
import com.tracker.data.stock.StockRepositoryImpl
import com.tracker.data.connection.client.WebSocketManager
import com.tracker.data.connection.repository.ConnectionRepositoryImpl
import com.tracker.data.theme.ThemeRepositoryImpl
import com.tracker.domain.connection.repository.ConnectionRepository
import com.tracker.domain.stock.repository.StockRepository
import com.tracker.domain.theme.repository.ThemeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing data layer implementations
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {

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