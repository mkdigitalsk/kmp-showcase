package mk.digital.kmpshowcase.presentation.screen.settings

import mk.digital.kmpshowcase.AppConfig
import mk.digital.kmpshowcase.BuildType
import mk.digital.kmpshowcase.presentation.foundation.ThemeMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SettingsViewModelTest {

    // === ThemeModeState Tests ===

    @Test
    fun `ThemeModeState LIGHT has correct mode`() {
        assertEquals(ThemeMode.LIGHT, ThemeModeState.LIGHT.mode)
    }

    @Test
    fun `ThemeModeState DARK has correct mode`() {
        assertEquals(ThemeMode.DARK, ThemeModeState.DARK.mode)
    }

    @Test
    fun `ThemeModeState SYSTEM has correct mode`() {
        assertEquals(ThemeMode.SYSTEM, ThemeModeState.SYSTEM.mode)
    }

    @Test
    fun `ThemeModeState fromMode returns correct state for LIGHT`() {
        assertEquals(ThemeModeState.LIGHT, ThemeModeState.fromMode(ThemeMode.LIGHT))
    }

    @Test
    fun `ThemeModeState fromMode returns correct state for DARK`() {
        assertEquals(ThemeModeState.DARK, ThemeModeState.fromMode(ThemeMode.DARK))
    }

    @Test
    fun `ThemeModeState fromMode returns correct state for SYSTEM`() {
        assertEquals(ThemeModeState.SYSTEM, ThemeModeState.fromMode(ThemeMode.SYSTEM))
    }

    @Test
    fun `ThemeModeState entries has all values`() {
        assertEquals(3, ThemeModeState.entries.size)
    }

    // === LanguageState Tests ===

    @Test
    fun `LanguageState SK has correct tag`() {
        assertEquals("sk-SK", LanguageState.SK.tag)
    }

    @Test
    fun `LanguageState EN has correct tag`() {
        assertEquals("en-US", LanguageState.EN.tag)
    }

    @Test
    fun `LanguageState fromTag returns SK for sk tag`() {
        assertEquals(LanguageState.SK, LanguageState.fromTag("sk"))
    }

    @Test
    fun `LanguageState fromTag returns SK for sk-SK tag`() {
        assertEquals(LanguageState.SK, LanguageState.fromTag("sk-SK"))
    }

    @Test
    fun `LanguageState fromTag returns EN for en tag`() {
        assertEquals(LanguageState.EN, LanguageState.fromTag("en"))
    }

    @Test
    fun `LanguageState fromTag returns EN for en-US tag`() {
        assertEquals(LanguageState.EN, LanguageState.fromTag("en-US"))
    }

    @Test
    fun `LanguageState fromTag returns EN for unknown tag`() {
        assertEquals(LanguageState.EN, LanguageState.fromTag("fr"))
    }

    @Test
    fun `LanguageState fromTag returns EN for null`() {
        assertEquals(LanguageState.EN, LanguageState.fromTag(null))
    }

    @Test
    fun `LanguageState fromTag handles underscore format`() {
        assertEquals(LanguageState.SK, LanguageState.fromTag("sk_SK"))
    }

    @Test
    fun `LanguageState entries has all values`() {
        assertEquals(2, LanguageState.entries.size)
    }

    // === SettingsState Tests ===

    @Test
    fun `SettingsState default theme is SYSTEM`() {
        val state = SettingsState(showCrashButton = false, versionName = "1.0", versionCode = "1")
        assertEquals(ThemeModeState.SYSTEM, state.themeModeState)
    }

    @Test
    fun `SettingsState default language is EN`() {
        val state = SettingsState(showCrashButton = false, versionName = "1.0", versionCode = "1")
        assertEquals(LanguageState.EN, state.currentLanguage)
    }

    @Test
    fun `SettingsState showThemeDialog default is false`() {
        val state = SettingsState(showCrashButton = false, versionName = "1.0", versionCode = "1")
        assertFalse(state.showThemeDialog)
    }

    @Test
    fun `SettingsState copy works correctly`() {
        val state = SettingsState(showCrashButton = false, versionName = "1.0", versionCode = "1")
        val copied = state.copy(showThemeDialog = true)
        assertTrue(copied.showThemeDialog)
        assertFalse(state.showThemeDialog)
    }

    // === BuildType Tests ===

    @Test
    fun `DEBUG build type isDebug returns true`() {
        assertTrue(BuildType.DEBUG.isDebug)
    }

    @Test
    fun `DEBUG build type isRelease returns false`() {
        assertFalse(BuildType.DEBUG.isRelease)
    }

    @Test
    fun `RELEASE build type isRelease returns true`() {
        assertTrue(BuildType.RELEASE.isRelease)
    }

    @Test
    fun `RELEASE build type isDebug returns false`() {
        assertFalse(BuildType.RELEASE.isDebug)
    }

    @Test
    fun `BuildType from returns DEBUG for debug string`() {
        assertEquals(BuildType.DEBUG, BuildType.from("debug"))
    }

    @Test
    fun `BuildType from returns RELEASE for release string`() {
        assertEquals(BuildType.RELEASE, BuildType.from("release"))
    }

    @Test
    fun `BuildType from returns DEBUG for unknown string`() {
        assertEquals(BuildType.DEBUG, BuildType.from("unknown"))
    }

    // === AppConfig Tests ===

    @Test
    fun `AppConfig stores buildType correctly`() {
        val config = AppConfig(BuildType.RELEASE, "2.0.0", "10")
        assertEquals(BuildType.RELEASE, config.buildType)
    }

    @Test
    fun `AppConfig stores versionName correctly`() {
        val config = AppConfig(BuildType.DEBUG, "2.0.0", "10")
        assertEquals("2.0.0", config.versionName)
    }

    @Test
    fun `AppConfig stores versionCode correctly`() {
        val config = AppConfig(BuildType.DEBUG, "2.0.0", "10")
        assertEquals("10", config.versionCode)
    }
}
