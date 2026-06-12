package com.mk.kmpshowcase.server.feature.note.persistence

import com.mk.kmpshowcase.server.feature.note.service.Note
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update

class NoteRepositoryImpl : NoteRepository {

    override suspend fun findAllByUserId(userId: Long): List<Note> = newSuspendedTransaction {
        NotesTable.selectAll()
            .where { NotesTable.userId eq userId }
            .orderBy(NotesTable.createdAt)
            .map { it.toNote() }
    }

    override suspend fun findByTitleQuery(userId: Long, query: String): List<Note> = newSuspendedTransaction {
        NotesTable.selectAll()
            .where { (NotesTable.userId eq userId) and (NotesTable.title like "%$query%") }
            .orderBy(NotesTable.createdAt)
            .map { it.toNote() }
    }

    override suspend fun findById(id: Long, userId: Long): Note? = newSuspendedTransaction {
        NotesTable.selectAll()
            .where { (NotesTable.id eq id) and (NotesTable.userId eq userId) }
            .map { it.toNote() }
            .singleOrNull()
    }

    override suspend fun create(userId: Long, title: String, content: String): Note = newSuspendedTransaction {
        val now = System.currentTimeMillis()
        val newId = NotesTable.insert {
            it[NotesTable.userId] = userId
            it[NotesTable.title] = title
            it[NotesTable.content] = content
            it[createdAt] = now
            it[updatedAt] = now
        } get NotesTable.id

        Note(
            id = newId.value,
            title = title,
            content = content,
            createdAt = now,
        )
    }

    override suspend fun update(
        id: Long,
        userId: Long,
        title: String,
        content: String,
    ): Note? = newSuspendedTransaction {
        val now = System.currentTimeMillis()
        val updated = NotesTable.update({ (NotesTable.id eq id) and (NotesTable.userId eq userId) }) {
            it[NotesTable.title] = title
            it[NotesTable.content] = content
            it[updatedAt] = now
        }
        if (updated > 0) {
            NotesTable.selectAll()
                .where { (NotesTable.id eq id) and (NotesTable.userId eq userId) }
                .map { it.toNote() }
                .singleOrNull()
        } else {
            null
        }
    }

    override suspend fun delete(id: Long, userId: Long): Boolean = newSuspendedTransaction {
        NotesTable.deleteWhere { (NotesTable.id eq id) and (NotesTable.userId eq userId) } > 0
    }

    private fun ResultRow.toNote() = Note(
        id = this[NotesTable.id].value,
        title = this[NotesTable.title],
        content = this[NotesTable.content],
        createdAt = this[NotesTable.createdAt],
    )
}
