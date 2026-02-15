package com.mk.kmpshowcase.server.routes

import com.mk.kmpshowcase.server.repository.UserRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

fun Route.usersRoutes() {
    val userRepository = UserRepository()

    route("/api/users") {
        authenticate("auth-jwt") {
            get("/me") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asLong()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)

                val user = userRepository.findById(userId)
                    ?: return@get call.respond(HttpStatusCode.NotFound)

                call.respond(user)
            }
        }
    }
}
