package com.mk.kmpshowcase.server.feature.admin.api

import com.mk.kmpshowcase.contracts.ApiVersion
import com.mk.kmpshowcase.server.core.auth.isAdmin
import com.mk.kmpshowcase.server.feature.lead.service.LeadArtifactStage
import com.mk.kmpshowcase.server.feature.lead.service.LeadService
import com.mk.kmpshowcase.server.feature.lead.service.LeadStatus
import com.mk.kmpshowcase.server.feature.project.service.ProjectService
import com.mk.kmpshowcase.server.feature.user.service.InviteService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

// Admin-only sales CRM (leads + pipeline artifacts). Role is enforced server-side per endpoint — a
// non-admin token never reaches the data. Delivery lives in feature/project (adminProjectRoutes).
internal fun Route.adminRoutes(leadService: LeadService, projectService: ProjectService, inviteService: InviteService) {
    route("${ApiVersion.BASE}/admin") {
        authenticate("auth-jwt") {
            // Portal is invite-only — send (or re-send, replacing the token) the client's access invite.
            post("/clients/{email}/invite") {
                if (!call.isAdmin()) return@post call.respond(HttpStatusCode.Forbidden)
                val email = call.emailParam() ?: return@post call.respond(HttpStatusCode.BadRequest)
                val lead = leadService.getByEmail(email)?.lead
                inviteService.invite(email, lead?.name, lead?.locale)
                call.respond(HttpStatusCode.NoContent)
            }

            get("/leads") {
                if (!call.isAdmin()) return@get call.respond(HttpStatusCode.Forbidden)
                call.respond(leadService.getAll().map { it.toAdminLeadDTO() })
            }

            // Clients = won leads joined with their delivery project's summary (state + health).
            get("/clients") {
                if (!call.isAdmin()) return@get call.respond(HttpStatusCode.Forbidden)
                val clients = leadService.getAll()
                    .filter { it.status == LeadStatus.WON }
                    .map { lead ->
                        val p = projectService.getAdminProject(lead.email)?.project
                        AdminClientDTO(lead.toAdminLeadDTO(), p?.state?.name, p?.health?.name)
                    }
                call.respond(clients)
            }

            get("/leads/{email}") {
                if (!call.isAdmin()) return@get call.respond(HttpStatusCode.Forbidden)
                val email = call.emailParam() ?: return@get call.respond(HttpStatusCode.BadRequest)
                val detail = leadService.getByEmail(email) ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(detail.toDTO())
            }

            patch("/leads/{email}/status") {
                if (!call.isAdmin()) return@patch call.respond(HttpStatusCode.Forbidden)
                val email = call.emailParam() ?: return@patch call.respond(HttpStatusCode.BadRequest)
                val status = runCatching { LeadStatus.valueOf(call.receive<UpdateStatusRequestDTO>().status) }
                    .getOrNull() ?: return@patch call.respond(HttpStatusCode.BadRequest)
                val updated = leadService.updateStatus(email, status) ?: return@patch call.respond(HttpStatusCode.NotFound)
                call.respond(updated.toAdminLeadDTO())
            }

            put("/leads/{email}/artifacts/{stage}") {
                if (!call.isAdmin()) return@put call.respond(HttpStatusCode.Forbidden)
                val email = call.emailParam() ?: return@put call.respond(HttpStatusCode.BadRequest)
                val stage = runCatching { LeadArtifactStage.valueOf(call.parameters["stage"]!!.uppercase()) }
                    .getOrNull() ?: return@put call.respond(HttpStatusCode.BadRequest)
                leadService.saveArtifact(email, stage, call.receive<SaveArtifactRequestDTO>().content)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}

private fun ApplicationCall.emailParam(): String? = parameters["email"]?.takeIf { it.isNotBlank() }
