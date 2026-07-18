package com.mk.kmpshowcase.server.feature.lead.api

import com.mk.kmpshowcase.contracts.ApiVersion
import com.mk.kmpshowcase.server.feature.lead.service.LeadService
import com.mk.kmpshowcase.server.plugins.LeadSubmitRateLimit
import io.ktor.http.HttpStatusCode
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

// Public (unauthenticated) — the portfolio contact form posts here. Strict per-IP limiter: an
// unauthenticated write endpoint is an abusable flow (backend-conventions §6).
internal fun Route.leadRoutes(leadService: LeadService) {
    route("${ApiVersion.BASE}/leads") {
        rateLimit(LeadSubmitRateLimit) {
            post {
                val request = call.receive<LeadRequestDTO>()
                leadService.submit(request.toDraft())
                call.respond(HttpStatusCode.Created, LeadResponseDTO(success = true))
            }
        }
    }
}
