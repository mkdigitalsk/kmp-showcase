package com.mk.kmpshowcase.server.routes

import com.mk.kmpshowcase.server.model.CreateNoteRequest
import com.mk.kmpshowcase.server.model.UpdateNoteRequest
import com.mk.kmpshowcase.server.repository.NoteRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.notesRoutes() {
    val noteRepository = NoteRepository()

    route("/api/notes") {
        authenticate("auth-jwt") {
            // Get all notes for current user
            get {
                val userId = call.principal<JWTPrincipal>()
                    ?.payload?.getClaim("userId")?.asLong()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)

                val notes = noteRepository.findAllByUserId(userId)
                call.respond(notes)
            }

            // Get single note
            get("/{id}") {
                val userId = call.principal<JWTPrincipal>()
                    ?.payload?.getClaim("userId")?.asLong()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)

                val noteId = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest)

                val note = noteRepository.findById(noteId, userId)
                    ?: return@get call.respond(HttpStatusCode.NotFound)

                call.respond(note)
            }

            // Create note
            post {
                val userId = call.principal<JWTPrincipal>()
                    ?.payload?.getClaim("userId")?.asLong()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)

                val request = call.receive<CreateNoteRequest>()
                require(request.title.isNotBlank()) { "Title cannot be blank" }

                val note = noteRepository.create(userId, request.title, request.content)
                call.respond(HttpStatusCode.Created, note)
            }

            // Update note
            put("/{id}") {
                val userId = call.principal<JWTPrincipal>()
                    ?.payload?.getClaim("userId")?.asLong()
                    ?: return@put call.respond(HttpStatusCode.Unauthorized)

                val noteId = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest)

                val request = call.receive<UpdateNoteRequest>()
                require(request.title.isNotBlank()) { "Title cannot be blank" }

                val note = noteRepository.update(noteId, userId, request.title, request.content)
                    ?: return@put call.respond(HttpStatusCode.NotFound)

                call.respond(note)
            }

            // Delete note
            delete("/{id}") {
                val userId = call.principal<JWTPrincipal>()
                    ?.payload?.getClaim("userId")?.asLong()
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized)

                val noteId = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)

                if (noteRepository.delete(noteId, userId)) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }
}
