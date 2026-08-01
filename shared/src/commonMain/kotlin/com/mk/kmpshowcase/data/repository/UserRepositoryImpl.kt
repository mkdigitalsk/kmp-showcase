package com.mk.kmpshowcase.data.repository

import com.mk.kmpshowcase.contracts.user.UserResponseDTO
import com.mk.kmpshowcase.data.client.UserClient
import com.mk.kmpshowcase.domain.model.User
import com.mk.kmpshowcase.domain.repository.UserRepository

class UserRepositoryImpl(
    private val client: UserClient
) : UserRepository {

    override suspend fun getUser(id: Long): User =
        client.fetchUser(id).toUser()

    override suspend fun getUsers(): List<User> =
        client.fetchUsers().map { it.toUser() }
}

private fun UserResponseDTO.toUser() = User(id = id, email = email, name = name)
