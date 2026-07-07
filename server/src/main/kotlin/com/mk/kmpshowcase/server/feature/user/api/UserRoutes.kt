package com.mk.kmpshowcase.server.feature.user.api

import com.mk.kmpshowcase.contracts.ApiVersion
import com.mk.kmpshowcase.contracts.user.UpdateLocaleRequestDTO
import com.mk.kmpshowcase.contracts.user.UpdateThemeModeRequestDTO
import com.mk.kmpshowcase.server.core.auth.role
import com.mk.kmpshowcase.server.core.auth.userId
import com.mk.kmpshowcase.server.feature.user.service.Role
import com.mk.kmpshowcase.server.feature.user.service.UserService
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
                val role = call.role()?.let { runCatching { Role.valueOf(it) }.getOrNull() }
                    ?: return@get call.respond(HttpStatusCode.Forbidden)
                call.respond(userService.getVisibleTo(role).map { it.toUserResponseDTO() })
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
