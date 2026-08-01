package com.mk.kmpshowcase.di

import org.koin.core.module.Module
import org.koin.core.qualifier.named

expect val platformModule: Module


object Qualifiers {
    val session = named(PreferencesScope.SESSION.value)
    val app = named(PreferencesScope.APP.value)
}

enum class PreferencesScope(val value: String) {
    SESSION("session"),
    APP("app")
}
