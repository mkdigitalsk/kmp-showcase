package com.mk.kmpshowcase.server.feature.note.persistence

import com.mk.kmpshowcase.server.feature.user.persistence.UsersTable
import org.jetbrains.exposed.dao.id.LongIdTable

object NotesTable : LongIdTable("notes") {
    val userId = reference("user_id", UsersTable)
    val title = varchar("title", TITLE_LENGTH)
    val content = text("content")
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")

    private const val TITLE_LENGTH = 255
}
