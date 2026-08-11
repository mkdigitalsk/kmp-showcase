package sk.mkdigital.kmpshowcase.data.repository

import sk.mkdigital.kmpshowcase.data.client.AuthClient
import sk.mkdigital.kmpshowcase.data.dto.toAuthSession
import sk.mkdigital.kmpshowcase.data.local.preferences.PersistentPreferences
import sk.mkdigital.kmpshowcase.data.local.preferences.SessionPreferences
import sk.mkdigital.kmpshowcase.domain.model.AuthSession
import sk.mkdigital.kmpshowcase.domain.repository.AuthRepository
import sk.mkdigital.kmpshowcase.domain.repository.NoteRepository

class AuthRepositoryImpl(
    private val client: AuthClient,
    private val preferences: PersistentPreferences,
    private val sessionPreferences: SessionPreferences,
    private val noteRepository: NoteRepository,
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): AuthSession {
        val response = client.signIn(email, password)
        val session = response.toAuthSession()
        persist(session)
        return session
    }

    override suspend fun signUp(email: String, password: String): AuthSession {
        val response = client.signUp(email, password)
        val session = response.toAuthSession()
        persist(session)
        return session
    }

    override suspend fun signInWithToken(): AuthSession? {
        val token = preferences.getToken() ?: return null
        return runCatching { client.me(token).toAuthSession() }
            .onSuccess { persist(it) }
            .getOrNull()
    }

    override suspend fun signOut() = clearLocalUserData()

    // The server's answer decides whether the account is gone, so a local store that will not clear
    // cannot turn a completed erasure into "deletion failed" and park the person on a dead account.
    override suspend fun deleteAccount() {
        client.deleteAccount()
        runCatching { clearLocalUserData() }
    }

    override suspend fun getToken(): String? = preferences.getToken()

    override suspend fun isDemoAccount(): Boolean = preferences.isDemoAccount()

    private suspend fun persist(session: AuthSession) {
        preferences.setToken(session.token)
        preferences.setDemoAccount(session.demo)
    }

    // The token clears last, and is the only clear worth failing on — a wipe that keeps the session alive
    // is retried on the next attempt, one that drops it strands the previous account's data on the device.
    private suspend fun clearLocalUserData() {
        runCatching { noteRepository.deleteAll() }
        runCatching { sessionPreferences.clear() }
        preferences.clearUserData()
    }
}
