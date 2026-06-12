package com.mk.kmpshowcase.server.feature.user.persistence

import at.favre.lib.crypto.bcrypt.BCrypt
import com.mk.kmpshowcase.server.feature.user.service.User
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class UserRepositoryImpl : UserRepository {

    override suspend fun findByEmail(email: String): User? = newSuspendedTransaction {
        UsersTable.selectAll()
            .where { UsersTable.email eq email }
            .map { it.toUser() }
            .singleOrNull()
    }

    override suspend fun findById(id: Long): User? = newSuspendedTransaction {
        UsersTable.selectAll()
            .where { UsersTable.id eq id }
            .map { it.toUser() }
            .singleOrNull()
    }

    override suspend fun create(email: String, password: String, name: String): User = newSuspendedTransaction {
        val now = System.currentTimeMillis()
        val passwordHash = BCrypt.withDefaults().hashToString(BCRYPT_COST, password.toCharArray())

        val id = UsersTable.insert {
            it[UsersTable.email] = email
            it[UsersTable.passwordHash] = passwordHash
            it[UsersTable.name] = name
            it[createdAt] = now
            it[updatedAt] = now
        } get UsersTable.id

        User(
            id = id.value,
            email = email,
            name = name,
            createdAt = now,
        )
    }

    override suspend fun authenticate(email: String, password: String): User? = newSuspendedTransaction {
        val row = UsersTable.selectAll()
            .where { UsersTable.email eq email }
            .singleOrNull()
            ?: return@newSuspendedTransaction null

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
    )

    private companion object {
        const val BCRYPT_COST = 12
    }
}
