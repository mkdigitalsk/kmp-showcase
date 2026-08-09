package sk.mkdigital.kmpshowcase.domain.useCase.auth

import sk.mkdigital.kmpshowcase.domain.model.AuthSession
import sk.mkdigital.kmpshowcase.domain.repository.AuthRepository
import sk.mkdigital.kmpshowcase.domain.useCase.base.UseCase
import sk.mkdigital.kmpshowcase.domain.useCase.base.None

class SignInWithTokenUseCase(
    private val authRepository: AuthRepository
) : UseCase<None, AuthSession?>() {
    override suspend fun run(params: None): AuthSession? = authRepository.signInWithToken()
}
