package com.mk.kmpshowcase.server.feature.user.persistence

internal data class Invite(val email: String, val tokenHash: String, val name: String?, val expiresAt: Long)

internal interface InviteRepository {
    suspend fun upsert(email: String, tokenHash: String, name: String?, expiresAt: Long)
    suspend fun findByTokenHash(tokenHash: String): Invite?
    suspend fun delete(email: String): Boolean
}
