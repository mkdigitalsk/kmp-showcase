package sk.mkdigital.kmpshowcase.domain.useCase.auth

import sk.mkdigital.kmpshowcase.domain.repository.AuthRepository
import sk.mkdigital.kmpshowcase.domain.useCase.base.None
import sk.mkdigital.kmpshowcase.domain.useCase.base.UseCase

class IsDemoAccountUseCase(
    private val authRepository: AuthRepository
) : UseCase<None, Boolean>() {

    override suspend fun run(params: None): Boolean = authRepository.isDemoAccount()
}
