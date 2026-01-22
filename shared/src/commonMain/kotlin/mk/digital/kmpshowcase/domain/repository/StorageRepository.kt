package mk.digital.kmpshowcase.domain.repository

import kotlinx.coroutines.flow.Flow
import mk.digital.kmpshowcase.domain.model.StorageData

interface StorageRepository : ClearableCache {
    val storageData: Flow<StorageData>

    suspend fun loadInitialData()
    suspend fun setSessionCounter(value: Int)
    suspend fun setPersistentCounter(value: Int)
}
