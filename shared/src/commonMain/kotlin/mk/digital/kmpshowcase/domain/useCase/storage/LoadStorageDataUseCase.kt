package mk.digital.kmpshowcase.domain.useCase.storage

import mk.digital.kmpshowcase.domain.repository.StorageRepository
import mk.digital.kmpshowcase.domain.useCase.base.None
import mk.digital.kmpshowcase.domain.useCase.base.UseCase

class LoadStorageDataUseCase(
    private val storageRepository: StorageRepository
) : UseCase<None, Unit>() {
    override suspend fun run(params: None) = storageRepository.loadInitialData()
}
