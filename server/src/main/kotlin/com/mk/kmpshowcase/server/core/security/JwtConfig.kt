package com.mk.kmpshowcase.server.core.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import java.util.Date

internal class JwtConfig(
    secret: String,
    private val issuer: String,
    private val audience: String,
) {
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

    private companion object {
        const val VALIDITY_IN_MS: Long = 3600_000L * 24
    }
}
