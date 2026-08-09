package sk.mkdigital.kmpshowcase.server.feature.user.api

import sk.mkdigital.kmpshowcase.contracts.ApiVersion
import sk.mkdigital.kmpshowcase.contracts.auth.AuthResponseDTO
import sk.mkdigital.kmpshowcase.contracts.auth.SignInRequestDTO
import sk.mkdigital.kmpshowcase.contracts.auth.SignUpRequestDTO
import sk.mkdigital.kmpshowcase.server.core.auth.userId
import sk.mkdigital.kmpshowcase.server.core.maskEmail
import sk.mkdigital.kmpshowcase.server.core.security.JwtConfig
import sk.mkdigital.kmpshowcase.server.feature.user.service.UserService
import sk.mkdigital.kmpshowcase.server.plugins.AuthRateLimit
import io.ktor.http.HttpHeaders
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

internal fun Route.authRoutes(userService: UserService, jwtConfig: JwtConfig) {
    route("${ApiVersion.BASE}/auth") {
        // Throttle the credential-accepting endpoints per client IP (brute-force / spray defense).
        rateLimit(AuthRateLimit) {
            post("/sign-up") {
                val request = call.receive<SignUpRequestDTO>()
                val user = userService.signUp(request.email, request.password)
                val token = jwtConfig.generateToken(user.id, user.email)
                logger.info("User signed up: ${user.id} (${user.email.maskEmail()})")
                call.response.headers.append(HttpHeaders.Location, "${ApiVersion.BASE}/users/me")
                call.respond(HttpStatusCode.Created, AuthResponseDTO(token, user.toAuthUserDTO()))
            }

            post("/sign-in") {
                val request = call.receive<SignInRequestDTO>()
                val user = userService.authenticate(request.email, request.password)
                    ?: run {
                        logger.warn("SignIn failed: invalid credentials for ${request.email.maskEmail()}")
                        call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Invalid credentials"))
                        return@post
                    }
                val token = jwtConfig.generateToken(user.id, user.email)
                logger.info("User logged in: ${user.id} (${user.email.maskEmail()})")
                call.respond(AuthResponseDTO(token, user.toAuthUserDTO()))
            }
        }

        authenticate("auth-jwt") {
            // POST, not GET: this issues a new bearer, and a private cache may store a GET response
            // carrying one (RFC 9110 §9.2.1, RFC 9111 §3.5). Read the user from GET /v1/users/me.
            post("/token") {
                val userId = call.userId()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val user = userService.getById(userId)
                    ?: return@post call.respond(HttpStatusCode.NotFound)
                logger.info("Token renewed: ${user.id} (${user.email.maskEmail()})")
                call.respond(AuthResponseDTO(jwtConfig.generateToken(user.id, user.email), user.toAuthUserDTO()))
            }
        }
    }
}
