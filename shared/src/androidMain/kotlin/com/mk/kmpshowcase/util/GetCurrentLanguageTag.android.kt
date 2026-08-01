package com.mk.kmpshowcase.util

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

actual fun getCurrentLanguageTag(): String {
    val appLocales = AppCompatDelegate.getApplicationLocales()
    val locale = appLocales.get(0) ?: LocaleListCompat.getAdjustedDefault().get(0)
    return locale?.toLanguageTag() ?: "en"
}
