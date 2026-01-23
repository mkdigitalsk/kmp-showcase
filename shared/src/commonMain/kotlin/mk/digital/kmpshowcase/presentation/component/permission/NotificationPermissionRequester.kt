package mk.digital.kmpshowcase.presentation.component.permission

import androidx.compose.runtime.Composable
import mk.digital.kmpshowcase.domain.repository.PushPermissionStatus

class NotificationPermissionRequester(
    val request: () -> Unit
)

@Composable
expect fun rememberNotificationPermissionRequester(
    onResult: (PushPermissionStatus) -> Unit
): NotificationPermissionRequester
