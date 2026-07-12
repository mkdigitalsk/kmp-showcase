package com.mk.kmpshowcase.server.feature.lead.persistence

import com.mk.kmpshowcase.server.feature.lead.service.Lead
import com.mk.kmpshowcase.server.feature.lead.service.LeadArtifact
import com.mk.kmpshowcase.server.feature.lead.service.LeadArtifactStage
import com.mk.kmpshowcase.server.feature.lead.service.LeadDraft
import com.mk.kmpshowcase.server.feature.lead.service.LeadStatus

internal interface LeadRepository {
    suspend fun create(draft: LeadDraft): Lead
    suspend fun findAll(): List<Lead>
    suspend fun findByEmail(email: String): Lead?
    suspend fun updateStatus(email: String, status: LeadStatus): Lead?
    suspend fun findArtifacts(email: String): List<LeadArtifact>
    suspend fun upsertArtifact(email: String, stage: LeadArtifactStage, content: String)
}
