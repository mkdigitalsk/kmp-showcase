package sk.mkdigital.kmpshowcase.domain.useCase.settings

import sk.mkdigital.kmpshowcase.domain.repository.SettingsRepository
import sk.mkdigital.kmpshowcase.domain.useCase.base.None
import sk.mkdigital.kmpshowcase.domain.useCase.base.UseCase
import sk.mkdigital.kmpshowcase.presentation.foundation.ThemeMode

class GetThemeModeUseCase(
    private val settingsRepository: SettingsRepository
) : UseCase<None, ThemeMode>() {
    override suspend fun run(params: None): ThemeMode = settingsRepository.getThemeMode()
}
