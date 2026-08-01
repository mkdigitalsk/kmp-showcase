package com.mk.kmpshowcase.domain.useCase.biometric

import com.mk.kmpshowcase.domain.repository.BiometricRepository
import com.mk.kmpshowcase.domain.useCase.base.None
import com.mk.kmpshowcase.domain.useCase.base.UseCase

class IsBiometricEnabledUseCase(
    private val biometricRepository: BiometricRepository
) : UseCase<None, Boolean>() {
    override suspend fun run(params: None): Boolean = biometricRepository.enabled()
}
