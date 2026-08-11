package sk.mkdigital.kmpshowcase.presentation.screen.networking

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import sk.mkdigital.kmpshowcase.domain.model.RemoteNote
import sk.mkdigital.kmpshowcase.presentation.base.AppError
import sk.mkdigital.kmpshowcase.presentation.foundation.AppTheme

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NetworkingScreenPreview(
    @PreviewParameter(NetworkingScreenPreviewParams::class) state: NetworkingUiState
) {
    AppTheme {
        NetworkingScreen(state = state)
    }
}

internal class NetworkingScreenPreviewParams : PreviewParameterProvider<NetworkingUiState> {
    override val values = sequenceOf(
        NetworkingUiState(isLoading = true),
        NetworkingUiState(error = AppError.UNAUTHORIZED),
        NetworkingUiState(
            notes = listOf(
                RemoteNote(
                    id = 1L,
                    title = "Buy milk",
                    content = "two litres",
                    createdAt = 0,
                    updatedAt = 0,
                    etag = "\"0\"",
                ).toUiModel()
            )
        )
    )
}
