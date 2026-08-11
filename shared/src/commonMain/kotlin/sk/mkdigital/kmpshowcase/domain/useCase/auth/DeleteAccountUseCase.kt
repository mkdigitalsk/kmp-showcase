package sk.mkdigital.kmpshowcase.domain.useCase.auth

import sk.mkdigital.kmpshowcase.domain.repository.AuthRepository
import sk.mkdigital.kmpshowcase.domain.useCase.base.None
import sk.mkdigital.kmpshowcase.domain.useCase.base.UseCase

class DeleteAccountUseCase(
    private val authRepository: AuthRepository
) : UseCase<None, Unit>() {

    override suspend fun run(params: None) = authRepository.deleteAccount()
}
