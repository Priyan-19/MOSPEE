package com.mospee.data.local.dao

import androidx.room.*
import com.mospee.data.local.entity.LocationPointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationPointDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoint(point: LocationPointEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoints(points: List<LocationPointEntity>)

    @Query("SELECT * FROM location_points WHERE tripId = :tripId ORDER BY timestamp ASC")
    suspend fun getPointsForTrip(tripId: Long): List<LocationPointEntity>

    @Query("SELECT * FROM location_points WHERE tripId = :tripId ORDER BY timestamp ASC")
    fun observePointsForTrip(tripId: Long): Flow<List<LocationPointEntity>>

    @Query("DELETE FROM location_points WHERE tripId = :tripId")
    suspend fun deletePointsForTrip(tripId: Long)

    @Query("SELECT COUNT(*) FROM location_points WHERE tripId = :tripId")
    suspend fun getPointCountForTrip(tripId: Long): Int
}
