package com.mk.kmpshowcase.server

import com.mk.kmpshowcase.server.feature.user.service.InviteEmail
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class InviteEmailTest {

    private val link = "https://mkdigital.sk/invite?token=abc123"

    @Test
    fun `subject is localized per locale`() {
        assertEquals("Your MK Digital client portal access", InviteEmail.subject(null))
        assertEquals("Váš prístup do klientskeho portálu MK Digital", InviteEmail.subject("sk"))
        assertEquals("Váš přístup do klientského portálu MK Digital", InviteEmail.subject("cs"))
        assertEquals("Ihr Zugang zum MK Digital Kundenportal", InviteEmail.subject("de"))
    }

    @Test
    fun `greeting is nameless per locale`() {
        assertContains(InviteEmail.text(link, null), "Hello,")
        assertContains(InviteEmail.text(link, "sk"), "Dobrý deň,")
        assertContains(InviteEmail.text(link, "cs"), "Dobrý den,")
        assertContains(InviteEmail.text(link, "de"), "Guten Tag,")
    }

    @Test
    fun `text carries the link, expiry and founder signature`() {
        val en = InviteEmail.text(link, null)
        assertContains(en, link)
        assertContains(en, "valid for 7 days")
        assertContains(en, "Kind regards,")
        assertContains(en, "Miroslav Kušnír")
        assertContains(en, "Founder, MK Digital s.r.o.")

        val sk = InviteEmail.text(link, "sk")
        assertContains(sk, "S pozdravom,")
        assertContains(sk, "zakladateľ, MK Digital s.r.o.")
        assertContains(sk, "7 dní")
    }
}
