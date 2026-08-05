package sk.mkdigital.kmpshowcase.data.repository

import sk.mkdigital.kmpshowcase.data.client.AuthClient
import sk.mkdigital.kmpshowcase.data.dto.toAuthSession
import sk.mkdigital.kmpshowcase.data.local.preferences.PersistentPreferences
import sk.mkdigital.kmpshowcase.domain.model.AuthSession
import sk.mkdigital.kmpshowcase.domain.repository.AuthRepository

class AuthRepositoryImpl(
    private val client: AuthClient,
    private val preferences: PersistentPreferences,
) : AuthRepository {

    override suspend fun login(email: String, password: String): AuthSession {
        val response = client.login(email, password)
        val session = response.toAuthSession()
        preferences.setToken(session.token)
        return session
    }

    override suspend fun register(name: String, email: String, password: String): AuthSession {
        val response = client.register(email, password, name)
        val session = response.toAuthSession()
        preferences.setToken(session.token)
        return session
    }

    override suspend fun loginWithToken(): AuthSession? {
        val token = preferences.getToken() ?: return null
        return runCatching { client.me(token).toAuthSession() }
            .onSuccess { preferences.setToken(it.token) }
            .getOrNull()
    }

    override suspend fun logout() {
        preferences.clearToken()
    }

    override suspend fun getToken(): String? = preferences.getToken()
}
