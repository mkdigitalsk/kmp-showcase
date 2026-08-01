package com.mk.kmpshowcase.presentation.screen.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mk.kmpshowcase.domain.repository.PushPermissionStatus
import com.mk.kmpshowcase.presentation.base.CollectNavEvents
import com.mk.kmpshowcase.presentation.base.NavRouter
import com.mk.kmpshowcase.presentation.base.Route
import com.mk.kmpshowcase.presentation.base.lifecycleAwareViewModel
import com.mk.kmpshowcase.presentation.component.buttons.OutlinedButton
import com.mk.kmpshowcase.presentation.component.permission.rememberNotificationPermissionRequester
import com.mk.kmpshowcase.presentation.component.cards.AppElevatedCard
import com.mk.kmpshowcase.presentation.component.spacers.ColumnSpacer.Spacer2
import com.mk.kmpshowcase.presentation.component.text.bodyLarge.TextBodyLargeNeutral80
import com.mk.kmpshowcase.presentation.component.text.bodyMedium.TextBodyMediumNeutral80
import com.mk.kmpshowcase.presentation.component.text.headlineMedium.TextHeadlineMediumPrimary
import com.mk.kmpshowcase.presentation.foundation.floatingNavBarSpace
import com.mk.kmpshowcase.presentation.foundation.space4
import com.mk.kmpshowcase.shared.generated.resources.Res
import com.mk.kmpshowcase.shared.generated.resources.notifications_cancel_all
import com.mk.kmpshowcase.shared.generated.resources.notifications_cancel_title
import com.mk.kmpshowcase.shared.generated.resources.notifications_open_settings
import com.mk.kmpshowcase.shared.generated.resources.notifications_last_received
import com.mk.kmpshowcase.shared.generated.resources.notifications_last_sent
import com.mk.kmpshowcase.shared.generated.resources.notifications_log_token
import com.mk.kmpshowcase.shared.generated.resources.notifications_no_token
import com.mk.kmpshowcase.shared.generated.resources.notifications_permission_denied
import com.mk.kmpshowcase.shared.generated.resources.notifications_permission_granted
import com.mk.kmpshowcase.shared.generated.resources.notifications_permission_title
import com.mk.kmpshowcase.shared.generated.resources.notifications_permission_unknown
import com.mk.kmpshowcase.shared.generated.resources.notifications_refresh_token
import com.mk.kmpshowcase.shared.generated.resources.notifications_request_permission
import com.mk.kmpshowcase.shared.generated.resources.notifications_promo_message
import com.mk.kmpshowcase.shared.generated.resources.notifications_promo_title
import com.mk.kmpshowcase.shared.generated.resources.notifications_reminder_message
import com.mk.kmpshowcase.shared.generated.resources.notifications_reminder_title
import com.mk.kmpshowcase.shared.generated.resources.notifications_send_promo
import com.mk.kmpshowcase.shared.generated.resources.notifications_send_reminder
import com.mk.kmpshowcase.shared.generated.resources.notifications_send_title
import com.mk.kmpshowcase.shared.generated.resources.notifications_subtitle
import com.mk.kmpshowcase.shared.generated.resources.notifications_title
import com.mk.kmpshowcase.shared.generated.resources.notifications_token
import com.mk.kmpshowcase.shared.generated.resources.notifications_token_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun NotificationsScreen(router: NavRouter<Route>, viewModel: NotificationsViewModel = lifecycleAwareViewModel()) {
    NotificationsNavEvents(router)

    val state by viewModel.state.collectAsStateWithLifecycle()
    val permissionRequester = rememberNotificationPermissionRequester { status ->
        viewModel.updatePermissionStatus(status)
    }

    NotificationsScreen(
        state = state,
        onRequestPermission = permissionRequester.request,
        onRefreshToken = viewModel::refreshToken,
        onLogToken = viewModel::logToken,
        onSendReminderNotification = viewModel::sendReminderNotification,
        onSendPromoNotification = viewModel::sendPromoNotification,
        onOpenNotificationSettings = viewModel::openNotificationSettings,
        onCancelAllNotifications = viewModel::cancelAllNotifications,
    )
}

@Composable
fun NotificationsScreen(
    state: NotificationsUiState,
    onRequestPermission: () -> Unit = {},
    onRefreshToken: () -> Unit = {},
    onLogToken: () -> Unit = {},
    onSendReminderNotification: (String, String) -> Unit = { _, _ -> },
    onSendPromoNotification: (String, String) -> Unit = { _, _ -> },
    onOpenNotificationSettings: () -> Unit = {},
    onCancelAllNotifications: () -> Unit = {},
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = space4,
            end = space4,
            top = space4,
            bottom = floatingNavBarSpace
        ),
        verticalArrangement = Arrangement.spacedBy(space4)
    ) {
        item {
            Column {
                TextHeadlineMediumPrimary(stringResource(Res.string.notifications_title))
                TextBodyMediumNeutral80(stringResource(Res.string.notifications_subtitle))
            }
        }

        item {
            val permissionText = when (state.permissionStatus) {
                PushPermissionStatus.GRANTED -> stringResource(Res.string.notifications_permission_granted)
                PushPermissionStatus.DENIED -> stringResource(Res.string.notifications_permission_denied)
                PushPermissionStatus.NOT_DETERMINED -> stringResource(Res.string.notifications_permission_unknown)
            }

            NotificationCard(
                icon = Icons.Outlined.Security,
                title = stringResource(Res.string.notifications_permission_title)
            ) {
                TextBodyMediumNeutral80(permissionText)
                Spacer2()
                if (state.permissionStatus != PushPermissionStatus.GRANTED) {
                    CardButton(
                        text = stringResource(Res.string.notifications_request_permission),
                        onClick = onRequestPermission,
                        enabled = !state.permissionLoading
                    )
                }
            }
        }

        item {
            val tokenText = state.pushToken?.let {
                stringResource(Res.string.notifications_token, it.take(30) + "...")
            } ?: stringResource(Res.string.notifications_no_token)

            NotificationCard(
                icon = Icons.Outlined.Key,
                title = stringResource(Res.string.notifications_token_title)
            ) {
                TextBodyMediumNeutral80(tokenText)
                Spacer2()
                Row(horizontalArrangement = Arrangement.spacedBy(space4)) {
                    OutlinedButton(
                        text = stringResource(Res.string.notifications_refresh_token),
                        onClick = onRefreshToken,
                        modifier = Modifier.weight(1f),
                        enabled = !state.tokenRefreshing
                    )
                    OutlinedButton(
                        text = stringResource(Res.string.notifications_log_token),
                        onClick = onLogToken,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            NotificationCard(
                icon = Icons.Outlined.NotificationsActive,
                title = stringResource(Res.string.notifications_send_title)
            ) {
                state.lastSentNotification?.let {
                    TextBodyMediumNeutral80(stringResource(Res.string.notifications_last_sent, it))
                    Spacer2()
                }
                val reminderTitle = stringResource(Res.string.notifications_reminder_title)
                val reminderMessage = stringResource(Res.string.notifications_reminder_message)
                val promoTitle = stringResource(Res.string.notifications_promo_title)
                val promoMessage = stringResource(Res.string.notifications_promo_message)

                Row(horizontalArrangement = Arrangement.spacedBy(space4)) {
                    OutlinedButton(
                        text = stringResource(Res.string.notifications_send_reminder),
                        onClick = { onSendReminderNotification(reminderTitle, reminderMessage) },
                        modifier = Modifier.weight(1f),
                        enabled = state.permissionStatus == PushPermissionStatus.GRANTED
                    )
                    OutlinedButton(
                        text = stringResource(Res.string.notifications_send_promo),
                        onClick = { onSendPromoNotification(promoTitle, promoMessage) },
                        modifier = Modifier.weight(1f),
                        enabled = state.permissionStatus == PushPermissionStatus.GRANTED
                    )
                }
            }
        }

        item {
            NotificationCard(
                icon = Icons.Outlined.Notifications,
                title = stringResource(Res.string.notifications_cancel_title)
            ) {
                state.lastReceivedNotification?.let {
                    TextBodyMediumNeutral80(stringResource(Res.string.notifications_last_received, it))
                    Spacer2()
                }
                Row(horizontalArrangement = Arrangement.spacedBy(space4)) {
                    OutlinedButton(
                        text = stringResource(Res.string.notifications_open_settings),
                        onClick = onOpenNotificationSettings,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedButton(
                        text = stringResource(Res.string.notifications_cancel_all),
                        onClick = onCancelAllNotifications,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(
    icon: ImageVector,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    AppElevatedCard(modifier = Modifier.fillMaxWidth().padding(space4)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null)
            Spacer(modifier = Modifier.width(space4))
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.End,
                content = {
                    TextBodyLargeNeutral80(title)
                    Spacer2()
                    content()
                }
            )
        }
    }
}

@Composable
private fun CardButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    OutlinedButton(
        text = text,
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled
    )
}

@Composable
private fun NotificationsNavEvents(
    router: NavRouter<Route>,
    viewModel: NotificationsViewModel = lifecycleAwareViewModel(),
) {
    CollectNavEvents(navEventFlow = viewModel.navEvent) { event ->
        when (event) {
            is NotificationsNavEvent.OpenSettings -> {
                router.openNotificationSettings()
            }
        }
    }
}
