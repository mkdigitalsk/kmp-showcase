package com.mk.kmpshowcase.server.feature.lead.service

import java.util.Locale
import java.util.ResourceBundle
import kotlinx.html.FlowContent
import kotlinx.html.TABLE
import kotlinx.html.TD
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.br
import kotlinx.html.html
import kotlinx.html.lang
import kotlinx.html.p
import kotlinx.html.span
import kotlinx.html.stream.createHTML
import kotlinx.html.strong
import kotlinx.html.style
import kotlinx.html.table
import kotlinx.html.td
import kotlinx.html.tr

// Client-facing lead confirmation, rendered in the requester's language (falls back to English).
// Copy lives in resources/i18n/confirmation[_xx].properties; the HTML is built with the kotlinx.html
// DSL (type-safe, auto-escaped). Sent as multipart text + HTML; transactional tone, no images.
internal object ClientConfirmationEmail {

    fun subject(locale: String?) = strings(locale).subject

    fun text(lead: Lead, locale: String?): String {
        val s = strings(locale)
        return buildString {
            appendLine("${s.greeting}${lead.name?.let { " $it" } ?: ""},")
            appendLine()
            appendLine(s.intro)
            appendLine()
            appendLine(s.requestHeading)
            appendLine("  ${s.projectLabel}: ${lead.appType}")
            if (lead.platforms.isNotEmpty()) appendLine("  ${s.platformsLabel}: ${lead.platforms.joinToString(", ")}")
            if (lead.features.isNotEmpty()) appendLine("  ${s.featuresLabel}: ${lead.features.joinToString(", ")}")
            appendLine()
            appendLine(s.replyLine)
            appendLine()
            appendLine(s.tagline)
            appendLine("mkdigital.sk")
        }
    }

    fun html(lead: Lead, locale: String?): String {
        val s = strings(locale)
        val greeting = "${s.greeting}${lead.name?.let { " $it" } ?: ""},"
        val rows = detailRows(lead, s)
        return DOCTYPE + createHTML().html {
            lang = s.lang
            body {
                style = BODY
                shell { card(greeting, s, rows) }
            }
        }
    }

    private fun FlowContent.shell(content: TD.() -> Unit) {
        presentationTable(OUTER) {
            tr { td { attributes["align"] = "center"; content() } }
        }
    }

    private fun TD.card(greeting: String, s: Strings, rows: List<Pair<String, String>>) {
        presentationTable(CARD) {
            tr { td { style = HEADER; span { style = BRAND; +"MK Digital" } } }
            tr {
                td {
                    style = CONTENT
                    p { style = GREETING; +greeting }
                    p { style = INTRO; +s.intro }
                    requestBox(s, rows)
                    p { style = REPLY; +s.replyLine }
                }
            }
            tr { td { style = FOOTER; footer(s) } }
        }
    }

    private fun TD.requestBox(s: Strings, rows: List<Pair<String, String>>) {
        presentationTable(BOX) {
            tr {
                td {
                    style = BOX_CELL
                    p { style = HEADING; +s.requestHeading }
                    detailTable(rows)
                }
            }
        }
    }

    private fun TD.detailTable(rows: List<Pair<String, String>>) {
        presentationTable(null) {
            rows.forEach { (label, value) -> detailRow(label, value) }
        }
    }

    private fun TABLE.detailRow(label: String, value: String) {
        tr { td { style = ROW; strong { style = STRONG; +"$label:" }; +" $value" } }
    }

    private fun TD.footer(s: Strings) {
        p {
            style = FOOTER_TEXT
            +s.tagline
            br { }
            a(href = "https://mkdigital.sk") { style = LINK; +"mkdigital.sk" }
        }
    }

    private fun FlowContent.presentationTable(styleValue: String?, block: TABLE.() -> Unit) {
        table {
            attributes["role"] = "presentation"
            attributes["width"] = "100%"
            attributes["cellpadding"] = "0"
            attributes["cellspacing"] = "0"
            styleValue?.let { style = it }
            block()
        }
    }

    private fun detailRows(lead: Lead, s: Strings): List<Pair<String, String>> = buildList {
        add(s.projectLabel to lead.appType)
        if (lead.platforms.isNotEmpty()) add(s.platformsLabel to lead.platforms.joinToString(", "))
        if (lead.features.isNotEmpty()) add(s.featuresLabel to lead.features.joinToString(", "))
    }

    private fun strings(locale: String?): Strings {
        val bundle = ResourceBundle.getBundle("i18n.confirmation", resolveLocale(locale), NO_FALLBACK)
        return Strings(
            lang = bundle.getString("lang"),
            subject = bundle.getString("subject"),
            greeting = bundle.getString("greeting"),
            intro = bundle.getString("intro"),
            requestHeading = bundle.getString("requestHeading"),
            projectLabel = bundle.getString("projectLabel"),
            platformsLabel = bundle.getString("platformsLabel"),
            featuresLabel = bundle.getString("featuresLabel"),
            replyLine = bundle.getString("replyLine"),
            tagline = bundle.getString("tagline"),
        )
    }

    private fun resolveLocale(code: String?): Locale = when (code?.lowercase()?.take(2)) {
        "sk" -> Locale.forLanguageTag("sk")
        "cs" -> Locale.forLanguageTag("cs")
        "de" -> Locale.forLanguageTag("de")
        else -> Locale.ENGLISH
    }

    private data class Strings(
        val lang: String,
        val subject: String,
        val greeting: String,
        val intro: String,
        val requestHeading: String,
        val projectLabel: String,
        val platformsLabel: String,
        val featuresLabel: String,
        val replyLine: String,
        val tagline: String,
    )

    // Resolve only the requested locale → base (English); never the JVM default locale.
    private val NO_FALLBACK = ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES)

    private const val DOCTYPE = "<!DOCTYPE html>\n"
    private const val BODY = "margin:0;padding:0;background:#f4f6f8;font-family:Arial,Helvetica,sans-serif;"
    private const val OUTER = "background:#f4f6f8;padding:24px 12px;"
    private const val CARD = "max-width:520px;background:#ffffff;border-radius:12px;border:1px solid #eaedf0;"
    private const val HEADER = "background:#0E2A47;padding:18px 28px;border-radius:12px 12px 0 0;"
    private const val BRAND = "color:#ffffff;font-size:17px;font-weight:700;letter-spacing:0.3px;"
    private const val CONTENT = "padding:28px;"
    private const val GREETING = "margin:0 0 16px;font-size:16px;color:#0E2A47;"
    private const val INTRO = "margin:0 0 22px;font-size:15px;line-height:1.55;color:#33414f;"
    private const val BOX = "background:#f4f6f8;border-radius:8px;margin:0 0 22px;"
    private const val BOX_CELL = "padding:16px 18px;"
    private const val HEADING = "margin:0 0 8px;font-size:12px;font-weight:700;color:#0E2A47;text-transform:uppercase;"
    private const val ROW = "padding:3px 0;font-size:14px;color:#33414f;"
    private const val STRONG = "color:#0E2A47;"
    private const val REPLY = "margin:0;font-size:15px;line-height:1.55;color:#33414f;"
    private const val FOOTER = "padding:18px 28px;border-top:1px solid #eaedf0;"
    private const val FOOTER_TEXT = "margin:0;font-size:13px;color:#8a97a3;line-height:1.5;"
    private const val LINK = "color:#37C2B4;text-decoration:none;"
}
