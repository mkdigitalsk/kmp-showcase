package com.mk.kmpshowcase.presentation.screen.home

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.mk.kmpshowcase.presentation.foundation.AppTheme

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomeScreenPreview(
    @PreviewParameter(HomeScreenPreviewParams::class) state: HomeUiState
) {
    AppTheme {
        HomeScreen(state = state)
    }
}

internal class HomeScreenPreviewParams : PreviewParameterProvider<HomeUiState> {
    override val values = sequenceOf(HomeUiState())
}
