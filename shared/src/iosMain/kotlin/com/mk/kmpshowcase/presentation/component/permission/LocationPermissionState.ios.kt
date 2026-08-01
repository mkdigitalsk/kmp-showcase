package com.mk.kmpshowcase.presentation.component.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.CoreLocation.kCLAuthorizationStatusRestricted
import platform.Foundation.NSNotificationCenter
import platform.UIKit.UIApplicationDidBecomeActiveNotification
import platform.darwin.NSObject

@Composable
actual fun rememberLocationPermissionState(): LocationPermissionState {
    val manager = remember { CLLocationManager() }
    val isGranted = remember { mutableStateOf(checkLocationGranted()) }
    val shouldShowRationale = remember { mutableStateOf(false) }

    val delegate = remember {
        object : NSObject(), CLLocationManagerDelegateProtocol {
            override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
                updateState(CLLocationManager.authorizationStatus(), isGranted, shouldShowRationale)
            }

            override fun locationManager(
                manager: CLLocationManager,
                didChangeAuthorizationStatus: CLAuthorizationStatus
            ) {
                updateState(didChangeAuthorizationStatus, isGranted, shouldShowRationale)
            }
        }
    }

    DisposableEffect(Unit) {
        manager.delegate = delegate

        val observer = NSNotificationCenter.defaultCenter.addObserverForName(
            name = UIApplicationDidBecomeActiveNotification,
            `object` = null,
            queue = null
        ) {
            isGranted.value = checkLocationGranted()
        }

        onDispose {
            manager.delegate = null
            NSNotificationCenter.defaultCenter.removeObserver(observer)
        }
    }

    return LocationPermissionState(
        isGranted = isGranted.value,
        shouldShowRationale = shouldShowRationale.value,
        requestPermission = { manager.requestWhenInUseAuthorization() }
    )
}

private fun checkLocationGranted(): Boolean {
    val status = CLLocationManager.authorizationStatus()
    return status == kCLAuthorizationStatusAuthorizedAlways ||
            status == kCLAuthorizationStatusAuthorizedWhenInUse
}

private fun updateState(
    status: CLAuthorizationStatus,
    isGranted: androidx.compose.runtime.MutableState<Boolean>,
    shouldShowRationale: androidx.compose.runtime.MutableState<Boolean>
) {
    when (status) {
        kCLAuthorizationStatusAuthorizedAlways,
        kCLAuthorizationStatusAuthorizedWhenInUse -> {
            isGranted.value = true
            shouldShowRationale.value = false
        }
        kCLAuthorizationStatusDenied,
        kCLAuthorizationStatusRestricted -> {
            isGranted.value = false
            shouldShowRationale.value = true
        }
        kCLAuthorizationStatusNotDetermined -> {
            isGranted.value = false
            shouldShowRationale.value = false
        }
    }
}
