package sk.mkdigital.kmpshowcase.domain.useCase.storage

import sk.mkdigital.kmpshowcase.domain.repository.StorageRepository
import sk.mkdigital.kmpshowcase.domain.useCase.base.None
import sk.mkdigital.kmpshowcase.domain.useCase.base.UseCase

class LoadStorageDataUseCase(
    private val storageRepository: StorageRepository
) : UseCase<None, Unit>() {
    override suspend fun run(params: None) = storageRepository.loadInitialData()
}
