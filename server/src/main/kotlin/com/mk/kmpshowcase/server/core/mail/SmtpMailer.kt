package com.mk.kmpshowcase.server.core.mail

import jakarta.mail.Authenticator
import jakarta.mail.Message
import jakarta.mail.PasswordAuthentication
import jakarta.mail.Session
import jakarta.mail.Transport
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import java.util.Properties

private val logger = LoggerFactory.getLogger("SmtpMailer")

internal class SmtpMailer(private val config: MailConfig) : Mailer {

    // No password (e.g. local dev) → mail is a no-op; /leads still works.
    private val session: Session? = if (config.password.isBlank()) {
        logger.warn("MAIL_PASSWORD not set — outgoing mail disabled (leads are still stored)")
        null
    } else {
        Session.getInstance(smtpProperties(), object : Authenticator() {
            override fun getPasswordAuthentication() = PasswordAuthentication(config.user, config.password)
        })
    }

    override suspend fun send(to: String, subject: String, body: String, replyTo: String?) {
        val activeSession = session ?: return
        withContext(Dispatchers.IO) {
            val message = MimeMessage(activeSession).apply {
                setFrom(InternetAddress(config.from))
                setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
                replyTo?.let { setReplyTo(InternetAddress.parse(it)) }
                setSubject(subject, "UTF-8")
                setText(body, "UTF-8")
            }
            Transport.send(message)
        }
    }

    private fun smtpProperties() = Properties().apply {
        put("mail.smtp.host", config.host)
        put("mail.smtp.port", config.port.toString())
        put("mail.smtp.auth", "true")
        put("mail.smtp.ssl.enable", "true") // port 465 = implicit SSL
    }
}
