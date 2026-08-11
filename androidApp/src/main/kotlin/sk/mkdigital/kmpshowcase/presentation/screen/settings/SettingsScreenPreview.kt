package sk.mkdigital.kmpshowcase.presentation.screen.settings

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import sk.mkdigital.kmpshowcase.presentation.foundation.AppTheme

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SettingsScreenPreview(
    @PreviewParameter(SettingsScreenPreviewParams::class) state: SettingsState
) {
    AppTheme {
        SettingsScreen(state = state)
    }
}

internal class SettingsScreenPreviewParams : PreviewParameterProvider<SettingsState> {
    override val values = sequenceOf(
        SettingsState(
            themeModeState = ThemeModeState.SYSTEM,
            currentLanguage = LanguageState.EN,
            showThemeDialog = false,
            showCrashButton = false,
            versionName = "1.0.0",
            versionCode = "1"
        ),
        SettingsState(
            themeModeState = ThemeModeState.DARK,
            currentLanguage = LanguageState.SK,
            showThemeDialog = false,
            showCrashButton = true,
            versionName = "1.0.0",
            versionCode = "1"
        ),
        SettingsState(
            themeModeState = ThemeModeState.LIGHT,
            currentLanguage = LanguageState.EN,
            showThemeDialog = true,
            showCrashButton = false,
            versionName = "1.0.0",
            versionCode = "1"
        ),
        SettingsState(
            themeModeState = ThemeModeState.LIGHT,
            currentLanguage = LanguageState.EN,
            showDeleteAccountDialog = true,
            showCrashButton = false,
            versionName = "1.0.0",
            versionCode = "1"
        ),
        SettingsState(
            themeModeState = ThemeModeState.LIGHT,
            currentLanguage = LanguageState.EN,
            isDemoAccount = true,
            showCrashButton = false,
            versionName = "1.0.0",
            versionCode = "1"
        ),
    )
}
