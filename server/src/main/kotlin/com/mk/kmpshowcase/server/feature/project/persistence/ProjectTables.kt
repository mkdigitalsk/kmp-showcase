package com.mk.kmpshowcase.server.feature.project.persistence

import com.mk.kmpshowcase.server.feature.project.service.DocumentType
import com.mk.kmpshowcase.server.feature.project.service.MilestoneStatus
import com.mk.kmpshowcase.server.feature.project.service.ProjectEventType
import com.mk.kmpshowcase.server.feature.project.service.ProjectHealth
import com.mk.kmpshowcase.server.feature.project.service.ProjectState
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

private const val EMAIL_LENGTH = 320
private const val TITLE_LENGTH = 200
private const val URL_LENGTH = 2048
private const val ENUM_LENGTH = 20

// One project per client (email) for now — a unique index enforces it. project_id FKs are the future
// move if a client ever has multiple projects (YAGNI today).
internal object ProjectsTable : LongIdTable("projects") {
    val email = varchar("email", EMAIL_LENGTH).uniqueIndex()
    val state = enumerationByName("state", ENUM_LENGTH, ProjectState::class).default(ProjectState.ACTIVE)
    val health = enumerationByName("health", ENUM_LENGTH, ProjectHealth::class).default(ProjectHealth.GREEN)
    val startDate = long("start_date")
    val targetEndDate = long("target_end_date").nullable()
    val actualEndDate = long("actual_end_date").nullable()
}

internal object MilestonesTable : LongIdTable("milestones") {
    val email = varchar("email", EMAIL_LENGTH)
    val title = varchar("title", TITLE_LENGTH)
    val description = text("description").nullable()
    val status = enumerationByName("status", ENUM_LENGTH, MilestoneStatus::class).default(MilestoneStatus.PENDING)
    val plannedDate = long("planned_date").nullable()
    val completedDate = long("completed_date").nullable()
    val position = integer("position")
    val updatedAt = long("updated_at")
}

internal object DemosTable : LongIdTable("demos") {
    val email = varchar("email", EMAIL_LENGTH)
    val title = varchar("title", TITLE_LENGTH)
    val url = varchar("url", URL_LENGTH)
    val released = bool("released").default(false)
    val updatedAt = long("updated_at")
}

internal object DocumentsTable : LongIdTable("documents") {
    val email = varchar("email", EMAIL_LENGTH)
    val type = enumerationByName("type", ENUM_LENGTH, DocumentType::class)
    val title = varchar("title", TITLE_LENGTH)
    val url = varchar("url", URL_LENGTH)
    val updatedAt = long("updated_at")
}

// Append-only: rows are only ever inserted — never updated or deleted (immutable project history).
internal object ProjectEventsTable : LongIdTable("project_events") {
    val email = varchar("email", EMAIL_LENGTH)
    val type = enumerationByName("type", ENUM_LENGTH, ProjectEventType::class)
    val detail = varchar("detail", TITLE_LENGTH).nullable()
    val at = long("at")
}
