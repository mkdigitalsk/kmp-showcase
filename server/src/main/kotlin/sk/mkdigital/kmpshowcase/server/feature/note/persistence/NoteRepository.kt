package sk.mkdigital.kmpshowcase.server.feature.note.persistence

import sk.mkdigital.kmpshowcase.server.feature.note.service.Note

internal interface NoteRepository {

    suspend fun findAllByOwner(ownerId: Long): List<Note>

    suspend fun findByIdAndOwner(id: Long, ownerId: Long): Note?

    suspend fun create(ownerId: Long, title: String, content: String): Note

    /**
     * Applies the write only where the owner matches, and where [expectedVersions] is given, only where
     * the version is one of them — so the check cannot be separated from the write by another writer.
     * Several versions because `If-Match` is a list and any member matching is a match; an empty set is
     * a precondition nothing can satisfy. Null is an unconditional write.
     *
     * Returns null when nothing matched. The caller resolves why by re-reading, because a missing row
     * and a stale one are different answers.
     */
    suspend fun update(
        id: Long,
        ownerId: Long,
        expectedVersions: Set<Long>?,
        title: String,
        content: String,
    ): Note?

    suspend fun delete(id: Long, ownerId: Long): Boolean
}
