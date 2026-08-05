package com.mk.kmpshowcase.presentation.component.biometric

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun BiometricView(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
)
