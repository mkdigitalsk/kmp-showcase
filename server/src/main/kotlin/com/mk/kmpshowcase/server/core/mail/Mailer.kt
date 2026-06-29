package com.mk.kmpshowcase.server.core.mail

internal interface Mailer {
    suspend fun send(to: String, subject: String, body: String, replyTo: String? = null)
}
