package com.mk.kmpshowcase.presentation.screen.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mk.kmpshowcase.presentation.base.CollectNavEvents
import com.mk.kmpshowcase.presentation.base.NavRouter
import com.mk.kmpshowcase.presentation.base.Route
import com.mk.kmpshowcase.presentation.base.lifecycleAwareViewModel
import com.mk.kmpshowcase.presentation.component.AppPasswordTextField
import com.mk.kmpshowcase.presentation.component.AppTextField
import com.mk.kmpshowcase.presentation.component.buttons.ContainedButton
import com.mk.kmpshowcase.presentation.component.image.AppIconNeutral80
import com.mk.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer4
import com.mk.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer8
import com.mk.kmpshowcase.presentation.component.text.bodyMedium.TextBodyMediumNeutral80
import com.mk.kmpshowcase.presentation.component.text.labelLarge.TextLabelLargePrimary
import com.mk.kmpshowcase.presentation.component.text.titleLarge.TextTitleLargePrimary
import com.mk.kmpshowcase.presentation.foundation.space12
import com.mk.kmpshowcase.presentation.foundation.space2
import com.mk.kmpshowcase.presentation.foundation.space4
import com.mk.kmpshowcase.presentation.foundation.space6
import com.mk.kmpshowcase.shared.generated.resources.Res
import com.mk.kmpshowcase.shared.generated.resources.register_button
import com.mk.kmpshowcase.shared.generated.resources.register_confirm_password_empty
import com.mk.kmpshowcase.shared.generated.resources.register_confirm_password_label
import com.mk.kmpshowcase.shared.generated.resources.register_confirm_password_mismatch
import com.mk.kmpshowcase.shared.generated.resources.register_confirm_password_placeholder
import com.mk.kmpshowcase.shared.generated.resources.register_email_already_exists
import com.mk.kmpshowcase.shared.generated.resources.register_email_empty
import com.mk.kmpshowcase.shared.generated.resources.register_email_invalid
import com.mk.kmpshowcase.shared.generated.resources.register_email_label
import com.mk.kmpshowcase.shared.generated.resources.register_email_placeholder
import com.mk.kmpshowcase.shared.generated.resources.register_has_account
import com.mk.kmpshowcase.shared.generated.resources.register_login
import com.mk.kmpshowcase.shared.generated.resources.register_name_empty
import com.mk.kmpshowcase.shared.generated.resources.register_name_label
import com.mk.kmpshowcase.shared.generated.resources.register_name_placeholder
import com.mk.kmpshowcase.shared.generated.resources.register_name_short
import com.mk.kmpshowcase.shared.generated.resources.register_password_empty
import com.mk.kmpshowcase.shared.generated.resources.register_password_label
import com.mk.kmpshowcase.shared.generated.resources.register_password_placeholder
import com.mk.kmpshowcase.shared.generated.resources.register_password_short
import com.mk.kmpshowcase.shared.generated.resources.register_password_weak
import com.mk.kmpshowcase.shared.generated.resources.register_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun RegisterScreen(
    router: NavRouter<Route>,
    viewModel: RegisterViewModel = lifecycleAwareViewModel(),
) {
    RegisterNavEvents(router)
    val state by viewModel.state.collectAsStateWithLifecycle()
    RegisterScreen(
        state = state,
        onNameChange = viewModel::onNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onRegister = viewModel::register,
        onToLogin = viewModel::toLogin,
    )
}

@Composable
fun RegisterScreen(
    state: RegisterUiState,
    onNameChange: (String) -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onPasswordChange: (String) -> Unit = {},
    onConfirmPasswordChange: (String) -> Unit = {},
    onRegister: () -> Unit = {},
    onToLogin: () -> Unit = {},
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
        TextTitleLargePrimary(stringResource(Res.string.register_title))
        Spacer8()

        AppTextField(
            value = state.name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(Res.string.register_name_label),
            placeholder = stringResource(Res.string.register_name_placeholder),
            isError = state.nameError != null,
            supportingText = state.nameError?.let { error ->
                when (error) {
                    RegisterNameError.EMPTY -> stringResource(Res.string.register_name_empty)
                    RegisterNameError.TOO_SHORT -> stringResource(Res.string.register_name_short)
                }
            },
            leadingIcon = {
                AppIconNeutral80(imageVector = Icons.Filled.Person, contentDescription = null)
            },
            showClearButton = false,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
        )

        Spacer(modifier = Modifier.height(space2))

        AppTextField(
            value = state.email,
            onValueChange = onEmailChange,
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(Res.string.register_email_label),
            placeholder = stringResource(Res.string.register_email_placeholder),
            isError = state.emailError != null,
            supportingText = state.emailError?.let { error ->
                when (error) {
                    RegisterEmailError.EMPTY -> stringResource(Res.string.register_email_empty)
                    RegisterEmailError.INVALID_FORMAT -> stringResource(Res.string.register_email_invalid)
                    RegisterEmailError.ALREADY_EXISTS -> stringResource(Res.string.register_email_already_exists)
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
            label = stringResource(Res.string.register_password_label),
            placeholder = stringResource(Res.string.register_password_placeholder),
            isError = state.passwordError != null,
            supportingText = state.passwordError?.let { error ->
                when (error) {
                    RegisterPasswordError.EMPTY -> stringResource(Res.string.register_password_empty)
                    RegisterPasswordError.TOO_SHORT -> stringResource(Res.string.register_password_short)
                    RegisterPasswordError.WEAK -> stringResource(Res.string.register_password_weak)
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
            label = stringResource(Res.string.register_confirm_password_label),
            placeholder = stringResource(Res.string.register_confirm_password_placeholder),
            isError = state.confirmPasswordError != null,
            supportingText = state.confirmPasswordError?.let { error ->
                when (error) {
                    RegisterConfirmPasswordError.EMPTY -> stringResource(Res.string.register_confirm_password_empty)
                    RegisterConfirmPasswordError.MISMATCH ->
                        stringResource(Res.string.register_confirm_password_mismatch)
                }
            },
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    onRegister()
                }
            )
        )

        Spacer(modifier = Modifier.height(space6))

        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(space12),
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            ContainedButton(
                text = stringResource(Res.string.register_button),
                onClick = {
                    focusManager.clearFocus()
                    onRegister()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer4()

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextBodyMediumNeutral80(stringResource(Res.string.register_has_account))
            TextButton(onClick = onToLogin) {
                TextLabelLargePrimary(stringResource(Res.string.register_login))
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun RegisterNavEvents(
    router: NavRouter<Route>,
    viewModel: RegisterViewModel = lifecycleAwareViewModel(),
) {
    CollectNavEvents(navEventFlow = viewModel.navEvent) { event ->

        when (event) {
            is RegisterNavEvent.ToHome -> router.navigateTo(
                Route.HomeSection.Home,
                popUpTo = Route.Register::class,
                inclusive = true
            )

            is RegisterNavEvent.ToLogin -> router.onBack()
        }
    }
}
