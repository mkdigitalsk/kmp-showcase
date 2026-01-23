package mk.digital.kmpshowcase.data.push

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import mk.digital.kmpshowcase.domain.model.Notification
import mk.digital.kmpshowcase.domain.model.NotificationChannel
import mk.digital.kmpshowcase.domain.repository.NotificationRepository
import mk.digital.kmpshowcase.domain.repository.PushNotificationService
import mk.digital.kmpshowcase.domain.repository.PushPermissionStatus
import kotlin.time.Clock

class IOSPushNotificationService(
    private val notificationRepository: NotificationRepository
) : PushNotificationService {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _token = MutableStateFlow<String?>(null)
    override val token: StateFlow<String?> = _token.asStateFlow()

    private val _notifications = Channel<Notification>(Channel.BUFFERED)
    override val notifications: Flow<Notification> = _notifications.receiveAsFlow()

    private val _deepLinks = Channel<String>(Channel.BUFFERED)
    override val deepLinks: Flow<String> = _deepLinks.receiveAsFlow()

    override fun getPermissionStatus(): PushPermissionStatus {
        return permissionStatus?.invoke() ?: PushPermissionStatus.NOT_DETERMINED
    }

    override suspend fun requestPermission(): PushPermissionStatus {
        return requestPermission?.invoke() ?: PushPermissionStatus.NOT_DETERMINED
    }

    override suspend fun refreshToken() {
        refreshToken?.invoke()
    }

    override fun logToken() {
        val currentToken = _token.value
        if (currentToken != null) {
            println("FCM Token: $currentToken")
        } else {
            println("FCM Token: not available yet")
        }
    }

    companion object {
        // Called from Swift (PushNotificationBridge.swift)
        @Suppress("unused")
        var permissionStatus: (() -> PushPermissionStatus)? = null
        @Suppress("unused")
        var requestPermission: (() -> PushPermissionStatus)? = null
        @Suppress("unused")
        var refreshToken: (() -> Unit)? = null

        private var instance: IOSPushNotificationService? = null

        // Called from iOS DI (platformModule.ios.kt)
        @Suppress("unused")
        fun setInstance(service: IOSPushNotificationService) {
            instance = service
        }

        // Called from Swift (AppDelegate.swift)
        @Suppress("unused")
        fun onTokenReceived(token: String) {
            instance?.let { service ->
                service._token.value = token
                service.scope.launch {
                    service.notificationRepository.setToken(token)
                }
                println("FCM Token received: ${token.take(10)}...")
            }
        }

        // Called from Swift (AppDelegate.swift)
        @Suppress("unused")
        fun onNotificationReceived(
            title: String?,
            body: String?,
            data: Map<String, String>,
            deepLink: String?
        ) {
            val notification = Notification(
                id = data["notificationId"] ?: currentTimeMillis().toString(),
                title = title ?: data["title"] ?: "Notification",
                message = body ?: data["body"] ?: data["message"] ?: "",
                channel = NotificationChannel.GENERAL,
                data = data,
                deepLink = deepLink
            )
            instance?._notifications?.trySend(notification)
            deepLink?.let { instance?._deepLinks?.trySend(it) }
        }

        // Called from Swift (AppDelegate.swift)
        @Suppress("unused")
        fun onDeepLinkReceived(deepLink: String) {
            instance?._deepLinks?.trySend(deepLink)
        }
    }
}

private fun currentTimeMillis(): Long {
    return Clock.System.now().toEpochMilliseconds()
}
