package com.tracker.domain.theme.usecase

import com.tracker.domain.theme.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow

/**
 * Use case for observing theme preference
 */
class ObserveIsDarkThemeUseCase(
    private val themeRepository: ThemeRepository
) {
    operator fun invoke(): Flow<Boolean?> {
        return themeRepository.isDarkTheme
    }
}

