package com.mk.kmpshowcase.server.repository

import org.jetbrains.exposed.dao.id.LongIdTable

object NotesTable : LongIdTable("notes") {
    val userId = reference("user_id", UsersTable)
    val title = varchar("title", 255)
    val content = text("content")
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")
}
