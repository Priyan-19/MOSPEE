package com.mospee.data.repository

import com.mospee.data.local.dao.LocationPointDao
import com.mospee.data.local.dao.TripDao
import com.mospee.data.local.entity.LocationPointEntity
import com.mospee.data.local.entity.TripEntity
import com.mospee.domain.model.LocationPoint
import com.mospee.domain.model.Trip
import com.mospee.domain.repository.TripRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TripRepositoryImpl @Inject constructor(
    private val tripDao: TripDao,
    private val locationPointDao: LocationPointDao
) : TripRepository {

    override suspend fun startTrip(startTime: Long): Long {
        val entity = TripEntity(startTime = startTime)
        return tripDao.insertTrip(entity)
    }

    override suspend fun stopTrip(
        tripId: Long,
        endTime: Long,
        distanceMeters: Float,
        avgSpeedKmh: Float,
        topSpeedKmh: Float,
        durationSeconds: Long
    ) {
        val existing = tripDao.getTripById(tripId) ?: return
        val updated = existing.copy(
            endTime = endTime,
            distanceMeters = distanceMeters,
            avgSpeedKmh = avgSpeedKmh,
            topSpeedKmh = topSpeedKmh,
            durationSeconds = durationSeconds
        )
        tripDao.updateTrip(updated)
    }

    override suspend fun saveLocationPoint(point: LocationPoint) {
        locationPointDao.insertPoint(point.toEntity())
    }

    override fun getAllTrips(): Flow<List<Trip>> =
        tripDao.getAllTrips().map { list -> list.map { it.toDomain() } }

    override suspend fun getTripById(id: Long): Trip? =
        tripDao.getTripById(id)?.toDomain()

    override suspend fun getLastTrip(): Trip? =
        tripDao.getLastTrip()?.toDomain()

    override suspend fun getLocationPointsForTrip(tripId: Long): List<LocationPoint> =
        locationPointDao.getPointsForTrip(tripId).map { it.toDomain() }

    override fun observeLocationPointsForTrip(tripId: Long): Flow<List<LocationPoint>> =
        locationPointDao.observePointsForTrip(tripId).map { points ->
            points.map { it.toDomain() }
        }

    override suspend fun deleteTrip(tripId: Long) {
        tripDao.deleteTripById(tripId)
    }

    // ---- Mappers ----

    private fun TripEntity.toDomain() = Trip(
        id = id,
        startTime = startTime,
        endTime = endTime,
        distanceMeters = distanceMeters,
        avgSpeedKmh = avgSpeedKmh,
        topSpeedKmh = topSpeedKmh,
        durationSeconds = durationSeconds
    )

    private fun LocationPointEntity.toDomain() = LocationPoint(
        id = id,
        tripId = tripId,
        latitude = latitude,
        longitude = longitude,
        speedKmh = speedKmh,
        accuracyMeters = accuracyMeters,
        timestamp = timestamp
    )

    private fun LocationPoint.toEntity() = LocationPointEntity(
        tripId = tripId,
        latitude = latitude,
        longitude = longitude,
        speedKmh = speedKmh,
        accuracyMeters = accuracyMeters,
        timestamp = timestamp
    )
}
