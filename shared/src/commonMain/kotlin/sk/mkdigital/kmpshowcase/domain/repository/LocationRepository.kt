package sk.mkdigital.kmpshowcase.domain.repository

import kotlinx.coroutines.flow.Flow
import sk.mkdigital.kmpshowcase.domain.model.Location

interface LocationRepository {
    suspend fun lastKnownLocation(): Location
    fun locationUpdates(highAccuracy: Boolean = false): Flow<Location>
}
