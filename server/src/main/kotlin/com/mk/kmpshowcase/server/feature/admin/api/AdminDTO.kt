package com.mk.kmpshowcase.server.feature.admin.api

import com.mk.kmpshowcase.server.feature.lead.service.Lead
import com.mk.kmpshowcase.server.feature.lead.service.LeadArtifact
import com.mk.kmpshowcase.server.feature.lead.service.LeadDetail
import java.time.Instant
import kotlinx.serialization.Serializable

// Epoch millis (how we store time) → ISO-8601 UTC string (how we put it on the wire) — self-describing + unambiguous.
private fun Long.toIso(): String = Instant.ofEpochMilli(this).toString()

@Serializable
internal data class AdminLeadDTO(
    val id: Long,
    val email: String,
    val appType: String,
    val platforms: List<String>,
    val features: List<String>,
    val name: String?,
    val phone: String?,
    val note: String?,
    val hasDoc: Boolean,
    val createdAt: String,
    val status: String,
)

@Serializable
internal data class AdminLeadArtifactDTO(
    val stage: String,
    val content: String,
    val updatedAt: String,
)

@Serializable
internal data class AdminLeadDetailDTO(
    val lead: AdminLeadDTO,
    val artifacts: List<AdminLeadArtifactDTO>,
)

@Serializable
internal data class UpdateStatusRequestDTO(val status: String)

@Serializable
internal data class SaveArtifactRequestDTO(val content: String)

internal fun Lead.toAdminLeadDTO() = AdminLeadDTO(
    id = id,
    email = email,
    appType = appType,
    platforms = platforms,
    features = features,
    name = name,
    phone = phone,
    note = note,
    hasDoc = hasDoc,
    createdAt = createdAt.toIso(),
    status = status.name,
)

internal fun LeadArtifact.toDTO() = AdminLeadArtifactDTO(stage = stage.name, content = content, updatedAt = updatedAt.toIso())

internal fun LeadDetail.toDTO() = AdminLeadDetailDTO(lead = lead.toAdminLeadDTO(), artifacts = artifacts.map { it.toDTO() })
