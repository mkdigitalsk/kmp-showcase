package com.mk.kmpshowcase.server.feature.lead.service

import java.time.Year
import java.util.Locale
import java.util.ResourceBundle
import kotlinx.html.FlowContent
import kotlinx.html.TABLE
import kotlinx.html.TD
import kotlinx.html.a
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.head
import kotlinx.html.html
import kotlinx.html.img
import kotlinx.html.lang
import kotlinx.html.meta
import kotlinx.html.p
import kotlinx.html.stream.createHTML
import kotlinx.html.strong
import kotlinx.html.style
import kotlinx.html.table
import kotlinx.html.td
import kotlinx.html.tr
import kotlinx.html.unsafe

// Client-facing lead confirmation, rendered in the requester's language (falls back to English).
// Copy lives in resources/i18n/confirmation[_xx].properties; the HTML is built with the kotlinx.html
// DSL (type-safe, auto-escaped). 600px single-column table layout, WCAG-contrast text, dark-mode
// signalled, hidden preheader. Sent as multipart text + HTML; transactional, no images.
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
            appendLine("$COMPANY · ${s.tagline}")
            appendLine(ADDRESS)
            appendLine(s.footerReason)
            appendLine("mkdigital.sk · https://mkdigital.sk/privacy")
            appendLine("© ${Year.now().value} $COMPANY")
        }
    }

    fun html(lead: Lead, locale: String?): String {
        val s = strings(locale)
        val greeting = "${s.greeting}${lead.name?.let { " $it" } ?: ""},"
        val rows = detailRows(lead, s)
        return DOCTYPE + createHTML().html {
            lang = s.lang
            head {
                meta { attributes["charset"] = "utf-8" }
                meta { attributes["name"] = "viewport"; attributes["content"] = "width=device-width,initial-scale=1" }
                meta { attributes["name"] = "color-scheme"; attributes["content"] = "light dark" }
                meta { attributes["name"] = "supported-color-schemes"; attributes["content"] = "light dark" }
                style { unsafe { +ROOT_CSS } }
            }
            body {
                style = BODY
                div { style = PREHEADER; +s.preheader }
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
            tr { td { style = HEADER; headerLockup() } }
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

    // Logo = pre-rendered PNG of the canonical SVG lockup (email clients strip inline SVG), hosted on
    // the API. Shows on the navy header; alt text covers image-blocking clients.
    private fun TD.headerLockup() {
        a(href = "https://mkdigital.sk") {
            img(src = LOGO_URL, alt = "MK Digital — Software Studio") { style = LOGO_IMG }
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
        p { style = FOOTER_COMPANY; +"$COMPANY · ${s.tagline}" }
        p { style = FOOTER_ADDR; +ADDRESS }
        p { style = FOOTER_TEXT; +s.footerReason }
        p {
            style = FOOTER_LINKS
            a(href = "https://mkdigital.sk") { style = LINK; +"mkdigital.sk" }
            +"  ·  "
            a(href = "https://mkdigital.sk/privacy") { style = LINK; +s.privacy }
        }
        p { style = FOOTER_COPY; +"© ${Year.now().value} $COMPANY" }
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
            preheader = bundle.getString("preheader"),
            greeting = bundle.getString("greeting"),
            intro = bundle.getString("intro"),
            requestHeading = bundle.getString("requestHeading"),
            projectLabel = bundle.getString("projectLabel"),
            platformsLabel = bundle.getString("platformsLabel"),
            featuresLabel = bundle.getString("featuresLabel"),
            replyLine = bundle.getString("replyLine"),
            tagline = bundle.getString("tagline"),
            footerReason = bundle.getString("footerReason"),
            privacy = bundle.getString("privacy"),
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
        val preheader: String,
        val greeting: String,
        val intro: String,
        val requestHeading: String,
        val projectLabel: String,
        val platformsLabel: String,
        val featuresLabel: String,
        val replyLine: String,
        val tagline: String,
        val footerReason: String,
        val privacy: String,
    )

    // Resolve only the requested locale → base (English); never the JVM default locale.
    private val NO_FALLBACK = ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES)

    private const val DOCTYPE = "<!DOCTYPE html>\n"
    private const val COMPANY = "MK Digital s.r.o."
    private const val ROOT_CSS = ":root{color-scheme:light dark;supported-color-schemes:light dark;}"
    private const val BODY = "margin:0;padding:0;background:#f4f6f8;font-family:Arial,Helvetica,sans-serif;"
    private const val PREHEADER = "display:none;font-size:1px;max-height:0;overflow:hidden;color:#f4f6f8;"
    private const val OUTER = "background:#f4f6f8;padding:24px 12px;"
    private const val CARD = "max-width:600px;background:#ffffff;border-radius:12px;overflow:hidden;"
    private const val HEADER = "background:#0E2A47;padding:20px 32px;border-bottom:3px solid #37C2B4;"
    private const val LOGO_URL = "https://api.mkdigital.sk/assets/mk-digital-logo.png"
    private const val LOGO_IMG = "display:block;width:240px;max-width:60%;height:auto;border:0;"
    private const val CONTENT = "padding:32px;"
    private const val GREETING = "margin:0 0 18px;font-size:16px;color:#0E2A47;"
    private const val INTRO = "margin:0 0 24px;font-size:15px;line-height:1.6;color:#33414f;"
    private const val BOX = "background:#f4f6f8;border-radius:8px;margin:0 0 24px;"
    private const val BOX_CELL = "padding:18px 20px;"
    private const val HEADING = "margin:0 0 10px;font-size:12px;font-weight:700;color:#0E2A47;text-transform:uppercase;"
    private const val ROW = "padding:4px 0;font-size:14px;line-height:1.5;color:#33414f;"
    private const val STRONG = "color:#0E2A47;"
    private const val REPLY = "margin:0;font-size:15px;line-height:1.6;color:#33414f;"
    private const val FOOTER = "padding:22px 32px;border-top:1px solid #eaedf0;"
    private const val FOOTER_COMPANY = "margin:0 0 6px;font-size:13px;font-weight:700;color:#0E2A47;"
    private const val FOOTER_ADDR = "margin:0 0 12px;font-size:12px;line-height:1.6;color:#8a97a3;"
    private const val FOOTER_TEXT = "margin:0 0 12px;font-size:12px;line-height:1.6;color:#5b6470;"
    private const val FOOTER_LINKS = "margin:0 0 12px;font-size:13px;color:#5b6470;"
    private const val FOOTER_COPY = "margin:0;font-size:12px;color:#8a97a3;"
    private const val ADDRESS = "Medená 15387/2, 974 05 Banská Bystrica, Slovakia · IČO 55 450 229"
    private const val LINK = "color:#0E2A47;text-decoration:underline;"
}
