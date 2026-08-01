package com.mk.kmpshowcase.domain.repository

import kotlinx.coroutines.flow.Flow
import com.mk.kmpshowcase.domain.model.StorageData

interface StorageRepository : ClearableCache {
    val storageData: Flow<StorageData>

    suspend fun loadInitialData()
    suspend fun setSessionCounter(value: Int)
    suspend fun setPersistentCounter(value: Int)
}
