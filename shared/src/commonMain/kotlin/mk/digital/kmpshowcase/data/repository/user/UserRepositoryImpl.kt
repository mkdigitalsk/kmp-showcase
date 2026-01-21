package mk.digital.kmpshowcase.data.repository.user

import mk.digital.kmpshowcase.data.base.transformAll
import mk.digital.kmpshowcase.domain.model.User
import mk.digital.kmpshowcase.domain.repository.UserRepository

class UserRepositoryImpl(
    private val client: UserClient
) : UserRepository {
    override suspend fun getUser(id: Int): User {
        return client.fetchUser(id).transform()
    }

    override suspend fun getUsers(): List<User> {
        return client.fetchUsers().transformAll()
    }
}
