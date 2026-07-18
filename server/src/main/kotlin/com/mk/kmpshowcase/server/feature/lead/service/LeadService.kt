package com.mk.kmpshowcase.server.feature.lead.service

import com.mk.kmpshowcase.server.core.mail.Mailer
import com.mk.kmpshowcase.server.core.maskEmail
import com.mk.kmpshowcase.server.feature.lead.persistence.LeadRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("LeadService")
private val EMAIL_REGEX = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")

internal class LeadService(
    private val repository: LeadRepository,
    private val mailer: Mailer,
    private val recipient: String,
    private val mailScope: CoroutineScope,
) {
    suspend fun getAll(): List<Lead> = repository.findAll()

    suspend fun getByEmail(email: String): LeadDetail? {
        val lead = repository.findByEmail(email) ?: return null
        return LeadDetail(lead, repository.findArtifacts(email), repository.findEvents(email))
    }

    // Jira-style workflow: the transition must be an edge in ALLOWED_TRANSITIONS. Anything else is a
    // conflict (409) unless explicitly forced — the portal UI never forces; force is a deliberate,
    // audited override (FORCED in the ledger).
    suspend fun updateStatus(email: String, status: LeadStatus, force: Boolean = false): Lead? {
        val current = repository.findByEmail(email) ?: return null
        if (current.status == status) return current
        val allowed = status in ALLOWED_TRANSITIONS.getValue(current.status)
        check(allowed || force) { "Invalid status transition ${current.status} -> $status" }
        return repository.updateStatus(email, status)?.also {
            val detail = "${current.status} -> $status" + if (!allowed) " FORCED" else ""
            repository.appendEvent(email, LeadEventType.STATUS_CHANGED, detail)
            logger.info("Lead ${it.id} status $detail (${email.maskEmail()})")
        }
    }

    suspend fun deleteById(id: Long): Boolean =
        repository.deleteById(id).also { if (it) logger.info("Lead $id deleted") }

    suspend fun saveArtifact(email: String, stage: LeadArtifactStage, content: String) {
        repository.upsertArtifact(email, stage, content)
        repository.appendEvent(email, LeadEventType.ARTIFACT_SAVED, stage.name)
    }

    // Operator-email ledger — the send is manual (Miro's mailbox); this records it durably so the same
    // kind is NEVER offered twice for one lead (idempotency guard the flow checks before offering).
    suspend fun recordEmailSent(email: String, kind: String): Boolean {
        require(kind.isNotBlank()) { "Email kind is required" }
        repository.findByEmail(email) ?: return false
        check(!emailAlreadySent(email, kind)) { "Email '$kind' already recorded for this lead" }
        repository.appendEvent(email, LeadEventType.EMAIL_SENT, kind)
        logger.info("Email '$kind' recorded for ${email.maskEmail()}")
        return true
    }

    suspend fun emailAlreadySent(email: String, kind: String): Boolean =
        repository.findEvents(email).any { it.type == LeadEventType.EMAIL_SENT && it.detail == kind }

    suspend fun recordInviteSent(email: String) =
        repository.appendEvent(email, LeadEventType.INVITE_SENT, null)

    suspend fun submit(draft: LeadDraft): Lead {
        require(EMAIL_REGEX.matches(draft.email)) { "A valid email is required" }
        require(draft.appType.isNotBlank()) { "App type is required" }

        val lead = repository.create(draft)
        repository.appendEvent(lead.email, LeadEventType.SUBMITTED, draft.appType)
        logger.info("Lead ${lead.id} submitted: ${draft.appType} (${draft.email.maskEmail()}, docs=${draft.hasDoc}, design=${draft.hasDesign})")

        // Fire-and-forget: the response returns immediately; mail must never block or fail the lead.
        // Sequential (one launch) keeps the two sends from hitting the Resend rate limit at once.
        mailScope.launch {
            notifyAdmin(lead)
            confirmToClient(lead, draft.locale)
        }

        return lead
    }

    private suspend fun notifyAdmin(lead: Lead) {
        runCatching { mailer.send(recipient, "New lead: ${lead.appType}", adminBody(lead)) }
            .onSuccess { repository.appendEvent(lead.email, LeadEventType.EMAIL_SENT, "admin-notify") }
            .onFailure { logger.error("Lead ${lead.id}: admin notification failed", it) }
    }

    // Reply-To = recipient (admin@) so the client's reply reaches a real mailbox — the from-domain is send-only.
    private suspend fun confirmToClient(lead: Lead, locale: String?) {
        runCatching {
            mailer.send(
                to = lead.email,
                subject = ClientConfirmationEmail.subject(locale),
                text = ClientConfirmationEmail.text(lead, locale),
                html = ClientConfirmationEmail.html(lead, locale),
                replyTo = recipient,
            )
        }
            .onSuccess { repository.appendEvent(lead.email, LeadEventType.EMAIL_SENT, "confirmation") }
            .onFailure { logger.error("Lead ${lead.id}: client confirmation failed", it) }
    }

    private fun adminBody(lead: Lead) = buildString {
        appendLine("New lead from the portfolio configurator.")
        appendLine()
        appendLine("App type: ${lead.appType}")
        if (lead.platforms.isNotEmpty()) appendLine("Platforms: ${lead.platforms.joinToString(", ")}")
        appendLine("Email: ${lead.email}")
        lead.name?.let { appendLine("Name: $it") }
        lead.phone?.let { appendLine("Phone: $it") }
        if (lead.features.isNotEmpty()) {
            appendLine()
            appendLine("Requested features:")
            lead.features.forEach { appendLine("  • $it") }
        }
        if (lead.hasDoc) {
            appendLine()
            appendLine("Client has their own documentation / spec.")
        }
        if (lead.hasDesign) {
            appendLine()
            appendLine("Client has their own design (Figma / wireframes / screens).")
        }
        lead.note?.let {
            appendLine()
            appendLine("Note:")
            appendLine(it)
        }
    }
}
