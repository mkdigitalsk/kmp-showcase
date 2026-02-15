package com.mk.kmpshowcase.server.repository

import at.favre.lib.crypto.bcrypt.BCrypt
import com.mk.kmpshowcase.server.model.UserDTO
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepository {

    fun findByEmail(email: String): UserDTO? = transaction {
        UsersTable.selectAll()
            .where { UsersTable.email eq email }
            .map { it.toUserDTO() }
            .singleOrNull()
    }

    fun findById(id: Long): UserDTO? = transaction {
        UsersTable.selectAll()
            .where { UsersTable.id eq id }
            .map { it.toUserDTO() }
            .singleOrNull()
    }

    fun create(email: String, password: String, name: String): UserDTO = transaction {
        val now = System.currentTimeMillis()
        val passwordHash = BCrypt.withDefaults().hashToString(12, password.toCharArray())

        val id = UsersTable.insert {
            it[UsersTable.email] = email
            it[UsersTable.passwordHash] = passwordHash
            it[UsersTable.name] = name
            it[createdAt] = now
            it[updatedAt] = now
        } get UsersTable.id

        UserDTO(
            id = id.value,
            email = email,
            name = name,
            createdAt = now
        )
    }

    fun verifyPassword(email: String, password: String): Boolean = transaction {
        val user = UsersTable.selectAll()
            .where { UsersTable.email eq email }
            .singleOrNull()

        user?.let {
            BCrypt.verifyer().verify(
                password.toCharArray(),
                it[UsersTable.passwordHash]
            ).verified
        } ?: false
    }

    private fun ResultRow.toUserDTO() = UserDTO(
        id = this[UsersTable.id].value,
        email = this[UsersTable.email],
        name = this[UsersTable.name],
        createdAt = this[UsersTable.createdAt]
    )
}
