package com.mk.kmpshowcase.domain.useCase.settings

import com.mk.kmpshowcase.domain.repository.SettingsRepository
import com.mk.kmpshowcase.domain.useCase.base.UseCase
import com.mk.kmpshowcase.presentation.foundation.ThemeMode

class SetThemeModeUseCase(
    private val settingsRepository: SettingsRepository
) : UseCase<ThemeMode, Unit>() {
    override suspend fun run(params: ThemeMode) = settingsRepository.setThemeMode(params)
}
