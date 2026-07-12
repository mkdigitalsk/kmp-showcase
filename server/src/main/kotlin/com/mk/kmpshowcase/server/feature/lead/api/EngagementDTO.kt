package com.mk.kmpshowcase.server.feature.lead.api

import com.mk.kmpshowcase.server.feature.lead.service.ClientEngagement
import kotlinx.serialization.Serializable
import java.time.Instant

// The client-safe view of an engagement. Only what the logged-in client should see — never internal
// statuses, drafts, or unreleased demos (the service already filters those out).
@Serializable
internal data class ClientEngagementDTO(
    val appType: String,
    val platforms: List<String>,
    val features: List<String>,
    val hasDoc: Boolean,
    val note: String?,
    val stage: String,
    val submittedAt: String,
    val proposal: String?,
    val milestones: List<ClientMilestoneDTO>,
    val demos: List<ClientDemoDTO>,
)

@Serializable
internal data class ClientMilestoneDTO(
    val title: String,
    val description: String?,
    val status: String,
    val position: Int,
)

@Serializable
internal data class ClientDemoDTO(
    val title: String,
    val url: String,
    val updatedAt: String,
)

internal fun ClientEngagement.toDTO() = ClientEngagementDTO(
    appType = lead.appType,
    platforms = lead.platforms,
    features = lead.features,
    hasDoc = lead.hasDoc,
    note = lead.note,
    stage = stage.name,
    submittedAt = Instant.ofEpochMilli(lead.createdAt).toString(),
    proposal = proposal?.content,
    milestones = milestones.map { ClientMilestoneDTO(it.title, it.description, it.status.name, it.position) },
    demos = demos.map { ClientDemoDTO(it.title, it.url, Instant.ofEpochMilli(it.updatedAt).toString()) },
)
