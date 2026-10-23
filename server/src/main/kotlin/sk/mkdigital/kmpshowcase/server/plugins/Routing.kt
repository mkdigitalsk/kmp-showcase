package sk.mkdigital.kmpshowcase.server.plugins

import sk.mkdigital.kmpshowcase.server.di.AppDependencies
import sk.mkdigital.kmpshowcase.server.feature.note.api.noteRoutes
import sk.mkdigital.kmpshowcase.server.feature.user.api.authRoutes
import sk.mkdigital.kmpshowcase.server.feature.user.api.userRoutes
import io.ktor.http.HttpStatusCode
import io.ktor.openapi.OpenApiDoc
import io.ktor.openapi.OpenApiInfo
import io.ktor.server.application.Application
import io.ktor.server.application.plugin
import io.ktor.server.http.content.staticResources
import io.ktor.server.plugins.ratelimit.rateLimit
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingRoot
import io.ktor.server.routing.get
import io.ktor.server.routing.getAllRoutes
import io.ktor.server.routing.openapi.describe
import io.ktor.server.routing.openapi.findSecuritySchemes
import io.ktor.server.routing.openapi.plus
import io.ktor.server.routing.routing
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

internal fun Application.configureRouting(dependencies: AppDependencies) {
    routing {
        get("/") {
            call.respondText("KMP Showcase API")
        }

        get("/health") {
            call.respondText("OK")
        }

        get("/purge-status") {
            val overdue = dependencies.inactiveAccountPurge.isOverdue()
            call.respondText(
                text = if (overdue) "OVERDUE" else "OK",
                status = if (overdue) HttpStatusCode.ServiceUnavailable else HttpStatusCode.OK,
            )
        }.describe {
            summary = "Whether the inactive-account sweep runs on time, as a status any uptime monitor alerts on."
            responses {
                HttpStatusCode.ServiceUnavailable {
                    description = "The sweep is overdue — a job that stops running raises no error of its own."
                }
            }
        }

        staticResources("/assets", "assets")

        apiRoutes(dependencies)
    }
}

private fun Route.apiRoutes(dependencies: AppDependencies) {
    rateLimit(ApiRateLimit) {
        get(SPEC_PATH) {
            call.respond(call.application.spec())
        }
        authRoutes(dependencies.userService, dependencies.jwtConfig)
        userRoutes(dependencies.userService)
        noteRoutes(dependencies.noteService)
    }
}

/**
 * The document the compiler plugin infers from the routing tree — encoded by a plain [Json], since the app's own
 * encodes defaults and would write every field the document leaves unset as a `null`.
 */
private fun Application.spec(): JsonObject {
    val document = OpenApiDoc(info = OpenApiInfo(title = "KMP Showcase API", version = "1.0")) +
        findSecuritySchemes() + describedRoutes()
    return Json.encodeToJsonElement(OpenApiDoc.serializer(), document).jsonObject
}

/** Every route but the specification's own, which would describe itself as one more path and a `JsonElement` schema. */
private fun Application.describedRoutes(): List<Route> =
    plugin(RoutingRoot).getAllRoutes().filterNot { it.toString().contains(SPEC_PATH) }

private const val SPEC_PATH = "/openapi.json"
