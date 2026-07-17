package com.mk.kmpshowcase.server.plugins

import com.mk.kmpshowcase.server.di.AppDependencies
import com.mk.kmpshowcase.server.feature.admin.api.adminRoutes
import com.mk.kmpshowcase.server.feature.project.api.documentRoutes
import com.mk.kmpshowcase.server.feature.lead.api.leadRoutes
import com.mk.kmpshowcase.server.feature.project.api.adminProjectRoutes
import com.mk.kmpshowcase.server.feature.project.api.clientProjectRoutes
import com.mk.kmpshowcase.server.feature.note.api.noteRoutes
import com.mk.kmpshowcase.server.feature.user.api.authRoutes
import com.mk.kmpshowcase.server.feature.user.api.userRoutes
import io.ktor.server.application.Application
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.routing

internal fun Application.configureRouting(dependencies: AppDependencies) {
    routing {
        get("/") {
            call.respondText("KMP Showcase API")
        }

        get("/health") {
            call.respondText("OK")
        }

        // Public static assets (e.g. the email logo PNG) — no auth.
        staticResources("/assets", "assets")

        apiRoutes(dependencies)
        // Legacy /api/v1 alias — deprecated. Kept so already-shipped clients (old railway host,
        // installed mobile builds) keep working during the custom-domain + /v1 migration.
        // TODO: remove once every client ships on /v1.
        route("/api") { apiRoutes(dependencies) }
    }
}

private fun Route.apiRoutes(dependencies: AppDependencies) {
    rateLimit(ApiRateLimit) {
        authRoutes(dependencies.userService, dependencies.inviteService, dependencies.jwtConfig)
        userRoutes(dependencies.userService)
        noteRoutes(dependencies.noteService)
        leadRoutes(dependencies.leadService)
        adminRoutes(dependencies.leadService, dependencies.projectService, dependencies.inviteService)
        documentRoutes(dependencies.projectService)
        clientProjectRoutes(dependencies.projectService)
        adminProjectRoutes(dependencies.projectService)
    }
}
