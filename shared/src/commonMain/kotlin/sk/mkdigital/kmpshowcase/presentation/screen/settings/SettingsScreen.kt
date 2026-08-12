package sk.mkdigital.kmpshowcase.presentation.screen.settings

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
import sk.mkdigital.kmpshowcase.presentation.base.CollectNavEvents
import sk.mkdigital.kmpshowcase.presentation.base.NavRouter
import sk.mkdigital.kmpshowcase.presentation.base.Route
import sk.mkdigital.kmpshowcase.presentation.base.lifecycleAwareViewModel
import sk.mkdigital.kmpshowcase.presentation.component.AppAlertDialog
import sk.mkdigital.kmpshowcase.presentation.component.AppRadioButton
import sk.mkdigital.kmpshowcase.presentation.component.AvatarState
import sk.mkdigital.kmpshowcase.presentation.component.AvatarView
import sk.mkdigital.kmpshowcase.presentation.component.buttons.AppTextButtonPrimary
import sk.mkdigital.kmpshowcase.presentation.component.buttons.AppTextButtonError
import sk.mkdigital.kmpshowcase.presentation.component.cards.AppElevatedCard
import sk.mkdigital.kmpshowcase.presentation.component.image.AppIconPrimary
import sk.mkdigital.kmpshowcase.presentation.component.image.AppImage
import sk.mkdigital.kmpshowcase.presentation.component.imagepicker.ImagePickerView
import sk.mkdigital.kmpshowcase.presentation.component.imagepicker.ImagePickerViewModel
import sk.mkdigital.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer2
import sk.mkdigital.kmpshowcase.presentation.component.text.bodyLarge.TextBodyLargeNeutral100
import sk.mkdigital.kmpshowcase.presentation.component.text.bodyLarge.TextBodyLargePrimary
import sk.mkdigital.kmpshowcase.presentation.component.text.bodyMedium.TextBodyMediumNeutral80
import sk.mkdigital.kmpshowcase.presentation.component.text.bodySmall.TextBodySmallNeutral80
import sk.mkdigital.kmpshowcase.presentation.component.text.labelLarge.TextLabelLargeError
import sk.mkdigital.kmpshowcase.presentation.component.text.titleLarge.TextTitleLargePrimary
import sk.mkdigital.kmpshowcase.presentation.foundation.ThemeMode
import sk.mkdigital.kmpshowcase.presentation.foundation.floatingNavBarSpace
import sk.mkdigital.kmpshowcase.presentation.foundation.space2
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.material3.MaterialTheme
import sk.mkdigital.kmpshowcase.presentation.component.text.bodySmall.TextBodySmall
import sk.mkdigital.kmpshowcase.presentation.foundation.space4
import sk.mkdigital.kmpshowcase.shared.generated.resources.Res
import sk.mkdigital.kmpshowcase.shared.generated.resources.button_cancel
import sk.mkdigital.kmpshowcase.shared.generated.resources.mk_digital_lockup
import sk.mkdigital.kmpshowcase.shared.generated.resources.settings_about
import sk.mkdigital.kmpshowcase.shared.generated.resources.settings_about_tagline
import sk.mkdigital.kmpshowcase.shared.generated.resources.settings_about_web
import sk.mkdigital.kmpshowcase.shared.generated.resources.settings_appearance
import sk.mkdigital.kmpshowcase.shared.generated.resources.settings_delete_account
import sk.mkdigital.kmpshowcase.shared.generated.resources.settings_delete_account_confirm
import sk.mkdigital.kmpshowcase.shared.generated.resources.settings_delete_account_demo
import sk.mkdigital.kmpshowcase.shared.generated.resources.settings_delete_account_error
import sk.mkdigital.kmpshowcase.shared.generated.resources.settings_delete_account_message
import sk.mkdigital.kmpshowcase.shared.generated.resources.settings_delete_account_title
import sk.mkdigital.kmpshowcase.shared.generated.resources.settings_sign_out
import sk.mkdigital.kmpshowcase.shared.generated.resources.settings_profile
import sk.mkdigital.kmpshowcase.shared.generated.resources.settings_profile_photo
import sk.mkdigital.kmpshowcase.shared.generated.resources.settings_profile_photo_hint
import sk.mkdigital.kmpshowcase.shared.generated.resources.settings_test_crash_subtitle
import sk.mkdigital.kmpshowcase.shared.generated.resources.settings_test_crash_title
import sk.mkdigital.kmpshowcase.shared.generated.resources.settings_theme
import sk.mkdigital.kmpshowcase.shared.generated.resources.settings_version
import org.jetbrains.compose.resources.stringResource

@Composable
fun SettingsScreen(
    router: NavRouter<Route>,
    onSetLocale: ((String) -> Unit)?,
    onThemeChange: (ThemeMode) -> Unit,
    viewModel: SettingsViewModel = lifecycleAwareViewModel(),
    imagePickerViewModel: ImagePickerViewModel = lifecycleAwareViewModel(),
) {
    SettingsNavEvents(
        router = router,
        onSetLocale = onSetLocale,
        onThemeChange = onThemeChange,
    )

    val state by viewModel.state.collectAsStateWithLifecycle()
    val imagePickerState by imagePickerViewModel.state.collectAsStateWithLifecycle()

    val avatarBitmap = imagePickerState.imageBitmap
    val avatarState = when {
        imagePickerState.isLoading -> AvatarState.Loading
        avatarBitmap != null -> AvatarState.Loaded(avatarBitmap)
        else -> AvatarState.Empty
    }

    SettingsScreen(
        state = state,
        avatarState = avatarState,
        onProfilePhotoClick = { imagePickerViewModel.showDialog() },
        onThemeClick = viewModel::showThemeDialog,
        onLanguageNavEvent = viewModel::onLanguageNavEvent,
        onTriggerTestCrash = viewModel::triggerTestCrash,
        onSignOut = viewModel::signOut,
        onDeleteAccountClick = viewModel::showDeleteAccountDialog,
        onDeleteAccountConfirm = viewModel::deleteAccount,
        onDeleteAccountDismiss = viewModel::hideDeleteAccountDialog,
        onThemeSelect = { themeModeState ->
            viewModel.setThemeMode(themeModeState)
            viewModel.hideThemeDialog()
        },
        onThemeDialogDismiss = viewModel::hideThemeDialog,
        onWebClick = viewModel::openWeb,
    )

    ImagePickerView(
        state = imagePickerState,
        onImageLoading = imagePickerViewModel::onImageLoading,
        onImageResult = imagePickerViewModel::onImageResult,
        onDialogDismiss = imagePickerViewModel::hideDialog,
        onActionSelect = imagePickerViewModel::onActionSelected,
        onActionReset = imagePickerViewModel::resetAction,
    )
}

@Composable
fun SettingsScreen(
    state: SettingsState,
    avatarState: AvatarState = AvatarState.Empty,
    onProfilePhotoClick: () -> Unit = {},
    onThemeClick: () -> Unit = {},
    onLanguageNavEvent: (SettingNavEvents) -> Unit = {},
    onTriggerTestCrash: () -> Unit = {},
    onSignOut: () -> Unit = {},
    onDeleteAccountClick: () -> Unit = {},
    onDeleteAccountConfirm: () -> Unit = {},
    onDeleteAccountDismiss: () -> Unit = {},
    onThemeSelect: (ThemeModeState) -> Unit = {},
    onThemeDialogDismiss: () -> Unit = {},
    onWebClick: () -> Unit = {},
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
            TextTitleLargePrimary(
                text = stringResource(Res.string.settings_appearance),
                modifier = Modifier.padding(top = space4)
            )
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
            TextTitleLargePrimary(
                text = stringResource(Res.string.settings_about),
                modifier = Modifier.padding(top = space4)
            )
        }

        item {
            AppElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = onWebClick
            ) {
                AboutItem()
            }
        }

        item {
            VersionFooter(
                versionName = state.versionName,
                versionCode = state.versionCode
            )
        }

        item {
            AppTextButtonPrimary(
                text = stringResource(Res.string.settings_sign_out),
                onClick = onSignOut,
                modifier = Modifier.fillMaxWidth()
            )
        }

        item {
            if (state.isDemoAccount) {
                TextBodyMediumNeutral80(
                    text = stringResource(Res.string.settings_delete_account_demo),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            } else {
                AppTextButtonError(
                    text = stringResource(Res.string.settings_delete_account),
                    onClick = onDeleteAccountClick,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (state.deleteAccountFailed) {
            item {
                TextLabelLargeError(
                    text = stringResource(Res.string.settings_delete_account_error),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    if (state.showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = state.themeModeState,
            onThemeSelect = onThemeSelect,
            onDismiss = onThemeDialogDismiss
        )
    }

    if (state.showDeleteAccountDialog) {
        DeleteAccountDialog(
            deleting = state.isDeletingAccount,
            onConfirm = onDeleteAccountConfirm,
            onDismiss = onDeleteAccountDismiss
        )
    }
}

@Composable
private fun DeleteAccountDialog(
    deleting: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AppAlertDialog(
        title = stringResource(Res.string.settings_delete_account_title),
        text = stringResource(Res.string.settings_delete_account_message),
        onDismissRequest = onDismiss,
        dismissButton = {
            AppTextButtonPrimary(text = stringResource(Res.string.button_cancel), onClick = onDismiss)
        },
        confirmButton = {
            AppTextButtonError(
                text = stringResource(Res.string.settings_delete_account_confirm),
                onClick = onConfirm,
                loading = deleting
            )
        }
    )
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
    onThemeSelect: (ThemeModeState) -> Unit,
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
                    onClick = { onThemeSelect(themeModeState) }
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
private fun AboutItem() {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Full-bleed: the card clips its own content, so the media inherits the card's top corners
        // instead of guessing an inner radius.
        AppImage(
            resource = Res.drawable.mk_digital_lockup,
            modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.FillWidth
        )
        Column(
            modifier = Modifier.fillMaxWidth().padding(space4),
            verticalArrangement = Arrangement.spacedBy(space2)
        ) {
            TextBodyLargeNeutral100(
                text = stringResource(Res.string.settings_about_tagline),
                fontWeight = FontWeight.Bold
            )
            TextBodySmall(
                text = stringResource(Res.string.settings_about_web),
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            )
        }
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
    onThemeChange: (ThemeMode) -> Unit,
    viewModel: SettingsViewModel = lifecycleAwareViewModel(),
) {
    CollectNavEvents(navEventFlow = viewModel.navEvent) { event ->
        when (event) {
            is SettingNavEvents.SetLocaleTag -> onSetLocale?.invoke(event.tag)
            is SettingNavEvents.ToSettings -> router.openSettings()
            is SettingNavEvents.SignOut, is SettingNavEvents.AccountDeleted -> router.navigateTo(
                Route.SignIn,
                popUpTo = Route.HomeSection.Home::class,
                inclusive = true
            )

            is SettingNavEvents.ThemeChanged -> onThemeChange(event.mode)
            is SettingNavEvents.OpenWeb -> router.openLink(event.url)
        }
    }
}
