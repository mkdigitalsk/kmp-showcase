package com.mk.kmpshowcase.server.feature.lead.api

import com.mk.kmpshowcase.contracts.ApiVersion
import com.mk.kmpshowcase.server.feature.lead.service.LeadService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

// Public — the portfolio contact form posts here (replaces the Web3Forms dependency).
internal fun Route.leadRoutes(leadService: LeadService) {
    route("${ApiVersion.BASE}/leads") {
        post {
            val request = call.receive<LeadRequestDTO>()
            leadService.submit(request.toDraft())
            call.respond(HttpStatusCode.Created, LeadResponseDTO(success = true))
        }
    }
}
