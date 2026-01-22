package mk.digital.kmpshowcase.domain.useCase.storage

import mk.digital.kmpshowcase.domain.repository.StorageRepository
import mk.digital.kmpshowcase.domain.useCase.base.UseCase

class SetSessionCounterUseCase(
    private val storageRepository: StorageRepository
) : UseCase<Int, Unit>() {
    override suspend fun run(params: Int) = storageRepository.setSessionCounter(params)
}
