package sk.mkdigital.kmpshowcase.server.feature.note.service

import sk.mkdigital.kmpshowcase.server.feature.note.persistence.NoteRepository

/**
 * A write resolves to exactly one of these. A missing row and a stale one are separate answers because
 * a precondition is only considered once the unconditional request would have succeeded, so `404`
 * outranks `412` — collapsing them would tell a caller their edit was stale when the note is gone.
 */
internal sealed interface WriteResult {
    data class Written(val note: Note) : WriteResult
    data class Stale(val current: Note) : WriteResult
    data object NotFound : WriteResult
}

internal class NoteService(private val repository: NoteRepository) {

    suspend fun getAllOwnedBy(ownerId: Long): List<Note> = repository.findAllByOwner(ownerId)

    suspend fun getOwnedBy(id: Long, ownerId: Long): Note? = repository.findByIdAndOwner(id, ownerId)

    suspend fun create(ownerId: Long, title: String, content: String): Note =
        repository.create(ownerId, title.trim(), content.trim())

    suspend fun update(
        id: Long,
        ownerId: Long,
        expectedVersions: Set<Long>?,
        title: String,
        content: String,
    ): WriteResult {
        val updated = repository.update(id, ownerId, expectedVersions, title.trim(), content.trim())
        if (updated != null) return WriteResult.Written(updated)

        // The write matched nothing, so re-read to learn why. Absent means gone or never theirs; present
        // means someone else has written since they read it, and they get the row back to compare.
        val current = repository.findByIdAndOwner(id, ownerId) ?: return WriteResult.NotFound
        return WriteResult.Stale(current)
    }

    suspend fun delete(id: Long, ownerId: Long): Boolean = repository.delete(id, ownerId)
}
