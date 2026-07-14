package com.mk.kmpshowcase.server.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.hsts.HSTS

// OWASP Secure Headers baseline for a JSON API (backend-conventions §6; OWASP Secure Headers Project + Cheat Sheet).
// Deliberate deviations, all grounded:
//  - COOP/COEP/CORP omitted — OWASP: "very related to browsers, may not make sense for REST APIs"; cross-origin
//    access here is already governed by CORS.
//  - `no-referrer` is stricter than OWASP's `strict-origin-when-cross-origin` — an API needn't emit referrers.
//  - No HSTS `preload` — a hard-to-reverse domain-wide commitment; left to a deliberate infra decision.
// XForwardedHeaders (configureRateLimit) lets HSTS see the real HTTPS scheme behind Railway's TLS proxy.
internal fun Application.configureSecurityHeaders() {
    install(HSTS) {
        maxAgeInSeconds = HSTS_MAX_AGE_SECONDS
        includeSubDomains = true
    }
    install(DefaultHeaders) {
        header("X-Content-Type-Options", "nosniff")
        header("Referrer-Policy", "no-referrer")
        header("X-Frame-Options", "DENY")
        header("Content-Security-Policy", "default-src 'none'; frame-ancestors 'none'")
        header("Permissions-Policy", "geolocation=(), microphone=(), camera=(), browsing-topics=()")
        // Authenticated JSON API — never let a browser or shared cache retain a response.
        header("Cache-Control", "no-store")
    }
}

private const val HSTS_MAX_AGE_SECONDS = 63_072_000L // 2 years (OWASP recommended)
