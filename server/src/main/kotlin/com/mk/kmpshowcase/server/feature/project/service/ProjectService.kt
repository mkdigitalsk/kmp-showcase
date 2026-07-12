package com.mk.kmpshowcase.server.feature.project.service

import com.mk.kmpshowcase.server.feature.project.persistence.ProjectRepository

internal class ProjectService(private val repository: ProjectRepository) {

    // Client-facing: the caller's own project (released demos only). Null if they have no project yet.
    suspend fun getClientProject(email: String): ClientProject? {
        val project = repository.find(email) ?: return null
        return ClientProject(
            project = project,
            documents = repository.findDocuments(email),
            milestones = repository.findMilestones(email),
            demos = repository.findDemos(email).filter { it.released },
        )
    }

    // Admin: everything, including unreleased demos.
    suspend fun getAdminProject(email: String): AdminProject? {
        val project = repository.find(email) ?: return null
        return AdminProject(project, repository.findDocuments(email), repository.findMilestones(email), repository.findDemos(email))
    }

    // A signed lead becomes a project. Fails (409 via StatusPages) if one already exists for the email.
    suspend fun startProject(email: String, draft: ProjectDraft): Project {
        check(repository.find(email) == null) { "A project already exists for $email" }
        return repository.create(email, draft)
    }

    suspend fun updateProject(email: String, health: ProjectHealth, targetEndDate: Long?): Project? {
        val current = repository.find(email) ?: return null
        return repository.update(email, current.state, health, targetEndDate, current.actualEndDate)
    }

    suspend fun completeProject(email: String): Project? {
        val current = repository.find(email) ?: return null
        return repository.update(email, ProjectState.COMPLETED, current.health, current.targetEndDate, System.currentTimeMillis())
    }

    suspend fun archiveProject(email: String): Project? {
        val current = repository.find(email) ?: return null
        return repository.update(email, ProjectState.ARCHIVED, current.health, current.targetEndDate, current.actualEndDate)
    }

    suspend fun addDocument(email: String, draft: DocumentDraft): Document = repository.addDocument(email, draft)
    suspend fun deleteDocument(id: Long): Boolean = repository.deleteDocument(id)

    suspend fun addMilestone(email: String, draft: MilestoneDraft): Milestone = repository.addMilestone(email, draft)
    suspend fun updateMilestone(id: Long, draft: MilestoneDraft): Milestone? = repository.updateMilestone(id, draft)
    suspend fun deleteMilestone(id: Long): Boolean = repository.deleteMilestone(id)

    suspend fun addDemo(email: String, draft: DemoDraft): Demo = repository.addDemo(email, draft)
    suspend fun updateDemo(id: Long, draft: DemoDraft): Demo? = repository.updateDemo(id, draft)
    suspend fun deleteDemo(id: Long): Boolean = repository.deleteDemo(id)
}
