package com.mk.kmpshowcase.domain.useCase.storage

import kotlinx.coroutines.flow.Flow
import com.mk.kmpshowcase.domain.model.StorageData
import com.mk.kmpshowcase.domain.repository.StorageRepository
import com.mk.kmpshowcase.domain.useCase.base.FlowUseCase
import com.mk.kmpshowcase.domain.useCase.base.None

class ObserveStorageDataUseCase(
    private val storageRepository: StorageRepository
) : FlowUseCase<None, StorageData>() {
    override fun run(params: None): Flow<StorageData> = storageRepository.storageData
}
