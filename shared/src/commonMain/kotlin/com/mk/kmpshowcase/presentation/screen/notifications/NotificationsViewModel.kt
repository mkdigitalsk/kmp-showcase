package com.mk.kmpshowcase.presentation.screen.notifications

import com.mk.kmpshowcase.domain.model.Notification
import com.mk.kmpshowcase.domain.model.NotificationChannel
import com.mk.kmpshowcase.domain.repository.PushPermissionStatus
import com.mk.kmpshowcase.domain.useCase.base.invoke
import com.mk.kmpshowcase.domain.useCase.notifications.CancelAllNotificationsUseCase
import com.mk.kmpshowcase.domain.useCase.notifications.GetPushPermissionStatusUseCase
import com.mk.kmpshowcase.domain.useCase.notifications.LogPushTokenUseCase
import com.mk.kmpshowcase.domain.useCase.notifications.ObservePushNotificationsUseCase
import com.mk.kmpshowcase.domain.useCase.notifications.ObservePushTokenUseCase
import com.mk.kmpshowcase.domain.useCase.notifications.RefreshPushTokenUseCase
import com.mk.kmpshowcase.domain.useCase.notifications.ShowLocalNotificationUseCase
import com.mk.kmpshowcase.presentation.base.BaseViewModel
import com.mk.kmpshowcase.presentation.base.NavEvent
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Suppress("LongParameterList")
class NotificationsViewModel(
    private val getPushPermissionStatusUseCase: GetPushPermissionStatusUseCase,
    private val observePushTokenUseCase: ObservePushTokenUseCase,
    private val observePushNotificationsUseCase: ObservePushNotificationsUseCase,
    private val refreshPushTokenUseCase: RefreshPushTokenUseCase,
    private val logPushTokenUseCase: LogPushTokenUseCase,
    private val showLocalNotificationUseCase: ShowLocalNotificationUseCase,
    private val cancelAllNotificationsUseCase: CancelAllNotificationsUseCase,
) : BaseViewModel<NotificationsUiState>(NotificationsUiState()) {

    override fun loadInitialData() {
        execute(
            action = { getPushPermissionStatusUseCase() },
            onSuccess = { status -> newState { it.copy(permissionStatus = status) } }
        )

        observe(
            flow = observePushTokenUseCase(),
            onEach = { token -> newState { it.copy(pushToken = token) } }
        )

        observe(
            flow = observePushNotificationsUseCase(),
            onEach = { notification ->
                newState { it.copy(lastReceivedNotification = "${notification.title}: ${notification.message}") }
            }
        )
    }

    fun updatePermissionStatus(status: PushPermissionStatus) {
        newState { it.copy(permissionStatus = status) }
    }

    fun refreshToken() {
        execute(
            action = { refreshPushTokenUseCase() },
            onLoading = { newState { it.copy(tokenRefreshing = true) } },
            onSuccess = { newState { it.copy(tokenRefreshing = false) } },
            onError = { newState { it.copy(tokenRefreshing = false) } }
        )
    }

    fun logToken() {
        execute(action = { logPushTokenUseCase() })
    }

    @OptIn(ExperimentalUuidApi::class)
    fun sendReminderNotification(title: String, message: String) {
        val notification = Notification(
            id = Uuid.random().toString(),
            title = title,
            message = message,
            channel = NotificationChannel.REMINDERS
        )
        execute(
            action = { showLocalNotificationUseCase(notification) },
            onSuccess = { newState { it.copy(lastSentNotification = notification.title) } }
        )
    }

    @OptIn(ExperimentalUuidApi::class)
    fun sendPromoNotification(title: String, message: String) {
        val notification = Notification(
            id = Uuid.random().toString(),
            title = title,
            message = message,
            channel = NotificationChannel.PROMOTIONS
        )
        execute(
            action = { showLocalNotificationUseCase(notification) },
            onSuccess = { newState { it.copy(lastSentNotification = notification.title) } }
        )
    }

    fun cancelAllNotifications() {
        execute(
            action = { cancelAllNotificationsUseCase() },
            onSuccess = { newState { it.copy(lastSentNotification = null) } }
        )
    }

    fun openNotificationSettings() {
        navigate(NotificationsNavEvent.OpenSettings)
    }
}

sealed interface NotificationsNavEvent : NavEvent {
    data object OpenSettings : NotificationsNavEvent
}

data class NotificationsUiState(
    val permissionStatus: PushPermissionStatus = PushPermissionStatus.NOT_DETERMINED,
    val permissionLoading: Boolean = false,
    val pushToken: String? = null,
    val tokenRefreshing: Boolean = false,
    val lastSentNotification: String? = null,
    val lastReceivedNotification: String? = null,
)
