package com.mk.kmpshowcase.server.feature.lead.api

import com.mk.kmpshowcase.server.feature.lead.service.LeadDraft
import kotlinx.serialization.Serializable

@Serializable
internal data class LeadRequestDTO(
    val email: String,
    val appType: String,
    val platforms: List<String> = emptyList(),
    val features: List<String> = emptyList(),
    val name: String? = null,
    val phone: String? = null,
    val note: String? = null,
    val hasDoc: Boolean = false,
    val locale: String? = null,
)

@Serializable
internal data class LeadResponseDTO(val success: Boolean)

internal fun LeadRequestDTO.toDraft() = LeadDraft(
    email = email.trim(),
    appType = appType.trim(),
    platforms = platforms,
    features = features,
    name = name?.trim()?.takeIf { it.isNotEmpty() },
    phone = phone?.trim()?.takeIf { it.isNotEmpty() },
    note = note?.trim()?.takeIf { it.isNotEmpty() },
    hasDoc = hasDoc,
    locale = locale?.trim()?.takeIf { it.isNotEmpty() },
)
