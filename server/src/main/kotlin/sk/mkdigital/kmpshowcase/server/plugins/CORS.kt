package sk.mkdigital.kmpshowcase.server.plugins

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

        if (allowsAnyHost(allowedHosts)) {
            anyHost()
        } else {
            allowedHosts.split(",")
                .map { it.trim().removePrefix("https://").removePrefix("http://").trimEnd('/') }
                .filter { it.isNotEmpty() }
                .forEach { host -> allowHost(host, schemes = listOf("https")) }
        }
    }
}

/**
 * `*` or unset — local dev and staging, whose callers are Vercel preview URLs with unpredictable hashes.
 * Production names its hosts in CORS_ALLOWED_HOSTS.
 */
private fun allowsAnyHost(allowedHosts: String) = allowedHosts.isBlank() || allowedHosts == "*"
