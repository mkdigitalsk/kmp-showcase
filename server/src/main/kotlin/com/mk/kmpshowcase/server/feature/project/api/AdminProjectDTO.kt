package com.mk.kmpshowcase.server.feature.project.api

import com.mk.kmpshowcase.server.feature.project.service.AdminProject
import com.mk.kmpshowcase.server.feature.project.service.Demo
import com.mk.kmpshowcase.server.feature.project.service.DemoDraft
import com.mk.kmpshowcase.server.feature.project.service.Document
import com.mk.kmpshowcase.server.feature.project.service.DocumentDraft
import com.mk.kmpshowcase.server.feature.project.service.DocumentType
import com.mk.kmpshowcase.server.feature.project.service.Milestone
import com.mk.kmpshowcase.server.feature.project.service.MilestoneDraft
import com.mk.kmpshowcase.server.feature.project.service.MilestoneStatus
import com.mk.kmpshowcase.server.feature.project.service.Project
import com.mk.kmpshowcase.server.feature.project.service.ProjectDraft
import com.mk.kmpshowcase.server.feature.project.service.ProjectHealth
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
    val documents: List<AdminDocumentDTO>,
    val milestones: List<AdminMilestoneDTO>,
    val demos: List<AdminDemoDTO>,
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
)

@Serializable
internal data class AdminDemoDTO(val id: Long, val title: String, val url: String, val released: Boolean, val updatedAt: String)

@Serializable
internal data class StartProjectRequestDTO(val startDate: Long, val targetEndDate: Long? = null, val health: String = "GREEN")

@Serializable
internal data class UpdateProjectRequestDTO(val health: String, val targetEndDate: Long? = null)

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
)

@Serializable
internal data class DemoRequestDTO(val title: String, val url: String, val released: Boolean = false)

internal fun Document.toDTO() = AdminDocumentDTO(id, type.name, title, url, updatedAt.toIso())

internal fun Milestone.toDTO() =
    AdminMilestoneDTO(id, title, description, status.name, plannedDate?.toIso(), completedDate?.toIso(), position, updatedAt.toIso())

internal fun Demo.toDTO() = AdminDemoDTO(id, title, url, released, updatedAt.toIso())

internal fun AdminProject.toDTO() = AdminProjectDTO(
    email = project.email,
    state = project.state.name,
    health = project.health.name,
    startDate = project.startDate.toIso(),
    targetEndDate = project.targetEndDate?.toIso(),
    actualEndDate = project.actualEndDate?.toIso(),
    documents = documents.map { it.toDTO() },
    milestones = milestones.map { it.toDTO() },
    demos = demos.map { it.toDTO() },
    history = history.map { ProjectEventDTO(it.type.name, it.detail, it.at.toIso()) },
)

// Bad enum / blank required field throws IllegalArgumentException → 400 via StatusPages.
internal fun StartProjectRequestDTO.toDraft() =
    ProjectDraft(startDate = startDate, targetEndDate = targetEndDate, health = ProjectHealth.valueOf(health.uppercase()))

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
    )
}

internal fun DemoRequestDTO.toDraft(): DemoDraft {
    require(title.isNotBlank()) { "Demo title is required" }
    require(url.isNotBlank()) { "Demo url is required" }
    return DemoDraft(title.trim(), url.trim(), released)
}
