package sk.mkdigital.kmpshowcase.presentation.screen.signUp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import sk.mkdigital.kmpshowcase.presentation.base.CollectNavEvents
import sk.mkdigital.kmpshowcase.presentation.base.NavRouter
import sk.mkdigital.kmpshowcase.presentation.base.Route
import sk.mkdigital.kmpshowcase.presentation.base.lifecycleAwareViewModel
import sk.mkdigital.kmpshowcase.presentation.component.AppPasswordTextField
import sk.mkdigital.kmpshowcase.presentation.component.AppTextField
import sk.mkdigital.kmpshowcase.presentation.component.buttons.AppTextButtonPrimary
import sk.mkdigital.kmpshowcase.presentation.component.buttons.ContainedButton
import sk.mkdigital.kmpshowcase.presentation.component.text.labelLarge.TextLabelLargeError
import sk.mkdigital.kmpshowcase.presentation.component.text
import sk.mkdigital.kmpshowcase.presentation.component.image.AppIconNeutral80
import sk.mkdigital.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer4
import sk.mkdigital.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer8
import sk.mkdigital.kmpshowcase.presentation.component.text.bodyMedium.TextBodyMediumNeutral80
import sk.mkdigital.kmpshowcase.presentation.component.text.titleLarge.TextTitleLargePrimary
import sk.mkdigital.kmpshowcase.presentation.foundation.space2
import sk.mkdigital.kmpshowcase.presentation.foundation.space4
import sk.mkdigital.kmpshowcase.presentation.foundation.space6
import sk.mkdigital.kmpshowcase.shared.generated.resources.Res
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_up_button
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_up_confirm_password_empty
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_up_confirm_password_label
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_up_confirm_password_mismatch
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_up_confirm_password_placeholder
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_up_email_already_exists
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_up_email_empty
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_up_email_invalid
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_up_email_label
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_up_email_placeholder
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_up_has_account
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_up_privacy
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_up_sign_in
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_up_password_empty
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_up_password_label
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_up_password_placeholder
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_up_password_short
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_up_password_weak
import sk.mkdigital.kmpshowcase.shared.generated.resources.sign_up_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun SignUpScreen(
    router: NavRouter<Route>,
    viewModel: SignUpViewModel = lifecycleAwareViewModel(),
) {
    SignUpNavEvents(router)
    val state by viewModel.state.collectAsStateWithLifecycle()
    SignUpScreen(
        state = state,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onSignUp = viewModel::signUp,
        onToSignIn = viewModel::toSignIn,
        onPrivacy = viewModel::openPrivacy,
    )
}

@Composable
fun SignUpScreen(
    state: SignUpUiState,
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onConfirmPasswordChange: (String) -> Unit = {},
    onSignUp: () -> Unit = {},
    onToSignIn: () -> Unit = {},
    onPrivacy: () -> Unit = {},
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
        Spacer8()
        TextTitleLargePrimary(stringResource(Res.string.sign_up_title))
        Spacer8()

        Spacer(modifier = Modifier.height(space2))

        AppTextField(
            value = state.email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(Res.string.sign_up_email_label),
            placeholder = stringResource(Res.string.sign_up_email_placeholder),
            isError = state.emailError != null,
            supportingText = state.emailError?.let { error ->
                when (error) {
                    SignUpEmailError.EMPTY -> stringResource(Res.string.sign_up_email_empty)
                    SignUpEmailError.INVALID_FORMAT -> stringResource(Res.string.sign_up_email_invalid)
                    SignUpEmailError.ALREADY_EXISTS -> stringResource(Res.string.sign_up_email_already_exists)
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

        Spacer(modifier = Modifier.height(space2))

        AppPasswordTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(Res.string.sign_up_password_label),
            placeholder = stringResource(Res.string.sign_up_password_placeholder),
            isError = state.passwordError != null,
            supportingText = state.passwordError?.let { error ->
                when (error) {
                    SignUpPasswordError.EMPTY -> stringResource(Res.string.sign_up_password_empty)
                    SignUpPasswordError.TOO_SHORT -> stringResource(Res.string.sign_up_password_short)
                    SignUpPasswordError.WEAK -> stringResource(Res.string.sign_up_password_weak)
                }
            },
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
        )

        Spacer(modifier = Modifier.height(space2))

        AppPasswordTextField(
            value = state.confirmPassword,
            onValueChange = onConfirmPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(Res.string.sign_up_confirm_password_label),
            placeholder = stringResource(Res.string.sign_up_confirm_password_placeholder),
            isError = state.confirmPasswordError != null,
            supportingText = state.confirmPasswordError?.let { error ->
                when (error) {
                    SignUpConfirmPasswordError.EMPTY -> stringResource(Res.string.sign_up_confirm_password_empty)
                    SignUpConfirmPasswordError.MISMATCH ->
                        stringResource(Res.string.sign_up_confirm_password_mismatch)
                }
            },
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    onSignUp()
                }
            )
        )

        state.error?.let { error ->
            TextLabelLargeError(text = error.text(), modifier = Modifier.fillMaxWidth())
            Spacer4()
        }

        Spacer(modifier = Modifier.height(space6))

        ContainedButton(
                text = stringResource(Res.string.sign_up_button),
                onClick = {
                    focusManager.clearFocus()
                    onSignUp()
                },
            modifier = Modifier.fillMaxWidth(),
            loading = state.isLoading
        )

        Spacer4()

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextBodyMediumNeutral80(stringResource(Res.string.sign_up_has_account))
            AppTextButtonPrimary(text = stringResource(Res.string.sign_up_sign_in), onClick = onToSignIn)
        }

        AppTextButtonPrimary(text = stringResource(Res.string.sign_up_privacy), onClick = onPrivacy)

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun SignUpNavEvents(
    router: NavRouter<Route>,
    viewModel: SignUpViewModel = lifecycleAwareViewModel(),
) {
    CollectNavEvents(navEventFlow = viewModel.navEvent) { event ->

        when (event) {
            is SignUpNavEvent.ToHome -> router.navigateTo(
                Route.HomeSection.Home,
                popUpTo = Route.SignUp::class,
                inclusive = true
            )

            is SignUpNavEvent.ToSignIn -> router.onBack()

            is SignUpNavEvent.OpenPrivacy -> router.openLink(event.url)
        }
    }
}
