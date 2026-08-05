package sk.mkdigital.kmpshowcase.data.repository

import sk.mkdigital.kmpshowcase.data.client.BiometricClient
import sk.mkdigital.kmpshowcase.domain.model.BiometricResult
import sk.mkdigital.kmpshowcase.domain.repository.BiometricRepository

class BiometricRepositoryImpl(
    private val biometricClient: BiometricClient,
) : BiometricRepository {

    override fun enabled(): Boolean = biometricClient.enabled()

    override suspend fun authenticate(): BiometricResult = biometricClient.authenticate()
}
