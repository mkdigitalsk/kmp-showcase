package com.mk.kmpshowcase.data.client

import com.mk.kmpshowcase.domain.model.BiometricResult

interface BiometricClient {
    fun enabled(): Boolean
    suspend fun authenticate(): BiometricResult
}

expect class BiometricClientImpl: BiometricClient {
    override fun enabled(): Boolean
    override suspend fun authenticate(): BiometricResult
}
