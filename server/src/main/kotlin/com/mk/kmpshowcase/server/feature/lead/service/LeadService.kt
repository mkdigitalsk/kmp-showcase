package com.mk.kmpshowcase.server.feature.lead.service

import com.mk.kmpshowcase.server.core.mail.Mailer
import com.mk.kmpshowcase.server.feature.lead.persistence.LeadRepository
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("LeadService")
private val EMAIL_REGEX = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")

internal class LeadService(
    private val repository: LeadRepository,
    private val mailer: Mailer,
    private val recipient: String,
) {
    suspend fun submit(draft: LeadDraft): Lead {
        require(EMAIL_REGEX.matches(draft.email)) { "A valid email is required" }
        require(draft.appType.isNotBlank()) { "App type is required" }

        val lead = repository.create(draft)

        // Notification is best-effort: a failed email must not lose a stored lead.
        runCatching { mailer.send(recipient, subject(lead), body(lead)) }
            .onFailure { logger.error("Lead saved (id=${lead.id}) but notification email failed", it) }

        return lead
    }

    private fun subject(lead: Lead) = "New lead: ${lead.appType}"

    private fun body(lead: Lead) = buildString {
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
}
