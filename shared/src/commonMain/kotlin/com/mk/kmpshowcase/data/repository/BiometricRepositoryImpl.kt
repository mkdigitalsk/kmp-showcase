package com.mk.kmpshowcase.data.repository

import com.mk.kmpshowcase.data.client.BiometricClient
import com.mk.kmpshowcase.domain.model.BiometricResult
import com.mk.kmpshowcase.domain.repository.BiometricRepository

class BiometricRepositoryImpl(
    private val biometricClient: BiometricClient,
) : BiometricRepository {

    override fun enabled(): Boolean = biometricClient.enabled()

    override suspend fun authenticate(): BiometricResult = biometricClient.authenticate()
}
