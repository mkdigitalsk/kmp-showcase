package com.mk.kmpshowcase.server.repository

import com.mk.kmpshowcase.server.model.NoteDTO
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

class NoteRepository {

    fun findAllByUserId(userId: Long): List<NoteDTO> = transaction {
        NotesTable.selectAll()
            .where { NotesTable.userId eq userId }
            .orderBy(NotesTable.createdAt)
            .map { it.toNoteDTO() }
    }

    fun findById(id: Long, userId: Long): NoteDTO? = transaction {
        NotesTable.selectAll()
            .where { (NotesTable.id eq id) and (NotesTable.userId eq userId) }
            .map { it.toNoteDTO() }
            .singleOrNull()
    }

    fun create(userId: Long, title: String, content: String): NoteDTO = transaction {
        val now = System.currentTimeMillis()

        val id = NotesTable.insert {
            it[NotesTable.userId] = userId
            it[NotesTable.title] = title
            it[NotesTable.content] = content
            it[createdAt] = now
            it[updatedAt] = now
        } get NotesTable.id

        NoteDTO(
            id = id.value,
            title = title,
            content = content,
            createdAt = now
        )
    }

    fun update(id: Long, userId: Long, title: String, content: String): NoteDTO? = transaction {
        val now = System.currentTimeMillis()

        val updated = NotesTable.update({ (NotesTable.id eq id) and (NotesTable.userId eq userId) }) {
            it[NotesTable.title] = title
            it[NotesTable.content] = content
            it[updatedAt] = now
        }

        if (updated > 0) {
            findById(id, userId)
        } else {
            null
        }
    }

    fun delete(id: Long, userId: Long): Boolean = transaction {
        NotesTable.deleteWhere { (NotesTable.id eq id) and (NotesTable.userId eq userId) } > 0
    }

    private fun ResultRow.toNoteDTO() = NoteDTO(
        id = this[NotesTable.id].value,
        title = this[NotesTable.title],
        content = this[NotesTable.content],
        createdAt = this[NotesTable.createdAt]
    )
}
