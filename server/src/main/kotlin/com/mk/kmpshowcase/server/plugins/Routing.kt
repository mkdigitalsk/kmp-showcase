package com.mk.kmpshowcase.server.plugins

import com.mk.kmpshowcase.server.di.AppDependencies
import com.mk.kmpshowcase.server.feature.note.api.noteRoutes
import com.mk.kmpshowcase.server.feature.user.api.authRoutes
import com.mk.kmpshowcase.server.feature.user.api.userRoutes
import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting(dependencies: AppDependencies) {
    routing {
        get("/") {
            call.respondText("KMP Showcase API")
        }

        get("/health") {
            call.respondText("OK")
        }

        authRoutes(dependencies.userService)
        userRoutes(dependencies.userService)
        noteRoutes(dependencies.noteService)
    }
}
