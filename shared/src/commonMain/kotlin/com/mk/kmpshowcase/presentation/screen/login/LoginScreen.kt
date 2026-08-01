package com.mk.kmpshowcase.presentation.screen.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mk.kmpshowcase.presentation.base.CollectNavEvents
import com.mk.kmpshowcase.presentation.base.NavRouter
import com.mk.kmpshowcase.presentation.base.Route
import com.mk.kmpshowcase.presentation.base.lifecycleAwareViewModel
import com.mk.kmpshowcase.presentation.component.AppPasswordTextField
import com.mk.kmpshowcase.presentation.component.AppTextField
import com.mk.kmpshowcase.presentation.component.biometric.BiometricView
import com.mk.kmpshowcase.presentation.component.buttons.ContainedButton
import com.mk.kmpshowcase.presentation.component.image.AppIconNeutral80
import com.mk.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer2
import com.mk.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer4
import com.mk.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer6
import com.mk.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer8
import com.mk.kmpshowcase.presentation.component.text.bodyMedium.TextBodyMediumNeutral80
import com.mk.kmpshowcase.presentation.component.text.bodySmall.TextBodySmallNeutral80
import com.mk.kmpshowcase.presentation.component.text.labelLarge.TextLabelLargePrimary
import com.mk.kmpshowcase.presentation.component.text.titleLarge.TextTitleLargePrimary
import com.mk.kmpshowcase.presentation.foundation.appColorScheme
import com.mk.kmpshowcase.presentation.foundation.space2
import com.mk.kmpshowcase.presentation.foundation.space4
import com.mk.kmpshowcase.shared.generated.resources.Res
import com.mk.kmpshowcase.shared.generated.resources.login_button
import com.mk.kmpshowcase.shared.generated.resources.login_email_empty
import com.mk.kmpshowcase.shared.generated.resources.login_email_invalid
import com.mk.kmpshowcase.shared.generated.resources.login_email_label
import com.mk.kmpshowcase.shared.generated.resources.login_email_placeholder
import com.mk.kmpshowcase.shared.generated.resources.login_no_account
import com.mk.kmpshowcase.shared.generated.resources.login_or_divider
import com.mk.kmpshowcase.shared.generated.resources.login_password_empty
import com.mk.kmpshowcase.shared.generated.resources.login_password_label
import com.mk.kmpshowcase.shared.generated.resources.login_password_placeholder
import com.mk.kmpshowcase.shared.generated.resources.login_password_short
import com.mk.kmpshowcase.shared.generated.resources.login_password_weak
import com.mk.kmpshowcase.shared.generated.resources.login_register
import com.mk.kmpshowcase.shared.generated.resources.login_skip
import com.mk.kmpshowcase.shared.generated.resources.login_test_account_fill
import com.mk.kmpshowcase.shared.generated.resources.login_test_account_hint
import com.mk.kmpshowcase.shared.generated.resources.login_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginScreen(
    router: NavRouter<Route>,
    viewModel: LoginViewModel = lifecycleAwareViewModel(),
) {
    LoginNavEvents(router)
    val state by viewModel.state.collectAsStateWithLifecycle()
    LoginScreen(
        state = state,
        onSkip = viewModel::skip,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onLogin = viewModel::login,
        onToRegister = viewModel::toRegister,
        onAuthenticateWithBiometrics = viewModel::authenticateWithBiometrics,
        onFillTestAccount = viewModel::fillTestAccount,
    )
}

@Composable
fun LoginScreen(
    state: LoginUiState,
    onSkip: () -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onLogin: () -> Unit = {},
    onToRegister: () -> Unit = {},
    onAuthenticateWithBiometrics: () -> Unit = {},
    onFillTestAccount: () -> Unit = {},
) {
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(space4),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onSkip) {
                TextLabelLargePrimary(stringResource(Res.string.login_skip))
            }
        }

        Spacer4()

        TextTitleLargePrimary(stringResource(Res.string.login_title))

        Spacer8()

        AppTextField(
            value = state.email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(Res.string.login_email_label),
            placeholder = stringResource(Res.string.login_email_placeholder),
            isError = state.emailError != null,
            supportingText = state.emailError?.let { error ->
                when (error) {
                    EmailError.EMPTY -> stringResource(Res.string.login_email_empty)
                    EmailError.INVALID_FORMAT -> stringResource(Res.string.login_email_invalid)
                }
            },
            leadingIcon = {
                AppIconNeutral80(imageVector = Icons.Filled.Email, contentDescription = null)
            },
            showClearButton = false,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
        )

        Spacer2()

        AppPasswordTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(Res.string.login_password_label),
            placeholder = stringResource(Res.string.login_password_placeholder),
            isError = state.passwordError != null,
            supportingText = state.passwordError?.let { error ->
                when (error) {
                    PasswordError.EMPTY -> stringResource(Res.string.login_password_empty)
                    PasswordError.TOO_SHORT -> stringResource(Res.string.login_password_short)
                    PasswordError.WEAK -> stringResource(Res.string.login_password_weak)
                }
            },
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    onLogin()
                }
            )
        )

        Spacer6()

        ContainedButton(
            text = stringResource(Res.string.login_button),
            onClick = {
                focusManager.clearFocus()
                onLogin()
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer4()

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextBodyMediumNeutral80(stringResource(Res.string.login_no_account))
            TextButton(onClick = onToRegister) {
                TextLabelLargePrimary(stringResource(Res.string.login_register))
            }
        }

        if (state.biometricsAvailable) {
            Spacer6()

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(space2)
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f))
                TextBodyMediumNeutral80(stringResource(Res.string.login_or_divider))
                HorizontalDivider(modifier = Modifier.weight(1f))
            }

            Spacer4()

            if (state.biometricsLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            } else {
                BiometricView(onClick = onAuthenticateWithBiometrics)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.appColorScheme.neutral20,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(space4)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextBodySmallNeutral80(stringResource(Res.string.login_test_account_hint))

                Spacer2()

                TextBodyMediumNeutral80(LoginViewModel.TEST_EMAIL)
                TextBodyMediumNeutral80(LoginViewModel.TEST_PASSWORD)

                Spacer2()

                OutlinedButton(
                    onClick = onFillTestAccount
                ) {
                    TextLabelLargePrimary(stringResource(Res.string.login_test_account_fill))
                }
            }
        }

        Spacer4()
    }
}

@Composable
private fun LoginNavEvents(
    router: NavRouter<Route>,
    viewModel: LoginViewModel = lifecycleAwareViewModel(),
) {
    CollectNavEvents(navEventFlow = viewModel.navEvent) { event ->
        when (event) {
            is LoginNavEvent.ToHome -> router.navigateTo(
                Route.HomeSection.Home,
                popUpTo = Route.Login::class,
                inclusive = true
            )

            is LoginNavEvent.ToRegister -> {
                router.navigateTo(Route.Register)
            }
        }
    }
}
