package sk.mkdigital.kmpshowcase.server.fake

import sk.mkdigital.kmpshowcase.server.feature.note.persistence.NoteRepository
import sk.mkdigital.kmpshowcase.server.feature.note.service.Note

/**
 * ⚠ Enforces the same predicate the SQL does — owner and version both matched. Ignoring either makes
 * every authorization and concurrency test above it pass while production fails.
 */
internal class FakeNoteRepository(stored: List<Note> = emptyList()) : NoteRepository {

    private val rows = stored.toMutableList()
    private var nextId = (stored.maxOfOrNull { it.id } ?: 0) + 1

    override suspend fun findAllByOwner(ownerId: Long): List<Note> = rows.filter { it.ownerId == ownerId }

    override suspend fun findByIdAndOwner(id: Long, ownerId: Long): Note? =
        rows.firstOrNull { it.id == id && it.ownerId == ownerId }

    override suspend fun create(ownerId: Long, title: String, content: String): Note {
        val now = System.currentTimeMillis()
        return Note(nextId++, ownerId, title, content, createdAt = now, updatedAt = now, version = 0)
            .also { rows += it }
    }

    override suspend fun update(
        id: Long,
        ownerId: Long,
        expectedVersions: Set<Long>?,
        title: String,
        content: String,
    ): Note? {
        val index = rows.indexOfFirst {
            it.id == id && it.ownerId == ownerId && (expectedVersions == null || it.version in expectedVersions)
        }
        if (index < 0) return null

        val stored = rows[index]
        return stored.copy(
            title = title,
            content = content,
            updatedAt = System.currentTimeMillis(),
            version = stored.version + 1,
        ).also { rows[index] = it }
    }

    override suspend fun delete(id: Long, ownerId: Long): Boolean =
        rows.removeAll { it.id == id && it.ownerId == ownerId }
}
