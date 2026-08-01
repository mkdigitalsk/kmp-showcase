package com.mk.kmpshowcase.presentation.component.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.mk.kmpshowcase.domain.repository.PushPermissionStatus
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNAuthorizationStatusDenied
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
                if (settings == null) {
                    dispatchMain { onResult(PushPermissionStatus.DENIED) }
                    return@getNotificationSettingsWithCompletionHandler
                }

                when (settings.authorizationStatus) {
                    UNAuthorizationStatusAuthorized,
                    UNAuthorizationStatusProvisional,
                    UNAuthorizationStatusEphemeral -> {
                        dispatchMain { onResult(PushPermissionStatus.GRANTED) }
                    }
                    UNAuthorizationStatusDenied -> {
                        dispatchMain { onResult(PushPermissionStatus.DENIED) }
                    }
                    UNAuthorizationStatusNotDetermined -> {
                        center.requestAuthorizationWithOptions(
                            options = UNAuthorizationOptionAlert or
                                    UNAuthorizationOptionBadge or
                                    UNAuthorizationOptionSound
                        ) { granted, _ ->
                            dispatchMain {
                                onResult(if (granted) PushPermissionStatus.GRANTED else PushPermissionStatus.DENIED)
                            }
                        }
                    }
                    else -> {
                        dispatchMain { onResult(PushPermissionStatus.DENIED) }
                    }
                }
            }
        }
    }
}

private fun dispatchMain(block: () -> Unit) {
    dispatch_async(dispatch_get_main_queue(), block)
}
