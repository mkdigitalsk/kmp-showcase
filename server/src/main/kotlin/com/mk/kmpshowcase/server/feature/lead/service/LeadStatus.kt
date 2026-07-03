package com.mk.kmpshowcase.server.feature.lead.service

// Lead lifecycle in the admin CRM. Advances by hand (admin sets it) — never automatically.
internal enum class LeadStatus { NEW, REVIEWING, ANALYZED, PROPOSAL_DRAFTED, PROPOSAL_SENT, WON, LOST }
