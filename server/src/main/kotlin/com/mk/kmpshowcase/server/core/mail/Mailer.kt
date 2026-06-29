package com.mk.kmpshowcase.server.core.mail

internal interface Mailer {
    suspend fun send(
        to: String,
        subject: String,
        text: String,
        html: String? = null,
        replyTo: String? = null,
    )
}
