package sk.mkdigital.kmpshowcase.data.repository

import sk.mkdigital.kmpshowcase.data.local.preferences.PersistentPreferences
import sk.mkdigital.kmpshowcase.domain.repository.SettingsRepository
import sk.mkdigital.kmpshowcase.presentation.foundation.ThemeMode

class SettingsRepositoryImpl(
    private val persistentPreferences: PersistentPreferences
) : SettingsRepository {

    override suspend fun getThemeMode(): ThemeMode {
        val mode = persistentPreferences.getThemeMode()
        return ThemeMode.entries.find { it.name == mode } ?: ThemeMode.SYSTEM
    }

    override suspend fun setThemeMode(mode: ThemeMode) {
        persistentPreferences.setThemeMode(mode.name)
    }
}
