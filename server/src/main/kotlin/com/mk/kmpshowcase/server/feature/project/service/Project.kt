package com.mk.kmpshowcase.server.feature.project.service

import kotlinx.serialization.Serializable

@Serializable
internal data class ScopeItem(val title: String, val detail: String? = null)

// The delivery side of the relationship — a signed client's live project, from kickoff through
// completion to a permanent read-only archive. Separate bounded context from the sales `lead`.
// Keyed by client email (one active project per client for now); see architecture notes.

internal enum class ProjectState { ACTIVE, COMPLETED, ARCHIVED }

// RAG schedule health, set deliberately by the studio (honest signal vs. pre-agreed tolerance).
internal enum class ProjectHealth { GREEN, AMBER, RED }

internal data class Project(
    val email: String,
    val state: ProjectState,
    val health: ProjectHealth,
    val startDate: Long,
    val targetEndDate: Long?,
    val actualEndDate: Long?,
    val scope: List<ScopeItem>,
    val outOfScope: List<ScopeItem>,
    // Internal tooling links (admin-only — never in the client projection).
    val jiraBoardUrl: String? = null,
    val specUrl: String? = null,
    val designUrl: String? = null,
)

internal data class ProjectDraft(
    val startDate: Long,
    val targetEndDate: Long?,
    val health: ProjectHealth,
    val scope: List<ScopeItem>,
    val outOfScope: List<ScopeItem>,
)

internal enum class MilestoneStatus { PENDING, IN_PROGRESS, DONE }

internal data class Milestone(
    val id: Long,
    val title: String,
    val description: String?,
    val status: MilestoneStatus,
    val plannedDate: Long?,
    val completedDate: Long?,
    val position: Int,
    val updatedAt: Long,
    val acceptanceCriteria: List<String>,
)

internal data class MilestoneDraft(
    val title: String,
    val description: String?,
    val status: MilestoneStatus,
    val plannedDate: Long?,
    val completedDate: Long?,
    val position: Int,
    val acceptanceCriteria: List<String>,
)

internal data class Demo(
    val id: Long,
    val title: String,
    val url: String,
    val thumbnailUrl: String?,
    val released: Boolean,
    val updatedAt: Long,
)

internal data class DemoDraft(
    val title: String,
    val url: String,
    val thumbnailUrl: String?,
    val released: Boolean,
)

internal enum class DocumentType { CONTRACT, PROPOSAL, DOCUMENTATION, DESIGN }

internal data class Document(
    val id: Long,
    val type: DocumentType,
    val title: String,
    val url: String,
    val updatedAt: Long,
)

internal data class DocumentDraft(
    val type: DocumentType,
    val title: String,
    val url: String,
)

internal enum class PaymentStatus { DUE, PAID }

// Curated ISO 4217 subset — the currencies the studio invoices in.
internal enum class Currency { EUR, USD, GBP, CZK }

// A payment stage tied to an acceptance gate (client-visible, per the transparency decision).
internal data class Payment(
    val id: Long,
    val label: String,
    val amountCents: Long,
    val currency: Currency,
    val status: PaymentStatus,
    val position: Int,
)

internal data class PaymentDraft(
    val label: String,
    val amountCents: Long,
    val currency: Currency,
    val status: PaymentStatus,
    val position: Int,
)

// Append-only history — every project change is recorded as an immutable event (never updated or
// deleted), giving a permanent audit trail that a current-state-only model cannot reconstruct.
internal enum class ProjectEventType {
    STARTED, HEALTH_CHANGED, LINKS_UPDATED, MILESTONE_ADDED, MILESTONE_UPDATED, MILESTONE_REMOVED,
    DOCUMENT_ADDED, DOCUMENT_REMOVED, DEMO_ADDED, DEMO_UPDATED, DEMO_REMOVED,
    PAYMENT_ADDED, PAYMENT_UPDATED, PAYMENT_REMOVED, COMPLETED, ARCHIVED, UNARCHIVED,
}

internal data class ProjectEvent(val id: Long, val type: ProjectEventType, val detail: String?, val at: Long)

// What a logged-in client sees for their own project (released demos only). Same shape reused by the
// admin read-only preview.
internal data class ClientProject(
    val project: Project,
    val documents: List<Document>,
    val milestones: List<Milestone>,
    val demos: List<Demo>,
    val payments: List<Payment>,
    val history: List<ProjectEvent>,
)

// What the admin manages — everything, including unreleased demos.
internal data class AdminProject(
    val project: Project,
    val documents: List<Document>,
    val milestones: List<Milestone>,
    val demos: List<Demo>,
    val payments: List<Payment>,
    val history: List<ProjectEvent>,
)
