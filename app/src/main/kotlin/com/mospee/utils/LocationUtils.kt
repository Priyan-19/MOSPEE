package com.mospee.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import java.util.*
import java.util.concurrent.TimeUnit

object LocationUtils {

    fun hasLocationPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun isLocationValid(newLocation: Location, lastLocation: Location?): Boolean {
        if (newLocation.accuracy > 20f) return false
        if (lastLocation == null) return true

        val distance = lastLocation.distanceTo(newLocation)
        val timeDelta = (newLocation.time - lastLocation.time) / 1000.0 // seconds
        
        if (timeDelta <= 0) return false
        
        // Speed filter: if speed > 300 km/h, likely a jump
        val speedKmh = (distance / timeDelta) * 3.6
        if (speedKmh > 300) return false

        return true
    }

    fun formatDuration(seconds: Long): String {
        val hours = TimeUnit.SECONDS.toHours(seconds)
        val minutes = TimeUnit.SECONDS.toMinutes(seconds) % 60
        val secs = seconds % 60
        return if (hours > 0) {
            String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, secs)
        } else {
            String.format(Locale.getDefault(), "%02d:%02d", minutes, secs)
        }
    }

    fun formatDistance(meters: Float, useKmh: Boolean): String {
        return if (useKmh) {
            val km = meters / 1000f
            String.format(Locale.getDefault(), "%.2f km", km)
        } else {
            val miles = meters * 0.000621371f
            String.format(Locale.getDefault(), "%.2f mi", miles)
        }
    }

    fun formatSpeed(speedKmh: Float, useKmh: Boolean): String {
        return if (useKmh) {
            String.format(Locale.getDefault(), "%.1f km/h", speedKmh)
        } else {
            val mph = kmhToMph(speedKmh)
            String.format(Locale.getDefault(), "%.1f mph", mph)
        }
    }

    fun msToKmh(ms: Float): Float = ms * 3.6f
    fun kmhToMph(kmh: Float): Float = kmh * 0.621371f
}

fun Context.hasLocationPermission(): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED &&
    ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
}
