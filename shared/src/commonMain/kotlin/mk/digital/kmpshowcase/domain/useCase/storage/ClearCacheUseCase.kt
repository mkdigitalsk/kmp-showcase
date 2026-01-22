package mk.digital.kmpshowcase.domain.useCase.storage

import mk.digital.kmpshowcase.domain.repository.StorageRepository
import mk.digital.kmpshowcase.domain.useCase.base.None
import mk.digital.kmpshowcase.domain.useCase.base.UseCase

class ClearCacheUseCase(
    private val storageRepository: StorageRepository
) : UseCase<None, Unit>() {
    override suspend fun run(params: None) = storageRepository.clear()
}
