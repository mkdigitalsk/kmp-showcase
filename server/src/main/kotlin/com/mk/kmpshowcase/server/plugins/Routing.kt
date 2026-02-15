package com.mk.kmpshowcase.server.plugins

import com.mk.kmpshowcase.server.routes.authRoutes
import com.mk.kmpshowcase.server.routes.notesRoutes
import com.mk.kmpshowcase.server.routes.usersRoutes
import io.ktor.server.application.Application
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("KMP Showcase API")
        }

        get("/health") {
            call.respondText("OK")
        }

        authRoutes()
        usersRoutes()
        notesRoutes()
    }
}
