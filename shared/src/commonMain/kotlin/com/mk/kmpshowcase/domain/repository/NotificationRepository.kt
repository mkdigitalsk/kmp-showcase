package com.mk.kmpshowcase.domain.repository

interface NotificationRepository {
    suspend fun getToken(): String?
    suspend fun setToken(token: String)
}
