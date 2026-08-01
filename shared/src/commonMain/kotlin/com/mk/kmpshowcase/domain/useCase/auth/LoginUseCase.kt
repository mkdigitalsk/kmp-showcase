package com.mk.kmpshowcase.domain.useCase.auth

import com.mk.kmpshowcase.domain.model.AuthSession
import com.mk.kmpshowcase.domain.repository.AuthRepository
import com.mk.kmpshowcase.domain.useCase.base.UseCase

class LoginUseCase(
    private val authRepository: AuthRepository
) : UseCase<LoginUseCase.Params, AuthSession>() {

    data class Params(val email: String, val password: String)

    override suspend fun run(params: Params): AuthSession =
        authRepository.login(params.email, params.password)
}
