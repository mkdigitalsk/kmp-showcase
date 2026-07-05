package com.mk.kmpshowcase.server.feature.user.persistence

import com.mk.kmpshowcase.server.feature.user.service.Role
import com.mk.kmpshowcase.server.feature.user.service.ThemeMode
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

internal object UsersTable : LongIdTable("users") {
    val email = varchar("email", EMAIL_LENGTH).uniqueIndex()
    val passwordHash = varchar("password_hash", PASSWORD_HASH_LENGTH)
    val name = varchar("name", NAME_LENGTH)
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")
    val themeMode = enumerationByName("theme_mode", THEME_MODE_LENGTH, ThemeMode::class).default(ThemeMode.SYSTEM)
    val locale = varchar("locale", LOCALE_LENGTH).default(DEFAULT_LOCALE)
    val role = enumerationByName("role", ROLE_LENGTH, Role::class).default(Role.CLIENT)

    private const val EMAIL_LENGTH = 255
    private const val PASSWORD_HASH_LENGTH = 255
    private const val NAME_LENGTH = 255
    private const val THEME_MODE_LENGTH = 10
    private const val LOCALE_LENGTH = 35
    private const val ROLE_LENGTH = 10
    const val DEFAULT_LOCALE = "en-GB"
}
