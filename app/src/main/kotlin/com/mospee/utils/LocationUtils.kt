package com.mospee.utils

import android.location.Location
import kotlin.math.abs

object LocationUtils {

    /**
     * Convert m/s to km/h
     */
    fun msToKmh(speedMs: Float): Float = speedMs * Constants.MS_TO_KMH

    /**
     * Convert km/h to mph
     */
    fun kmhToMph(speedKmh: Float): Float = speedKmh * Constants.KMH_TO_MPH

    /**
     * Format speed based on unit preference
     */
    fun formatSpeed(speedKmh: Float, useKmh: Boolean): String {
        return if (useKmh) {
            "%.1f".format(speedKmh)
        } else {
            "%.1f".format(kmhToMph(speedKmh))
        }
    }

    /**
     * Format distance e.g. "1.23 km" or "0.76 mi"
     */
    fun formatDistance(distanceMeters: Float, useKmh: Boolean): String {
        return if (useKmh) {
            if (distanceMeters < 1000) "%.0f m".format(distanceMeters)
            else "%.2f km".format(distanceMeters * Constants.METERS_TO_KM)
        } else {
            val miles = distanceMeters * Constants.METERS_TO_MILES
            if (miles < 0.1f) "%.0f ft".format(distanceMeters * 3.28084f)
            else "%.2f mi".format(miles)
        }
    }

    /**
     * Format elapsed seconds as HH:MM:SS
     */
    fun formatDuration(seconds: Long): String {
        val h = seconds / 3600
        val m = (seconds % 3600) / 60
        val s = seconds % 60
        return "%02d:%02d:%02d".format(h, m, s)
    }

    /**
     * Filter out GPS noise spikes.
     * Returns true if the location point is valid.
     */
    fun isLocationValid(
        newLocation: Location,
        lastLocation: Location?
    ): Boolean {
        // Reject poor accuracy
        if (newLocation.accuracy > Constants.MIN_ACCURACY_METERS) return false

        // Reject unrealistic speed values
        val speedKmh = msToKmh(newLocation.speed)
        if (speedKmh > Constants.MAX_VALID_SPEED_KMH) return false

        // Reject teleport jumps
        if (lastLocation != null) {
            val distanceToLast = newLocation.distanceTo(lastLocation)
            val timeDiffSeconds = abs(newLocation.time - lastLocation.time) / 1000f
            if (timeDiffSeconds > 0.5f) {
                val impliedSpeed = distanceToLast / timeDiffSeconds  // m/s
                val impliedSpeedKmh = msToKmh(impliedSpeed)
                // Allow a bit more buffer for implied speed (1.5x) to account for jitter
                if (impliedSpeedKmh > Constants.MAX_VALID_SPEED_KMH * 1.5f) return false
            }
            // Also reject if jumped more than 500m instantly
            if (distanceToLast > Constants.MAX_CONSECUTIVE_DISTANCE_M && timeDiffSeconds < 5f) {
                return false
            }
        }

        return true
    }

    /**
     * Calculate distance between two lat/lng pairs in metres
     */
    fun distanceBetween(
        lat1: Double, lng1: Double,
        lat2: Double, lng2: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lng1, lat2, lng2, results)
        return results[0]
    }
}
