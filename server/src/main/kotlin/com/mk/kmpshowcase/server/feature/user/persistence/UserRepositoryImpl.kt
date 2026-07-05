package com.mk.kmpshowcase.server.feature.user.persistence

import at.favre.lib.crypto.bcrypt.BCrypt
import com.mk.kmpshowcase.server.core.persistence.mapToSingleOrNull
import com.mk.kmpshowcase.server.feature.user.service.Role
import com.mk.kmpshowcase.server.feature.user.service.ThemeMode
import com.mk.kmpshowcase.server.feature.user.service.User
import org.jetbrains.exposed.v1.core.ResultRow
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.insert
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.update

internal class UserRepositoryImpl : UserRepository {

    override suspend fun findAll(): List<User> = suspendTransaction {
        UsersTable.selectAll().map { it.toUser() }
    }

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

    override suspend fun create(email: String, password: String, name: String, role: Role): User =
        suspendTransaction {
            val now = System.currentTimeMillis()
            val passwordHash = BCrypt.withDefaults().hashToString(BCRYPT_COST, password.toCharArray())

            val id = UsersTable.insert {
                it[UsersTable.email] = email
                it[UsersTable.passwordHash] = passwordHash
                it[UsersTable.name] = name
                it[UsersTable.role] = role
                it[createdAt] = now
                it[updatedAt] = now
            } get UsersTable.id

            User(
                id = id.value,
                email = email,
                name = name,
                createdAt = now,
                themeMode = ThemeMode.SYSTEM,
                locale = UsersTable.DEFAULT_LOCALE,
                role = role,
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

        val matches = BCrypt.verifyer()
            .verify(password.toCharArray(), row[UsersTable.passwordHash])
            .verified

        if (matches) row.toUser() else null
    }

    private fun ResultRow.toUser() = User(
        id = this[UsersTable.id].value,
        email = this[UsersTable.email],
        name = this[UsersTable.name],
        createdAt = this[UsersTable.createdAt],
        themeMode = this[UsersTable.themeMode],
        locale = this[UsersTable.locale],
        role = this[UsersTable.role],
    )

    private companion object {
        const val BCRYPT_COST = 12
    }
}
