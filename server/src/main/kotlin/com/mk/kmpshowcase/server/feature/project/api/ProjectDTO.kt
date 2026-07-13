package com.mk.kmpshowcase.server.feature.project.api

import com.mk.kmpshowcase.server.feature.project.service.ClientProject
import kotlinx.serialization.Serializable
import java.time.Instant

private fun Long.toIso(): String = Instant.ofEpochMilli(this).toString()

// Client-safe project view — the service already filters to released demos and the client never sees
// internal-only content. Dates are ISO-8601 UTC on the wire.
@Serializable
internal data class ClientProjectDTO(
    val state: String,
    val health: String,
    val startDate: String,
    val targetEndDate: String?,
    val actualEndDate: String?,
    val scope: List<ScopeItemDTO>,
    val outOfScope: List<ScopeItemDTO>,
    val documents: List<ClientDocumentDTO>,
    val milestones: List<ClientMilestoneDTO>,
    val demos: List<ClientDemoDTO>,
    val payments: List<ClientPaymentDTO>,
    val history: List<ProjectEventDTO>,
)

@Serializable
internal data class ProjectEventDTO(val type: String, val detail: String?, val at: String)

@Serializable
internal data class ScopeItemDTO(val title: String, val detail: String? = null)

@Serializable
internal data class ClientDocumentDTO(val type: String, val title: String, val url: String)

@Serializable
internal data class ClientMilestoneDTO(
    val title: String,
    val description: String?,
    val status: String,
    val plannedDate: String?,
    val completedDate: String?,
    val position: Int,
    val acceptanceCriteria: List<String>,
)

@Serializable
internal data class ClientDemoDTO(val title: String, val url: String, val thumbnailUrl: String?, val updatedAt: String)

@Serializable
internal data class ClientPaymentDTO(
    val label: String,
    val amountCents: Long,
    val currency: String,
    val status: String,
    val position: Int,
)

internal fun ClientProject.toDTO() = ClientProjectDTO(
    state = project.state.name,
    health = project.health.name,
    startDate = project.startDate.toIso(),
    targetEndDate = project.targetEndDate?.toIso(),
    actualEndDate = project.actualEndDate?.toIso(),
    scope = project.scope.map { ScopeItemDTO(it.title, it.detail) },
    outOfScope = project.outOfScope.map { ScopeItemDTO(it.title, it.detail) },
    documents = documents.map { ClientDocumentDTO(it.type.name, it.title, it.url) },
    milestones = milestones.map {
        ClientMilestoneDTO(
            it.title, it.description, it.status.name, it.plannedDate?.toIso(),
            it.completedDate?.toIso(), it.position, it.acceptanceCriteria,
        )
    },
    demos = demos.map { ClientDemoDTO(it.title, it.url, it.thumbnailUrl, it.updatedAt.toIso()) },
    payments = payments.map { ClientPaymentDTO(it.label, it.amountCents, it.currency.name, it.status.name, it.position) },
    history = history.map { ProjectEventDTO(it.type.name, it.detail, it.at.toIso()) },
)
