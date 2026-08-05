package sk.mkdigital.kmpshowcase.domain.useCase.storage

import kotlinx.coroutines.flow.Flow
import sk.mkdigital.kmpshowcase.domain.model.StorageData
import sk.mkdigital.kmpshowcase.domain.repository.StorageRepository
import sk.mkdigital.kmpshowcase.domain.useCase.base.FlowUseCase
import sk.mkdigital.kmpshowcase.domain.useCase.base.None

class ObserveStorageDataUseCase(
    private val storageRepository: StorageRepository
) : FlowUseCase<None, StorageData>() {
    override fun run(params: None): Flow<StorageData> = storageRepository.storageData
}
