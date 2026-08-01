package com.mk.kmpshowcase.presentation.screen.login

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.mk.kmpshowcase.presentation.foundation.AppTheme

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LoginScreenPreview(
    @PreviewParameter(LoginScreenPreviewParams::class) state: LoginUiState
) {
    AppTheme {
        LoginScreen(state = state)
    }
}

internal class LoginScreenPreviewParams : PreviewParameterProvider<LoginUiState> {
    override val values = sequenceOf(
        LoginUiState(email = "test@example.com", password = "Test123!"),
        LoginUiState(biometricsAvailable = true),
        LoginUiState(emailError = EmailError.INVALID_FORMAT, passwordError = PasswordError.TOO_SHORT)
    )
}
