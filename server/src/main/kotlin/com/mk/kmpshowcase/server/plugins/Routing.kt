package com.mk.kmpshowcase.server.plugins

import com.mk.kmpshowcase.server.di.AppDependencies
import com.mk.kmpshowcase.server.feature.lead.api.leadRoutes
import com.mk.kmpshowcase.server.feature.note.api.noteRoutes
import com.mk.kmpshowcase.server.feature.user.api.authRoutes
import com.mk.kmpshowcase.server.feature.user.api.userRoutes
import io.ktor.server.application.Application
import io.ktor.server.http.content.staticResources
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
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

        authRoutes(dependencies.userService, dependencies.jwtConfig)
        userRoutes(dependencies.userService)
        noteRoutes(dependencies.noteService)
        leadRoutes(dependencies.leadService)
    }
}
