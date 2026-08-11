package sk.mkdigital.kmpshowcase.server.feature.note.persistence

import sk.mkdigital.kmpshowcase.server.core.persistence.mapToSingleOrNull
import sk.mkdigital.kmpshowcase.server.feature.note.service.Note
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.plus
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.updateReturning

// The owner is part of every predicate rather than checked after the read, so a caller can never reach
// or enumerate another account's rows — including through the list, which filters by absence.
internal class NoteRepositoryImpl : NoteRepository {

    override suspend fun findAllByOwner(ownerId: Long): List<Note> = suspendTransaction {
        NotesTable.selectAll()
            .where { NotesTable.userId eq ownerId }
            .orderBy(NotesTable.createdAt to SortOrder.DESC)
            .map { it.toNote() }
    }

    override suspend fun findByIdAndOwner(id: Long, ownerId: Long): Note? = suspendTransaction {
        NotesTable.selectAll()
            .where { (NotesTable.id eq id) and (NotesTable.userId eq ownerId) }
            .mapToSingleOrNull { it.toNote() }
    }

    override suspend fun create(ownerId: Long, title: String, content: String): Note = suspendTransaction {
        val now = System.currentTimeMillis()
        val id = NotesTable.insert {
            it[NotesTable.userId] = ownerId
            it[NotesTable.title] = title
            it[NotesTable.content] = content
            it[createdAt] = now
            it[updatedAt] = now
        } get NotesTable.id

        Note(
            id = id.value,
            ownerId = ownerId,
            title = title,
            content = content,
            createdAt = now,
            updatedAt = now,
            version = 0,
        )
    }

    override suspend fun update(
        id: Long,
        ownerId: Long,
        expectedVersions: Set<Long>?,
        title: String,
        content: String,
    ): Note? = suspendTransaction {
        // One statement: the precondition is the WHERE clause and the bump is the SET, so no other
        // writer fits between them. Read back rather than re-queried, or the row could move again first.
        // The bump is relative because the caller may have named several versions.
        NotesTable.updateReturning(
            where = {
                val scoped = (NotesTable.id eq id) and (NotesTable.userId eq ownerId)
                if (expectedVersions == null) scoped else scoped and (NotesTable.version inList expectedVersions)
            },
        ) {
            it[NotesTable.title] = title
            it[NotesTable.content] = content
            it[updatedAt] = System.currentTimeMillis()
            it[version] = NotesTable.version + 1
        }.singleOrNull()?.toNote()
    }

    override suspend fun delete(id: Long, ownerId: Long): Boolean = suspendTransaction {
        NotesTable.deleteWhere { (NotesTable.id eq id) and (NotesTable.userId eq ownerId) } > 0
    }
}

private fun ResultRow.toNote() = Note(
    id = this[NotesTable.id].value,
    ownerId = this[NotesTable.userId].value,
    title = this[NotesTable.title],
    content = this[NotesTable.content],
    createdAt = this[NotesTable.createdAt],
    updatedAt = this[NotesTable.updatedAt],
    version = this[NotesTable.version],
)
