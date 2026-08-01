package com.mk.kmpshowcase.server

import com.mk.kmpshowcase.server.plugins.configureSecurityHeaders
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SecurityHeadersTest {

    // HSTS is intentionally not asserted: it is only emitted over HTTPS, and testApplication runs over
    // plain HTTP (no X-Forwarded-Proto), so its absence here is correct, not a regression.
    @Test
    fun `every response carries the OWASP security header baseline`() = testApplication {
        application {
            configureSecurityHeaders()
            routing { get("/probe") { call.respondText("ok") } }
        }

        val response = client.get("/probe")

        assertEquals(HttpStatusCode.OK, response.status)
        assertEquals("nosniff", response.headers["X-Content-Type-Options"])
        assertEquals("DENY", response.headers["X-Frame-Options"])
        assertEquals("no-referrer", response.headers["Referrer-Policy"])
        assertTrue(
            response.headers["Content-Security-Policy"]?.contains("default-src 'none'") == true,
            "CSP must lock the API down to default-src 'none'",
        )
        assertTrue(
            response.headers["Permissions-Policy"]?.contains("camera=()") == true,
            "Permissions-Policy must deny sensitive features",
        )
        assertEquals("no-store", response.headers["Cache-Control"], "authenticated API responses must not be cached")
    }
}
