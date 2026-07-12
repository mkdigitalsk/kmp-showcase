package com.mk.kmpshowcase.server.feature.lead.service

// The client-facing delivery side of a lead. A lead is the engagement anchor (keyed by email);
// milestones and demos hang off it. Admin manages them; the client sees a safe projection.

internal enum class MilestoneStatus { PENDING, IN_PROGRESS, DONE }

internal data class Milestone(
    val id: Long,
    val title: String,
    val description: String?,
    val status: MilestoneStatus,
    val position: Int,
    val updatedAt: Long,
)

internal data class MilestoneDraft(
    val title: String,
    val description: String?,
    val status: MilestoneStatus,
    val position: Int,
)

internal data class Demo(
    val id: Long,
    val title: String,
    val url: String,
    val released: Boolean,
    val updatedAt: Long,
)

internal data class DemoDraft(
    val title: String,
    val url: String,
    val released: Boolean,
)

// Client-safe stage — never exposes internal admin statuses (PROPOSAL_DRAFTED, INTERNAL_SCOPE, LOST detail).
internal enum class ClientStage { IN_REVIEW, PREPARING_PROPOSAL, PROPOSAL_READY, IN_PROGRESS, CLOSED }

internal fun LeadStatus.toClientStage(): ClientStage = when (this) {
    LeadStatus.NEW, LeadStatus.REVIEWING -> ClientStage.IN_REVIEW
    LeadStatus.ANALYZED, LeadStatus.PROPOSAL_DRAFTED -> ClientStage.PREPARING_PROPOSAL
    LeadStatus.PROPOSAL_SENT -> ClientStage.PROPOSAL_READY
    LeadStatus.WON -> ClientStage.IN_PROGRESS
    LeadStatus.LOST -> ClientStage.CLOSED
}

// The proposal is only visible to the client once it's actually been sent (never while drafting).
internal fun LeadStatus.proposalVisible(): Boolean = this == LeadStatus.PROPOSAL_SENT || this == LeadStatus.WON

// What a logged-in client sees for their own engagement (proposal + released demos only).
internal data class ClientEngagement(
    val lead: Lead,
    val stage: ClientStage,
    val proposal: LeadArtifact?,
    val milestones: List<Milestone>,
    val demos: List<Demo>,
)

// What the admin sees/manages — everything, including unreleased demos.
internal data class AdminEngagement(
    val milestones: List<Milestone>,
    val demos: List<Demo>,
)
