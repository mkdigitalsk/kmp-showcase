package com.mk.kmpshowcase.domain.useCase.auth

import com.mk.kmpshowcase.domain.model.AuthSession
import com.mk.kmpshowcase.domain.repository.AuthRepository
import com.mk.kmpshowcase.domain.useCase.base.UseCase
import com.mk.kmpshowcase.domain.useCase.base.None

class LoginWithTokenUseCase(
    private val authRepository: AuthRepository
) : UseCase<None, AuthSession?>() {
    override suspend fun run(params: None): AuthSession? = authRepository.loginWithToken()
}
