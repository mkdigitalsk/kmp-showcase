package sk.mkdigital.kmpshowcase.domain.useCase.auth

import sk.mkdigital.kmpshowcase.domain.model.AuthSession
import sk.mkdigital.kmpshowcase.domain.repository.AuthRepository
import sk.mkdigital.kmpshowcase.domain.useCase.base.UseCase

class RegisterUserUseCase(
    private val authRepository: AuthRepository
) : UseCase<RegisterUserUseCase.Params, AuthSession>() {

    data class Params(val name: String, val email: String, val password: String)

    override suspend fun run(params: Params): AuthSession =
        authRepository.register(params.name, params.email, params.password)
}
