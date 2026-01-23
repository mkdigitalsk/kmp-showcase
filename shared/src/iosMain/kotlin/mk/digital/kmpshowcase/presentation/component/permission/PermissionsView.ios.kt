package mk.digital.kmpshowcase.presentation.component.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusDenied
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLAuthorizationStatusDenied
import platform.CoreLocation.kCLAuthorizationStatusNotDetermined
import platform.CoreLocation.kCLAuthorizationStatusRestricted
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSThread
import platform.Foundation.NSURL
import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHAuthorizationStatusDenied
import platform.Photos.PHAuthorizationStatusNotDetermined
import platform.Photos.PHPhotoLibrary
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationDidBecomeActiveNotification
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNAuthorizationStatusDenied
import platform.UserNotifications.UNAuthorizationStatusEphemeral
import platform.UserNotifications.UNAuthorizationStatusNotDetermined
import platform.UserNotifications.UNAuthorizationStatusProvisional
import platform.UserNotifications.UNUserNotificationCenter
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@Composable
actual fun PermissionView(
    permission: PermissionType,
    onDeniedDialogDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val isGranted = remember { mutableStateOf(permissionGrantedNow(permission)) }

    LaunchedEffect(permission) {
        if (!isGranted.value) {
            checkPermission(permission) { granted -> isGranted.value = granted }
        }
    }

    DisposableEffect(Unit) {
        val obs = NSNotificationCenter.defaultCenter.addObserverForName(
            name = UIApplicationDidBecomeActiveNotification,
            `object` = null,
            queue = null
        ) {
            isGranted.value = permissionGrantedNow(permission)
        }
        onDispose { NSNotificationCenter.defaultCenter.removeObserver(obs) }
    }

    if (isGranted.value) {
        content()
    } else {
        PermissionDenyUi(
            message = permission.deniedMessage,
            onConfirm = ::launchSettings,
        )
    }
}

private fun permissionGrantedNow(permission: PermissionType) = when (permission) {
    PermissionType.CAMERA -> cameraAuthorized
    PermissionType.GALLERY -> galleryAuthorized
    PermissionType.LOCATION -> locationAuthorized
    PermissionType.NOTIFICATION -> notificationAuthorized
}

private fun checkPermission(permission: PermissionType, onResult: (Boolean) -> Unit) {
    when (permission) {
        PermissionType.CAMERA -> checkCameraPermission(onResult)
        PermissionType.GALLERY -> checkGalleryPermission(onResult)
        PermissionType.LOCATION -> checkLocationPermission(onResult)
        PermissionType.NOTIFICATION -> checkNotificationPermission(onResult)
    }
}

private fun checkCameraPermission(onResult: (Boolean) -> Unit) {
    val status = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
    when (status) {
        AVAuthorizationStatusAuthorized -> onResult(true)
        AVAuthorizationStatusDenied -> onResult(false)
        AVAuthorizationStatusNotDetermined -> requestCameraPermission(onResult)
        else -> onResult(false)
    }
}

// ===== Location  =====
private var locationManagerRef: CLLocationManager? = null
private var locationDelegateRef: NSObject? = null

private fun checkLocationPermission(onResult: (Boolean) -> Unit) {
    val status = CLLocationManager.authorizationStatus()
    when (status) {
        kCLAuthorizationStatusAuthorizedAlways,
        kCLAuthorizationStatusAuthorizedWhenInUse -> onResult(true)

        kCLAuthorizationStatusDenied,
        kCLAuthorizationStatusRestricted -> onResult(false)

        else -> requestLocationPermission(onResult)
    }
}

private fun requestLocationPermission(onResult: (Boolean) -> Unit) {
    val manager = CLLocationManager().also { locationManagerRef = it }

    val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
        override fun locationManagerDidChangeAuthorization(manager: CLLocationManager) {
            handleAuthChange(CLLocationManager.authorizationStatus(), onResult)
        }

        override fun locationManager(
            manager: CLLocationManager,
            didChangeAuthorizationStatus: CLAuthorizationStatus
        ) {
            handleAuthChange(didChangeAuthorizationStatus, onResult)
        }
    }

    locationDelegateRef = delegate
    manager.delegate = delegate
    manager.requestWhenInUseAuthorization()
}

private fun handleAuthChange(status: CLAuthorizationStatus, onResult: (Boolean) -> Unit) {
    when (status) {
        kCLAuthorizationStatusAuthorizedAlways,
        kCLAuthorizationStatusAuthorizedWhenInUse -> {
            cleanupLocationDelegate()
            runOnMain { onResult(true) }
        }
        kCLAuthorizationStatusDenied,
        kCLAuthorizationStatusRestricted -> {
            cleanupLocationDelegate()
            runOnMain { onResult(false) }
        }
        kCLAuthorizationStatusNotDetermined -> Unit
        else -> Unit
    }
}

private fun runOnMain(block: () -> Unit) {
    if (NSThread.isMainThread) block() else dispatch_async(dispatch_get_main_queue(), block)
}

private fun cleanupLocationDelegate() {
    locationManagerRef?.delegate = null
    locationManagerRef = null
    locationDelegateRef = null
}

private fun requestCameraPermission(onResult: (Boolean) -> Unit) {
    AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo, onResult)
}

private fun requestGalleryPermission(onResult: (Boolean) -> Unit) {
    PHPhotoLibrary.requestAuthorization {
        onResult(it == PHAuthorizationStatusAuthorized)
    }
}

private fun checkGalleryPermission(onResult: (Boolean) -> Unit) {
    val status = PHPhotoLibrary.authorizationStatus()
    return when (status) {
        PHAuthorizationStatusAuthorized -> onResult(true)
        PHAuthorizationStatusDenied -> onResult(false)
        PHAuthorizationStatusNotDetermined -> requestGalleryPermission(onResult)
        else -> onResult(false)
    }
}

private val cameraAuthorized: Boolean
    get() = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo) ==
            AVAuthorizationStatusAuthorized

private val galleryAuthorized: Boolean
    get() = PHPhotoLibrary.authorizationStatus() == PHAuthorizationStatusAuthorized

private val locationAuthorized: Boolean
    get() {
        val st = CLLocationManager.authorizationStatus()
        return st == kCLAuthorizationStatusAuthorizedAlways ||
                st == kCLAuthorizationStatusAuthorizedWhenInUse
    }

// ===== Notification =====
private var notificationAuthorizedCache: Boolean? = null

private val notificationAuthorized: Boolean
    get() = notificationAuthorizedCache ?: false

private fun checkNotificationPermission(onResult: (Boolean) -> Unit) {
    UNUserNotificationCenter.currentNotificationCenter().getNotificationSettingsWithCompletionHandler { settings ->
        if (settings == null) {
            runOnMain { onResult(false) }
            return@getNotificationSettingsWithCompletionHandler
        }
        when (settings.authorizationStatus) {
            UNAuthorizationStatusAuthorized,
            UNAuthorizationStatusProvisional,
            UNAuthorizationStatusEphemeral -> {
                notificationAuthorizedCache = true
                runOnMain { onResult(true) }
            }
            UNAuthorizationStatusDenied -> {
                notificationAuthorizedCache = false
                runOnMain { onResult(false) }
            }
            UNAuthorizationStatusNotDetermined -> requestNotificationPermission(onResult)
            else -> {
                notificationAuthorizedCache = false
                runOnMain { onResult(false) }
            }
        }
    }
}

private fun requestNotificationPermission(onResult: (Boolean) -> Unit) {
    UNUserNotificationCenter.currentNotificationCenter().requestAuthorizationWithOptions(
        options = UNAuthorizationOptionAlert or UNAuthorizationOptionBadge or UNAuthorizationOptionSound
    ) { granted, _ ->
        notificationAuthorizedCache = granted
        runOnMain { onResult(granted) }
    }
}

private fun launchSettings() {
    NSURL.URLWithString(UIApplicationOpenSettingsURLString)?.let {
        UIApplication.sharedApplication.openURL(
            url = it,
            options = emptyMap<Any?, Any?>(),
            completionHandler = null
        )
    }
}
