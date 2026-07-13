package com.mk.kmpshowcase.server.feature.project.persistence

import com.mk.kmpshowcase.server.feature.project.service.Demo
import com.mk.kmpshowcase.server.feature.project.service.DemoDraft
import com.mk.kmpshowcase.server.feature.project.service.Document
import com.mk.kmpshowcase.server.feature.project.service.DocumentDraft
import com.mk.kmpshowcase.server.feature.project.service.Milestone
import com.mk.kmpshowcase.server.feature.project.service.MilestoneDraft
import com.mk.kmpshowcase.server.feature.project.service.Project
import com.mk.kmpshowcase.server.feature.project.service.ProjectDraft
import com.mk.kmpshowcase.server.feature.project.service.ProjectEvent
import com.mk.kmpshowcase.server.feature.project.service.ProjectEventType
import com.mk.kmpshowcase.server.feature.project.service.ProjectHealth
import com.mk.kmpshowcase.server.feature.project.service.ProjectState

internal interface ProjectRepository {
    suspend fun find(email: String): Project?
    suspend fun create(email: String, draft: ProjectDraft): Project
    suspend fun update(
        email: String,
        state: ProjectState,
        health: ProjectHealth,
        targetEndDate: Long?,
        actualEndDate: Long?,
    ): Project?

    suspend fun findDocuments(email: String): List<Document>
    suspend fun addDocument(email: String, draft: DocumentDraft): Document
    suspend fun deleteDocument(id: Long): Boolean

    suspend fun findMilestones(email: String): List<Milestone>
    suspend fun addMilestone(email: String, draft: MilestoneDraft): Milestone
    suspend fun updateMilestone(id: Long, draft: MilestoneDraft): Milestone?
    suspend fun deleteMilestone(id: Long): Boolean

    suspend fun findDemos(email: String): List<Demo>
    suspend fun addDemo(email: String, draft: DemoDraft): Demo
    suspend fun updateDemo(id: Long, draft: DemoDraft): Demo?
    suspend fun deleteDemo(id: Long): Boolean

    suspend fun appendEvent(email: String, type: ProjectEventType, detail: String?)
    suspend fun findEvents(email: String): List<ProjectEvent>
}
