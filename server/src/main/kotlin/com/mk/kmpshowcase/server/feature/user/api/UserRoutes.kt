package com.mk.kmpshowcase.server.feature.user.api

import com.mk.kmpshowcase.server.core.auth.userId
import com.mk.kmpshowcase.server.feature.user.service.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

fun Route.userRoutes(userService: UserService) {
    route("/api/users") {
        authenticate("auth-jwt") {
            get("/me") {
                val userId = call.userId() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val user = userService.getById(userId) ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(user.toDTO())
            }
        }
    }
}
