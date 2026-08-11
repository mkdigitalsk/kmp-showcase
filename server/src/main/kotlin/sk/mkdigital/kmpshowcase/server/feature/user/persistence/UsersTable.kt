package sk.mkdigital.kmpshowcase.server.feature.user.persistence

import sk.mkdigital.kmpshowcase.server.feature.user.service.ThemeMode
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

internal object UsersTable : LongIdTable("users") {
    val email = varchar("email", EMAIL_LENGTH).uniqueIndex()
    val passwordHash = varchar("password_hash", PASSWORD_HASH_LENGTH)
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")

    // Moved when someone opens the app, which updatedAt does not do — it moves only on a profile edit,
    // so an account edited once and abandoned would read as active forever.
    val lastSeenAt = long("last_seen_at").default(0).index()
    val themeMode = enumerationByName("theme_mode", THEME_MODE_LENGTH, ThemeMode::class).default(ThemeMode.SYSTEM)
    val locale = varchar("locale", LOCALE_LENGTH).default(DEFAULT_LOCALE)

    private const val EMAIL_LENGTH = 255
    private const val PASSWORD_HASH_LENGTH = 255
    private const val THEME_MODE_LENGTH = 10
    private const val LOCALE_LENGTH = 35
    const val DEFAULT_LOCALE = "en-GB"
}
