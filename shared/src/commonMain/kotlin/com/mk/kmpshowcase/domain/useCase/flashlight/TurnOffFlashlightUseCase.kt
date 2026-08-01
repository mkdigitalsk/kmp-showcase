package com.mk.kmpshowcase.domain.useCase.flashlight

import com.mk.kmpshowcase.domain.repository.FlashlightRepository
import com.mk.kmpshowcase.domain.useCase.base.None
import com.mk.kmpshowcase.domain.useCase.base.UseCase

class TurnOffFlashlightUseCase(
    private val flashlightRepository: FlashlightRepository
) : UseCase<None, Boolean>() {
    override suspend fun run(params: None): Boolean = flashlightRepository.turnOff()
}
