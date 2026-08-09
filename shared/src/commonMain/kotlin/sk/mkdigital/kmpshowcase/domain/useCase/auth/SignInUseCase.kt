package sk.mkdigital.kmpshowcase.domain.useCase.auth

import sk.mkdigital.kmpshowcase.domain.model.AuthSession
import sk.mkdigital.kmpshowcase.domain.repository.AuthRepository
import sk.mkdigital.kmpshowcase.domain.useCase.base.UseCase

class SignInUseCase(
    private val authRepository: AuthRepository
) : UseCase<SignInUseCase.Params, AuthSession>() {

    data class Params(val email: String, val password: String)

    override suspend fun run(params: Params): AuthSession =
        authRepository.signIn(params.email, params.password)
}
