package com.mk.kmpshowcase.server.feature.admin.api

import com.mk.kmpshowcase.server.feature.lead.service.AdminEngagement
import com.mk.kmpshowcase.server.feature.lead.service.Demo
import com.mk.kmpshowcase.server.feature.lead.service.DemoDraft
import com.mk.kmpshowcase.server.feature.lead.service.Milestone
import com.mk.kmpshowcase.server.feature.lead.service.MilestoneDraft
import com.mk.kmpshowcase.server.feature.lead.service.MilestoneStatus
import java.time.Instant
import kotlinx.serialization.Serializable

private fun Long.toIso(): String = Instant.ofEpochMilli(this).toString()

@Serializable
internal data class AdminEngagementDTO(
    val milestones: List<AdminMilestoneDTO>,
    val demos: List<AdminDemoDTO>,
)

@Serializable
internal data class AdminMilestoneDTO(
    val id: Long,
    val title: String,
    val description: String?,
    val status: String,
    val position: Int,
    val updatedAt: String,
)

@Serializable
internal data class AdminDemoDTO(
    val id: Long,
    val title: String,
    val url: String,
    val released: Boolean,
    val updatedAt: String,
)

@Serializable
internal data class MilestoneRequestDTO(
    val title: String,
    val description: String? = null,
    val status: String = "PENDING",
    val position: Int = 0,
)

@Serializable
internal data class DemoRequestDTO(
    val title: String,
    val url: String,
    val released: Boolean = false,
)

internal fun Milestone.toDTO() =
    AdminMilestoneDTO(id, title, description, status.name, position, updatedAt.toIso())

internal fun Demo.toDTO() = AdminDemoDTO(id, title, url, released, updatedAt.toIso())

internal fun AdminEngagement.toDTO() = AdminEngagementDTO(milestones.map { it.toDTO() }, demos.map { it.toDTO() })

// Bad status / blank title throw IllegalArgumentException → 400 via StatusPages.
internal fun MilestoneRequestDTO.toDraft(): MilestoneDraft {
    require(title.isNotBlank()) { "Milestone title is required" }
    return MilestoneDraft(
        title = title.trim(),
        description = description?.trim()?.takeIf { it.isNotEmpty() },
        status = MilestoneStatus.valueOf(status.uppercase()),
        position = position,
    )
}

internal fun DemoRequestDTO.toDraft(): DemoDraft {
    require(title.isNotBlank()) { "Demo title is required" }
    require(url.isNotBlank()) { "Demo url is required" }
    return DemoDraft(title = title.trim(), url = url.trim(), released = released)
}
