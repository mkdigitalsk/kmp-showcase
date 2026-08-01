package com.mk.kmpshowcase.presentation.screen.register

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.mk.kmpshowcase.presentation.foundation.AppTheme

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun RegisterScreenPreview(
    @PreviewParameter(RegisterScreenPreviewParams::class) state: RegisterUiState
) {
    AppTheme {
        RegisterScreen(state = state)
    }
}

internal class RegisterScreenPreviewParams : PreviewParameterProvider<RegisterUiState> {
    override val values = sequenceOf(
        RegisterUiState(),
        RegisterUiState(
            name = "John Doe",
            email = "john@example.com",
            password = "password123",
            confirmPassword = "password123"
        ),
        RegisterUiState(
            email = "invalid",
            emailError = RegisterEmailError.INVALID_FORMAT,
            passwordError = RegisterPasswordError.TOO_SHORT
        ),
        RegisterUiState(isLoading = true),
    )
}
