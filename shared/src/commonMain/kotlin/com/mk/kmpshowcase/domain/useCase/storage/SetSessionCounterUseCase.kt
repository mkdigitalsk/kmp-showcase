package com.mk.kmpshowcase.domain.useCase.storage

import com.mk.kmpshowcase.domain.repository.StorageRepository
import com.mk.kmpshowcase.domain.useCase.base.UseCase

class SetSessionCounterUseCase(
    private val storageRepository: StorageRepository
) : UseCase<Int, Unit>() {
    override suspend fun run(params: Int) = storageRepository.setSessionCounter(params)
}
