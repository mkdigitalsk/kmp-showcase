package mk.digital.kmpshowcase.presentation.screen.notifications

import mk.digital.kmpshowcase.domain.model.Notification
import mk.digital.kmpshowcase.domain.model.NotificationChannel
import mk.digital.kmpshowcase.domain.repository.LocalNotificationService
import mk.digital.kmpshowcase.domain.repository.PushNotificationService
import mk.digital.kmpshowcase.domain.repository.PushPermissionStatus
import mk.digital.kmpshowcase.presentation.base.BaseViewModel
import mk.digital.kmpshowcase.presentation.base.NavEvent
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class NotificationsViewModel(
    private val pushNotificationService: PushNotificationService,
    private val localNotificationService: LocalNotificationService,
) : BaseViewModel<NotificationsUiState>(NotificationsUiState()) {

    override fun loadInitialData() {
        newState { it.copy(permissionStatus = pushNotificationService.getPermissionStatus()) }

        observe(
            flow = pushNotificationService.token,
            onEach = { token -> newState { it.copy(pushToken = token) } }
        )

        observe(
            flow = pushNotificationService.notifications,
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
            action = { pushNotificationService.refreshToken() },
            onLoading = { newState { it.copy(tokenRefreshing = true) } },
            onSuccess = { newState { it.copy(tokenRefreshing = false) } },
            onError = { newState { it.copy(tokenRefreshing = false) } }
        )
    }

    fun logToken() {
        pushNotificationService.logToken()
    }

    @OptIn(ExperimentalUuidApi::class)
    fun sendReminderNotification(title: String, message: String) {
        val notification = Notification(
            id = Uuid.random().toString(),
            title = title,
            message = message,
            channel = NotificationChannel.REMINDERS
        )
        localNotificationService.showNotification(notification)
        newState { it.copy(lastSentNotification = notification.title) }
    }

    @OptIn(ExperimentalUuidApi::class)
    fun sendPromoNotification(title: String, message: String) {
        val notification = Notification(
            id = Uuid.random().toString(),
            title = title,
            message = message,
            channel = NotificationChannel.PROMOTIONS
        )
        localNotificationService.showNotification(notification)
        newState { it.copy(lastSentNotification = notification.title) }
    }

    fun cancelAllNotifications() {
        localNotificationService.cancelAllNotifications()
        newState { it.copy(lastSentNotification = null) }
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
