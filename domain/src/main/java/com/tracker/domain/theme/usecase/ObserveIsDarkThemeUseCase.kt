package com.tracker.domain.theme.usecase

import com.tracker.domain.base.BaseFlowUseCase
import com.tracker.domain.error.ErrorConverter
import com.tracker.domain.theme.repository.ThemeRepository
import kotlinx.coroutines.flow.Flow
import kotlin.coroutines.CoroutineContext

/**
 * Use case for observing theme preference
 */
class ObserveIsDarkThemeUseCase(
    context: CoroutineContext,
    converter: ErrorConverter,
    private val themeRepository: ThemeRepository
) : BaseFlowUseCase<Unit, Boolean?>(context, converter) {

    override fun createFlow(params: Unit): Flow<Boolean?> {
        return themeRepository.isDarkTheme
    }
}

