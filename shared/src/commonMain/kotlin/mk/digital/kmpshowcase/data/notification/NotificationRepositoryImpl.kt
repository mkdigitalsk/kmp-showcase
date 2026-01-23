package mk.digital.kmpshowcase.data.notification

import mk.digital.kmpshowcase.data.local.preferences.PersistentPreferences
import mk.digital.kmpshowcase.domain.repository.NotificationRepository

class NotificationRepositoryImpl(
    private val persistentPreferences: PersistentPreferences,
) : NotificationRepository {

    override suspend fun getToken(): String? = persistentPreferences.getFcmToken()

    override suspend fun setToken(token: String): Unit = persistentPreferences.setFcmToken(token)
}
