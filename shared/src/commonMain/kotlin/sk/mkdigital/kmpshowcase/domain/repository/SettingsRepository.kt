package sk.mkdigital.kmpshowcase.domain.repository

import sk.mkdigital.kmpshowcase.presentation.foundation.ThemeMode

interface SettingsRepository {
    suspend fun getThemeMode(): ThemeMode
    suspend fun setThemeMode(mode: ThemeMode)
}
