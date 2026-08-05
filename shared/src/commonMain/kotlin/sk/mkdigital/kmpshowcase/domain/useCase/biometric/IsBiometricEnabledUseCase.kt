package sk.mkdigital.kmpshowcase.domain.useCase.biometric

import sk.mkdigital.kmpshowcase.domain.repository.BiometricRepository
import sk.mkdigital.kmpshowcase.domain.useCase.base.None
import sk.mkdigital.kmpshowcase.domain.useCase.base.UseCase

class IsBiometricEnabledUseCase(
    private val biometricRepository: BiometricRepository
) : UseCase<None, Boolean>() {
    override suspend fun run(params: None): Boolean = biometricRepository.enabled()
}
