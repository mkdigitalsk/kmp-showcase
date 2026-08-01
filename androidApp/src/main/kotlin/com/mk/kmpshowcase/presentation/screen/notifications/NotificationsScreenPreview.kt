package com.mk.kmpshowcase.presentation.screen.notifications

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.mk.kmpshowcase.domain.repository.PushPermissionStatus
import com.mk.kmpshowcase.presentation.foundation.AppTheme

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun NotificationsScreenPreview(
    @PreviewParameter(NotificationsScreenPreviewParams::class) state: NotificationsUiState
) {
    AppTheme {
        NotificationsScreen(state = state)
    }
}

internal class NotificationsScreenPreviewParams : PreviewParameterProvider<NotificationsUiState> {
    override val values = sequenceOf(
        NotificationsUiState(permissionStatus = PushPermissionStatus.GRANTED),
        NotificationsUiState(
            pushToken = "fcm_token_example_12345",
            tokenRefreshing = true,
            lastSentNotification = "today",
            lastReceivedNotification = "yesterday"
        )
    )
}
