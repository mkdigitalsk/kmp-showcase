package sk.mkdigital.kmpshowcase.domain.useCase.storage

import sk.mkdigital.kmpshowcase.domain.repository.StorageRepository
import sk.mkdigital.kmpshowcase.domain.useCase.base.UseCase

class SetSessionCounterUseCase(
    private val storageRepository: StorageRepository
) : UseCase<Int, Unit>() {
    override suspend fun run(params: Int) = storageRepository.setSessionCounter(params)
}
