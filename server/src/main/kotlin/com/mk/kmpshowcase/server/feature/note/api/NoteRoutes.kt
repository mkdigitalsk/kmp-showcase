package com.mk.kmpshowcase.server.feature.note.api

import com.mk.kmpshowcase.server.core.auth.userId
import com.mk.kmpshowcase.server.feature.note.service.NoteService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

fun Route.noteRoutes(noteService: NoteService) {
    route("/api/notes") {
        authenticate("auth-jwt") {
            get {
                val userId = call.userId() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                call.respond(noteService.listForUser(userId).map { it.toDTO() })
            }

            get("/search") {
                val userId = call.userId() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val query = call.request.queryParameters["q"]
                call.respond(noteService.listForUser(userId, query).map { it.toDTO() })
            }

            get("/{id}") {
                val userId = call.userId() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val noteId = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest)
                val note = noteService.getById(noteId, userId)
                    ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(note.toDTO())
            }

            post {
                val userId = call.userId() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val request = call.receive<CreateNoteRequest>()
                val note = noteService.create(userId, request.title, request.content)
                call.respond(HttpStatusCode.Created, note.toDTO())
            }

            put("/{id}") {
                val userId = call.userId() ?: return@put call.respond(HttpStatusCode.Unauthorized)
                val noteId = call.parameters["id"]?.toLongOrNull()
                    ?: return@put call.respond(HttpStatusCode.BadRequest)
                val request = call.receive<UpdateNoteRequest>()
                val note = noteService.update(noteId, userId, request.title, request.content)
                    ?: return@put call.respond(HttpStatusCode.NotFound)
                call.respond(note.toDTO())
            }

            delete("/{id}") {
                val userId = call.userId() ?: return@delete call.respond(HttpStatusCode.Unauthorized)
                val noteId = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest)
                if (noteService.delete(noteId, userId)) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }
}
