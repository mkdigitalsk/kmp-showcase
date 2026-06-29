package com.mk.kmpshowcase.server.feature.lead.service

import com.mk.kmpshowcase.server.core.mail.Mailer
import com.mk.kmpshowcase.server.feature.lead.persistence.LeadRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("LeadService")
private val EMAIL_REGEX = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
private const val CLIENT_SUBJECT = "We received your request — MK Digital"

internal class LeadService(
    private val repository: LeadRepository,
    private val mailer: Mailer,
    private val recipient: String,
    private val mailScope: CoroutineScope,
) {
    suspend fun submit(draft: LeadDraft): Lead {
        require(EMAIL_REGEX.matches(draft.email)) { "A valid email is required" }
        require(draft.appType.isNotBlank()) { "App type is required" }

        val lead = repository.create(draft)

        // Fire-and-forget: the response returns immediately; mail must never block or fail the lead.
        mailScope.launch { notifyAdmin(lead) }
        mailScope.launch { confirmToClient(lead) }

        return lead
    }

    private suspend fun notifyAdmin(lead: Lead) {
        runCatching { mailer.send(recipient, "New lead: ${lead.appType}", adminBody(lead)) }
            .onFailure { logger.error("Lead ${lead.id}: admin notification failed", it) }
    }

    // Reply-To = recipient (admin@) so the client's reply reaches a real mailbox — the from-domain is send-only.
    private suspend fun confirmToClient(lead: Lead) {
        runCatching { mailer.send(lead.email, CLIENT_SUBJECT, clientBody(lead), replyTo = recipient) }
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
        lead.note?.let {
            appendLine()
            appendLine("Note:")
            appendLine(it)
        }
    }

    private fun clientBody(lead: Lead) = buildString {
        appendLine("Hi${lead.name?.let { " $it" } ?: ""},")
        appendLine()
        appendLine("Thanks for reaching out to MK Digital — we've received your request and will reply shortly.")
        appendLine()
        appendLine("What you sent us:")
        appendLine("  App type: ${lead.appType}")
        if (lead.platforms.isNotEmpty()) appendLine("  Platforms: ${lead.platforms.joinToString(", ")}")
        if (lead.features.isNotEmpty()) appendLine("  Features: ${lead.features.joinToString(", ")}")
        appendLine()
        appendLine("Want to add anything? Just reply to this email.")
        appendLine()
        appendLine("— MK Digital")
        appendLine("Senior-led software studio")
    }
}
