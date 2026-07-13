package com.mk.kmpshowcase.server.feature.project.api

import com.mk.kmpshowcase.server.feature.project.service.AdminProject
import com.mk.kmpshowcase.server.feature.project.service.Demo
import com.mk.kmpshowcase.server.feature.project.service.DemoDraft
import com.mk.kmpshowcase.server.feature.project.service.Document
import com.mk.kmpshowcase.server.feature.project.service.DocumentDraft
import com.mk.kmpshowcase.server.feature.project.service.DocumentType
import com.mk.kmpshowcase.server.feature.project.service.Milestone
import com.mk.kmpshowcase.server.feature.project.service.MilestoneDraft
import com.mk.kmpshowcase.server.feature.project.service.Currency
import com.mk.kmpshowcase.server.feature.project.service.MilestoneStatus
import com.mk.kmpshowcase.server.feature.project.service.Payment
import com.mk.kmpshowcase.server.feature.project.service.PaymentDraft
import com.mk.kmpshowcase.server.feature.project.service.PaymentStatus
import com.mk.kmpshowcase.server.feature.project.service.Project
import com.mk.kmpshowcase.server.feature.project.service.ProjectDraft
import com.mk.kmpshowcase.server.feature.project.service.ProjectHealth
import com.mk.kmpshowcase.server.feature.project.service.ScopeItem
import kotlinx.serialization.Serializable
import java.time.Instant

private fun Long.toIso(): String = Instant.ofEpochMilli(this).toString()

@Serializable
internal data class AdminProjectDTO(
    val email: String,
    val state: String,
    val health: String,
    val startDate: String,
    val targetEndDate: String?,
    val actualEndDate: String?,
    val scope: List<ScopeItemDTO>,
    val outOfScope: List<ScopeItemDTO>,
    val documents: List<AdminDocumentDTO>,
    val milestones: List<AdminMilestoneDTO>,
    val demos: List<AdminDemoDTO>,
    val payments: List<AdminPaymentDTO>,
    val history: List<ProjectEventDTO>,
)

@Serializable
internal data class AdminDocumentDTO(val id: Long, val type: String, val title: String, val url: String, val updatedAt: String)

@Serializable
internal data class AdminMilestoneDTO(
    val id: Long,
    val title: String,
    val description: String?,
    val status: String,
    val plannedDate: String?,
    val completedDate: String?,
    val position: Int,
    val updatedAt: String,
    val acceptanceCriteria: List<String>,
)

@Serializable
internal data class AdminDemoDTO(val id: Long, val title: String, val url: String, val released: Boolean, val updatedAt: String)

@Serializable
internal data class AdminPaymentDTO(
    val id: Long,
    val label: String,
    val amountCents: Long,
    val currency: String,
    val status: String,
    val position: Int,
)

@Serializable
internal data class StartProjectRequestDTO(
    val startDate: Long,
    val targetEndDate: Long? = null,
    val health: String = "GREEN",
    val scope: List<ScopeItemDTO> = emptyList(),
    val outOfScope: List<ScopeItemDTO> = emptyList(),
)

@Serializable
internal data class UpdateProjectRequestDTO(
    val health: String,
    val targetEndDate: Long? = null,
    val scope: List<ScopeItemDTO> = emptyList(),
    val outOfScope: List<ScopeItemDTO> = emptyList(),
)

@Serializable
internal data class DocumentRequestDTO(val type: String, val title: String, val url: String)

@Serializable
internal data class MilestoneRequestDTO(
    val title: String,
    val description: String? = null,
    val status: String = "PENDING",
    val plannedDate: Long? = null,
    val completedDate: Long? = null,
    val position: Int = 0,
    val acceptanceCriteria: List<String> = emptyList(),
)

@Serializable
internal data class DemoRequestDTO(val title: String, val url: String, val released: Boolean = false)

@Serializable
internal data class PaymentRequestDTO(
    val label: String,
    val amountCents: Long,
    val currency: String = "EUR",
    val status: String = "DUE",
    val position: Int = 0,
)

internal fun Document.toDTO() = AdminDocumentDTO(id, type.name, title, url, updatedAt.toIso())

internal fun Milestone.toDTO() = AdminMilestoneDTO(
    id, title, description, status.name, plannedDate?.toIso(), completedDate?.toIso(), position, updatedAt.toIso(),
    acceptanceCriteria,
)

internal fun Demo.toDTO() = AdminDemoDTO(id, title, url, released, updatedAt.toIso())

internal fun Payment.toDTO() = AdminPaymentDTO(id, label, amountCents, currency.name, status.name, position)

internal fun AdminProject.toDTO() = AdminProjectDTO(
    email = project.email,
    state = project.state.name,
    health = project.health.name,
    startDate = project.startDate.toIso(),
    targetEndDate = project.targetEndDate?.toIso(),
    actualEndDate = project.actualEndDate?.toIso(),
    scope = project.scope.map { ScopeItemDTO(it.title, it.detail) },
    outOfScope = project.outOfScope.map { ScopeItemDTO(it.title, it.detail) },
    documents = documents.map { it.toDTO() },
    milestones = milestones.map { it.toDTO() },
    demos = demos.map { it.toDTO() },
    payments = payments.map { it.toDTO() },
    history = history.map { ProjectEventDTO(it.type.name, it.detail, it.at.toIso()) },
)

// Drop items with a blank title; trim the rest. Keeps empty rows from the admin editor out of the domain.
internal fun List<ScopeItemDTO>.toDomain(): List<ScopeItem> = mapNotNull { item ->
    item.title.trim().takeIf { it.isNotEmpty() }?.let { ScopeItem(it, item.detail?.trim()?.takeIf { d -> d.isNotEmpty() }) }
}

private fun List<String>.cleaned(): List<String> = map { it.trim() }.filter { it.isNotEmpty() }

// Bad enum / blank required field throws IllegalArgumentException → 400 via StatusPages.
internal fun StartProjectRequestDTO.toDraft() = ProjectDraft(
    startDate = startDate,
    targetEndDate = targetEndDate,
    health = ProjectHealth.valueOf(health.uppercase()),
    scope = scope.toDomain(),
    outOfScope = outOfScope.toDomain(),
)

internal fun DocumentRequestDTO.toDraft(): DocumentDraft {
    require(title.isNotBlank()) { "Document title is required" }
    require(url.isNotBlank()) { "Document url is required" }
    return DocumentDraft(DocumentType.valueOf(type.uppercase()), title.trim(), url.trim())
}

internal fun MilestoneRequestDTO.toDraft(): MilestoneDraft {
    require(title.isNotBlank()) { "Milestone title is required" }
    return MilestoneDraft(
        title = title.trim(),
        description = description?.trim()?.takeIf { it.isNotEmpty() },
        status = MilestoneStatus.valueOf(status.uppercase()),
        plannedDate = plannedDate,
        completedDate = completedDate,
        position = position,
        acceptanceCriteria = acceptanceCriteria.cleaned(),
    )
}

internal fun DemoRequestDTO.toDraft(): DemoDraft {
    require(title.isNotBlank()) { "Demo title is required" }
    require(url.isNotBlank()) { "Demo url is required" }
    return DemoDraft(title.trim(), url.trim(), released)
}

internal fun PaymentRequestDTO.toDraft(): PaymentDraft {
    require(label.isNotBlank()) { "Payment label is required" }
    require(amountCents >= 0) { "Payment amount must be non-negative" }
    return PaymentDraft(
        label = label.trim(),
        amountCents = amountCents,
        currency = Currency.valueOf(currency.trim().uppercase()),
        status = PaymentStatus.valueOf(status.uppercase()),
        position = position,
    )
}
