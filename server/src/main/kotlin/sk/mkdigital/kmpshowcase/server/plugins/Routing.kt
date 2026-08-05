package sk.mkdigital.kmpshowcase.server.plugins

import sk.mkdigital.kmpshowcase.server.di.AppDependencies
import sk.mkdigital.kmpshowcase.server.feature.user.api.authRoutes
import sk.mkdigital.kmpshowcase.server.feature.user.api.userRoutes
import io.ktor.server.application.Application
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
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

        apiRoutes(dependencies)
    }
}

private fun Route.apiRoutes(dependencies: AppDependencies) {
    rateLimit(ApiRateLimit) {
        authRoutes(dependencies.userService, dependencies.jwtConfig)
        userRoutes(dependencies.userService)
    }
}
