package com.mk.kmpshowcase.data.push

import com.mk.kmpshowcase.domain.model.Notification
import com.mk.kmpshowcase.domain.model.NotificationChannel
import com.mk.kmpshowcase.domain.repository.NotificationRepository
import com.mk.kmpshowcase.domain.repository.PushNotificationService
import com.mk.kmpshowcase.domain.repository.PushPermissionStatus
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
import kotlin.time.Clock

private const val TOKEN_PREVIEW_LENGTH = 10

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
        private var pendingDeepLink: String? = null

        // Called from iOS DI (platformModule.ios.kt)
        @Suppress("unused")
        fun setInstance(service: IOSPushNotificationService) {
            instance = service
            // Send any pending deep link that arrived before instance was ready
            pendingDeepLink?.let { deepLink ->
                service._deepLinks.trySend(deepLink)
                pendingDeepLink = null
            }
        }

        // Called from Swift (AppDelegate.swift)
        @Suppress("unused")
        fun onTokenReceived(token: String) {
            instance?.let { service ->
                service._token.value = token
                service.scope.launch {
                    service.notificationRepository.setToken(token)
                }
                println("FCM Token received: ${token.take(TOKEN_PREVIEW_LENGTH)}...")
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

        // Called from Swift (iOSApp.swift via onOpenURL)
        @Suppress("unused")
        fun onDeepLinkReceived(deepLink: String) {
            val service = instance
            if (service != null) {
                service._deepLinks.trySend(deepLink)
            } else {
                // Store for later when instance is ready (cold start from deep link)
                pendingDeepLink = deepLink
            }
        }
    }
}

private fun currentTimeMillis(): Long {
    return Clock.System.now().toEpochMilliseconds()
}
