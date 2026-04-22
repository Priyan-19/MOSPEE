package com.mospee.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mospee.data.local.dao.LocationPointDao
import com.mospee.data.local.dao.TripDao
import com.mospee.data.local.entity.LocationPointEntity
import com.mospee.data.local.entity.TripEntity

@Database(
    entities = [TripEntity::class, LocationPointEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun tripDao(): TripDao
    abstract fun locationPointDao(): LocationPointDao

    companion object {
        const val DATABASE_NAME = "mospee.db"
    }
}
