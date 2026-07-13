package com.mk.kmpshowcase.server.feature.project.persistence

import com.mk.kmpshowcase.server.feature.project.service.Demo
import com.mk.kmpshowcase.server.feature.project.service.DemoDraft
import com.mk.kmpshowcase.server.feature.project.service.Document
import com.mk.kmpshowcase.server.feature.project.service.DocumentDraft
import com.mk.kmpshowcase.server.feature.project.service.Milestone
import com.mk.kmpshowcase.server.feature.project.service.MilestoneDraft
import com.mk.kmpshowcase.server.feature.project.service.Payment
import com.mk.kmpshowcase.server.feature.project.service.PaymentDraft
import com.mk.kmpshowcase.server.feature.project.service.Project
import com.mk.kmpshowcase.server.feature.project.service.ProjectDraft
import com.mk.kmpshowcase.server.feature.project.service.ProjectEvent
import com.mk.kmpshowcase.server.feature.project.service.ProjectEventType
import com.mk.kmpshowcase.server.feature.project.service.ProjectHealth
import com.mk.kmpshowcase.server.feature.project.service.ProjectState
import com.mk.kmpshowcase.server.feature.project.service.ScopeItem
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.SortOrder
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.update

private const val LIST_DELIMITER = "\n"
private val scopeJson = Json { ignoreUnknownKeys = true }

// Acceptance criteria are one-line strings — newline-joined text.
private fun List<String>.encode(): String = joinToString(LIST_DELIMITER)

private fun String?.decodeList(): List<String> =
    this?.split(LIST_DELIMITER)?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()

// Scope items are value objects — persisted as a JSON array in a text column.
private fun List<ScopeItem>.encodeScope(): String = scopeJson.encodeToString(this)

private fun String?.decodeScope(): List<ScopeItem> =
    this?.takeIf { it.isNotBlank() }?.let { scopeJson.decodeFromString<List<ScopeItem>>(it) } ?: emptyList()

internal class ProjectRepositoryImpl : ProjectRepository {

    override suspend fun find(email: String): Project? = suspendTransaction {
        ProjectsTable.selectAll().where { ProjectsTable.email eq email }.firstOrNull()?.toProject()
    }

    override suspend fun create(email: String, draft: ProjectDraft): Project = suspendTransaction {
        ProjectsTable.insert {
            it[ProjectsTable.email] = email
            it[state] = ProjectState.ACTIVE
            it[health] = draft.health
            it[startDate] = draft.startDate
            it[targetEndDate] = draft.targetEndDate
            it[actualEndDate] = null
            it[scope] = draft.scope.encodeScope()
            it[outOfScope] = draft.outOfScope.encodeScope()
        }
        Project(
            email, ProjectState.ACTIVE, draft.health, draft.startDate, draft.targetEndDate, null,
            draft.scope, draft.outOfScope,
        )
    }

    override suspend fun update(
        email: String,
        state: ProjectState,
        health: ProjectHealth,
        targetEndDate: Long?,
        actualEndDate: Long?,
        scope: List<ScopeItem>,
        outOfScope: List<ScopeItem>,
    ): Project? = suspendTransaction {
        val updated = ProjectsTable.update({ ProjectsTable.email eq email }) {
            it[ProjectsTable.state] = state
            it[ProjectsTable.health] = health
            it[ProjectsTable.targetEndDate] = targetEndDate
            it[ProjectsTable.actualEndDate] = actualEndDate
            it[ProjectsTable.scope] = scope.encodeScope()
            it[ProjectsTable.outOfScope] = outOfScope.encodeScope()
        }
        if (updated == 0) null else ProjectsTable.selectAll().where { ProjectsTable.email eq email }.first().toProject()
    }

    override suspend fun findDocuments(email: String): List<Document> = suspendTransaction {
        DocumentsTable.selectAll()
            .where { DocumentsTable.email eq email }
            .orderBy(DocumentsTable.updatedAt, SortOrder.DESC)
            .map { it.toDocument() }
    }

    override suspend fun addDocument(email: String, draft: DocumentDraft): Document = suspendTransaction {
        val now = System.currentTimeMillis()
        val newId = DocumentsTable.insert {
            it[DocumentsTable.email] = email
            it[type] = draft.type
            it[title] = draft.title
            it[url] = draft.url
            it[updatedAt] = now
        } get DocumentsTable.id
        Document(newId.value, draft.type, draft.title, draft.url, now)
    }

    override suspend fun deleteDocument(id: Long): Boolean = suspendTransaction {
        DocumentsTable.deleteWhere { DocumentsTable.id eq id } > 0
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
            it[plannedDate] = draft.plannedDate
            it[completedDate] = draft.completedDate
            it[position] = draft.position
            it[updatedAt] = now
            it[acceptanceCriteria] = draft.acceptanceCriteria.encode()
        } get MilestonesTable.id
        Milestone(
            newId.value, draft.title, draft.description, draft.status, draft.plannedDate,
            draft.completedDate, draft.position, now, draft.acceptanceCriteria,
        )
    }

    override suspend fun updateMilestone(id: Long, draft: MilestoneDraft): Milestone? = suspendTransaction {
        val now = System.currentTimeMillis()
        val updated = MilestonesTable.update({ MilestonesTable.id eq id }) {
            it[title] = draft.title
            it[description] = draft.description
            it[status] = draft.status
            it[plannedDate] = draft.plannedDate
            it[completedDate] = draft.completedDate
            it[position] = draft.position
            it[updatedAt] = now
            it[acceptanceCriteria] = draft.acceptanceCriteria.encode()
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

    override suspend fun findPayments(email: String): List<Payment> = suspendTransaction {
        PaymentsTable.selectAll()
            .where { PaymentsTable.email eq email }
            .orderBy(PaymentsTable.position, SortOrder.ASC)
            .map { it.toPayment() }
    }

    override suspend fun addPayment(email: String, draft: PaymentDraft): Payment = suspendTransaction {
        val newId = PaymentsTable.insert {
            it[PaymentsTable.email] = email
            it[label] = draft.label
            it[amountCents] = draft.amountCents
            it[currency] = draft.currency
            it[status] = draft.status
            it[position] = draft.position
        } get PaymentsTable.id
        Payment(newId.value, draft.label, draft.amountCents, draft.currency, draft.status, draft.position)
    }

    override suspend fun updatePayment(id: Long, draft: PaymentDraft): Payment? = suspendTransaction {
        val updated = PaymentsTable.update({ PaymentsTable.id eq id }) {
            it[label] = draft.label
            it[amountCents] = draft.amountCents
            it[currency] = draft.currency
            it[status] = draft.status
            it[position] = draft.position
        }
        if (updated == 0) null else PaymentsTable.selectAll().where { PaymentsTable.id eq id }.first().toPayment()
    }

    override suspend fun deletePayment(id: Long): Boolean = suspendTransaction {
        PaymentsTable.deleteWhere { PaymentsTable.id eq id } > 0
    }

    override suspend fun appendEvent(email: String, type: ProjectEventType, detail: String?) {
        suspendTransaction {
            ProjectEventsTable.insert {
                it[ProjectEventsTable.email] = email
                it[ProjectEventsTable.type] = type
                it[ProjectEventsTable.detail] = detail
                it[at] = System.currentTimeMillis()
            }
        }
    }

    override suspend fun findEvents(email: String): List<ProjectEvent> = suspendTransaction {
        ProjectEventsTable.selectAll()
            .where { ProjectEventsTable.email eq email }
            .orderBy(ProjectEventsTable.at, SortOrder.DESC)
            .map { it.toEvent() }
    }

    private fun ResultRow.toEvent() = ProjectEvent(
        id = this[ProjectEventsTable.id].value,
        type = this[ProjectEventsTable.type],
        detail = this[ProjectEventsTable.detail],
        at = this[ProjectEventsTable.at],
    )

    private fun ResultRow.toProject() = Project(
        email = this[ProjectsTable.email],
        state = this[ProjectsTable.state],
        health = this[ProjectsTable.health],
        startDate = this[ProjectsTable.startDate],
        targetEndDate = this[ProjectsTable.targetEndDate],
        actualEndDate = this[ProjectsTable.actualEndDate],
        scope = this[ProjectsTable.scope].decodeScope(),
        outOfScope = this[ProjectsTable.outOfScope].decodeScope(),
    )

    private fun ResultRow.toDocument() = Document(
        id = this[DocumentsTable.id].value,
        type = this[DocumentsTable.type],
        title = this[DocumentsTable.title],
        url = this[DocumentsTable.url],
        updatedAt = this[DocumentsTable.updatedAt],
    )

    private fun ResultRow.toMilestone() = Milestone(
        id = this[MilestonesTable.id].value,
        title = this[MilestonesTable.title],
        description = this[MilestonesTable.description],
        status = this[MilestonesTable.status],
        plannedDate = this[MilestonesTable.plannedDate],
        completedDate = this[MilestonesTable.completedDate],
        position = this[MilestonesTable.position],
        updatedAt = this[MilestonesTable.updatedAt],
        acceptanceCriteria = this[MilestonesTable.acceptanceCriteria].decodeList(),
    )

    private fun ResultRow.toDemo() = Demo(
        id = this[DemosTable.id].value,
        title = this[DemosTable.title],
        url = this[DemosTable.url],
        released = this[DemosTable.released],
        updatedAt = this[DemosTable.updatedAt],
    )

    private fun ResultRow.toPayment() = Payment(
        id = this[PaymentsTable.id].value,
        label = this[PaymentsTable.label],
        amountCents = this[PaymentsTable.amountCents],
        currency = this[PaymentsTable.currency],
        status = this[PaymentsTable.status],
        position = this[PaymentsTable.position],
    )
}
