package sk.mkdigital.kmpshowcase.presentation.screen.settings

import androidx.compose.ui.graphics.vector.ImageVector
import sk.mkdigital.kmpshowcase.AppConfig
import sk.mkdigital.kmpshowcase.domain.useCase.auth.SignOutUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.base.invoke
import sk.mkdigital.kmpshowcase.domain.useCase.settings.GetThemeModeUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.settings.SetThemeModeUseCase
import sk.mkdigital.kmpshowcase.presentation.base.BaseViewModel
import sk.mkdigital.kmpshowcase.presentation.base.NavEvent
import sk.mkdigital.kmpshowcase.presentation.foundation.AppIcons
import sk.mkdigital.kmpshowcase.presentation.foundation.ThemeMode
import sk.mkdigital.kmpshowcase.shared.generated.resources.Res
import sk.mkdigital.kmpshowcase.shared.generated.resources.language_en
import sk.mkdigital.kmpshowcase.shared.generated.resources.language_sk
import sk.mkdigital.kmpshowcase.shared.generated.resources.settings_theme_dark
import sk.mkdigital.kmpshowcase.shared.generated.resources.settings_theme_light
import sk.mkdigital.kmpshowcase.shared.generated.resources.settings_theme_system
import sk.mkdigital.kmpshowcase.util.getCurrentLanguageTag
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
    private val signOutUseCase: SignOutUseCase,
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

    fun openWeb() {
        navigate(SettingNavEvents.OpenWeb(STUDIO_URL))
    }

    fun onLanguageNavEvent(event: SettingNavEvents) {
        navigate(event)
    }

    fun signOut() {
        execute(
            action = { signOutUseCase() },
            onSuccess = { navigate(SettingNavEvents.SignOut) }
        )
    }

    @Suppress("TooGenericExceptionThrown")
    fun triggerTestCrash() {
        execute(action = { throw RuntimeException("Test Crash for Firebase Crashlytics") })
    }

    private companion object {
        const val STUDIO_URL = "https://mkdigital.sk"
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

    data object SignOut : SettingNavEvents

    data class ThemeChanged(val mode: ThemeMode) : SettingNavEvents

    data class OpenWeb(val url: String) : SettingNavEvents
}
