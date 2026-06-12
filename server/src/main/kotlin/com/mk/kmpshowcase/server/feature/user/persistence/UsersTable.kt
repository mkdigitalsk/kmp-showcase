package com.mk.kmpshowcase.server.feature.user.persistence

import org.jetbrains.exposed.dao.id.LongIdTable

object UsersTable : LongIdTable("users") {
    val email = varchar("email", EMAIL_LENGTH).uniqueIndex()
    val passwordHash = varchar("password_hash", PASSWORD_HASH_LENGTH)
    val name = varchar("name", NAME_LENGTH)
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")

    private const val EMAIL_LENGTH = 255
    private const val PASSWORD_HASH_LENGTH = 255
    private const val NAME_LENGTH = 255
}
