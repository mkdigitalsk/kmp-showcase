package com.mk.kmpshowcase.server.feature.lead.persistence

import com.mk.kmpshowcase.server.feature.lead.service.MilestoneStatus
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

// Delivery milestones for an engagement — email links back to LeadsTable (soft key, like lead_artifacts).
internal object MilestonesTable : LongIdTable("milestones") {
    val email = varchar("email", EMAIL_LENGTH)
    val title = varchar("title", TITLE_LENGTH)
    val description = text("description").nullable()
    val status = enumerationByName("status", STATUS_LENGTH, MilestoneStatus::class).default(MilestoneStatus.PENDING)
    val position = integer("position")
    val updatedAt = long("updated_at")

    private const val EMAIL_LENGTH = 320
    private const val TITLE_LENGTH = 200
    private const val STATUS_LENGTH = 20
}

// Demo builds/links the client can view — only released ones reach the client.
internal object DemosTable : LongIdTable("demos") {
    val email = varchar("email", EMAIL_LENGTH)
    val title = varchar("title", TITLE_LENGTH)
    val url = varchar("url", URL_LENGTH)
    val released = bool("released").default(false)
    val updatedAt = long("updated_at")

    private const val EMAIL_LENGTH = 320
    private const val TITLE_LENGTH = 200
    private const val URL_LENGTH = 2048
}
