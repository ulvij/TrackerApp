package com.tracker.domain.theme.usecase

import com.tracker.domain.base.BaseUseCase
import com.tracker.domain.error.ErrorConverter
import com.tracker.domain.theme.repository.ThemeRepository
import kotlin.coroutines.CoroutineContext

/**
 * Use case for toggling theme
 */
class ToggleThemeUseCase(
    context: CoroutineContext,
    converter: ErrorConverter,
    private val themeRepository: ThemeRepository
): BaseUseCase<Boolean?, Unit>(context,converter) {

    override suspend fun executeOnBackground(params: Boolean?) {
        themeRepository.toggleTheme(params)
    }
}

