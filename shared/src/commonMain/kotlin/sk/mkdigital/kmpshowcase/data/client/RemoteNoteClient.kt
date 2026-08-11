package sk.mkdigital.kmpshowcase.data.client

import sk.mkdigital.kmpshowcase.contracts.note.CreateNoteRequestDTO
import sk.mkdigital.kmpshowcase.contracts.note.NoteResponseDTO
import sk.mkdigital.kmpshowcase.contracts.note.UpdateNoteRequestDTO
import sk.mkdigital.kmpshowcase.data.network.handleApiCall
import sk.mkdigital.kmpshowcase.data.repository.toRemoteNote
import sk.mkdigital.kmpshowcase.domain.exceptions.NoteConflictException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders

interface RemoteNoteClient {
    suspend fun fetchNotes(): List<NoteResponseDTO>
    suspend fun createNote(request: CreateNoteRequestDTO): NoteResponseDTO
    suspend fun updateNote(id: Long, request: UpdateNoteRequestDTO, etag: String): NoteResponseDTO
    suspend fun deleteNote(id: Long)
}

class RemoteNoteClientImpl(
    private val client: HttpClient
) : RemoteNoteClient {

    override suspend fun fetchNotes(): List<NoteResponseDTO> = handleApiCall {
        client.get("notes").body()
    }

    override suspend fun createNote(request: CreateNoteRequestDTO): NoteResponseDTO = handleApiCall {
        client.post("notes") { setBody(request) }.body()
    }

    override suspend fun updateNote(id: Long, request: UpdateNoteRequestDTO, etag: String): NoteResponseDTO =
        handleApiCall {
            try {
                client.put("notes/$id") {
                    header(HttpHeaders.IfMatch, etag)
                    setBody(request)
                }.body()
            } catch (e: ClientRequestException) {
                // handleApiCall would flatten this into a generic ApiException and lose the row.
                if (e.response.status == HttpStatusCode.PreconditionFailed) {
                    throw NoteConflictException(e.response.body<NoteResponseDTO>().toRemoteNote(), e)
                }
                throw e
            }
        }

    override suspend fun deleteNote(id: Long) = handleApiCall {
        client.delete("notes/$id").body<Unit>()
    }
}
