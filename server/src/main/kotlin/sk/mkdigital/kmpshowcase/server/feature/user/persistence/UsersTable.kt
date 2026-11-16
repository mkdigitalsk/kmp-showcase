package sk.mkdigital.kmpshowcase.server.feature.user.persistence

import sk.mkdigital.kmpshowcase.server.feature.user.service.Language
import sk.mkdigital.kmpshowcase.server.feature.user.service.ThemeMode
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

internal object UsersTable : LongIdTable("users") {
    val email = varchar("email", EMAIL_LENGTH).uniqueIndex()
    val passwordHash = varchar("password_hash", PASSWORD_HASH_LENGTH)
    val createdAt = long("created_at")
    val updatedAt = long("updated_at")

    /**
     * Moved when someone opens the app, which [updatedAt] does not do — it moves only on a profile edit, so an
     * account edited once and abandoned would read as active forever.
     */
    val lastSeenAt = long("last_seen_at").default(0).index()
    val themeMode = enumerationByName("theme_mode", THEME_MODE_LENGTH, ThemeMode::class).default(ThemeMode.SYSTEM)

    /** The language an account opens in until its holder picks another, and what a stored tag naming none reads as. */
    val DEFAULT_LANGUAGE = Language.EN

    /**
     * The account's own language, written by its key. The column keeps the name it had while it held a free tag —
     * boot adds a missing column and renames none — and a row still holding one reads as the language it names.
     */
    val language = varchar("locale", LOCALE_LENGTH)
        .transform(wrap = ::languageOfStored, unwrap = Language::name)
        .default(DEFAULT_LANGUAGE)

    private const val EMAIL_LENGTH = 255
    private const val PASSWORD_HASH_LENGTH = 255
    private const val THEME_MODE_LENGTH = 10

    /** The width a free tag had, which a row written before keys may still fill. */
    private const val LOCALE_LENGTH = 35
}

/**
 * The language a stored value names by its language subtag, in any case — the key, its subtag in capitals, or a free
 * tag stored before keys were, as `sk-SK` or `en_GB` — and [UsersTable.DEFAULT_LANGUAGE] for any other.
 */
private fun languageOfStored(stored: String): Language {
    val subtag = stored.trim().replace('_', '-').substringBefore('-')
    return Language.entries.firstOrNull { it.name.equals(subtag, ignoreCase = true) } ?: UsersTable.DEFAULT_LANGUAGE
}
