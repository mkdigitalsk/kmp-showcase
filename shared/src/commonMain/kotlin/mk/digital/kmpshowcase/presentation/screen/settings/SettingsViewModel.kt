package mk.digital.kmpshowcase.presentation.screen.settings

import androidx.compose.ui.graphics.vector.ImageVector
import mk.digital.kmpshowcase.AppConfig
import mk.digital.kmpshowcase.domain.useCase.base.invoke
import mk.digital.kmpshowcase.domain.useCase.settings.GetThemeModeUseCase
import mk.digital.kmpshowcase.domain.useCase.settings.SetThemeModeUseCase
import mk.digital.kmpshowcase.presentation.base.BaseViewModel
import mk.digital.kmpshowcase.presentation.base.NavEvent
import mk.digital.kmpshowcase.presentation.foundation.AppIcons
import mk.digital.kmpshowcase.presentation.foundation.ThemeMode
import mk.digital.kmpshowcase.shared.generated.resources.Res
import mk.digital.kmpshowcase.shared.generated.resources.language_en
import mk.digital.kmpshowcase.shared.generated.resources.language_sk
import mk.digital.kmpshowcase.shared.generated.resources.settings_theme_dark
import mk.digital.kmpshowcase.shared.generated.resources.settings_theme_light
import mk.digital.kmpshowcase.shared.generated.resources.settings_theme_system
import mk.digital.kmpshowcase.util.getCurrentLanguageTag
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
    appConfig: AppConfig,
    private val onThemeChanged: (ThemeMode) -> Unit,
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
            onSuccess = { themeMode ->
                newState { it.copy(themeModeState = ThemeModeState.fromMode(themeMode)) }
            }
        )
    }

    fun setThemeMode(themeModeState: ThemeModeState) {
        execute(
            action = { setThemeModeUseCase(themeModeState.mode) },
            onSuccess = {
                newState { it.copy(themeModeState = themeModeState) }
                onThemeChanged(themeModeState.mode)
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

    // Android
    data class SetLocaleTag(val tag: String) : SettingNavEvents

    // iOS
    data object ToSettings : SettingNavEvents
}
