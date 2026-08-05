package sk.mkdigital.kmpshowcase.data.repository

import sk.mkdigital.kmpshowcase.data.local.preferences.PersistentPreferences
import sk.mkdigital.kmpshowcase.domain.repository.NotificationRepository

class NotificationRepositoryImpl(
    private val persistentPreferences: PersistentPreferences,
) : NotificationRepository {

    override suspend fun getToken(): String? = persistentPreferences.getFcmToken()

    override suspend fun setToken(token: String): Unit = persistentPreferences.setFcmToken(token)
}
