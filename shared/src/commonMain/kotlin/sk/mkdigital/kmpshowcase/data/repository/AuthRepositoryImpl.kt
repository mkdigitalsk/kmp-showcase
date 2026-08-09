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

    override suspend fun signIn(email: String, password: String): AuthSession {
        val response = client.signIn(email, password)
        val session = response.toAuthSession()
        preferences.setToken(session.token)
        return session
    }

    override suspend fun signUp(email: String, password: String): AuthSession {
        val response = client.signUp(email, password)
        val session = response.toAuthSession()
        preferences.setToken(session.token)
        return session
    }

    override suspend fun signInWithToken(): AuthSession? {
        val token = preferences.getToken() ?: return null
        return runCatching { client.me(token).toAuthSession() }
            .onSuccess { preferences.setToken(it.token) }
            .getOrNull()
    }

    override suspend fun signOut() {
        preferences.clearToken()
    }

    override suspend fun getToken(): String? = preferences.getToken()
}
