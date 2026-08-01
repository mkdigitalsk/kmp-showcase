package com.mk.kmpshowcase.server.plugins

import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.plugins.cors.routing.CORS

internal fun Application.configureCORS(config: ApplicationConfig) {
    val allowedHosts = config.propertyOrNull("cors.allowedHosts")?.getString().orEmpty().trim()

    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowMethod(HttpMethod.Patch)

        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)

        // "*" (or unset) = permissive — for local dev and the staging env, where callers are Vercel
        // preview URLs (unpredictable hashes). Production sets CORS_ALLOWED_HOSTS to an explicit list.
        if (allowedHosts.isBlank() || allowedHosts == "*") {
            anyHost()
        } else {
            allowedHosts.split(",")
                .map { it.trim().removePrefix("https://").removePrefix("http://").trimEnd('/') }
                .filter { it.isNotEmpty() }
                .forEach { host -> allowHost(host, schemes = listOf("https")) }
        }
    }
}
