package com.mk.kmpshowcase.server.repository

import org.jetbrains.exposed.dao.id.LongIdTable

object UsersTable : LongIdTable("users") {
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = varchar("password_hash", 255)
    val name = varchar("name", 255)
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")
}
