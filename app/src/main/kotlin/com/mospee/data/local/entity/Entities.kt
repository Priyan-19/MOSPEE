package com.mospee.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val startTime: Long,
    val endTime: Long = 0L,
    val distanceMeters: Float = 0f,
    val avgSpeedKmh: Float = 0f,
    val topSpeedKmh: Float = 0f,
    val durationSeconds: Long = 0L
)

@Entity(
    tableName = "location_points",
    foreignKeys = [
        ForeignKey(
            entity = TripEntity::class,
            parentColumns = ["id"],
            childColumns = ["tripId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("tripId")]
)
data class LocationPointEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tripId: Long,
    val latitude: Double,
    val longitude: Double,
    val speedKmh: Float,
    val accuracyMeters: Float,
    val timestamp: Long
)
