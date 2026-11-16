package sk.mkdigital.kmpshowcase.server

import sk.mkdigital.kmpshowcase.server.feature.user.persistence.UsersTable
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update

/** The accounts table as its rows hold it, the language column plain text, past [UsersTable]'s own mapping. */
internal object StoredAccounts : LongIdTable(UsersTable.tableName) {
    private val email = text(UsersTable.email.name)
    private val language = text(UsersTable.language.name)

    fun languageOf(account: String): String = transaction {
        select(language).where { email eq account }.single()[language]
    }

    fun storeLanguage(account: String, stored: String) {
        transaction { update({ email eq account }) { it[language] = stored } }
    }
}
