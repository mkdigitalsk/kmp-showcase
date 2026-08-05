package sk.mkdigital.kmpshowcase.domain.useCase.biometric

import sk.mkdigital.kmpshowcase.domain.model.BiometricResult
import sk.mkdigital.kmpshowcase.domain.repository.BiometricRepository
import sk.mkdigital.kmpshowcase.domain.useCase.base.None
import sk.mkdigital.kmpshowcase.domain.useCase.base.UseCase

class AuthenticateWithBiometricUseCase(
    private val biometricRepository: BiometricRepository
) : UseCase<None, BiometricResult>() {
    override suspend fun run(params: None): BiometricResult = biometricRepository.authenticate()
}
