package com.mk.kmpshowcase.server.feature.user.persistence

import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.update

internal class InviteRepositoryImpl : InviteRepository {

    override suspend fun upsert(email: String, tokenHash: String, name: String?, expiresAt: Long) = suspendTransaction {
        val now = System.currentTimeMillis()
        val updated = InvitesTable.update({ InvitesTable.email eq email }) {
            it[InvitesTable.tokenHash] = tokenHash
            it[InvitesTable.name] = name
            it[InvitesTable.expiresAt] = expiresAt
            it[createdAt] = now
        }
        if (updated == 0) {
            InvitesTable.insert {
                it[InvitesTable.email] = email
                it[InvitesTable.tokenHash] = tokenHash
                it[InvitesTable.name] = name
                it[InvitesTable.expiresAt] = expiresAt
                it[createdAt] = now
            }
        }
        Unit
    }

    override suspend fun findByTokenHash(tokenHash: String): Invite? = suspendTransaction {
        InvitesTable.selectAll().where { InvitesTable.tokenHash eq tokenHash }.firstOrNull()?.let {
            Invite(it[InvitesTable.email], it[InvitesTable.tokenHash], it[InvitesTable.name], it[InvitesTable.expiresAt])
        }
    }

    override suspend fun delete(email: String): Boolean = suspendTransaction {
        InvitesTable.deleteWhere { InvitesTable.email eq email } > 0
    }
}
