package sk.mkdigital.kmpshowcase.server.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.plugins.forwardedheaders.XForwardedHeaders
import io.ktor.server.plugins.origin
import io.ktor.server.plugins.ratelimit.RateLimit
import io.ktor.server.plugins.ratelimit.RateLimitName
import java.security.MessageDigest
import kotlin.time.Duration.Companion.seconds

/** Baseline for every API endpoint — generous; stops scraping / runaway clients / light DoS. */
internal val ApiRateLimit = RateLimitName("api")

/** Strict, credential endpoints only — brute-force / password-spray defense (nested under API). */
internal val AuthRateLimit = RateLimitName("auth")

/** The header the web app's own proxy puts the visitor's address in; it counts only beside [PROXY_KEY_HEADER]. */
internal const val VISITOR_IP_HEADER = "X-Visitor-IP"

/** The shared key the web proxy proves itself with — `PROXY_KEY`, the same value on the API and on the proxy. */
internal const val PROXY_KEY_HEADER = "X-Proxy-Key"

/**
 * Behind Railway's proxy every request's remoteHost is the proxy IP; the real client IP is read from
 * X-Forwarded-For so the limiter buckets per client, not per proxy. `useLastProxy` because X-Forwarded-For
 * is append-only, so the default (`useFirstProxy`) reads an entry the caller can forge — even behind our
 * own proxy.
 *
 * ⚠ The buckets live in each instance's memory, so N instances allow N times each limit.
 *
 * @param proxyKey the key the web proxy presents; unset or blank, no request may name its own visitor.
 */
internal fun Application.configureRateLimit(proxyKey: String? = null) {
    install(XForwardedHeaders) { useLastProxy() }
    install(RateLimit) {
        register(ApiRateLimit) {
            rateLimiter(limit = API_LIMIT, refillPeriod = WINDOW_SECONDS.seconds)
            requestKey { call -> call.rateLimitKey(proxyKey) }
        }
        register(AuthRateLimit) {
            rateLimiter(limit = AUTH_LIMIT, refillPeriod = WINDOW_SECONDS.seconds)
            requestKey { call -> call.rateLimitKey(proxyKey) }
        }
    }
}

/**
 * The address a request is counted against. Every visitor of the web app arrives from the platform's egress, so
 * the web proxy names the visitor and proves itself with the shared key; anything else — the mobile apps among
 * them — is counted on the connection, which Railway's edge sets and a caller cannot forge.
 */
internal fun ApplicationCall.rateLimitKey(proxyKey: String?): String {
    val visitor = request.headers[VISITOR_IP_HEADER]
    return if (visitor != null && presentsProxyKey(proxyKey)) visitor else request.origin.remoteHost
}

/** Compared in constant time, so the key cannot be recovered a character at a time from response timings. */
private fun ApplicationCall.presentsProxyKey(proxyKey: String?): Boolean {
    val presented = request.headers[PROXY_KEY_HEADER] ?: return false
    if (proxyKey.isNullOrBlank()) return false
    return MessageDigest.isEqual(presented.toByteArray(), proxyKey.toByteArray())
}

private const val API_LIMIT = 120
private const val AUTH_LIMIT = 10
private const val WINDOW_SECONDS = 60
