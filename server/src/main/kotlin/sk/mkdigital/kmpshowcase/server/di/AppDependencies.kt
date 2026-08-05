package sk.mkdigital.kmpshowcase.server.di

import sk.mkdigital.kmpshowcase.server.core.security.JwtConfig
import sk.mkdigital.kmpshowcase.server.feature.user.persistence.UserRepository
import sk.mkdigital.kmpshowcase.server.feature.user.persistence.UserRepositoryImpl
import sk.mkdigital.kmpshowcase.server.feature.user.service.UserService

internal class AppDependencies(val jwtConfig: JwtConfig) {

    private val userRepository: UserRepository = UserRepositoryImpl()

    val userService = UserService(userRepository)
}
