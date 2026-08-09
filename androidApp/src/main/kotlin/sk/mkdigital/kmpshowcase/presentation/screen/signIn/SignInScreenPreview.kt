package sk.mkdigital.kmpshowcase.presentation.screen.signIn

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import sk.mkdigital.kmpshowcase.presentation.foundation.AppTheme

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun SignInScreenPreview(
    @PreviewParameter(SignInScreenPreviewParams::class) state: SignInUiState
) {
    AppTheme {
        SignInScreen(state = state)
    }
}

internal class SignInScreenPreviewParams : PreviewParameterProvider<SignInUiState> {
    override val values = sequenceOf(
        SignInUiState(email = "test@example.com", password = "Test123!"),
        SignInUiState(biometricsAvailable = true),
        SignInUiState(emailError = EmailError.INVALID_FORMAT, passwordError = PasswordError.TOO_SHORT)
    )
}
