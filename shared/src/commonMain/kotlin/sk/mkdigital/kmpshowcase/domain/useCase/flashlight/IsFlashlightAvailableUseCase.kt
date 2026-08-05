package sk.mkdigital.kmpshowcase.domain.useCase.flashlight

import sk.mkdigital.kmpshowcase.domain.repository.FlashlightRepository
import sk.mkdigital.kmpshowcase.domain.useCase.base.None
import sk.mkdigital.kmpshowcase.domain.useCase.base.UseCase

class IsFlashlightAvailableUseCase(
    private val flashlightRepository: FlashlightRepository
) : UseCase<None, Boolean>() {
    override suspend fun run(params: None): Boolean = flashlightRepository.isAvailable()
}
