package com.mk.kmpshowcase.server.feature.user.service

import com.mk.kmpshowcase.server.feature.user.persistence.UserRepository

internal class UserService(
    private val repository: UserRepository,
) {
    suspend fun register(email: String, password: String, name: String): User {
        require(email.contains("@")) { "Invalid email format" }
        require(PASSWORD_REGEX.matches(password)) { "Password must be at least 8 characters and contain uppercase, lowercase, digit and special character (@\$!%*?&)" }
        require(name.isNotBlank()) { "Name cannot be blank" }

        check(repository.findByEmail(email) == null) { "User already exists" }

        return repository.create(email, password, name)
    }

    suspend fun authenticate(email: String, password: String): User? =
        repository.authenticate(email, password)

    suspend fun getById(id: Long): User? = repository.findById(id)

    suspend fun getAll(): List<User> = repository.findAll()

    suspend fun updateThemeMode(id: Long, themeMode: ThemeMode): User? = repository.updateThemeMode(id, themeMode)

    suspend fun updateLocale(id: Long, locale: String): User? = repository.updateLocale(id, locale)

    private companion object {
        val PASSWORD_REGEX = Regex(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$"
        )
    }
}
