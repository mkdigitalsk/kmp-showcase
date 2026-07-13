package com.mk.kmpshowcase.server.feature.project.service

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
)

internal data class ProjectDraft(
    val startDate: Long,
    val targetEndDate: Long?,
    val health: ProjectHealth,
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
)

internal data class MilestoneDraft(
    val title: String,
    val description: String?,
    val status: MilestoneStatus,
    val plannedDate: Long?,
    val completedDate: Long?,
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

// Append-only history — every project change is recorded as an immutable event (never updated or
// deleted), giving a permanent audit trail that a current-state-only model cannot reconstruct.
internal enum class ProjectEventType {
    STARTED, HEALTH_CHANGED, MILESTONE_ADDED, MILESTONE_UPDATED, MILESTONE_REMOVED,
    DOCUMENT_ADDED, DOCUMENT_REMOVED, DEMO_ADDED, DEMO_UPDATED, DEMO_REMOVED, COMPLETED, ARCHIVED,
}

internal data class ProjectEvent(val id: Long, val type: ProjectEventType, val detail: String?, val at: Long)

// What a logged-in client sees for their own project (released demos only). Same shape reused by the
// admin read-only preview.
internal data class ClientProject(
    val project: Project,
    val documents: List<Document>,
    val milestones: List<Milestone>,
    val demos: List<Demo>,
    val history: List<ProjectEvent>,
)

// What the admin manages — everything, including unreleased demos.
internal data class AdminProject(
    val project: Project,
    val documents: List<Document>,
    val milestones: List<Milestone>,
    val demos: List<Demo>,
    val history: List<ProjectEvent>,
)
