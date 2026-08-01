package com.mk.kmpshowcase.server.di

import com.mk.kmpshowcase.server.core.security.JwtConfig
import com.mk.kmpshowcase.server.feature.user.persistence.UserRepository
import com.mk.kmpshowcase.server.feature.user.persistence.UserRepositoryImpl
import com.mk.kmpshowcase.server.feature.user.service.UserService

internal class AppDependencies(val jwtConfig: JwtConfig) {

    private val userRepository: UserRepository = UserRepositoryImpl()

    val userService = UserService(userRepository)
}
