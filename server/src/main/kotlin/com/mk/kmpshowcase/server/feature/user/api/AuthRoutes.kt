package com.mk.kmpshowcase.server.feature.user.api

import com.mk.kmpshowcase.server.core.security.JwtConfig
import com.mk.kmpshowcase.server.feature.user.service.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("AuthRoutes")

fun Route.authRoutes(userService: UserService) {
    route("/api/auth") {
        post("/register") {
            val request = call.receive<RegisterRequest>()
            val user = userService.register(request.email, request.password, request.name)
            val token = JwtConfig.generateToken(user.id, user.email)
            logger.info("User registered: ${user.id} (${user.email})")
            call.respond(HttpStatusCode.Created, AuthResponse(token, user.toDTO()))
        }

        post("/login") {
            val request = call.receive<LoginRequest>()
            val user = userService.authenticate(request.email, request.password)
                ?: run {
                    logger.warn("Login failed: invalid credentials for ${request.email}")
                    call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Invalid credentials"))
                    return@post
                }
            val token = JwtConfig.generateToken(user.id, user.email)
            logger.info("User logged in: ${user.id} (${user.email})")
            call.respond(AuthResponse(token, user.toDTO()))
        }
    }
}
