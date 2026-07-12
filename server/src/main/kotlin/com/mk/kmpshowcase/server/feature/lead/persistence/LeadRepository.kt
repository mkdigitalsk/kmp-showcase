package com.mk.kmpshowcase.server.feature.lead.persistence

import com.mk.kmpshowcase.server.feature.lead.service.Demo
import com.mk.kmpshowcase.server.feature.lead.service.DemoDraft
import com.mk.kmpshowcase.server.feature.lead.service.Lead
import com.mk.kmpshowcase.server.feature.lead.service.LeadArtifact
import com.mk.kmpshowcase.server.feature.lead.service.LeadArtifactStage
import com.mk.kmpshowcase.server.feature.lead.service.LeadDraft
import com.mk.kmpshowcase.server.feature.lead.service.LeadStatus
import com.mk.kmpshowcase.server.feature.lead.service.Milestone
import com.mk.kmpshowcase.server.feature.lead.service.MilestoneDraft

internal interface LeadRepository {
    suspend fun create(draft: LeadDraft): Lead
    suspend fun findAll(): List<Lead>
    suspend fun findByEmail(email: String): Lead?
    suspend fun updateStatus(email: String, status: LeadStatus): Lead?
    suspend fun findArtifacts(email: String): List<LeadArtifact>
    suspend fun upsertArtifact(email: String, stage: LeadArtifactStage, content: String)

    suspend fun findMilestones(email: String): List<Milestone>
    suspend fun addMilestone(email: String, draft: MilestoneDraft): Milestone
    suspend fun updateMilestone(id: Long, draft: MilestoneDraft): Milestone?
    suspend fun deleteMilestone(id: Long): Boolean

    suspend fun findDemos(email: String): List<Demo>
    suspend fun addDemo(email: String, draft: DemoDraft): Demo
    suspend fun updateDemo(id: Long, draft: DemoDraft): Demo?
    suspend fun deleteDemo(id: Long): Boolean
}
