package com.mk.kmpshowcase.data.repository

import com.mk.kmpshowcase.data.local.StorageLocalStore
import com.mk.kmpshowcase.domain.model.StorageData
import com.mk.kmpshowcase.domain.repository.StorageRepository
import kotlinx.coroutines.flow.Flow

class StorageRepositoryImpl(
    private val storageLocalStore: StorageLocalStore
) : StorageRepository {

    override val storageData: Flow<StorageData> = storageLocalStore.data

    override suspend fun loadInitialData() = storageLocalStore.load()

    override suspend fun setSessionCounter(value: Int) = storageLocalStore.setSessionCounter(value)

    override suspend fun setPersistentCounter(value: Int) = storageLocalStore.setPersistentCounter(value)

    override suspend fun clear() {
        storageLocalStore.clear()
    }
}
