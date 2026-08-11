package sk.mkdigital.kmpshowcase.server.di

import sk.mkdigital.kmpshowcase.server.core.security.JwtConfig
import sk.mkdigital.kmpshowcase.server.feature.note.persistence.NoteRepository
import sk.mkdigital.kmpshowcase.server.feature.note.persistence.NoteRepositoryImpl
import sk.mkdigital.kmpshowcase.server.feature.note.service.NoteService
import sk.mkdigital.kmpshowcase.server.feature.user.persistence.UserRepository
import sk.mkdigital.kmpshowcase.server.feature.user.persistence.UserRepositoryImpl
import sk.mkdigital.kmpshowcase.server.feature.user.service.InactiveAccountPurge
import sk.mkdigital.kmpshowcase.server.feature.user.service.UserService

internal class AppDependencies(val jwtConfig: JwtConfig) {

    private val userRepository: UserRepository = UserRepositoryImpl()

    val userService = UserService(userRepository)

    val inactiveAccountPurge = InactiveAccountPurge(userRepository)

    private val noteRepository: NoteRepository = NoteRepositoryImpl()

    val noteService = NoteService(noteRepository)
}
