package com.mk.kmpshowcase.server.core

// PII discipline (conventions §9): emails identify clients across this schema — log them masked.
internal fun String.maskEmail(): String {
    val at = indexOf('@')
    if (at < 1) return "***"
    return "${take(1)}***${substring(at)}"
}

// Matches plain and URL-encoded (%40) emails — request paths carry both (/admin/leads/a%40b.com).
private val EMAIL_IN_TEXT = Regex("""[A-Za-z0-9._+-]+(@|%40)[A-Za-z0-9.-]+\.[A-Za-z]{2,}""")

// Masks every email found in free text (log messages, request paths) — first char + *** + domain.
internal fun String.maskEmails(): String = EMAIL_IN_TEXT.replace(this) { match ->
    val separator = match.groupValues[1]
    val local = match.value.substringBefore(separator)
    "${local.take(1)}***$separator${match.value.substringAfter(separator)}"
}
