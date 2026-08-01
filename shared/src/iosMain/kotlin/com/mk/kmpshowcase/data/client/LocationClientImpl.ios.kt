package com.mk.kmpshowcase.data.client

import kotlinx.cinterop.useContents
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import com.mk.kmpshowcase.domain.model.Location
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.CoreLocation.kCLLocationAccuracyHundredMeters
import platform.Foundation.NSError
import platform.Foundation.NSLog
import platform.darwin.NSObject

actual class LocationClientImpl : LocationClient {

    private val manager = CLLocationManager()
    private var out: SendChannel<Location>? = null

    private val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {

        override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
            val l = didUpdateLocations.lastOrNull() as? CLLocation ?: run {
                NSLog("[%s] didUpdateLocations: empty or not CLLocation", TAG)
                return
            }
            val (lat, lng) = l.coordinate.useContents { latitude to longitude }
            NSLog("[%s] didUpdateLocations: lat=%f, lng=%f, accuracy=%.2f", TAG, lat, lng, l.horizontalAccuracy)
            out?.trySend(Location(lat, lng))
        }

        override fun locationManager(manager: CLLocationManager, didFailWithError: NSError) {
            NSLog("[%s] didFailWithError: %@", TAG, didFailWithError)
        }
    }

    init {
        manager.delegate = delegate
    }

    actual override suspend fun lastKnown(): Location? {
        val loc = manager.location
        if (loc == null) {
            NSLog("[%s] lastKnown: null (no cached location yet)", TAG)
            return null
        }
        val (lat, long) = loc.coordinate.useContents { latitude to longitude }
        NSLog("[%s] lastKnown: lat=%f, lng=%f, accuracy=%.2f", TAG, lat, long, loc.horizontalAccuracy)
        return Location(lat, long)
    }

    actual override fun updates(highAccuracy: Boolean): Flow<Location> = callbackFlow {
        out = channel

        manager.desiredAccuracy = if (highAccuracy) kCLLocationAccuracyBest else kCLLocationAccuracyHundredMeters
        manager.startUpdatingLocation()
        awaitClose {
            NSLog("[%s] updates(stop): stopping updates", TAG)
            out = null
            manager.stopUpdatingLocation()
        }
    }

    private companion object {
        private const val TAG = "LocationClientImpl"
    }
}
