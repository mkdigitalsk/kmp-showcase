package com.mk.kmpshowcase.server.core.auth

import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal

internal fun ApplicationCall.userId(): Long? =
    principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()

internal fun ApplicationCall.email(): String? =
    principal<JWTPrincipal>()?.payload?.getClaim("email")?.asString()

internal fun ApplicationCall.role(): String? =
    principal<JWTPrincipal>()?.payload?.getClaim("role")?.asString()

internal fun ApplicationCall.isAdmin(): Boolean = role() == "ADMIN"
