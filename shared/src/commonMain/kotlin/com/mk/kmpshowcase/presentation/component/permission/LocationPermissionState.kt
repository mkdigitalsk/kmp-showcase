package com.mk.kmpshowcase.presentation.component.permission

import androidx.compose.runtime.Composable

data class LocationPermissionState(
    val isGranted: Boolean,
    val shouldShowRationale: Boolean,
    val requestPermission: () -> Unit,
)

@Composable
expect fun rememberLocationPermissionState(): LocationPermissionState
