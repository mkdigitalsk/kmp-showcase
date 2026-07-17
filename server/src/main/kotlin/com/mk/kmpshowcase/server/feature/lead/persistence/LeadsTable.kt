package com.mk.kmpshowcase.server.feature.lead.persistence

import com.mk.kmpshowcase.server.feature.lead.service.LeadArtifactStage
import com.mk.kmpshowcase.server.feature.lead.service.LeadStatus
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

internal object LeadsTable : LongIdTable("leads") {
    val email = varchar("email", EMAIL_LENGTH)
    val appType = varchar("app_type", APP_TYPE_LENGTH)
    val platforms = text("platforms")
    val features = text("features")
    val name = varchar("name", NAME_LENGTH).nullable()
    val phone = varchar("phone", PHONE_LENGTH).nullable()
    val note = text("note").nullable()
    val hasDoc = bool("has_doc")
    val hasDesign = bool("has_design").default(false)
    val createdAt = long("created_at")
    val status = enumerationByName("status", STATUS_LENGTH, LeadStatus::class).default(LeadStatus.NEW)
    val locale = varchar("locale", LOCALE_LENGTH).nullable()

    private const val EMAIL_LENGTH = 320
    private const val APP_TYPE_LENGTH = 120
    private const val NAME_LENGTH = 200
    private const val PHONE_LENGTH = 40
    private const val STATUS_LENGTH = 20
    private const val LOCALE_LENGTH = 20
}

// The documents the `analyze lead` pipeline produces for a lead (requirements, questions, analysis,
// proposal, internal-scope) — one row per (email, stage), upserted. Email links back to LeadsTable.
internal object LeadArtifactsTable : LongIdTable("lead_artifacts") {
    val email = varchar("email", EMAIL_LENGTH)
    val stage = enumerationByName("stage", STAGE_LENGTH, LeadArtifactStage::class)
    val content = text("content")
    val updatedAt = long("updated_at")

    init {
        uniqueIndex(email, stage)
    }

    private const val EMAIL_LENGTH = 320
    private const val STAGE_LENGTH = 20
}
