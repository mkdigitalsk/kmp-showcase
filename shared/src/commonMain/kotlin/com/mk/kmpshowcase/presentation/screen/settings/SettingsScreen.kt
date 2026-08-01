package com.mk.kmpshowcase.presentation.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mk.kmpshowcase.presentation.base.CollectNavEvents
import com.mk.kmpshowcase.presentation.base.NavRouter
import com.mk.kmpshowcase.presentation.base.Route
import com.mk.kmpshowcase.presentation.base.lifecycleAwareViewModel
import com.mk.kmpshowcase.presentation.component.AppAlertDialog
import com.mk.kmpshowcase.presentation.component.AppRadioButton
import com.mk.kmpshowcase.presentation.component.AvatarState
import com.mk.kmpshowcase.presentation.component.AvatarView
import com.mk.kmpshowcase.presentation.component.buttons.AppTextButtonError
import com.mk.kmpshowcase.presentation.component.cards.AppElevatedCard
import com.mk.kmpshowcase.presentation.component.image.AppIconPrimary
import com.mk.kmpshowcase.presentation.component.imagepicker.ImagePickerView
import com.mk.kmpshowcase.presentation.component.imagepicker.ImagePickerViewModel
import com.mk.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer2
import com.mk.kmpshowcase.presentation.component.text.bodyLarge.TextBodyLargeNeutral100
import com.mk.kmpshowcase.presentation.component.text.bodyLarge.TextBodyLargePrimary
import com.mk.kmpshowcase.presentation.component.text.bodyMedium.TextBodyMediumNeutral80
import com.mk.kmpshowcase.presentation.component.text.bodySmall.TextBodySmallNeutral80
import com.mk.kmpshowcase.presentation.component.text.titleLarge.TextTitleLargePrimary
import com.mk.kmpshowcase.presentation.foundation.ThemeMode
import com.mk.kmpshowcase.presentation.foundation.floatingNavBarSpace
import com.mk.kmpshowcase.presentation.foundation.space4
import com.mk.kmpshowcase.shared.generated.resources.Res
import com.mk.kmpshowcase.shared.generated.resources.settings_appearance
import com.mk.kmpshowcase.shared.generated.resources.settings_logout
import com.mk.kmpshowcase.shared.generated.resources.settings_profile
import com.mk.kmpshowcase.shared.generated.resources.settings_profile_photo
import com.mk.kmpshowcase.shared.generated.resources.settings_profile_photo_hint
import com.mk.kmpshowcase.shared.generated.resources.settings_test_crash_subtitle
import com.mk.kmpshowcase.shared.generated.resources.settings_test_crash_title
import com.mk.kmpshowcase.shared.generated.resources.settings_theme
import com.mk.kmpshowcase.shared.generated.resources.settings_version
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(
    router: NavRouter<Route>,
    onSetLocale: ((String) -> Unit)?,
    onThemeChanged: (ThemeMode) -> Unit,
    viewModel: SettingsViewModel = lifecycleAwareViewModel(),
    imagePickerViewModel: ImagePickerViewModel = lifecycleAwareViewModel(),
) {
    SettingsNavEvents(
        router = router,
        onSetLocale = onSetLocale,
        onThemeChanged = onThemeChanged,
    )

    val state by viewModel.state.collectAsStateWithLifecycle()
    val imagePickerState by imagePickerViewModel.state.collectAsStateWithLifecycle()

    val avatarState = when {
        imagePickerState.isLoading -> AvatarState.Loading
        imagePickerState.imageBitmap != null -> AvatarState.Loaded(imagePickerState.imageBitmap!!)
        else -> AvatarState.Empty
    }

    SettingsScreen(
        state = state,
        avatarState = avatarState,
        onProfilePhotoClick = { imagePickerViewModel.showDialog() },
        onThemeClick = viewModel::showThemeDialog,
        onLanguageNavEvent = viewModel::onLanguageNavEvent,
        onTriggerTestCrash = viewModel::triggerTestCrash,
        onLogout = viewModel::logout,
        onThemeSelected = { themeModeState ->
            viewModel.setThemeMode(themeModeState)
            viewModel.hideThemeDialog()
        },
        onThemeDialogDismiss = viewModel::hideThemeDialog,
    )

    ImagePickerView(viewModel = imagePickerViewModel)
}

@Composable
fun SettingsScreen(
    state: SettingsState,
    avatarState: AvatarState = AvatarState.Empty,
    onProfilePhotoClick: () -> Unit = {},
    onThemeClick: () -> Unit = {},
    onLanguageNavEvent: (SettingNavEvents) -> Unit = {},
    onTriggerTestCrash: () -> Unit = {},
    onLogout: () -> Unit = {},
    onThemeSelected: (ThemeModeState) -> Unit = {},
    onThemeDialogDismiss: () -> Unit = {},
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = space4,
            end = space4,
            top = space4,
            bottom = floatingNavBarSpace
        ),
        verticalArrangement = Arrangement.spacedBy(space4)
    ) {
        item {
            TextTitleLargePrimary(stringResource(Res.string.settings_profile))
        }

        item {
            AppElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = onProfilePhotoClick
            ) {
                ProfileItem(
                    avatarState = avatarState,
                    title = stringResource(Res.string.settings_profile_photo),
                    hint = stringResource(Res.string.settings_profile_photo_hint)
                )
            }
        }

        item {
            TextTitleLargePrimary(stringResource(Res.string.settings_appearance))
        }

        item {
            AppElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = onThemeClick
            ) {
                SettingsItem(
                    icon = {
                        AppIconPrimary(
                            Icons.Outlined.DarkMode,
                            contentDescription = stringResource(Res.string.settings_theme)
                        )
                    },
                    title = stringResource(Res.string.settings_theme),
                    value = stringResource(state.themeModeState.textId)
                )
            }
        }

        item {
            LanguageSelector(
                currentLanguage = state.currentLanguage,
                onNavigate = onLanguageNavEvent
            )
        }

        if (state.showCrashButton) {
            item {
                AppElevatedCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onTriggerTestCrash
                ) {
                    SettingsItem(
                        icon = {
                            AppIconPrimary(
                                Icons.Outlined.BugReport,
                                contentDescription = stringResource(Res.string.settings_test_crash_title)
                            )
                        },
                        title = stringResource(Res.string.settings_test_crash_title),
                        value = stringResource(Res.string.settings_test_crash_subtitle)
                    )
                }
            }
        }

        item {
            VersionFooter(
                versionName = state.versionName,
                versionCode = state.versionCode
            )
        }

        item {
            AppTextButtonError(
                text = stringResource(Res.string.settings_logout),
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (state.showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = state.themeModeState,
            onThemeSelected = onThemeSelected,
            onDismiss = onThemeDialogDismiss
        )
    }
}

@Composable
private fun ProfileItem(
    avatarState: AvatarState,
    title: String,
    hint: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(space4),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space4)
    ) {
        AvatarView(state = avatarState)
        Column(modifier = Modifier.weight(1f)) {
            TextBodyLargePrimary(title)
            Spacer2()
            TextBodyMediumNeutral80(hint)
        }
    }
}

@Composable
private fun SettingsItem(
    icon: @Composable () -> Unit,
    title: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(space4),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(space4)
    ) {
        icon()
        Column(modifier = Modifier.weight(1f)) {
            TextBodyLargePrimary(title)
            Spacer2()
            TextBodyMediumNeutral80(value)
        }
    }
}

@Composable
private fun ThemeSelectionDialog(
    currentTheme: ThemeModeState,
    onThemeSelected: (ThemeModeState) -> Unit,
    onDismiss: () -> Unit,
) {
    AppAlertDialog(
        title = stringResource(Res.string.settings_theme),
        onDismissRequest = onDismiss,
    ) {
        Column {
            ThemeModeState.entries.forEach { themeModeState ->
                ThemeOption(
                    title = stringResource(themeModeState.textId),
                    selected = currentTheme == themeModeState,
                    onClick = { onThemeSelected(themeModeState) }
                )
            }
        }
    }
}

@Composable
private fun ThemeOption(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = space4),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppRadioButton(selected = selected, onClick = onClick)
        TextBodyLargeNeutral100(title)
    }
}

@Composable
private fun VersionFooter(
    versionName: String,
    versionCode: String,
) {
    TextBodySmallNeutral80(
        text = stringResource(Res.string.settings_version, versionName, versionCode),
        modifier = Modifier.fillMaxWidth(),
        textAlign = androidx.compose.ui.text.style.TextAlign.End,
    )
}

@Composable
private fun SettingsNavEvents(
    router: NavRouter<Route>,
    onSetLocale: ((String) -> Unit)?,
    onThemeChanged: (ThemeMode) -> Unit,
    viewModel: SettingsViewModel = lifecycleAwareViewModel(),
) {
    CollectNavEvents(navEventFlow = viewModel.navEvent) { event ->
        when (event) {
            is SettingNavEvents.SetLocaleTag -> onSetLocale?.invoke(event.tag)
            is SettingNavEvents.ToSettings -> router.openSettings()
            is SettingNavEvents.Logout -> router.navigateTo(
                Route.Login,
                popUpTo = Route.HomeSection.Home::class,
                inclusive = true
            )

            is SettingNavEvents.ThemeChanged -> onThemeChanged(event.mode)
        }
    }
}
