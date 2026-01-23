package mk.digital.kmpshowcase.data.notification

import mk.digital.kmpshowcase.data.local.preferences.AppPreferences
import mk.digital.kmpshowcase.domain.repository.NotificationRepository

class NotificationRepositoryImpl(
    private val preferences: AppPreferences,
) : NotificationRepository {

    override suspend fun getToken(): String? = preferences.getFcmToken()

    override suspend fun setToken(token: String): Unit = preferences.setFcmToken(token)
}
