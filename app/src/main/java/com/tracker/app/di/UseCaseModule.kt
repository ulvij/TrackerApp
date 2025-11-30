package com.tracker.app.di

import com.tracker.domain.repository.ConnectionRepository
import com.tracker.domain.repository.StockRepository
import com.tracker.domain.usecase.ConnectWebSocketUseCase
import com.tracker.domain.usecase.DisconnectWebSocketUseCase
import com.tracker.domain.usecase.ObserveConnectionStateUseCase
import com.tracker.domain.usecase.ObservePriceUpdatesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

