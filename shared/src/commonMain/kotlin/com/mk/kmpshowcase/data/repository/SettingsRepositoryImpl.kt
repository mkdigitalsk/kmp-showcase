package com.mk.kmpshowcase.data.repository

import com.mk.kmpshowcase.data.local.preferences.PersistentPreferences
import com.mk.kmpshowcase.domain.repository.SettingsRepository
import com.mk.kmpshowcase.presentation.foundation.ThemeMode

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
