package sk.mkdigital.kmpshowcase.presentation.screen.signIn

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
import sk.mkdigital.kmpshowcase.presentation.base.CollectNavEvents
import sk.mkdigital.kmpshowcase.presentation.base.NavRouter
import sk.mkdigital.kmpshowcase.presentation.base.Route
import sk.mkdigital.kmpshowcase.presentation.base.lifecycleAwareViewModel
import sk.mkdigital.kmpshowcase.presentation.component.AppPasswordTextField
import sk.mkdigital.kmpshowcase.presentation.component.AppTextField
import sk.mkdigital.kmpshowcase.presentation.component.biometric.BiometricView
import sk.mkdigital.kmpshowcase.presentation.component.buttons.ContainedButton
import sk.mkdigital.kmpshowcase.presentation.component.text
import sk.mkdigital.kmpshowcase.presentation.component.text.labelLarge.TextLabelLargeError
import sk.mkdigital.kmpshowcase.presentation.component.image.AppIconNeutral80
import sk.mkdigital.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer2
import sk.mkdigital.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer4
import sk.mkdigital.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer6
import sk.mkdigital.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer8
import sk.mkdigital.kmpshowcase.presentation.component.text.bodyMedium.TextBodyMediumNeutral80
import sk.mkdigital.kmpshowcase.presentation.component.text.bodySmall.TextBodySmallNeutral80
import sk.mkdigital.kmpshowcase.presentation.component.text.labelLarge.TextLabelLargePrimary
import sk.mkdigital.kmpshowcase.presentation.component.text.titleLarge.TextTitleLargePrimary
import sk.mkdigital.kmpshowcase.presentation.foundation.appColorScheme
import sk.mkdigital.kmpshowcase.presentation.foundation.space2
import sk.mkdigital.kmpshowcase.presentation.foundation.space4
import sk.mkdigital.kmpshowcase.shared.generated.resources.Res
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_in_button
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_in_email_empty
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_in_email_invalid
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_in_email_label
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_in_email_placeholder
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_in_no_account
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_in_or_divider
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_in_password_empty
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_in_password_label
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_in_password_placeholder
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_in_password_short
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_in_password_weak
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_in_sign_up
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_in_test_account_fill
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_in_test_account_hint
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_in_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun SignInScreen(
    router: NavRouter<Route>,
    viewModel: SignInViewModel = lifecycleAwareViewModel(),
) {
    SignInNavEvents(router)
    val state by viewModel.state.collectAsStateWithLifecycle()
    SignInScreen(
        state = state,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onSignIn = viewModel::signIn,
        onToSignUp = viewModel::toSignUp,
        onAuthenticateWithBiometrics = viewModel::authenticateWithBiometrics,
        onFillTestAccount = viewModel::fillTestAccount,
    )
}

@Composable
fun SignInScreen(
    state: SignInUiState,
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onSignIn: () -> Unit = {},
    onToSignUp: () -> Unit = {},
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
        TextTitleLargePrimary(stringResource(Res.string.sign_in_title))

        Spacer8()

        AppTextField(
            value = state.email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(Res.string.sign_in_email_label),
            placeholder = stringResource(Res.string.sign_in_email_placeholder),
            isError = state.emailError != null,
            supportingText = state.emailError?.let { error ->
                when (error) {
                    EmailError.EMPTY -> stringResource(Res.string.sign_in_email_empty)
                    EmailError.INVALID_FORMAT -> stringResource(Res.string.sign_in_email_invalid)
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
            label = stringResource(Res.string.sign_in_password_label),
            placeholder = stringResource(Res.string.sign_in_password_placeholder),
            isError = state.passwordError != null,
            supportingText = state.passwordError?.let { error ->
                when (error) {
                    PasswordError.EMPTY -> stringResource(Res.string.sign_in_password_empty)
                    PasswordError.TOO_SHORT -> stringResource(Res.string.sign_in_password_short)
                    PasswordError.WEAK -> stringResource(Res.string.sign_in_password_weak)
                }
            },
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    onSignIn()
                }
            )
        )

        state.serverError?.let { error ->
            TextLabelLargeError(text = error.text(), modifier = Modifier.fillMaxWidth())
            Spacer4()
        }

        Spacer6()

        ContainedButton(
            text = stringResource(Res.string.sign_in_button),
            onClick = {
                focusManager.clearFocus()
                onSignIn()
            },
            modifier = Modifier.fillMaxWidth(),
            loading = state.isLoading
        )

        Spacer4()

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextBodyMediumNeutral80(stringResource(Res.string.sign_in_no_account))
            TextButton(onClick = onToSignUp) {
                TextLabelLargePrimary(stringResource(Res.string.sign_in_sign_up))
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
                TextBodyMediumNeutral80(stringResource(Res.string.sign_in_or_divider))
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
                TextBodySmallNeutral80(stringResource(Res.string.sign_in_test_account_hint))

                Spacer2()

                TextBodyMediumNeutral80(SignInViewModel.TEST_EMAIL)
                TextBodyMediumNeutral80(SignInViewModel.TEST_PASSWORD)

                Spacer2()

                OutlinedButton(
                    onClick = onFillTestAccount
                ) {
                    TextLabelLargePrimary(stringResource(Res.string.sign_in_test_account_fill))
                }
            }
        }

        Spacer4()
    }
}

@Composable
private fun SignInNavEvents(
    router: NavRouter<Route>,
    viewModel: SignInViewModel = lifecycleAwareViewModel(),
) {
    CollectNavEvents(navEventFlow = viewModel.navEvent) { event ->
        when (event) {
            is SignInNavEvent.ToHome -> router.navigateTo(
                Route.HomeSection.Home,
                popUpTo = Route.SignIn::class,
                inclusive = true
            )

            is SignInNavEvent.ToSignUp -> {
                router.navigateTo(Route.SignUp)
            }
        }
    }
}
