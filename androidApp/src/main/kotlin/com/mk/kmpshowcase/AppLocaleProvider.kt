package com.mk.kmpshowcase

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

interface AppLocaleProvider {
    fun setLocale(languageTag: String?)

    class Impl : AppLocaleProvider {
        override fun setLocale(languageTag: String?) {
            if (languageTag == null) return
            val newLocales = LocaleListCompat.forLanguageTags(languageTag)
            AppCompatDelegate.setApplicationLocales(newLocales)
        }
    }
}
