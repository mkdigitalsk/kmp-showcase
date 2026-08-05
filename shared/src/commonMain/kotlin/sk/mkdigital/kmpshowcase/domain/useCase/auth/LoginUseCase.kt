package sk.mkdigital.kmpshowcase.domain.useCase.auth

import sk.mkdigital.kmpshowcase.domain.model.AuthSession
import sk.mkdigital.kmpshowcase.domain.repository.AuthRepository
import sk.mkdigital.kmpshowcase.domain.useCase.base.UseCase

class LoginUseCase(
    private val authRepository: AuthRepository
) : UseCase<LoginUseCase.Params, AuthSession>() {

    data class Params(val email: String, val password: String)

    override suspend fun run(params: Params): AuthSession =
        authRepository.login(params.email, params.password)
}
