package com.mk.kmpshowcase.server.feature.lead.service

// Lead lifecycle in the admin CRM — one state machine (status IS the stage). Advances by hand (admin
// sets it), never automatically. The entity evolves along it: a lead (NEW / INTAKE), an opportunity
// (DISCOVERY / PROPOSAL), a client (WON). Delivery-side statuses (development → handover → done) arrive
// when S4+ merges this with ProjectState.
internal enum class LeadStatus { NEW, INTAKE, DISCOVERY, PROPOSAL, WON, DECLINED }

// Jira-style workflow: an explicit graph of allowed transitions (not ordinal — DECLINED reaches from any
// active status and can reopen to INTAKE). Anything else → 409 unless explicitly forced (audited as
// FORCED, outside the portal UI). WON is terminal here; the delivery lifecycle takes over from it (S4+).
internal val ALLOWED_TRANSITIONS: Map<LeadStatus, Set<LeadStatus>> = mapOf(
    LeadStatus.NEW to setOf(LeadStatus.INTAKE, LeadStatus.DECLINED),
    LeadStatus.INTAKE to setOf(LeadStatus.DISCOVERY, LeadStatus.DECLINED),
    LeadStatus.DISCOVERY to setOf(LeadStatus.PROPOSAL, LeadStatus.DECLINED),
    LeadStatus.PROPOSAL to setOf(LeadStatus.WON, LeadStatus.DECLINED),
    LeadStatus.WON to emptySet(),
    LeadStatus.DECLINED to setOf(LeadStatus.INTAKE),
)
