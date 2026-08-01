package com.mk.kmpshowcase.domain.repository

import com.mk.kmpshowcase.domain.model.AuthSession

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthSession
    suspend fun register(name: String, email: String, password: String): AuthSession
    suspend fun loginWithToken(): AuthSession?
    suspend fun logout()
    suspend fun getToken(): String?
}
