package com.mk.kmpshowcase.domain.useCase.auth

import com.mk.kmpshowcase.domain.repository.AuthRepository
import com.mk.kmpshowcase.domain.useCase.base.None
import com.mk.kmpshowcase.domain.useCase.base.UseCase

class LogoutUseCase(
    private val authRepository: AuthRepository
) : UseCase<None, Unit>() {

    override suspend fun run(params: None) = authRepository.logout()
}
