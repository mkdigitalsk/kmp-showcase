package com.mk.kmpshowcase.domain.useCase.biometric

import com.mk.kmpshowcase.domain.model.BiometricResult
import com.mk.kmpshowcase.domain.repository.BiometricRepository
import com.mk.kmpshowcase.domain.useCase.base.None
import com.mk.kmpshowcase.domain.useCase.base.UseCase

class AuthenticateWithBiometricUseCase(
    private val biometricRepository: BiometricRepository
) : UseCase<None, BiometricResult>() {
    override suspend fun run(params: None): BiometricResult = biometricRepository.authenticate()
}
