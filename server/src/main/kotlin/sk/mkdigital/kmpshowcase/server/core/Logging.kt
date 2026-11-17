package sk.mkdigital.kmpshowcase.server.core

/** An email identifies a person, so only its first character and its domain are left. */
internal fun String.maskEmail(): String {
    val at = indexOf('@')
    if (at < 1) return "***"
    return "${take(1)}***${substring(at)}"
}

/** An email plain or URL-encoded (%40) — a request path carries either. */
private val EMAIL_IN_TEXT = Regex("""[A-Za-z0-9._+-]+(@|%40)[A-Za-z0-9.-]+\.[A-Za-z]{2,}""")

/** [maskEmail] for each email inside a longer text. */
internal fun String.maskEmails(): String = EMAIL_IN_TEXT.replace(this) { match ->
    val separator = match.groupValues[1]
    val local = match.value.substringBefore(separator)
    "${local.take(1)}***$separator${match.value.substringAfter(separator)}"
}
