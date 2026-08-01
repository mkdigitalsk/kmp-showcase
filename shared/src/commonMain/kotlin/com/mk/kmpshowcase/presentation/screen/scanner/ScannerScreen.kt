package com.mk.kmpshowcase.presentation.screen.scanner

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mk.kmpshowcase.presentation.base.lifecycleAwareViewModel
import com.mk.kmpshowcase.presentation.component.AppTextField
import com.mk.kmpshowcase.presentation.component.barcode.CodeFormat
import com.mk.kmpshowcase.presentation.component.barcode.CodeScanner
import com.mk.kmpshowcase.presentation.component.buttons.AppSegmentedButton
import com.mk.kmpshowcase.presentation.component.buttons.ContainedButton
import com.mk.kmpshowcase.presentation.component.cards.AppElevatedCard
import com.mk.kmpshowcase.presentation.component.permission.PermissionType
import com.mk.kmpshowcase.presentation.component.permission.PermissionView
import com.mk.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer2
import com.mk.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer4
import com.mk.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer8
import com.mk.kmpshowcase.presentation.component.text.bodyLarge.TextBodyLargeNeutral80
import com.mk.kmpshowcase.presentation.component.text.bodyMedium.TextBodyMediumNeutral80
import com.mk.kmpshowcase.presentation.component.text.headlineMedium.TextHeadlineMediumPrimary
import com.mk.kmpshowcase.presentation.foundation.floatingNavBarSpace
import com.mk.kmpshowcase.presentation.foundation.keyboardPadding
import com.mk.kmpshowcase.presentation.foundation.space4
import com.mk.kmpshowcase.shared.generated.resources.Res
import com.mk.kmpshowcase.shared.generated.resources.scanner_format_barcode
import com.mk.kmpshowcase.shared.generated.resources.scanner_format_qr
import com.mk.kmpshowcase.shared.generated.resources.scanner_generate_button
import com.mk.kmpshowcase.shared.generated.resources.scanner_hint
import com.mk.kmpshowcase.shared.generated.resources.scanner_input_hint
import com.mk.kmpshowcase.shared.generated.resources.scanner_input_label
import com.mk.kmpshowcase.shared.generated.resources.scanner_mode_generate
import com.mk.kmpshowcase.shared.generated.resources.scanner_mode_scan
import com.mk.kmpshowcase.shared.generated.resources.scanner_result_title
import com.mk.kmpshowcase.shared.generated.resources.scanner_scan_again
import com.mk.kmpshowcase.shared.generated.resources.scanner_scanned_result
import com.mk.kmpshowcase.shared.generated.resources.scanner_subtitle
import com.mk.kmpshowcase.shared.generated.resources.scanner_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun ScannerScreen(viewModel: ScannerViewModel = lifecycleAwareViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ScannerScreen(
        state = state,
        onModeChanged = viewModel::onModeChanged,
        onFormatChanged = viewModel::onFormatChanged,
        onTextChanged = viewModel::onTextChanged,
        onGenerateCode = viewModel::generateCode,
        onCodeScanned = viewModel::onCodeScanned,
        onClearScannedResult = viewModel::clearScannedResult,
    )
}

@Suppress("CognitiveComplexMethod")
@Composable
fun ScannerScreen(
    state: ScannerUiState,
    onModeChanged: (Int) -> Unit = {},
    onFormatChanged: (Int) -> Unit = {},
    onTextChanged: (String) -> Unit = {},
    onGenerateCode: () -> Unit = {},
    onCodeScanned: (String) -> Unit = {},
    onClearScannedResult: () -> Unit = {},
) {
    val modeOptions = listOf(
        stringResource(Res.string.scanner_mode_generate),
        stringResource(Res.string.scanner_mode_scan)
    )

    val formatOptions = listOf(
        stringResource(Res.string.scanner_format_qr),
        stringResource(Res.string.scanner_format_barcode)
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().keyboardPadding(),
        contentPadding = PaddingValues(
            start = space4,
            end = space4,
            top = space4,
            bottom = floatingNavBarSpace
        ),
        verticalArrangement = Arrangement.spacedBy(space4)
    ) {
        item {
            Column {
                TextHeadlineMediumPrimary(stringResource(Res.string.scanner_title))
                TextBodyMediumNeutral80(stringResource(Res.string.scanner_subtitle))
            }
        }

        item {
            AppSegmentedButton(
                options = modeOptions,
                selectedIndex = state.selectedModeIndex,
                onSelectionChanged = onModeChanged,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (state.selectedModeIndex == 0) {
            item {
                AppElevatedCard(modifier = Modifier.fillMaxWidth().padding(space4)) {
                    AppSegmentedButton(
                        options = formatOptions,
                        selectedIndex = state.selectedFormatIndex,
                        onSelectionChanged = onFormatChanged,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer4()
                    AppTextField(
                        value = state.inputText,
                        onValueChange = onTextChanged,
                        label = stringResource(Res.string.scanner_input_label),
                        placeholder = stringResource(Res.string.scanner_input_hint),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer4()
                    ContainedButton(
                        text = stringResource(Res.string.scanner_generate_button),
                        onClick = onGenerateCode,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            state.generatedBitmap?.let { bitmap ->
                item {
                    AppElevatedCard(modifier = Modifier.fillMaxWidth().padding(space4)) {
                        TextBodyMediumNeutral80(stringResource(Res.string.scanner_result_title))
                        Spacer2()
                        Image(
                            bitmap = bitmap,
                            contentDescription = "Generated Code",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(if (state.selectedFormat == CodeFormat.QR_CODE) 250.dp else 150.dp),
                            contentScale = ContentScale.Fit
                        )
                        Spacer2()
                        TextBodyLargeNeutral80(
                            text = state.inputText,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                        Spacer8()
                    }
                }
            }
        } else {
            item {
                AppElevatedCard(modifier = Modifier.fillMaxWidth().padding(space4)) {
                    TextBodyMediumNeutral80(stringResource(Res.string.scanner_hint))
                    Spacer4()

                    if (state.scannedResult == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            PermissionView(
                                PermissionType.CAMERA,
                                onDeniedDialogDismiss = {},
                            ) {
                                CodeScanner(
                                    onScanned = onCodeScanned,
                                    onError = { },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            TextBodyMediumNeutral80(stringResource(Res.string.scanner_scanned_result))
                            Spacer2()
                            TextBodyLargeNeutral80(state.scannedResult)
                            Spacer4()
                            ContainedButton(
                                text = stringResource(Res.string.scanner_scan_again),
                                onClick = onClearScannedResult,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}
