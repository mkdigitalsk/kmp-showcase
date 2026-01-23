package mk.digital.kmpshowcase.data.push

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.tasks.await
import mk.digital.kmpshowcase.data.analytics.AnalyticsClient
import mk.digital.kmpshowcase.domain.model.Notification
import mk.digital.kmpshowcase.domain.model.NotificationChannel
import mk.digital.kmpshowcase.domain.repository.NotificationRepository
import mk.digital.kmpshowcase.domain.repository.PushNotificationService
import mk.digital.kmpshowcase.domain.repository.PushPermissionStatus

class AndroidPushNotificationService(
    private val context: Context,
    private val firebaseMessaging: FirebaseMessaging,
    private val notificationRepository: NotificationRepository,
    private val analyticsClient: AnalyticsClient
) : PushNotificationService {

    private val _token = MutableStateFlow<String?>(null)
    override val token: StateFlow<String?> = _token.asStateFlow()

    private val _notifications = Channel<Notification>(Channel.BUFFERED)
    override val notifications: Flow<Notification> = _notifications.receiveAsFlow()

    private val _deepLinks = Channel<String>(Channel.BUFFERED)
    override val deepLinks: Flow<String> = _deepLinks.receiveAsFlow()

    override fun getPermissionStatus(): PushPermissionStatus {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> PushPermissionStatus.GRANTED
                else -> PushPermissionStatus.DENIED
            }
        } else {
            PushPermissionStatus.GRANTED
        }
    }

    override suspend fun requestPermission(): PushPermissionStatus {
        return getPermissionStatus()
    }

    override suspend fun refreshToken() {
        try {
            val token = firebaseMessaging.token.await()
            updateToken(token)
        } catch (e: Exception) {
            analyticsClient.recordException(e)
        }
    }

    override fun logToken() {
        val currentToken = _token.value
        if (currentToken != null) {
            Log.d(TAG, "FCM Token: $currentToken")
            analyticsClient.log("FCM Token logged: ${currentToken.take(10)}...")
        } else {
            Log.d(TAG, "FCM Token: not available yet")
        }
    }

    suspend fun updateToken(token: String) {
        _token.value = token
        notificationRepository.setToken(token)
        Log.d(TAG, "FCM Token updated: ${token.take(10)}...")
        analyticsClient.log("FCM token updated")
    }

    fun onNotificationReceived(
        title: String?,
        body: String?,
        data: Map<String, String>
    ) {
        val deepLink = data[KEY_DEEP_LINK]

        val notification = Notification(
            id = data[KEY_NOTIFICATION_ID] ?: System.currentTimeMillis().toString(),
            title = title ?: data[KEY_TITLE] ?: DEFAULT_TITLE,
            message = body ?: data[KEY_BODY] ?: data[KEY_MESSAGE] ?: "",
            channel = NotificationChannel.GENERAL,
            data = data,
            deepLink = deepLink
        )

        _notifications.trySend(notification)
        deepLink?.let { _deepLinks.trySend(it) }

        analyticsClient.log("Push notification received: ${notification.title}")
    }

    fun onDeepLinkReceived(deepLink: String) {
        _deepLinks.trySend(deepLink)
    }

    companion object {
        private const val TAG = "PushNotificationService"
        private const val KEY_DEEP_LINK = "deep_link"
        private const val KEY_NOTIFICATION_ID = "notificationId"
        private const val KEY_TITLE = "title"
        private const val KEY_BODY = "body"
        private const val KEY_MESSAGE = "message"
        private const val DEFAULT_TITLE = "Notification"
    }
}
