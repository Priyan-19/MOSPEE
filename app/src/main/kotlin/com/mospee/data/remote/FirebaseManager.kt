package com.mospee.data.remote

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import com.mospee.data.local.entity.TripEntity
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseManager @Inject constructor() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    suspend fun signInAnonymously(): String? {
        return try {
            val result = auth.signInAnonymously().await()
            result.user?.uid
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    suspend fun uploadTrip(trip: TripEntity): String? {
        val userId = getCurrentUserId() ?: return null
        
        val tripData = hashMapOf(
            "userId" to userId,
            "startTime" to trip.startTime,
            "endTime" to trip.endTime,
            "distance" to trip.distanceMeters,
            "avgSpeed" to trip.avgSpeedKmh,
            "topSpeed" to trip.topSpeedKmh,
            "duration" to trip.durationSeconds,
            "encodedRoute" to trip.encodedRoute,
            "createdAt" to FieldValue.serverTimestamp()
        )

        return try {
            val docRef = db.collection("trips").add(tripData).await()
            docRef.id
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
