package sk.mkdigital.kmpshowcase.server.feature.note.api

import sk.mkdigital.kmpshowcase.contracts.ApiVersion
import sk.mkdigital.kmpshowcase.contracts.note.CreateNoteRequestDTO
import sk.mkdigital.kmpshowcase.contracts.note.NoteResponseDTO
import sk.mkdigital.kmpshowcase.contracts.note.UpdateNoteRequestDTO
import sk.mkdigital.kmpshowcase.server.core.auth.userId
import sk.mkdigital.kmpshowcase.server.feature.note.persistence.NotesTable
import sk.mkdigital.kmpshowcase.server.feature.note.service.NoteService
import sk.mkdigital.kmpshowcase.server.feature.note.service.WriteResult
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

// Ktor's HttpStatusCode stops short of 428, so the code RFC 6585 §3 assigns is named here rather than
// left as a bare number at the call site.
// https://www.rfc-editor.org/rfc/rfc6585#section-3
private val PreconditionRequired = HttpStatusCode(428, "Precondition Required")

// The remote half of the showcase's notes: the same object the Database screen keeps on the device,
// kept on the server instead. Every route is scoped to the caller, so this is also what replaced the
// unscoped user list the Networking screen used to read.
internal fun Route.noteRoutes(noteService: NoteService) {
    route("${ApiVersion.BASE}/notes") {
        authenticate("auth-jwt") {
            listNotes(noteService)
            getNote(noteService)
            createNote(noteService)
            updateNote(noteService)
            deleteNote(noteService)
        }
    }
}

private fun Route.listNotes(noteService: NoteService) = get {
    val userId = call.userId() ?: return@get call.respond(HttpStatusCode.Unauthorized)
    call.respond(noteService.getAllOwnedBy(userId).map { it.toNoteResponseDTO() })
}

private fun Route.getNote(noteService: NoteService) = get("/{id}") {
    val userId = call.userId() ?: return@get call.respond(HttpStatusCode.Unauthorized)
    val id = call.noteId() ?: return@get call.respond(HttpStatusCode.BadRequest)
    val note = noteService.getOwnedBy(id, userId) ?: return@get call.respond(HttpStatusCode.NotFound)
    call.respondNote(note.toNoteResponseDTO())
}

private fun Route.createNote(noteService: NoteService) = post {
    val userId = call.userId() ?: return@post call.respond(HttpStatusCode.Unauthorized)
    val request = call.receive<CreateNoteRequestDTO>()
    request.validate()?.let { return@post call.respond(HttpStatusCode.BadRequest, it) }

    val note = noteService.create(userId, request.title, request.content)
    call.respond(HttpStatusCode.Created, note.toNoteResponseDTO())
}

private fun Route.updateNote(noteService: NoteService) = put("/{id}") {
    val userId = call.userId() ?: return@put call.respond(HttpStatusCode.Unauthorized)
    val id = call.noteId() ?: return@put call.respond(HttpStatusCode.BadRequest)
    val request = call.receive<UpdateNoteRequestDTO>()
    request.validate()?.let { return@put call.respond(HttpStatusCode.BadRequest, it) }

    // Refused rather than applied: without a precondition the last writer silently wins and the other
    // edit is gone with nobody told. `*` and a `W/` tag parse to nothing usable and are refused the
    // same way — `*` matches merely because the row exists, and a gzipping proxy is what turns a strong
    // tag weak in transit.
    val expected = call.request.headers[HttpHeaders.IfMatch].orEmpty().toExpectedVersions()
    if (expected.isEmpty()) return@put call.respond(PreconditionRequired)

    when (val result = noteService.update(id, userId, expected, request.title, request.content)) {
        is WriteResult.Written -> call.respondNote(result.note.toNoteResponseDTO())
        // The current row rides along so the client can show both versions and let the person choose.
        // Never merged for them — only they know which edit mattered.
        is WriteResult.Stale -> call.respond(HttpStatusCode.PreconditionFailed, result.current.toNoteResponseDTO())
        WriteResult.NotFound -> call.respond(HttpStatusCode.NotFound)
    }
}

private fun Route.deleteNote(noteService: NoteService) = delete("/{id}") {
    val userId = call.userId() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
    val id = call.noteId() ?: return@delete call.respond(HttpStatusCode.BadRequest)
    if (!noteService.delete(id, userId)) return@delete call.respond(HttpStatusCode.NotFound)
    call.respond(HttpStatusCode.NoContent)
}

private fun ApplicationCall.noteId(): Long? = parameters["id"]?.toLongOrNull()

private suspend fun ApplicationCall.respondNote(dto: NoteResponseDTO) {
    response.header(HttpHeaders.ETag, dto.etag)
    respond(dto)
}

private fun CreateNoteRequestDTO.validate(): String? = validateFields(title, content)

private fun UpdateNoteRequestDTO.validate(): String? = validateFields(title, content)

// A stable code rather than prose, because it reaches the client as a field error it has to branch on.
private fun validateFields(title: String, content: String): String? = when {
    title.isBlank() -> "note.title.blank"
    title.length > NotesTable.TITLE_LENGTH -> "note.title.tooLong"
    content.length > NotesTable.CONTENT_LENGTH -> "note.content.tooLong"
    else -> null
}
