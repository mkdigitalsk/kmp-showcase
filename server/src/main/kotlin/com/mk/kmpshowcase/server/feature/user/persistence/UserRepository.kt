package com.mk.kmpshowcase.server.feature.user.persistence

import com.mk.kmpshowcase.server.feature.user.service.User

interface UserRepository {
    suspend fun findByEmail(email: String): User?
    suspend fun findById(id: Long): User?
    suspend fun create(email: String, password: String, name: String): User
    suspend fun authenticate(email: String, password: String): User?
}
