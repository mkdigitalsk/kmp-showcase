package sk.mkdigital.kmpshowcase.server.feature.user.service

import sk.mkdigital.kmpshowcase.server.feature.user.persistence.UserRepository

internal sealed interface DeleteResult {
    data object Deleted : DeleteResult
    data object Refused : DeleteResult
}

internal class UserService(
    private val repository: UserRepository,
) {
    suspend fun signUp(email: String, password: String): User {
        require(email.contains("@")) { "Invalid email format" }
        require(PASSWORD_REGEX.matches(password)) { PASSWORD_REQUIREMENT }

        check(repository.findByEmail(email) == null) { "User already exists" }

        return repository.create(email, password)
    }

    suspend fun authenticate(email: String, password: String): User? =
        repository.authenticate(email, password)

    suspend fun getById(id: Long): User? = repository.findById(id)

    suspend fun markSeen(id: Long) = repository.touchLastSeen(id)

    suspend fun updateThemeMode(id: Long, themeMode: ThemeMode): User? = repository.updateThemeMode(id, themeMode)

    suspend fun updateLocale(id: Long, locale: String): User? = repository.updateLocale(id, locale)

    suspend fun delete(id: Long): DeleteResult {
        val user = repository.findById(id) ?: return DeleteResult.Deleted
        if (user.demo) return DeleteResult.Refused
        repository.delete(id)
        return DeleteResult.Deleted
    }

    private companion object {
        val PASSWORD_REGEX = Regex(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$"
        )

        const val PASSWORD_REQUIREMENT =
            "Password must be at least 8 characters and contain uppercase, lowercase, digit " +
                "and special character (@\$!%*?&)"
    }
}
