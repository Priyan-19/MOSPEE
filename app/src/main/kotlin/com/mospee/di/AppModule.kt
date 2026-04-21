package com.mospee.di

import android.content.Context
import android.location.LocationManager
import androidx.room.Room
import com.mospee.data.local.AppDatabase
import com.mospee.data.local.dao.LocationPointDao
import com.mospee.data.local.dao.TripDao
import com.mospee.data.repository.TripRepositoryImpl
import com.mospee.domain.repository.TripRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideTripDao(db: AppDatabase): TripDao = db.tripDao()

    @Provides
    fun provideLocationPointDao(db: AppDatabase): LocationPointDao = db.locationPointDao()

    @Provides
    @Singleton
    fun provideTripRepository(impl: TripRepositoryImpl): TripRepository = impl

    @Provides
    @Singleton
    fun provideLocationManager(
        @ApplicationContext context: Context
    ): LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
}
