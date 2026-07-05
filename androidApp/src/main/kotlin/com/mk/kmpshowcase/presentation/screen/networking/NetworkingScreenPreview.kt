package com.mk.kmpshowcase.presentation.screen.networking

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.mk.kmpshowcase.domain.model.User
import com.mk.kmpshowcase.presentation.foundation.AppTheme

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
        NetworkingUiState(error = "401"),
        NetworkingUiState(
            users = listOf(
                User(
                    email = "mir.kusnir@gmail.com",
                    id = 1L,
                    name = "Miroslav Coder"
                ).toUiModel()
            )
        )
    )
}
