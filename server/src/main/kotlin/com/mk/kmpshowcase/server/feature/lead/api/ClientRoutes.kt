package com.mk.kmpshowcase.server.feature.lead.api

import com.mk.kmpshowcase.contracts.ApiVersion
import com.mk.kmpshowcase.server.core.auth.email
import com.mk.kmpshowcase.server.feature.lead.service.LeadService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

// The logged-in client's own engagement. Scoped to the caller's email from the JWT — a client can
// only ever see their own (object-level authz at the identity level, not a path param).
internal fun Route.clientRoutes(leadService: LeadService) {
    authenticate("auth-jwt") {
        route("${ApiVersion.BASE}/me") {
            get("/engagement") {
                val email = call.email() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val engagement = leadService.getClientEngagement(email)
                    ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(engagement.toDTO())
            }
        }
    }
}
