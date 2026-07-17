package com.mk.kmpshowcase.server.feature.project.api

import com.mk.kmpshowcase.contracts.ApiVersion
import com.mk.kmpshowcase.server.core.auth.email
import com.mk.kmpshowcase.server.core.auth.isAdmin
import com.mk.kmpshowcase.server.feature.project.service.ProjectService
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

// Auth-gated download of stored document files. Admin sees all; a client only their own project's —
// a foreign id answers 404, never 403 (existence itself is client data).
internal fun Route.documentRoutes(projectService: ProjectService) {
    route("${ApiVersion.BASE}/documents") {
        authenticate("auth-jwt") {
            get("/{id}/file") {
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest)
                val (ownerEmail, file) = projectService.getDocumentFile(id)
                    ?: return@get call.respond(HttpStatusCode.NotFound)
                if (!call.isAdmin() && call.email() != ownerEmail) return@get call.respond(HttpStatusCode.NotFound)
                call.response.header(
                    HttpHeaders.ContentDisposition,
                    """attachment; filename="${file.filename.replace("\"", "")}"""",
                )
                call.respondBytes(file.bytes, ContentType.parse(file.contentType))
            }
        }
    }

    // Client-scoped document management (admin) — documents attach to an email, project or not:
    // a lead's spec arrives before any project exists; the project view later reads the same rows.
    route("${ApiVersion.BASE}/admin/documents/{email}") {
        authenticate("auth-jwt") {
            get {
                if (!call.isAdmin()) return@get call.respond(HttpStatusCode.Forbidden)
                val email = call.parameters["email"]?.takeIf { it.isNotBlank() }
                    ?: return@get call.respond(HttpStatusCode.BadRequest)
                call.respond(projectService.getDocuments(email).map { it.toDTO() })
            }

            post("/upload") {
                if (!call.isAdmin()) return@post call.respond(HttpStatusCode.Forbidden)
                val email = call.parameters["email"]?.takeIf { it.isNotBlank() }
                    ?: return@post call.respond(HttpStatusCode.BadRequest)
                val (draft, file) = call.receive<UploadDocumentRequestDTO>().toDraftAndFile()
                call.respond(HttpStatusCode.Created, projectService.uploadDocument(email, draft, file).toDTO())
            }
        }
    }
}
