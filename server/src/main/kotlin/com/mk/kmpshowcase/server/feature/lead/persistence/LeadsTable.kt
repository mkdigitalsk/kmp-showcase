package com.mk.kmpshowcase.server.feature.lead.persistence

import org.jetbrains.exposed.dao.id.LongIdTable

internal object LeadsTable : LongIdTable("leads") {
    val email = varchar("email", EMAIL_LENGTH)
    val appType = varchar("app_type", APP_TYPE_LENGTH)
    val platforms = text("platforms")
    val features = text("features")
    val name = varchar("name", NAME_LENGTH).nullable()
    val phone = varchar("phone", PHONE_LENGTH).nullable()
    val note = text("note").nullable()
    val hasDoc = bool("has_doc")
    val createdAt = long("created_at")

    private const val EMAIL_LENGTH = 320
    private const val APP_TYPE_LENGTH = 120
    private const val NAME_LENGTH = 200
    private const val PHONE_LENGTH = 40
}
