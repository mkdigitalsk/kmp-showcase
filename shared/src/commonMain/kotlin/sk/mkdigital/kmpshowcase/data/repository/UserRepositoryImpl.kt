package sk.mkdigital.kmpshowcase.data.repository

import sk.mkdigital.kmpshowcase.contracts.user.UserResponseDTO
import sk.mkdigital.kmpshowcase.data.client.UserClient
import sk.mkdigital.kmpshowcase.domain.model.User
import sk.mkdigital.kmpshowcase.domain.repository.UserRepository

class UserRepositoryImpl(
    private val client: UserClient
) : UserRepository {

    override suspend fun getUser(id: Long): User =
        client.fetchUser(id).toUser()

    override suspend fun getUsers(): List<User> =
        client.fetchUsers().map { it.toUser() }
}

private fun UserResponseDTO.toUser() = User(id = id, email = email)
