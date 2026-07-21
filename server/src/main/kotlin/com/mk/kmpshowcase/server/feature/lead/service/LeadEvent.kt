package com.mk.kmpshowcase.server.feature.lead.service

// The lead's durable ledger — every mutation and every client-facing send leaves a row. It is the
// audit trail (who-did-what survives sessions) AND the idempotency guard for operator emails: an
// email kind is offered only if the ledger has no EMAIL_SENT row for it yet.
internal enum class LeadEventType { SUBMITTED, STATUS_CHANGED, ARTIFACT_SAVED, EMAIL_SENT, INVITE_SENT, NDA_SENT }

internal data class LeadEvent(
    val id: Long,
    val type: LeadEventType,
    val detail: String?,
    val at: Long,
)
