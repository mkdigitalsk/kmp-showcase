package com.mk.kmpshowcase.server.feature.lead.service

internal data class Lead(
    val id: Long,
    val email: String,
    val appType: String,
    val platforms: List<String>,
    val features: List<String>,
    val name: String?,
    val phone: String?,
    val note: String?,
    val hasDoc: Boolean,
    val createdAt: Long,
)

internal data class LeadDraft(
    val email: String,
    val appType: String,
    val platforms: List<String>,
    val features: List<String>,
    val name: String?,
    val phone: String?,
    val note: String?,
    val hasDoc: Boolean,
)
