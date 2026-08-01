package com.mk.kmpshowcase.domain.useCase.location

import kotlinx.coroutines.flow.Flow
import com.mk.kmpshowcase.domain.model.Location
import com.mk.kmpshowcase.domain.repository.LocationRepository
import com.mk.kmpshowcase.domain.useCase.base.FlowUseCase

class ObserveLocationUpdatesUseCase(
    private val locationRepository: LocationRepository
) : FlowUseCase<ObserveLocationUpdatesUseCase.Params, Location>() {

    override fun run(params: Params): Flow<Location> =
        locationRepository.locationUpdates(params.highAccuracy)

    data class Params(val highAccuracy: Boolean = false)
}
