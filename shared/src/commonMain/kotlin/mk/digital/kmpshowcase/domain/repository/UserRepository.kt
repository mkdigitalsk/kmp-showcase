package mk.digital.kmpshowcase.domain.repository

import mk.digital.kmpshowcase.domain.model.User

interface UserRepository {

    suspend fun getUser(id: Int): User

    suspend fun getUsers(): List<User>

}
