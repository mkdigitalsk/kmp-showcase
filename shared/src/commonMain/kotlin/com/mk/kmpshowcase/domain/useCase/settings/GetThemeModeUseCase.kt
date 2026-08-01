package com.mk.kmpshowcase.domain.useCase.settings

import com.mk.kmpshowcase.domain.repository.SettingsRepository
import com.mk.kmpshowcase.domain.useCase.base.None
import com.mk.kmpshowcase.domain.useCase.base.UseCase
import com.mk.kmpshowcase.presentation.foundation.ThemeMode

class GetThemeModeUseCase(
    private val settingsRepository: SettingsRepository
) : UseCase<None, ThemeMode>() {
    override suspend fun run(params: None): ThemeMode = settingsRepository.getThemeMode()
}
