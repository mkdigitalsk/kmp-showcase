package com.mk.kmpshowcase.server.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.forwardedheaders.XForwardedHeaders
import io.ktor.server.plugins.origin
import io.ktor.server.plugins.ratelimit.RateLimit
import io.ktor.server.plugins.ratelimit.RateLimitName
import kotlin.time.Duration.Companion.seconds

internal val ApiRateLimit = RateLimitName("api")
internal val AuthRateLimit = RateLimitName("auth")
internal val LeadSubmitRateLimit = RateLimitName("lead-submit")

internal fun Application.configureRateLimit() {
    // Behind Railway's proxy every request's remoteHost is the proxy IP; read the real client IP
    // from X-Forwarded-For so the limiter buckets per client, not per proxy. Safe to trust here —
    // the app is only reachable through Railway's managed edge, never directly.
    install(XForwardedHeaders)
    install(RateLimit) {
        // Baseline for every API endpoint — generous; stops scraping / runaway clients / light DoS.
        register(ApiRateLimit) {
            rateLimiter(limit = API_LIMIT, refillPeriod = WINDOW_SECONDS.seconds)
            requestKey { call -> call.request.origin.remoteHost }
        }
        // Strict, credential endpoints only — brute-force / password-spray defense (nested under API).
        register(AuthRateLimit) {
            rateLimiter(limit = AUTH_LIMIT, refillPeriod = WINDOW_SECONDS.seconds)
            requestKey { call -> call.request.origin.remoteHost }
        }
        // Strict, the public lead form — an unauthenticated write endpoint is an abusable flow.
        register(LeadSubmitRateLimit) {
            rateLimiter(limit = LEAD_SUBMIT_LIMIT, refillPeriod = WINDOW_SECONDS.seconds)
            requestKey { call -> call.request.origin.remoteHost }
        }
    }
}

private const val API_LIMIT = 120
private const val AUTH_LIMIT = 10
private const val LEAD_SUBMIT_LIMIT = 5
private const val WINDOW_SECONDS = 60
