package sk.mkdigital.kmpshowcase.data.repository

import sk.mkdigital.kmpshowcase.data.local.StorageLocalStore
import sk.mkdigital.kmpshowcase.domain.model.StorageData
import sk.mkdigital.kmpshowcase.domain.repository.StorageRepository
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
