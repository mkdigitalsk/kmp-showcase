package sk.mkdigital.kmpshowcase.data.repository

import sk.mkdigital.kmpshowcase.contracts.note.CreateNoteRequestDTO
import sk.mkdigital.kmpshowcase.contracts.note.NoteResponseDTO
import sk.mkdigital.kmpshowcase.contracts.note.UpdateNoteRequestDTO
import sk.mkdigital.kmpshowcase.data.client.RemoteNoteClient
import sk.mkdigital.kmpshowcase.domain.model.RemoteNote
import sk.mkdigital.kmpshowcase.domain.repository.RemoteNoteRepository

class RemoteNoteRepositoryImpl(
    private val client: RemoteNoteClient
) : RemoteNoteRepository {

    override suspend fun getNotes(): List<RemoteNote> =
        client.fetchNotes().map { it.toRemoteNote() }

    override suspend fun createNote(title: String, content: String): RemoteNote =
        client.createNote(CreateNoteRequestDTO(title = title, content = content)).toRemoteNote()

    override suspend fun updateNote(id: Long, title: String, content: String, etag: String): RemoteNote =
        client.updateNote(id, UpdateNoteRequestDTO(title = title, content = content), etag).toRemoteNote()

    override suspend fun deleteNote(id: Long) = client.deleteNote(id)
}

internal fun NoteResponseDTO.toRemoteNote() = RemoteNote(
    id = id,
    title = title,
    content = content,
    createdAt = createdAt,
    updatedAt = updatedAt,
    etag = etag,
)
