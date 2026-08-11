package sk.mkdigital.kmpshowcase.server.feature.user.persistence

import sk.mkdigital.kmpshowcase.server.feature.user.service.ThemeMode
import sk.mkdigital.kmpshowcase.server.feature.user.service.User

internal interface UserRepository {
    suspend fun findByEmail(email: String): User?
    suspend fun findById(id: Long): User?
    suspend fun create(email: String, password: String): User
    suspend fun authenticate(email: String, password: String): User?
    suspend fun updateThemeMode(id: Long, themeMode: ThemeMode): User?
    suspend fun updateLocale(id: Long, locale: String): User?
    suspend fun delete(id: Long): Boolean
}
