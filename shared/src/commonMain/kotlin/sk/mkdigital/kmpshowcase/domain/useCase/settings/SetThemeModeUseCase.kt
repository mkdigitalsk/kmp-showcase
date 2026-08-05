package sk.mkdigital.kmpshowcase.domain.useCase.settings

import sk.mkdigital.kmpshowcase.domain.repository.SettingsRepository
import sk.mkdigital.kmpshowcase.domain.useCase.base.UseCase
import sk.mkdigital.kmpshowcase.presentation.foundation.ThemeMode

class SetThemeModeUseCase(
    private val settingsRepository: SettingsRepository
) : UseCase<ThemeMode, Unit>() {
    override suspend fun run(params: ThemeMode) = settingsRepository.setThemeMode(params)
}
