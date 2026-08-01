package com.mk.kmpshowcase.presentation.screen.storage

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.mk.kmpshowcase.presentation.foundation.AppTheme

@Preview(showBackground = true)
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun StorageScreenPreview(
    @PreviewParameter(StorageScreenPreviewParams::class) state: StorageUiState,
) {
    AppTheme {
        StorageScreen(state = state)
    }
}

class StorageScreenPreviewParams : PreviewParameterProvider<StorageUiState> {
    override val values = sequenceOf(
        StorageUiState(sessionCounter = 0, persistentCounter = 0),
        StorageUiState(sessionCounter = 5, persistentCounter = 10),
    )
}
