package com.mk.kmpshowcase.data.client

import kotlinx.coroutines.flow.Flow
import com.mk.kmpshowcase.domain.model.Location

interface LocationClient {
    suspend fun lastKnown(): Location?
    fun updates(highAccuracy: Boolean = false): Flow<Location>
}

expect class LocationClientImpl : LocationClient {
    override suspend fun lastKnown(): Location?
    override fun updates(highAccuracy: Boolean): Flow<Location>
}
