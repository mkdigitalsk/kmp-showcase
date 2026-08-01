package com.mk.kmpshowcase.domain.repository

import com.mk.kmpshowcase.domain.model.User

interface UserRepository {

    suspend fun getUser(id: Long): User

    suspend fun getUsers(): List<User>

}
