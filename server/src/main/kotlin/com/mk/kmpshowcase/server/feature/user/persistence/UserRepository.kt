package com.mk.kmpshowcase.server.feature.user.persistence

import com.mk.kmpshowcase.server.feature.user.service.Role
import com.mk.kmpshowcase.server.feature.user.service.ThemeMode
import com.mk.kmpshowcase.server.feature.user.service.User

internal interface UserRepository {
    suspend fun findAll(): List<User>
    suspend fun findByRoles(roles: Set<Role>): List<User>
    suspend fun findByEmail(email: String): User?
    suspend fun findById(id: Long): User?
    suspend fun create(email: String, password: String, name: String, role: Role = Role.CLIENT): User
    suspend fun authenticate(email: String, password: String): User?
    suspend fun updateThemeMode(id: Long, themeMode: ThemeMode): User?
    suspend fun updateLocale(id: Long, locale: String): User?
}
