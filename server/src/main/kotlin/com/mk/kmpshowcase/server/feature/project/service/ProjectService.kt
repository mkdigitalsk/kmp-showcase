package com.mk.kmpshowcase.server.feature.project.service

import com.mk.kmpshowcase.server.feature.project.persistence.ProjectRepository

internal class ProjectService(private val repository: ProjectRepository) {

    // Client-facing: the caller's own project (released demos only, removals hidden from the history).
    suspend fun getClientProject(email: String): ClientProject? {
        val project = repository.find(email) ?: return null
        return ClientProject(
            project = project,
            documents = repository.findDocuments(email),
            milestones = repository.findMilestones(email),
            demos = repository.findDemos(email).filter { it.released },
            history = repository.findEvents(email).filter { it.type in CLIENT_VISIBLE },
        )
    }

    // Admin: everything, including unreleased demos and the full audit trail.
    suspend fun getAdminProject(email: String): AdminProject? {
        val project = repository.find(email) ?: return null
        return AdminProject(
            project = project,
            documents = repository.findDocuments(email),
            milestones = repository.findMilestones(email),
            demos = repository.findDemos(email),
            history = repository.findEvents(email),
        )
    }

    // A signed lead becomes a project. Fails (409 via StatusPages) if one already exists for the email.
    suspend fun startProject(email: String, draft: ProjectDraft): Project {
        check(repository.find(email) == null) { "A project already exists for $email" }
        return repository.create(email, draft).also { repository.appendEvent(email, ProjectEventType.STARTED, null) }
    }

    suspend fun updateProject(email: String, health: ProjectHealth, targetEndDate: Long?): Project? {
        val current = repository.find(email) ?: return null
        val updated = repository.update(email, current.state, health, targetEndDate, current.actualEndDate)
        if (updated != null && health != current.health) {
            repository.appendEvent(email, ProjectEventType.HEALTH_CHANGED, health.name)
        }
        return updated
    }

    suspend fun completeProject(email: String): Project? {
        val current = repository.find(email) ?: return null
        return repository.update(email, ProjectState.COMPLETED, current.health, current.targetEndDate, System.currentTimeMillis())
            ?.also { repository.appendEvent(email, ProjectEventType.COMPLETED, null) }
    }

    suspend fun archiveProject(email: String): Project? {
        val current = repository.find(email) ?: return null
        return repository.update(email, ProjectState.ARCHIVED, current.health, current.targetEndDate, current.actualEndDate)
            ?.also { repository.appendEvent(email, ProjectEventType.ARCHIVED, null) }
    }

    suspend fun addDocument(email: String, draft: DocumentDraft): Document =
        repository.addDocument(email, draft).also { repository.appendEvent(email, ProjectEventType.DOCUMENT_ADDED, it.title) }

    suspend fun deleteDocument(email: String, id: Long): Boolean =
        repository.deleteDocument(id).also { if (it) repository.appendEvent(email, ProjectEventType.DOCUMENT_REMOVED, null) }

    suspend fun addMilestone(email: String, draft: MilestoneDraft): Milestone =
        repository.addMilestone(email, draft).also { repository.appendEvent(email, ProjectEventType.MILESTONE_ADDED, it.title) }

    suspend fun updateMilestone(email: String, id: Long, draft: MilestoneDraft): Milestone? =
        repository.updateMilestone(id, draft)?.also { repository.appendEvent(email, ProjectEventType.MILESTONE_UPDATED, it.title) }

    suspend fun deleteMilestone(email: String, id: Long): Boolean =
        repository.deleteMilestone(id).also { if (it) repository.appendEvent(email, ProjectEventType.MILESTONE_REMOVED, null) }

    suspend fun addDemo(email: String, draft: DemoDraft): Demo =
        repository.addDemo(email, draft).also { repository.appendEvent(email, ProjectEventType.DEMO_ADDED, it.title) }

    suspend fun updateDemo(email: String, id: Long, draft: DemoDraft): Demo? =
        repository.updateDemo(id, draft)?.also { repository.appendEvent(email, ProjectEventType.DEMO_UPDATED, it.title) }

    suspend fun deleteDemo(email: String, id: Long): Boolean =
        repository.deleteDemo(id).also { if (it) repository.appendEvent(email, ProjectEventType.DEMO_REMOVED, null) }

    private companion object {
        // The client sees only project-level lifecycle events — never granular admin actions, which could
        // leak titles of unreleased/internal work. The admin keeps the full audit trail.
        val CLIENT_VISIBLE = setOf(
            ProjectEventType.STARTED,
            ProjectEventType.HEALTH_CHANGED,
            ProjectEventType.COMPLETED,
            ProjectEventType.ARCHIVED,
        )
    }
}
