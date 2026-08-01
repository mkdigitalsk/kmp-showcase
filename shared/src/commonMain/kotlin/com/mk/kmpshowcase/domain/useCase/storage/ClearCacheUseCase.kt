package com.mk.kmpshowcase.domain.useCase.storage

import com.mk.kmpshowcase.domain.repository.StorageRepository
import com.mk.kmpshowcase.domain.useCase.base.None
import com.mk.kmpshowcase.domain.useCase.base.UseCase

class ClearCacheUseCase(
    private val storageRepository: StorageRepository
) : UseCase<None, Unit>() {
    override suspend fun run(params: None) = storageRepository.clear()
}
