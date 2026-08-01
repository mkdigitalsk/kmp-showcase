package com.mk.kmpshowcase.presentation.component.permission

import androidx.compose.runtime.Composable
import com.mk.kmpshowcase.domain.repository.PushPermissionStatus

class NotificationPermissionRequester(
    val request: () -> Unit
)

@Composable
expect fun rememberNotificationPermissionRequester(
    onResult: (PushPermissionStatus) -> Unit
): NotificationPermissionRequester
