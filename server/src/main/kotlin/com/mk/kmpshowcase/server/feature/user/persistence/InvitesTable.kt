package com.mk.kmpshowcase.server.feature.user.persistence

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

// Pending portal invitations — the portal is invite-only (no self-registration). A row exists only
// between "admin invited" and "client accepted"; accepting creates the user and deletes the row.
internal object InvitesTable : LongIdTable("invites") {
    val email = varchar("email", EMAIL_LENGTH).uniqueIndex()
    val tokenHash = varchar("token_hash", TOKEN_HASH_LENGTH)
    val name = varchar("name", NAME_LENGTH).nullable()
    val expiresAt = long("expires_at")
    val createdAt = long("created_at")

    private const val EMAIL_LENGTH = 320
    private const val TOKEN_HASH_LENGTH = 64
    private const val NAME_LENGTH = 255
}
