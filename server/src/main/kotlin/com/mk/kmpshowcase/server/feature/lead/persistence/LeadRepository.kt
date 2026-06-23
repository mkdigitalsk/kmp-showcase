package com.mk.kmpshowcase.server.feature.lead.persistence

import com.mk.kmpshowcase.server.feature.lead.service.Lead
import com.mk.kmpshowcase.server.feature.lead.service.LeadDraft

internal interface LeadRepository {
    suspend fun create(draft: LeadDraft): Lead
}
