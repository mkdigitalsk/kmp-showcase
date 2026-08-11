package sk.mkdigital.kmpshowcase.presentation.component.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import sk.mkdigital.kmpshowcase.domain.repository.PushPermissionStatus
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatus
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNAuthorizationStatusEphemeral
import platform.UserNotifications.UNAuthorizationStatusNotDetermined
import platform.UserNotifications.UNAuthorizationStatusProvisional
import platform.UserNotifications.UNUserNotificationCenter
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@Composable
actual fun rememberNotificationPermissionRequester(
    onResult: (PushPermissionStatus) -> Unit
): NotificationPermissionRequester {
    return remember {
        NotificationPermissionRequester {
            val center = UNUserNotificationCenter.currentNotificationCenter()
            center.getNotificationSettingsWithCompletionHandler { settings ->
                center.resolvePermission(settings?.authorizationStatus, onResult)
            }
        }
    }
}

private fun UNUserNotificationCenter.resolvePermission(
    status: UNAuthorizationStatus?,
    onResult: (PushPermissionStatus) -> Unit
) {
    when (status) {
        UNAuthorizationStatusAuthorized,
        UNAuthorizationStatusProvisional,
        UNAuthorizationStatusEphemeral -> {
            dispatchMain { onResult(PushPermissionStatus.GRANTED) }
        }
        UNAuthorizationStatusNotDetermined -> {
            requestPermission(onResult)
        }
        else -> {
            dispatchMain { onResult(PushPermissionStatus.DENIED) }
        }
    }
}

private fun UNUserNotificationCenter.requestPermission(onResult: (PushPermissionStatus) -> Unit) {
    requestAuthorizationWithOptions(
        options = UNAuthorizationOptionAlert or
                UNAuthorizationOptionBadge or
                UNAuthorizationOptionSound
    ) { granted, _ ->
        dispatchMain {
            onResult(if (granted) PushPermissionStatus.GRANTED else PushPermissionStatus.DENIED)
        }
    }
}

private fun dispatchMain(block: () -> Unit) {
    dispatch_async(dispatch_get_main_queue(), block)
}
