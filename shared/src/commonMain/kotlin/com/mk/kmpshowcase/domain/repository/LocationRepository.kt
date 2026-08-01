package com.mk.kmpshowcase.domain.repository

import kotlinx.coroutines.flow.Flow
import com.mk.kmpshowcase.domain.model.Location

interface LocationRepository {
    suspend fun lastKnownLocation(): Location
    fun locationUpdates(highAccuracy: Boolean = false): Flow<Location>
}
