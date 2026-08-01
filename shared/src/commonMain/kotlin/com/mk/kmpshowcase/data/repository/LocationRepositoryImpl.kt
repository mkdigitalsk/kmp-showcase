package com.mk.kmpshowcase.data.repository

import kotlinx.coroutines.flow.Flow
import com.mk.kmpshowcase.data.client.LocationClient
import com.mk.kmpshowcase.domain.exceptions.base.LocationErrorCode
import com.mk.kmpshowcase.domain.exceptions.base.LocationException
import com.mk.kmpshowcase.domain.model.Location
import com.mk.kmpshowcase.domain.repository.LocationRepository

class LocationRepositoryImpl(
    private val locationClient: LocationClient
) : LocationRepository {

    override suspend fun lastKnownLocation(): Location {
        return locationClient.lastKnown() ?: throw LocationException(
            message = "Last known location not available",
            userMessage = "Location not available. Please enable location services.",
            errorCode = LocationErrorCode.NOT_AVAILABLE
        )
    }

    override fun locationUpdates(highAccuracy: Boolean): Flow<Location> {
        return locationClient.updates(highAccuracy)
    }
}
