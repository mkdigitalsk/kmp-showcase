package com.mk.kmpshowcase.server.feature.admin.api

import com.mk.kmpshowcase.contracts.ApiVersion
import com.mk.kmpshowcase.server.core.auth.isAdmin
import com.mk.kmpshowcase.server.feature.lead.api.toDTO
import com.mk.kmpshowcase.server.feature.lead.service.LeadArtifactStage
import com.mk.kmpshowcase.server.feature.lead.service.LeadService
import com.mk.kmpshowcase.server.feature.lead.service.LeadStatus
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
import io.ktor.server.routing.put
import io.ktor.server.routing.route

// Admin-only. Role is enforced server-side per endpoint — a non-admin token never reaches the data.
internal fun Route.adminRoutes(leadService: LeadService) {
    route("${ApiVersion.BASE}/admin") {
        authenticate("auth-jwt") {
            get("/leads") {
                if (!call.isAdmin()) return@get call.respond(HttpStatusCode.Forbidden)
                call.respond(leadService.getAll().map { it.toAdminLeadDTO() })
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

            get("/leads/{email}/engagement") {
                if (!call.isAdmin()) return@get call.respond(HttpStatusCode.Forbidden)
                val email = call.emailParam() ?: return@get call.respond(HttpStatusCode.BadRequest)
                call.respond(leadService.getEngagement(email).toDTO())
            }

            // Read-only "view as client" — the same client-safe projection the client gets, admin-gated.
            // Best practice over impersonation: no session takeover, no over-exposure (already client-safe).
            get("/leads/{email}/client-preview") {
                if (!call.isAdmin()) return@get call.respond(HttpStatusCode.Forbidden)
                val email = call.emailParam() ?: return@get call.respond(HttpStatusCode.BadRequest)
                val engagement = leadService.getClientEngagement(email)
                    ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(engagement.toDTO())
            }

            post("/leads/{email}/milestones") {
                if (!call.isAdmin()) return@post call.respond(HttpStatusCode.Forbidden)
                val email = call.emailParam() ?: return@post call.respond(HttpStatusCode.BadRequest)
                val milestone = leadService.addMilestone(email, call.receive<MilestoneRequestDTO>().toDraft())
                call.respond(HttpStatusCode.Created, milestone.toDTO())
            }

            patch("/leads/{email}/milestones/{id}") {
                if (!call.isAdmin()) return@patch call.respond(HttpStatusCode.Forbidden)
                val id = call.idParam() ?: return@patch call.respond(HttpStatusCode.BadRequest)
                val milestone = leadService.updateMilestone(id, call.receive<MilestoneRequestDTO>().toDraft())
                    ?: return@patch call.respond(HttpStatusCode.NotFound)
                call.respond(milestone.toDTO())
            }

            delete("/leads/{email}/milestones/{id}") {
                if (!call.isAdmin()) return@delete call.respond(HttpStatusCode.Forbidden)
                val id = call.idParam() ?: return@delete call.respond(HttpStatusCode.BadRequest)
                if (leadService.deleteMilestone(id)) call.respond(HttpStatusCode.NoContent)
                else call.respond(HttpStatusCode.NotFound)
            }

            post("/leads/{email}/demos") {
                if (!call.isAdmin()) return@post call.respond(HttpStatusCode.Forbidden)
                val email = call.emailParam() ?: return@post call.respond(HttpStatusCode.BadRequest)
                val demo = leadService.addDemo(email, call.receive<DemoRequestDTO>().toDraft())
                call.respond(HttpStatusCode.Created, demo.toDTO())
            }

            patch("/leads/{email}/demos/{id}") {
                if (!call.isAdmin()) return@patch call.respond(HttpStatusCode.Forbidden)
                val id = call.idParam() ?: return@patch call.respond(HttpStatusCode.BadRequest)
                val demo = leadService.updateDemo(id, call.receive<DemoRequestDTO>().toDraft())
                    ?: return@patch call.respond(HttpStatusCode.NotFound)
                call.respond(demo.toDTO())
            }

            delete("/leads/{email}/demos/{id}") {
                if (!call.isAdmin()) return@delete call.respond(HttpStatusCode.Forbidden)
                val id = call.idParam() ?: return@delete call.respond(HttpStatusCode.BadRequest)
                if (leadService.deleteDemo(id)) call.respond(HttpStatusCode.NoContent)
                else call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}

private fun ApplicationCall.emailParam(): String? = parameters["email"]?.takeIf { it.isNotBlank() }

private fun ApplicationCall.idParam(): Long? = parameters["id"]?.toLongOrNull()
