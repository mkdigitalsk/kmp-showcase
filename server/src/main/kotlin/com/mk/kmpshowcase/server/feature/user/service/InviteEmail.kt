package com.mk.kmpshowcase.server.feature.user.service

import java.util.Locale
import java.util.ResourceBundle

// Client-portal invitation, rendered in the client's language (falls back to English).
// Copy lives in resources/i18n/invite[_xx].properties. Plain text — transactional, one link.
internal object InviteEmail {

    fun subject(locale: String?): String = bundle(locale).getString("subject")

    fun text(link: String, locale: String?): String {
        val b = bundle(locale)
        return buildString {
            appendLine("${b.getString("greeting")},")
            appendLine()
            appendLine(b.getString("intro"))
            appendLine()
            appendLine(link)
            appendLine()
            appendLine(b.getString("expiry"))
            appendLine()
            appendLine(b.getString("signoff"))
            appendLine(SIGNATURE_NAME)
            appendLine(b.getString("signatureLine"))
        }
    }

    private fun bundle(locale: String?): ResourceBundle =
        ResourceBundle.getBundle("i18n.invite", resolveLocale(locale), NO_FALLBACK)

    private fun resolveLocale(locale: String?): Locale =
        locale?.takeIf { it.isNotBlank() }?.let { Locale.forLanguageTag(it) } ?: Locale.ENGLISH

    private val NO_FALLBACK: ResourceBundle.Control =
        ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES)

    private const val SIGNATURE_NAME = "Miroslav Kušnír"
}
