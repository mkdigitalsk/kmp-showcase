package com.mk.kmpshowcase.domain.useCase.auth

import com.mk.kmpshowcase.domain.model.AuthSession
import com.mk.kmpshowcase.domain.repository.AuthRepository
import com.mk.kmpshowcase.domain.useCase.base.UseCase

class RegisterUserUseCase(
    private val authRepository: AuthRepository
) : UseCase<RegisterUserUseCase.Params, AuthSession>() {

    data class Params(val name: String, val email: String, val password: String)

    override suspend fun run(params: Params): AuthSession =
        authRepository.register(params.name, params.email, params.password)
}
