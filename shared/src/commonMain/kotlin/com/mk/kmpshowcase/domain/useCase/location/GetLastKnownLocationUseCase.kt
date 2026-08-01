package com.mk.kmpshowcase.domain.useCase.location

import com.mk.kmpshowcase.domain.model.Location
import com.mk.kmpshowcase.domain.repository.LocationRepository
import com.mk.kmpshowcase.domain.useCase.base.None
import com.mk.kmpshowcase.domain.useCase.base.UseCase

class GetLastKnownLocationUseCase(
    private val locationRepository: LocationRepository
) : UseCase<None, Location>() {
    override suspend fun run(params: None): Location = locationRepository.lastKnownLocation()
}
