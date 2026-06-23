package com.mk.kmpshowcase.server.core.mail

internal data class MailConfig(
    val host: String,
    val port: Int,
    val user: String,
    val password: String,
    val from: String,
    val recipient: String,
)
