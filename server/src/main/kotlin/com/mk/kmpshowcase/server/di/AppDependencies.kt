package com.mk.kmpshowcase.server.di

import com.mk.kmpshowcase.server.feature.note.persistence.NoteRepository
import com.mk.kmpshowcase.server.feature.note.persistence.NoteRepositoryImpl
import com.mk.kmpshowcase.server.feature.note.service.NoteService
import com.mk.kmpshowcase.server.feature.user.persistence.UserRepository
import com.mk.kmpshowcase.server.feature.user.persistence.UserRepositoryImpl
import com.mk.kmpshowcase.server.feature.user.service.UserService

class AppDependencies {

    private val userRepository: UserRepository = UserRepositoryImpl()
    private val noteRepository: NoteRepository = NoteRepositoryImpl()

    val userService = UserService(userRepository)
    val noteService = NoteService(noteRepository)
}
