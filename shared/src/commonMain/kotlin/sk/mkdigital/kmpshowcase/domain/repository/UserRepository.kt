package sk.mkdigital.kmpshowcase.domain.repository

import sk.mkdigital.kmpshowcase.domain.model.User

interface UserRepository {

    suspend fun getUser(id: Long): User

    suspend fun getUsers(): List<User>

}
