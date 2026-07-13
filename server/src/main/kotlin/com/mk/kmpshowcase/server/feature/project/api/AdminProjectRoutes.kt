package com.mk.kmpshowcase.server.feature.project.api

import com.mk.kmpshowcase.contracts.ApiVersion
import com.mk.kmpshowcase.server.core.auth.isAdmin
import com.mk.kmpshowcase.server.feature.project.service.ProjectHealth
import com.mk.kmpshowcase.server.feature.project.service.ProjectService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route

// Admin-only project management. Role enforced server-side per endpoint — a non-admin token never
// reaches the data. Routes are thin: extract → call service → respond.
internal fun Route.adminProjectRoutes(projectService: ProjectService) {
    route("${ApiVersion.BASE}/admin/projects") {
        authenticate("auth-jwt") {
            get("/{email}") {
                if (!call.isAdmin()) return@get call.respond(HttpStatusCode.Forbidden)
                val email = call.emailParam() ?: return@get call.respond(HttpStatusCode.BadRequest)
                val project = projectService.getAdminProject(email) ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(project.toDTO())
            }

            // Read-only "view as client" — same client-safe projection the client sees (best practice over impersonation).
            get("/{email}/client-preview") {
                if (!call.isAdmin()) return@get call.respond(HttpStatusCode.Forbidden)
                val email = call.emailParam() ?: return@get call.respond(HttpStatusCode.BadRequest)
                val preview = projectService.getClientProject(email) ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(preview.toDTO())
            }

            // A signed lead becomes a project.
            post("/{email}") {
                if (!call.isAdmin()) return@post call.respond(HttpStatusCode.Forbidden)
                val email = call.emailParam() ?: return@post call.respond(HttpStatusCode.BadRequest)
                projectService.startProject(email, call.receive<StartProjectRequestDTO>().toDraft())
                call.respond(HttpStatusCode.Created, projectService.getAdminProject(email)!!.toDTO())
            }

            patch("/{email}") {
                if (!call.isAdmin()) return@patch call.respond(HttpStatusCode.Forbidden)
                val email = call.emailParam() ?: return@patch call.respond(HttpStatusCode.BadRequest)
                val body = call.receive<UpdateProjectRequestDTO>()
                projectService.updateProject(email, ProjectHealth.valueOf(body.health.uppercase()), body.targetEndDate)
                    ?: return@patch call.respond(HttpStatusCode.NotFound)
                call.respond(projectService.getAdminProject(email)!!.toDTO())
            }

            post("/{email}/complete") {
                if (!call.isAdmin()) return@post call.respond(HttpStatusCode.Forbidden)
                val email = call.emailParam() ?: return@post call.respond(HttpStatusCode.BadRequest)
                projectService.completeProject(email) ?: return@post call.respond(HttpStatusCode.NotFound)
                call.respond(projectService.getAdminProject(email)!!.toDTO())
            }

            post("/{email}/archive") {
                if (!call.isAdmin()) return@post call.respond(HttpStatusCode.Forbidden)
                val email = call.emailParam() ?: return@post call.respond(HttpStatusCode.BadRequest)
                projectService.archiveProject(email) ?: return@post call.respond(HttpStatusCode.NotFound)
                call.respond(projectService.getAdminProject(email)!!.toDTO())
            }

            post("/{email}/documents") {
                if (!call.isAdmin()) return@post call.respond(HttpStatusCode.Forbidden)
                val email = call.emailParam() ?: return@post call.respond(HttpStatusCode.BadRequest)
                call.respond(HttpStatusCode.Created, projectService.addDocument(email, call.receive<DocumentRequestDTO>().toDraft()).toDTO())
            }

            delete("/{email}/documents/{id}") {
                if (!call.isAdmin()) return@delete call.respond(HttpStatusCode.Forbidden)
                val email = call.emailParam() ?: return@delete call.respond(HttpStatusCode.BadRequest)
                val id = call.idParam() ?: return@delete call.respond(HttpStatusCode.BadRequest)
                if (projectService.deleteDocument(email, id)) call.respond(HttpStatusCode.NoContent) else call.respond(HttpStatusCode.NotFound)
            }

            post("/{email}/milestones") {
                if (!call.isAdmin()) return@post call.respond(HttpStatusCode.Forbidden)
                val email = call.emailParam() ?: return@post call.respond(HttpStatusCode.BadRequest)
                call.respond(HttpStatusCode.Created, projectService.addMilestone(email, call.receive<MilestoneRequestDTO>().toDraft()).toDTO())
            }

            patch("/{email}/milestones/{id}") {
                if (!call.isAdmin()) return@patch call.respond(HttpStatusCode.Forbidden)
                val email = call.emailParam() ?: return@patch call.respond(HttpStatusCode.BadRequest)
                val id = call.idParam() ?: return@patch call.respond(HttpStatusCode.BadRequest)
                val milestone = projectService.updateMilestone(email, id, call.receive<MilestoneRequestDTO>().toDraft())
                    ?: return@patch call.respond(HttpStatusCode.NotFound)
                call.respond(milestone.toDTO())
            }

            delete("/{email}/milestones/{id}") {
                if (!call.isAdmin()) return@delete call.respond(HttpStatusCode.Forbidden)
                val email = call.emailParam() ?: return@delete call.respond(HttpStatusCode.BadRequest)
                val id = call.idParam() ?: return@delete call.respond(HttpStatusCode.BadRequest)
                if (projectService.deleteMilestone(email, id)) call.respond(HttpStatusCode.NoContent) else call.respond(HttpStatusCode.NotFound)
            }

            post("/{email}/demos") {
                if (!call.isAdmin()) return@post call.respond(HttpStatusCode.Forbidden)
                val email = call.emailParam() ?: return@post call.respond(HttpStatusCode.BadRequest)
                call.respond(HttpStatusCode.Created, projectService.addDemo(email, call.receive<DemoRequestDTO>().toDraft()).toDTO())
            }

            patch("/{email}/demos/{id}") {
                if (!call.isAdmin()) return@patch call.respond(HttpStatusCode.Forbidden)
                val email = call.emailParam() ?: return@patch call.respond(HttpStatusCode.BadRequest)
                val id = call.idParam() ?: return@patch call.respond(HttpStatusCode.BadRequest)
                val demo = projectService.updateDemo(email, id, call.receive<DemoRequestDTO>().toDraft())
                    ?: return@patch call.respond(HttpStatusCode.NotFound)
                call.respond(demo.toDTO())
            }

            delete("/{email}/demos/{id}") {
                if (!call.isAdmin()) return@delete call.respond(HttpStatusCode.Forbidden)
                val email = call.emailParam() ?: return@delete call.respond(HttpStatusCode.BadRequest)
                val id = call.idParam() ?: return@delete call.respond(HttpStatusCode.BadRequest)
                if (projectService.deleteDemo(email, id)) call.respond(HttpStatusCode.NoContent) else call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}

private fun ApplicationCall.emailParam(): String? = parameters["email"]?.takeIf { it.isNotBlank() }

private fun ApplicationCall.idParam(): Long? = parameters["id"]?.toLongOrNull()
