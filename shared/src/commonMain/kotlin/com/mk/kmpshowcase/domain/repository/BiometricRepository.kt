package com.mk.kmpshowcase.domain.repository

import com.mk.kmpshowcase.domain.model.BiometricResult

interface BiometricRepository {
    fun enabled(): Boolean
    suspend fun authenticate(): BiometricResult
}
