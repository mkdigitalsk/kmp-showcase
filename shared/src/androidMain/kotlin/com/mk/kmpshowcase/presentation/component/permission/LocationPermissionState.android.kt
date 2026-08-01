package com.mk.kmpshowcase.presentation.component.permission

import android.Manifest
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
actual fun rememberLocationPermissionState(): LocationPermissionState {
    val permissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)

    return LocationPermissionState(
        isGranted = permissionState.status.isGranted,
        shouldShowRationale = permissionState.status.shouldShowRationale,
        requestPermission = { permissionState.launchPermissionRequest() }
    )
}
