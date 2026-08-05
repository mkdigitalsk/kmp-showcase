package sk.mkdigital.kmpshowcase.domain.repository

import kotlinx.coroutines.flow.Flow
import sk.mkdigital.kmpshowcase.domain.model.StorageData

interface StorageRepository : ClearableCache {
    val storageData: Flow<StorageData>

    suspend fun loadInitialData()
    suspend fun setSessionCounter(value: Int)
    suspend fun setPersistentCounter(value: Int)
}
