package sk.mkdigital.kmpshowcase.server.feature.user.persistence

import at.favre.lib.crypto.bcrypt.BCrypt
import sk.mkdigital.kmpshowcase.server.core.persistence.mapToSingleOrNull
import sk.mkdigital.kmpshowcase.server.feature.user.service.ThemeMode
import sk.mkdigital.kmpshowcase.server.feature.user.service.User
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.inList
import org.jetbrains.exposed.v1.core.less
import org.jetbrains.exposed.v1.core.not
import org.jetbrains.exposed.v1.jdbc.deleteWhere
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.update

internal class UserRepositoryImpl : UserRepository {

    override suspend fun findByEmail(email: String): User? = suspendTransaction {
        UsersTable.selectAll()
            .where { UsersTable.email eq email }
            .mapToSingleOrNull { it.toUser() }
    }

    override suspend fun findById(id: Long): User? = suspendTransaction {
        UsersTable.selectAll()
            .where { UsersTable.id eq id }
            .mapToSingleOrNull { it.toUser() }
    }

    override suspend fun create(email: String, password: String): User =
        suspendTransaction {
            val now = System.currentTimeMillis()
            val passwordHash = BCrypt.withDefaults().hashToString(BCRYPT_COST, password.toCharArray())

            val id = UsersTable.insert {
                it[UsersTable.email] = email
                it[UsersTable.passwordHash] = passwordHash
                it[createdAt] = now
                it[updatedAt] = now
                it[lastSeenAt] = now
            } get UsersTable.id

            User(
                id = id.value,
                email = email,
                createdAt = now,
                themeMode = ThemeMode.SYSTEM,
                locale = UsersTable.DEFAULT_LOCALE,
            )
        }

    override suspend fun updateThemeMode(id: Long, themeMode: ThemeMode): User? = suspendTransaction {
        val updated = UsersTable.update({ UsersTable.id eq id }) {
            it[UsersTable.themeMode] = themeMode
        }
        if (updated > 0) {
            UsersTable.selectAll()
                .where { UsersTable.id eq id }
                .mapToSingleOrNull { it.toUser() }
        } else {
            null
        }
    }

    override suspend fun updateLocale(id: Long, locale: String): User? = suspendTransaction {
        val updated = UsersTable.update({ UsersTable.id eq id }) {
            it[UsersTable.locale] = locale
        }
        if (updated > 0) {
            UsersTable.selectAll()
                .where { UsersTable.id eq id }
                .mapToSingleOrNull { it.toUser() }
        } else {
            null
        }
    }

    override suspend fun authenticate(email: String, password: String): User? = suspendTransaction {
        val row = UsersTable.selectAll()
            .where { UsersTable.email eq email }
            .singleOrNull()
            ?: return@suspendTransaction null

        val isPasswordValid = BCrypt.verifyer()
            .verify(password.toCharArray(), row[UsersTable.passwordHash])
            .verified

        if (!isPasswordValid) return@suspendTransaction null

        val id = row[UsersTable.id].value
        UsersTable.update({ UsersTable.id eq id }) { it[lastSeenAt] = System.currentTimeMillis() }
        UsersTable.selectAll().where { UsersTable.id eq id }.mapToSingleOrNull { it.toUser() }
    }

    override suspend fun touchLastSeen(id: Long) {
        suspendTransaction {
            UsersTable.update({ UsersTable.id eq id }) { it[lastSeenAt] = System.currentTimeMillis() }
        }
    }

    override suspend fun deleteInactiveSince(cutoff: Long, keep: Set<String>): Int = suspendTransaction {
        UsersTable.deleteWhere { (lastSeenAt less cutoff) and not(email inList keep) }
    }

    override suspend fun delete(id: Long): Boolean = suspendTransaction {
        UsersTable.deleteWhere { UsersTable.id eq id } > 0
    }

    private fun ResultRow.toUser() = User(
        id = this[UsersTable.id].value,
        email = this[UsersTable.email],
        createdAt = this[UsersTable.createdAt],
        themeMode = this[UsersTable.themeMode],
        locale = this[UsersTable.locale],
    )

    private companion object {
        const val BCRYPT_COST = 12
    }
}
