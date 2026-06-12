package com.mk.kmpshowcase.server.core.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

object JwtConfig {
    private val secret = System.getenv("JWT_SECRET") ?: devFallbackSecret()
    private val issuer = System.getenv("JWT_ISSUER") ?: "kmp-showcase"
    private val audience = System.getenv("JWT_AUDIENCE") ?: "kmp-showcase-users"
    private const val VALIDITY_IN_MS: Long = 3600_000L * 24

    private fun devFallbackSecret(): String {
        val isDevelopment = System.getenv("USE_H2")?.toBoolean() ?: true
        check(isDevelopment) { "JWT_SECRET must be set when running against a production database" }
        return "development-only-secret"
    }

    private val algorithm = Algorithm.HMAC256(secret)

    val verifier: JWTVerifier = JWT.require(algorithm)
        .withIssuer(issuer)
        .withAudience(audience)
        .build()

    fun generateToken(userId: Long, email: String): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withAudience(audience)
        .withClaim("userId", userId)
        .withClaim("email", email)
        .withExpiresAt(Date(System.currentTimeMillis() + VALIDITY_IN_MS))
        .sign(algorithm)
}
