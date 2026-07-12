package com.mk.kmpshowcase.server.feature.lead.persistence

import com.mk.kmpshowcase.server.feature.lead.service.Demo
import com.mk.kmpshowcase.server.feature.lead.service.DemoDraft
import com.mk.kmpshowcase.server.feature.lead.service.Lead
import com.mk.kmpshowcase.server.feature.lead.service.LeadArtifact
import com.mk.kmpshowcase.server.feature.lead.service.LeadArtifactStage
import com.mk.kmpshowcase.server.feature.lead.service.LeadDraft
import com.mk.kmpshowcase.server.feature.lead.service.LeadStatus
import com.mk.kmpshowcase.server.feature.lead.service.Milestone
import com.mk.kmpshowcase.server.feature.lead.service.MilestoneDraft
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
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
            it[createdAt] = now
            it[status] = LeadStatus.NEW
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
            createdAt = now,
            status = LeadStatus.NEW,
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

    override suspend fun findMilestones(email: String): List<Milestone> = suspendTransaction {
        MilestonesTable.selectAll()
            .where { MilestonesTable.email eq email }
            .orderBy(MilestonesTable.position, SortOrder.ASC)
            .map { it.toMilestone() }
    }

    override suspend fun addMilestone(email: String, draft: MilestoneDraft): Milestone = suspendTransaction {
        val now = System.currentTimeMillis()
        val newId = MilestonesTable.insert {
            it[MilestonesTable.email] = email
            it[title] = draft.title
            it[description] = draft.description
            it[status] = draft.status
            it[position] = draft.position
            it[updatedAt] = now
        } get MilestonesTable.id
        Milestone(newId.value, draft.title, draft.description, draft.status, draft.position, now)
    }

    override suspend fun updateMilestone(id: Long, draft: MilestoneDraft): Milestone? = suspendTransaction {
        val now = System.currentTimeMillis()
        val updated = MilestonesTable.update({ MilestonesTable.id eq id }) {
            it[title] = draft.title
            it[description] = draft.description
            it[status] = draft.status
            it[position] = draft.position
            it[updatedAt] = now
        }
        if (updated == 0) null else MilestonesTable.selectAll().where { MilestonesTable.id eq id }.first().toMilestone()
    }

    override suspend fun deleteMilestone(id: Long): Boolean = suspendTransaction {
        MilestonesTable.deleteWhere { MilestonesTable.id eq id } > 0
    }

    override suspend fun findDemos(email: String): List<Demo> = suspendTransaction {
        DemosTable.selectAll()
            .where { DemosTable.email eq email }
            .orderBy(DemosTable.updatedAt, SortOrder.DESC)
            .map { it.toDemo() }
    }

    override suspend fun addDemo(email: String, draft: DemoDraft): Demo = suspendTransaction {
        val now = System.currentTimeMillis()
        val newId = DemosTable.insert {
            it[DemosTable.email] = email
            it[title] = draft.title
            it[url] = draft.url
            it[released] = draft.released
            it[updatedAt] = now
        } get DemosTable.id
        Demo(newId.value, draft.title, draft.url, draft.released, now)
    }

    override suspend fun updateDemo(id: Long, draft: DemoDraft): Demo? = suspendTransaction {
        val now = System.currentTimeMillis()
        val updated = DemosTable.update({ DemosTable.id eq id }) {
            it[title] = draft.title
            it[url] = draft.url
            it[released] = draft.released
            it[updatedAt] = now
        }
        if (updated == 0) null else DemosTable.selectAll().where { DemosTable.id eq id }.first().toDemo()
    }

    override suspend fun deleteDemo(id: Long): Boolean = suspendTransaction {
        DemosTable.deleteWhere { DemosTable.id eq id } > 0
    }

    private fun ResultRow.toMilestone() = Milestone(
        id = this[MilestonesTable.id].value,
        title = this[MilestonesTable.title],
        description = this[MilestonesTable.description],
        status = this[MilestonesTable.status],
        position = this[MilestonesTable.position],
        updatedAt = this[MilestonesTable.updatedAt],
    )

    private fun ResultRow.toDemo() = Demo(
        id = this[DemosTable.id].value,
        title = this[DemosTable.title],
        url = this[DemosTable.url],
        released = this[DemosTable.released],
        updatedAt = this[DemosTable.updatedAt],
    )

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
        createdAt = this[LeadsTable.createdAt],
        status = this[LeadsTable.status],
    )

    private fun String.splitOrEmpty(): List<String> =
        if (isEmpty()) emptyList() else split(DELIMITER)

    private companion object {
        const val DELIMITER = "\n"
    }
}
