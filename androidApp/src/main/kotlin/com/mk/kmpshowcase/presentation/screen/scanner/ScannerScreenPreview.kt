package com.mk.kmpshowcase.presentation.screen.scanner

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.mk.kmpshowcase.presentation.component.barcode.CodeFormat
import com.mk.kmpshowcase.presentation.foundation.AppTheme

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun ScannerScreenPreview(
    @PreviewParameter(ScannerScreenPreviewParams::class) state: ScannerUiState
) {
    AppTheme {
        ScannerScreen(state = state)
    }
}

internal class ScannerScreenPreviewParams : PreviewParameterProvider<ScannerUiState> {
    override val values = sequenceOf(
        ScannerUiState(
            selectedModeIndex = 0,
            selectedFormatIndex = 0,
            selectedFormat = CodeFormat.QR_CODE,
            inputText = ""
        ),
        ScannerUiState(
            selectedModeIndex = 0,
            selectedFormatIndex = 0,
            selectedFormat = CodeFormat.QR_CODE,
            inputText = "https://example.com"
        ),
        ScannerUiState(
            selectedModeIndex = 1,
            scannedResult = "Scanned: https://example.com"
        ),
    )
}
