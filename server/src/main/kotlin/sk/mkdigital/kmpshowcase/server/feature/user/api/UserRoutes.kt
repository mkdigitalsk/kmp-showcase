package sk.mkdigital.kmpshowcase.server.feature.user.api

import sk.mkdigital.kmpshowcase.contracts.ApiVersion
import sk.mkdigital.kmpshowcase.contracts.user.UpdateLocaleRequestDTO
import sk.mkdigital.kmpshowcase.contracts.user.UpdateThemeModeRequestDTO
import sk.mkdigital.kmpshowcase.server.core.auth.userId
import sk.mkdigital.kmpshowcase.server.feature.user.service.UserService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.put
import io.ktor.server.routing.route

internal fun Route.userRoutes(userService: UserService) {
    route("${ApiVersion.BASE}/users") {
        authenticate("auth-jwt") {
            get {
                call.respond(userService.getAll().map { it.toUserResponseDTO() })
            }
            get("/me") {
                val userId = call.userId() ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val user = userService.getById(userId) ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(user.toUserResponseDTO())
            }
            put("/me/theme-mode") {
                val userId = call.userId() ?: return@put call.respond(HttpStatusCode.Unauthorized)
                val request = call.receive<UpdateThemeModeRequestDTO>()
                val user = userService.updateThemeMode(userId, request.themeMode.toThemeMode())
                    ?: return@put call.respond(HttpStatusCode.NotFound)
                call.respond(user.toUserResponseDTO())
            }
            put("/me/locale") {
                val userId = call.userId() ?: return@put call.respond(HttpStatusCode.Unauthorized)
                val request = call.receive<UpdateLocaleRequestDTO>()
                val user = userService.updateLocale(userId, request.locale)
                    ?: return@put call.respond(HttpStatusCode.NotFound)
                call.respond(user.toUserResponseDTO())
            }
        }
    }
}
