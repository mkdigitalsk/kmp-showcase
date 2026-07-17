package com.mk.kmpshowcase.server.feature.user.service

import com.mk.kmpshowcase.server.core.mail.Mailer
import com.mk.kmpshowcase.server.feature.user.persistence.InviteRepository
import com.mk.kmpshowcase.server.feature.user.persistence.UserRepository
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

// Portal access is INVITE-ONLY (no self-registration). Admin invites a client → tokenized email →
// the client sets a password via accept, which creates the CLIENT user and consumes the invite.
// Only the SHA-256 of the token is stored; the raw token exists in the email link alone.
internal class InviteService(
    private val inviteRepository: InviteRepository,
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val mailer: Mailer,
    private val portalBaseUrl: String,
    private val mailScope: CoroutineScope,
) {
    private val logger = LoggerFactory.getLogger(InviteService::class.java)

    // Re-inviting replaces the pending token (old link dies). Returns the raw token (route never exposes it).
    suspend fun invite(email: String, name: String?, locale: String?): String {
        require(email.contains("@")) { "Invalid email format" }
        check(userRepository.findByEmail(email) == null) { "User already exists" }

        val token = generateToken()
        inviteRepository.upsert(email, token.sha256(), name, System.currentTimeMillis() + VALIDITY_MS)

        val link = "$portalBaseUrl/invite?token=$token"   // public route — /account/* sits behind the auth gate
        mailScope.launch {
            runCatching { mailer.send(email, InviteEmail.subject(locale), InviteEmail.text(name, link, locale)) }
                .onFailure { logger.warn("Invite email to $email failed: ${it.message}") }
        }
        return token
    }

    suspend fun accept(token: String, password: String, name: String?): User {
        val invite = inviteRepository.findByTokenHash(token.sha256())
        require(invite != null) { "Invalid or already used invite" }
        require(invite.expiresAt > System.currentTimeMillis()) { "Invite expired — ask for a new one" }
        val resolvedName = name?.takeIf { it.isNotBlank() } ?: invite.name ?: invite.email.substringBefore("@")
        val user = userService.register(invite.email, password, resolvedName)
        inviteRepository.delete(invite.email)
        return user
    }

    private fun generateToken(): String =
        ByteArray(TOKEN_BYTES).also { SecureRandom().nextBytes(it) }
            .let { Base64.getUrlEncoder().withoutPadding().encodeToString(it) }

    private fun String.sha256(): String =
        MessageDigest.getInstance("SHA-256").digest(toByteArray()).joinToString("") { "%02x".format(it) }

    private companion object {
        const val TOKEN_BYTES = 32
        const val VALIDITY_MS = 7L * 24 * 60 * 60 * 1000
    }
}
