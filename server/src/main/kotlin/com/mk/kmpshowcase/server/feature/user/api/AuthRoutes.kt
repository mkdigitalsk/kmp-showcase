package com.mk.kmpshowcase.server.feature.user.api

import com.mk.kmpshowcase.contracts.ApiVersion
import com.mk.kmpshowcase.contracts.auth.AuthResponseDTO
import com.mk.kmpshowcase.contracts.auth.LoginRequestDTO
import com.mk.kmpshowcase.contracts.auth.RegisterRequestDTO
import com.mk.kmpshowcase.server.core.auth.userId
import com.mk.kmpshowcase.server.core.maskEmail
import com.mk.kmpshowcase.server.core.security.JwtConfig
import com.mk.kmpshowcase.server.feature.user.service.InviteService
import com.mk.kmpshowcase.server.feature.user.service.UserService
import com.mk.kmpshowcase.server.plugins.AuthRateLimit
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("AuthRoutes")

internal fun Route.authRoutes(userService: UserService, inviteService: InviteService, jwtConfig: JwtConfig) {
    route("${ApiVersion.BASE}/auth") {
        // Throttle the credential-accepting endpoints per client IP (brute-force / spray defense).
        rateLimit(AuthRateLimit) {
            // Portal is invite-only: consumes an invite token, creates the CLIENT user, signs them in.
            post("/accept-invite") {
                val request = call.receive<AcceptInviteRequestDTO>()
                val user = inviteService.accept(request.token, request.password, request.name)
                val token = jwtConfig.generateToken(user.id, user.email, user.role.name)
                logger.info("Invite accepted: ${user.id} (${user.email.maskEmail()})")
                call.respond(HttpStatusCode.Created, AuthResponseDTO(token, user.toAuthUserDTO()))
            }

            post("/register") {
                val request = call.receive<RegisterRequestDTO>()
                val user = userService.register(request.email, request.password, request.name)
                val token = jwtConfig.generateToken(user.id, user.email, user.role.name)
                logger.info("User registered: ${user.id} (${user.email.maskEmail()})")
                call.respond(HttpStatusCode.Created, AuthResponseDTO(token, user.toAuthUserDTO()))
            }

            post("/login") {
                val request = call.receive<LoginRequestDTO>()
                val user = userService.authenticate(request.email, request.password)
                    ?: run {
                        logger.warn("Login failed: invalid credentials for ${request.email.maskEmail()}")
                        call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Invalid credentials"))
                        return@post
                    }
                val token = jwtConfig.generateToken(user.id, user.email, user.role.name)
                logger.info("User logged in: ${user.id} (${user.email.maskEmail()})")
                call.respond(AuthResponseDTO(token, user.toAuthUserDTO()))
            }
        }

        authenticate("auth-jwt") {
            get("/me") {
                val userId = call.userId()
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val user = userService.getById(userId)
                    ?: return@get call.respond(HttpStatusCode.NotFound)
                logger.info("Token login: ${user.id} (${user.email.maskEmail()})")
                call.respond(AuthResponseDTO(jwtConfig.generateToken(user.id, user.email, user.role.name), user.toAuthUserDTO()))
            }
        }
    }
}
