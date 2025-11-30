package com.tracker.domain.theme.usecase

import com.tracker.domain.theme.repository.ThemeRepository

/**
 * Use case for toggling theme
 */
class ToggleThemeUseCase(
    private val themeRepository: ThemeRepository
) {
    suspend operator fun invoke(currentIsDark: Boolean?) {
        themeRepository.toggleTheme(currentIsDark)
    }
}

