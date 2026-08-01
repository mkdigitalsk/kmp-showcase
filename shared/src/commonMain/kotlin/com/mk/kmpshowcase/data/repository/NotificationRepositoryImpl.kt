package com.mk.kmpshowcase.data.repository

import com.mk.kmpshowcase.data.local.preferences.PersistentPreferences
import com.mk.kmpshowcase.domain.repository.NotificationRepository

class NotificationRepositoryImpl(
    private val persistentPreferences: PersistentPreferences,
) : NotificationRepository {

    override suspend fun getToken(): String? = persistentPreferences.getFcmToken()

    override suspend fun setToken(token: String): Unit = persistentPreferences.setFcmToken(token)
}
