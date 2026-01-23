package mk.digital.kmpshowcase.data.repository

import mk.digital.kmpshowcase.data.local.preferences.PersistentPreferences
import mk.digital.kmpshowcase.domain.repository.SettingsRepository
import mk.digital.kmpshowcase.presentation.foundation.ThemeMode

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
