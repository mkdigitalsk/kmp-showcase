package com.mk.kmpshowcase.domain.useCase

import com.mk.kmpshowcase.domain.model.User
import com.mk.kmpshowcase.domain.repository.UserRepository
import com.mk.kmpshowcase.domain.useCase.base.None
import com.mk.kmpshowcase.domain.useCase.base.UseCase

class GetUsersUseCase(
    private val userRepository: UserRepository
) : UseCase<None, List<User>>() {
    override suspend fun run(params: None): List<User> = userRepository.getUsers()
}
