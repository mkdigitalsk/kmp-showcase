package com.mk.kmpshowcase.server.feature.project.api

import com.mk.kmpshowcase.contracts.ApiVersion
import com.mk.kmpshowcase.server.core.auth.email
import com.mk.kmpshowcase.server.feature.project.service.ProjectService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

// The logged-in client's own project — scoped to the caller's JWT email, never a path param.
internal fun Route.clientProjectRoutes(projectService: ProjectService) {
    authenticate("auth-jwt") {
        route("${ApiVersion.BASE}/me") {
            get("/project") {
                val email = call.email() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val project = projectService.getClientProject(email)
                    ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(project.toDTO())
            }
        }
    }
}
