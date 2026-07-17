package com.mk.kmpshowcase.server.feature.lead.persistence

import com.mk.kmpshowcase.server.feature.lead.service.Lead
import com.mk.kmpshowcase.server.feature.lead.service.LeadArtifact
import com.mk.kmpshowcase.server.feature.lead.service.LeadArtifactStage
import com.mk.kmpshowcase.server.feature.lead.service.LeadDraft
import com.mk.kmpshowcase.server.feature.lead.service.LeadStatus
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.update

internal class LeadRepositoryImpl : LeadRepository {

    override suspend fun findAll(): List<Lead> = suspendTransaction {
        LeadsTable.selectAll()
            .orderBy(LeadsTable.createdAt, SortOrder.DESC)
            .map { it.toLead() }
    }

    override suspend fun findByEmail(email: String): Lead? = suspendTransaction {
        latestByEmail(email)
    }

    override suspend fun create(draft: LeadDraft): Lead = suspendTransaction {
        val now = System.currentTimeMillis()
        val newId = LeadsTable.insert {
            it[email] = draft.email
            it[appType] = draft.appType
            it[platforms] = draft.platforms.joinToString(DELIMITER)
            it[features] = draft.features.joinToString(DELIMITER)
            it[name] = draft.name
            it[phone] = draft.phone
            it[note] = draft.note
            it[hasDoc] = draft.hasDoc
            it[hasDesign] = draft.hasDesign
            it[createdAt] = now
            it[status] = LeadStatus.NEW
            it[locale] = draft.locale
        } get LeadsTable.id

        Lead(
            id = newId.value,
            email = draft.email,
            appType = draft.appType,
            platforms = draft.platforms,
            features = draft.features,
            name = draft.name,
            phone = draft.phone,
            note = draft.note,
            hasDoc = draft.hasDoc,
            hasDesign = draft.hasDesign,
            createdAt = now,
            status = LeadStatus.NEW,
            locale = draft.locale,
        )
    }

    override suspend fun updateStatus(email: String, status: LeadStatus): Lead? = suspendTransaction {
        val updated = LeadsTable.update({ LeadsTable.email eq email }) { it[LeadsTable.status] = status }
        if (updated == 0) null else latestByEmail(email)
    }

    override suspend fun findArtifacts(email: String): List<LeadArtifact> = suspendTransaction {
        LeadArtifactsTable.selectAll()
            .where { LeadArtifactsTable.email eq email }
            .map {
                LeadArtifact(
                    stage = it[LeadArtifactsTable.stage],
                    content = it[LeadArtifactsTable.content],
                    updatedAt = it[LeadArtifactsTable.updatedAt],
                )
            }
    }

    override suspend fun upsertArtifact(email: String, stage: LeadArtifactStage, content: String) {
        suspendTransaction {
            val now = System.currentTimeMillis()
            val updated = LeadArtifactsTable.update(
                { (LeadArtifactsTable.email eq email) and (LeadArtifactsTable.stage eq stage) },
            ) {
                it[LeadArtifactsTable.content] = content
                it[updatedAt] = now
            }
            if (updated == 0) {
                LeadArtifactsTable.insert {
                    it[LeadArtifactsTable.email] = email
                    it[LeadArtifactsTable.stage] = stage
                    it[LeadArtifactsTable.content] = content
                    it[updatedAt] = now
                }
            }
        }
    }

    // Latest lead for an email (email is the identity; a customer may submit more than once).
    private fun latestByEmail(email: String): Lead? =
        LeadsTable.selectAll()
            .where { LeadsTable.email eq email }
            .orderBy(LeadsTable.createdAt, SortOrder.DESC)
            .firstOrNull()
            ?.toLead()

    private fun ResultRow.toLead() = Lead(
        id = this[LeadsTable.id].value,
        email = this[LeadsTable.email],
        appType = this[LeadsTable.appType],
        platforms = this[LeadsTable.platforms].splitOrEmpty(),
        features = this[LeadsTable.features].splitOrEmpty(),
        name = this[LeadsTable.name],
        phone = this[LeadsTable.phone],
        note = this[LeadsTable.note],
        hasDoc = this[LeadsTable.hasDoc],
        hasDesign = this[LeadsTable.hasDesign],
        createdAt = this[LeadsTable.createdAt],
        status = this[LeadsTable.status],
        locale = this[LeadsTable.locale],
    )

    private fun String.splitOrEmpty(): List<String> =
        if (isEmpty()) emptyList() else split(DELIMITER)

    private companion object {
        const val DELIMITER = "\n"
    }
}
