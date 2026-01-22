package mk.digital.kmpshowcase.domain.useCase.storage

import kotlinx.coroutines.flow.Flow
import mk.digital.kmpshowcase.domain.model.StorageData
import mk.digital.kmpshowcase.domain.repository.StorageRepository
import mk.digital.kmpshowcase.domain.useCase.base.FlowUseCase
import mk.digital.kmpshowcase.domain.useCase.base.None

class ObserveStorageDataUseCase(
    private val storageRepository: StorageRepository
) : FlowUseCase<None, StorageData>() {
    override fun run(params: None): Flow<StorageData> = storageRepository.storageData
}
