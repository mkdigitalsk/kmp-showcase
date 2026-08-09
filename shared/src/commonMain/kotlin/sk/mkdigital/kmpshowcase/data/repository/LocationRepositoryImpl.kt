package sk.mkdigital.kmpshowcase.data.repository

import kotlinx.coroutines.flow.Flow
import sk.mkdigital.kmpshowcase.data.client.LocationClient
import sk.mkdigital.kmpshowcase.domain.exceptions.base.LocationErrorCode
import sk.mkdigital.kmpshowcase.domain.exceptions.base.LocationException
import sk.mkdigital.kmpshowcase.domain.model.Location
import sk.mkdigital.kmpshowcase.domain.repository.LocationRepository

class LocationRepositoryImpl(
    private val locationClient: LocationClient
) : LocationRepository {

    override suspend fun lastKnownLocation(): Location {
        return locationClient.lastKnown() ?: throw LocationException(
            message = "Last known location not available",
            logMessage = "Location not available. Please enable location services.",
            errorCode = LocationErrorCode.NOT_AVAILABLE
        )
    }

    override fun locationUpdates(highAccuracy: Boolean): Flow<Location> {
        return locationClient.updates(highAccuracy)
    }
}
