package com.mk.kmpshowcase.domain.repository

import com.mk.kmpshowcase.presentation.foundation.ThemeMode

interface SettingsRepository {
    suspend fun getThemeMode(): ThemeMode
    suspend fun setThemeMode(mode: ThemeMode)
}
