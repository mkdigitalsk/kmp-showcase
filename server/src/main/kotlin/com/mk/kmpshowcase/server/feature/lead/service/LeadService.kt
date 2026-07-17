package com.mk.kmpshowcase.server.feature.lead.service

import com.mk.kmpshowcase.server.core.mail.Mailer
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
        return LeadDetail(lead, repository.findArtifacts(email))
    }

    suspend fun updateStatus(email: String, status: LeadStatus): Lead? = repository.updateStatus(email, status)

    suspend fun saveArtifact(email: String, stage: LeadArtifactStage, content: String) =
        repository.upsertArtifact(email, stage, content)

    suspend fun submit(draft: LeadDraft): Lead {
        require(EMAIL_REGEX.matches(draft.email)) { "A valid email is required" }
        require(draft.appType.isNotBlank()) { "App type is required" }

        val lead = repository.create(draft)

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
        }.onFailure { logger.error("Lead ${lead.id}: client confirmation failed", it) }
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
