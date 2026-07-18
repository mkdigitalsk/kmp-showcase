package com.mk.kmpshowcase.server.feature.lead.service

// Lead lifecycle in the admin CRM. Advances by hand (admin sets it) — never automatically.
internal enum class LeadStatus { NEW, REVIEWING, ANALYZED, PROPOSAL_DRAFTED, PROPOSAL_SENT, WON, LOST }

// Jira-style workflow: statuses + an explicit graph of allowed transitions (not ordinal succession —
// decline reaches LOST from any active stage, PROPOSAL_DRAFTED is skippable, LOST can reopen).
// Anything else is rejected (409) unless explicitly forced — the override lives outside the portal UI
// and is audited as FORCED. WON is terminal: unwinding a won deal is always a forced, audited act.
internal val ALLOWED_TRANSITIONS: Map<LeadStatus, Set<LeadStatus>> = mapOf(
    LeadStatus.NEW to setOf(LeadStatus.REVIEWING, LeadStatus.LOST),
    LeadStatus.REVIEWING to setOf(LeadStatus.ANALYZED, LeadStatus.LOST),
    LeadStatus.ANALYZED to setOf(LeadStatus.PROPOSAL_DRAFTED, LeadStatus.PROPOSAL_SENT, LeadStatus.LOST),
    LeadStatus.PROPOSAL_DRAFTED to setOf(LeadStatus.PROPOSAL_SENT, LeadStatus.LOST),
    LeadStatus.PROPOSAL_SENT to setOf(LeadStatus.WON, LeadStatus.LOST),
    LeadStatus.WON to emptySet(),
    LeadStatus.LOST to setOf(LeadStatus.REVIEWING),
)
