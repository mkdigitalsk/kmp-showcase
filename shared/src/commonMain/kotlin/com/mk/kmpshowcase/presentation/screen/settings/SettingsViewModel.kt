package com.mk.kmpshowcase.presentation.screen.settings

import androidx.compose.ui.graphics.vector.ImageVector
import com.mk.kmpshowcase.AppConfig
import com.mk.kmpshowcase.domain.useCase.auth.LogoutUseCase
import com.mk.kmpshowcase.domain.useCase.base.invoke
import com.mk.kmpshowcase.domain.useCase.settings.GetThemeModeUseCase
import com.mk.kmpshowcase.domain.useCase.settings.SetThemeModeUseCase
import com.mk.kmpshowcase.presentation.base.BaseViewModel
import com.mk.kmpshowcase.presentation.base.NavEvent
import com.mk.kmpshowcase.presentation.foundation.AppIcons
import com.mk.kmpshowcase.presentation.foundation.ThemeMode
import com.mk.kmpshowcase.shared.generated.resources.Res
import com.mk.kmpshowcase.shared.generated.resources.language_en
import com.mk.kmpshowcase.shared.generated.resources.language_sk
import com.mk.kmpshowcase.shared.generated.resources.settings_theme_dark
import com.mk.kmpshowcase.shared.generated.resources.settings_theme_light
import com.mk.kmpshowcase.shared.generated.resources.settings_theme_system
import com.mk.kmpshowcase.util.getCurrentLanguageTag
import org.jetbrains.compose.resources.StringResource

data class SettingsState(
    val themeModeState: ThemeModeState = ThemeModeState.SYSTEM,
    val currentLanguage: LanguageState = LanguageState.EN,
    val showThemeDialog: Boolean = false,
    val showCrashButton: Boolean,
    val versionName: String,
    val versionCode: String,
)

enum class ThemeModeState(val textId: StringResource, val mode: ThemeMode) {
    LIGHT(Res.string.settings_theme_light, ThemeMode.LIGHT),
    DARK(Res.string.settings_theme_dark, ThemeMode.DARK),
    SYSTEM(Res.string.settings_theme_system, ThemeMode.SYSTEM);

    companion object {
        fun fromMode(mode: ThemeMode): ThemeModeState =
            entries.find { it.mode == mode } ?: SYSTEM
    }
}

class SettingsViewModel(
    private val getThemeModeUseCase: GetThemeModeUseCase,
    private val setThemeModeUseCase: SetThemeModeUseCase,
    private val logoutUseCase: LogoutUseCase,
    appConfig: AppConfig,
) : BaseViewModel<SettingsState>(
    SettingsState(
        showCrashButton = appConfig.buildType.isDebug,
        versionName = appConfig.versionName,
        versionCode = appConfig.versionCode
    )
) {

    override fun loadInitialData() {
        loadThemeMode()
        loadCurrentLanguage()
    }

    override fun onResumed() {
        loadCurrentLanguage()
    }

    private fun loadCurrentLanguage() {
        val currentTag = getCurrentLanguageTag()
        val language = LanguageState.fromTag(currentTag)
        newState { it.copy(currentLanguage = language) }
    }

    private fun loadThemeMode() {
        execute(
            action = { getThemeModeUseCase() },
            onSuccess = { themeMode -> newState { it.copy(themeModeState = ThemeModeState.fromMode(themeMode)) } }
        )
    }

    fun setThemeMode(themeModeState: ThemeModeState) {
        execute(
            action = { setThemeModeUseCase(themeModeState.mode) },
            onSuccess = {
                newState { it.copy(themeModeState = themeModeState) }
                navigate(SettingNavEvents.ThemeChanged(themeModeState.mode))
            }
        )
    }

    fun showThemeDialog() {
        newState { it.copy(showThemeDialog = true) }
    }

    fun hideThemeDialog() {
        newState { it.copy(showThemeDialog = false) }
    }

    fun onLanguageNavEvent(event: SettingNavEvents) {
        navigate(event)
    }

    fun logout() {
        execute(
            action = { logoutUseCase() },
            onSuccess = { navigate(SettingNavEvents.Logout) }
        )
    }

    @Suppress("TooGenericExceptionThrown")
    fun triggerTestCrash() {
        execute(action = { throw RuntimeException("Test Crash for Firebase Crashlytics") })
    }
}

enum class LanguageState(
    val stringRes: StringResource,
    val icon: ImageVector,
    val tag: String,
) {
    SK(Res.string.language_sk, AppIcons.FlagSK, "sk-SK"),
    EN(Res.string.language_en, AppIcons.FlagEN, "en-US");

    companion object {
        fun fromTag(tag: String?): LanguageState =
            entries.find {
                it.tag.substringBefore('-') == tag
                    ?.lowercase()
                    ?.replace('_', '-')
                    ?.substringBefore('-')
            }
                ?: EN
    }
}

sealed interface SettingNavEvents : NavEvent {

    data class SetLocaleTag(val tag: String) : SettingNavEvents

    data object ToSettings : SettingNavEvents

    data object Logout : SettingNavEvents

    data class ThemeChanged(val mode: ThemeMode) : SettingNavEvents
}
