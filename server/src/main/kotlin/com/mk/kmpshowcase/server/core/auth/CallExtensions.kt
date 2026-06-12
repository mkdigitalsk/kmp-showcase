package com.mk.kmpshowcase.server.core.auth

import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal

fun ApplicationCall.userId(): Long? =
    principal<JWTPrincipal>()?.payload?.getClaim("userId")?.asLong()
