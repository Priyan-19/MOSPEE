package com.mospee.domain.usecase

import com.mospee.domain.model.LocationPoint
import com.mospee.domain.model.Trip
import com.mospee.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StartTripUseCase @Inject constructor(
    private val repository: TripRepository
) {
    suspend operator fun invoke(): Long {
        val startTime = System.currentTimeMillis()
        return repository.startTrip(startTime)
    }
}

class StopTripUseCase @Inject constructor(
    private val repository: TripRepository
) {
    suspend operator fun invoke(
        tripId: Long,
        distanceMeters: Float,
        avgSpeedKmh: Float,
        topSpeedKmh: Float,
        durationSeconds: Long
    ) {
        repository.stopTrip(
            tripId = tripId,
            endTime = System.currentTimeMillis(),
            distanceMeters = distanceMeters,
            avgSpeedKmh = avgSpeedKmh,
            topSpeedKmh = topSpeedKmh,
            durationSeconds = durationSeconds
        )
    }
}

class GetAllTripsUseCase @Inject constructor(
    private val repository: TripRepository
) {
    operator fun invoke(): Flow<List<Trip>> = repository.getAllTrips()
}

class GetTripDetailsUseCase @Inject constructor(
    private val repository: TripRepository
) {
    suspend operator fun invoke(tripId: Long): Pair<Trip?, List<LocationPoint>> {
        val trip = repository.getTripById(tripId) ?: return Pair(null, emptyList())
        val points = repository.getLocationPointsForTrip(tripId)
        
        return if (points.isEmpty() && trip.encodedRoute.isNotEmpty()) {
            val decoded = com.mospee.utils.RouteUtils.decodeRoute(trip.encodedRoute).mapIndexed { index, gp ->
                LocationPoint(
                    id = index.toLong(),
                    tripId = tripId,
                    latitude = gp.latitude,
                    longitude = gp.longitude,
                    speedKmh = 0f,
                    accuracyMeters = 0f,
                    timestamp = 0L
                )
            }
            Pair(trip, decoded)
        } else {
            Pair(trip, points)
        }
    }
}

class DeleteTripUseCase @Inject constructor(
    private val repository: TripRepository
) {
    suspend operator fun invoke(tripId: Long) = repository.deleteTrip(tripId)
}

class GetLastTripUseCase @Inject constructor(
    private val repository: TripRepository
) {
    suspend operator fun invoke(): Trip? = repository.getLastTrip()
}
