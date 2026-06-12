package com.mk.kmpshowcase.server.feature.user.service

import com.mk.kmpshowcase.server.feature.user.persistence.UserRepository

class UserService(
    private val repository: UserRepository,
) {
    suspend fun register(email: String, password: String, name: String): User {
        require(email.contains("@")) { "Invalid email format" }
        require(password.length >= MIN_PASSWORD_LENGTH) { "Password must be at least $MIN_PASSWORD_LENGTH characters" }
        require(name.isNotBlank()) { "Name cannot be blank" }

        check(repository.findByEmail(email) == null) { "User already exists" }

        return repository.create(email, password, name)
    }

    suspend fun authenticate(email: String, password: String): User? =
        repository.authenticate(email, password)

    suspend fun getById(id: Long): User? = repository.findById(id)

    private companion object {
        const val MIN_PASSWORD_LENGTH = 8
    }
}
