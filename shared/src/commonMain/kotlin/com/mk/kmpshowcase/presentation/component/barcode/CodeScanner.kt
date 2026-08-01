package com.mk.kmpshowcase.presentation.component.barcode

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun CodeScanner(
    onScanned: (String) -> Unit,
    onError: (String) -> Unit = {},
    modifier: Modifier = Modifier
)
