package sk.mkdigital.kmpshowcase.presentation.screen.signUp

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import sk.mkdigital.kmpshowcase.presentation.foundation.AppTheme

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SignUpScreenPreview(
    @PreviewParameter(SignUpScreenPreviewParams::class) state: SignUpUiState
) {
    AppTheme {
        SignUpScreen(state = state)
    }
}

internal class SignUpScreenPreviewParams : PreviewParameterProvider<SignUpUiState> {
    override val values = sequenceOf(
        SignUpUiState(),
        SignUpUiState(
            name = "John Doe",
            email = "john@example.com",
            password = "password123",
            confirmPassword = "password123"
        ),
        SignUpUiState(
            email = "invalid",
            emailError = SignUpEmailError.INVALID_FORMAT,
            passwordError = SignUpPasswordError.TOO_SHORT
        ),
        SignUpUiState(isLoading = true),
    )
}
