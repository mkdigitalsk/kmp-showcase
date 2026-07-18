package com.mk.kmpshowcase.server.feature.lead.service

// The documents the `analyze lead` pipeline produces, keyed to a lead by email (the identity/divisor).
internal enum class LeadArtifactStage { REQUIREMENTS, QUESTIONS, ANALYSIS, PROPOSAL, INTERNAL_SCOPE }

internal data class LeadArtifact(
    val stage: LeadArtifactStage,
    val content: String,
    val updatedAt: Long,
)

// A lead plus everything hanging off its email — the CRM detail view.
internal data class LeadDetail(
    val lead: Lead,
    val artifacts: List<LeadArtifact>,
    val events: List<LeadEvent>,
)
