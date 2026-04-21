package com.mospee.domain.model

data class Trip(
    val id: Long = 0,
    val startTime: Long,
    val endTime: Long = 0L,
    val distanceMeters: Float = 0f,
    val avgSpeedKmh: Float = 0f,
    val topSpeedKmh: Float = 0f,
    val durationSeconds: Long = 0L
) {
    val isActive: Boolean get() = endTime == 0L
}

data class LocationPoint(
    val id: Long = 0,
    val tripId: Long,
    val latitude: Double,
    val longitude: Double,
    val speedKmh: Float,
    val accuracyMeters: Float,
    val timestamp: Long
)

data class LiveTripData(
    val currentSpeedKmh: Float = 0f,
    val avgSpeedKmh: Float = 0f,
    val topSpeedKmh: Float = 0f,
    val distanceMeters: Float = 0f,
    val elapsedSeconds: Long = 0L,
    val isTracking: Boolean = false,
    val isPaused: Boolean = false,
    val tripId: Long = -1L
)
