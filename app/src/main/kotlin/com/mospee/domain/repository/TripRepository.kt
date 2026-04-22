package com.mospee.domain.repository

import com.mospee.domain.model.LocationPoint
import com.mospee.domain.model.Trip
import kotlinx.coroutines.flow.Flow

interface TripRepository {

    /**
     * Start a new trip — inserts a TripEntity with startTime and returns its ID.
     */
    suspend fun startTrip(startTime: Long): Long

    /**
     * Finalise the trip by writing endTime, distance, avgSpeed, topSpeed.
     */
    suspend fun stopTrip(
        tripId: Long,
        endTime: Long,
        distanceMeters: Float,
        avgSpeedKmh: Float,
        topSpeedKmh: Float,
        durationSeconds: Long
    )

    /**
     * Save a single location point during an active trip.
     */
    suspend fun saveLocationPoint(point: LocationPoint)

    /**
     * Observe all trips ordered by most recent first.
     */
    fun getAllTrips(): Flow<List<Trip>>

    /**
     * Get a single trip by ID.
     */
    suspend fun getTripById(id: Long): Trip?

    /**
     * Get the most recently recorded trip.
     */
    suspend fun getLastTrip(): Trip?

    /**
     * Get all location points for a specific trip.
     */
    suspend fun getLocationPointsForTrip(tripId: Long): List<LocationPoint>

    /**
     * Observe location points for an active or completed trip.
     */
    fun observeLocationPointsForTrip(tripId: Long): Flow<List<LocationPoint>>

    /**
     * Delete a trip and all its location points (cascade).
     */
    suspend fun deleteTrip(tripId: Long)

    /**
     * Sync all pending trips to Firebase.
     */
    suspend fun syncPendingTrips()
}
