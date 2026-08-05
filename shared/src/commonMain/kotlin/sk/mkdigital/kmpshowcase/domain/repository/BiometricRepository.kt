package sk.mkdigital.kmpshowcase.domain.repository

import sk.mkdigital.kmpshowcase.domain.model.BiometricResult

interface BiometricRepository {
    fun enabled(): Boolean
    suspend fun authenticate(): BiometricResult
}
