package com.mk.kmpshowcase.server

import com.mk.kmpshowcase.server.feature.lead.service.ClientConfirmationEmail
import com.mk.kmpshowcase.server.feature.lead.service.Lead
import com.mk.kmpshowcase.server.feature.lead.service.LeadStatus
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ClientConfirmationEmailTest {

    // name is ambiguous (person or company) → it must NEVER enter a personal greeting.
    private fun lead(hasDoc: Boolean = false) = Lead(
        id = 1L,
        email = "acme@example.com",
        appType = "Mobile app",
        platforms = listOf("iOS", "Android"),
        features = listOf("Auth", "Payments"),
        name = "Acme s.r.o.",
        phone = null,
        note = null,
        hasDoc = hasDoc,
        hasDesign = false,
        createdAt = 0L,
        status = LeadStatus.NEW,
    )

    @Test
    fun `subject is localized per locale`() {
        assertEquals("We received your request — MK Digital", ClientConfirmationEmail.subject(null))
        assertEquals("Prijali sme Váš dopyt — MK Digital", ClientConfirmationEmail.subject("sk"))
        assertEquals("Přijali jsme Vaši poptávku — MK Digital", ClientConfirmationEmail.subject("cs"))
        assertEquals("Wir haben Ihre Anfrage erhalten — MK Digital", ClientConfirmationEmail.subject("de"))
    }

    @Test
    fun `greeting is nameless — the ambiguous name never enters the email`() {
        for (locale in listOf(null, "sk", "cs", "de")) {
            assertFalse(ClientConfirmationEmail.text(lead(), locale).contains("Acme"), "name leaked for locale=$locale")
        }
        assertContains(ClientConfirmationEmail.text(lead(), null), "Hello,")
        assertContains(ClientConfirmationEmail.text(lead(), "sk"), "Dobrý deň,")
        assertContains(ClientConfirmationEmail.text(lead(), "cs"), "Dobrý den,")
        assertContains(ClientConfirmationEmail.text(lead(), "de"), "Guten Tag,")
    }

    @Test
    fun `text carries the founder signature and localized closing`() {
        val en = ClientConfirmationEmail.text(lead(), null)
        assertContains(en, "Kind regards,")
        assertContains(en, "Miroslav Kušnír")
        assertContains(en, "Founder, MK Digital s.r.o.")

        assertContains(ClientConfirmationEmail.text(lead(), "sk"), "S pozdravom,")
        assertContains(ClientConfirmationEmail.text(lead(), "sk"), "zakladateľ, MK Digital s.r.o.")
        assertContains(ClientConfirmationEmail.text(lead(), "cs"), "S pozdravem")
        assertContains(ClientConfirmationEmail.text(lead(), "de"), "Mit freundlichen Grüßen")
        assertContains(ClientConfirmationEmail.text(lead(), "de"), "Gründer, MK Digital s.r.o.")
    }

    @Test
    fun `SK and CS capitalize the formal Vy pronoun`() {
        val sk = ClientConfirmationEmail.text(lead(), "sk")
        assertContains(sk, "ozveme sa Vám")
        assertFalse(sk.contains("ozveme sa vám"), "SK formal pronoun not capitalized")

        val cs = ClientConfirmationEmail.text(lead(), "cs")
        assertContains(cs, "ozveme se Vám")
        assertFalse(cs.contains("ozveme se vám"), "CS formal pronoun not capitalized")
    }

    @Test
    fun `request details are echoed`() {
        val text = ClientConfirmationEmail.text(lead(), null)
        assertContains(text, "Mobile app")
        assertContains(text, "iOS")
        assertContains(text, "Payments")
    }

    @Test
    fun `docs line appears only when the lead has documentation`() {
        assertFalse(ClientConfirmationEmail.text(lead(hasDoc = false), null).contains("documentation"))
        assertContains(ClientConfirmationEmail.text(lead(hasDoc = true), null), "documentation")
    }

    @Test
    fun `html renders doctype, nameless greeting and founder signature`() {
        val html = ClientConfirmationEmail.html(lead(), null)
        assertTrue(html.startsWith("<!DOCTYPE html>"))
        assertContains(html, "Hello,")
        assertContains(html, "Miroslav Kušnír")
        assertContains(html, "Founder, MK Digital s.r.o.")
        assertContains(html, "Kind regards,")
        assertFalse(html.contains("Acme"))
    }
}
