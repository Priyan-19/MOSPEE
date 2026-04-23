package com.mospee.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.mospee.MainActivity
import com.mospee.R
import com.mospee.domain.model.LocationPoint
import com.mospee.domain.repository.TripRepository
import com.mospee.utils.Constants
import com.mospee.utils.LocationUtils
import com.mospee.data.repository.UserPreferencesRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class LocationForegroundService : Service() {

    @Inject lateinit var repository: TripRepository
    @Inject lateinit var locationManager: LocationManager
    @Inject lateinit var prefsRepository: UserPreferencesRepository
    @Inject lateinit var locationClient: com.mospee.location.LocationClient

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var locationJob: Job? = null

    private var currentTripId: Long = -1L
    private var lastLocation: Location? = null
    private var totalDistanceMeters: Float = 0f
    private var topSpeedKmh: Float = 0f
    private var speedReadings = mutableListOf<Float>()
    private var tripStartTime: Long = 0L
    private var isPaused: Boolean = false
    private var useKmh: Boolean = true


    companion object {
        // Shared live data from service → ViewModels
        private val _liveTripData = MutableStateFlow(ServiceTripState())
        val liveTripData: StateFlow<ServiceTripState> = _liveTripData.asStateFlow()
    }

    data class ServiceTripState(
        val currentSpeedKmh: Float = 0f,
        val avgSpeedKmh: Float = 0f,
        val topSpeedKmh: Float = 0f,
        val distanceMeters: Float = 0f,
        val elapsedSeconds: Long = 0L,
        val currentLat: Double? = null,
        val currentLng: Double? = null,
        val isTracking: Boolean = false,
        val isPaused: Boolean = false,
        val tripId: Long = -1L
    )

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        observePreferences()
    }

    private fun observePreferences() {
        serviceScope.launch {
            prefsRepository.useKmh.collect {
                useKmh = it
                if (_liveTripData.value.isTracking) {
                    updateNotification()
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            Constants.ACTION_START_TRACKING -> {
                val tripId = intent.getLongExtra(Constants.EXTRA_TRIP_ID, -1L)
                if (tripId != -1L) startTracking(tripId)
            }
            Constants.ACTION_PAUSE_TRACKING -> togglePause()
            Constants.ACTION_STOP_TRACKING  -> stopTracking()
        }
        return START_STICKY
    }

    // ── Location Request ──────────────────────────────────────────────────────

    @SuppressLint("MissingPermission")
    private fun startTracking(tripId: Long) {
        currentTripId = tripId
        tripStartTime = System.currentTimeMillis()
        totalDistanceMeters = 0f
        topSpeedKmh = 0f
        speedReadings.clear()
        lastLocation = null
        isPaused = false

        startForeground(
            Constants.NOTIFICATION_ID,
            buildNotification(
                title = "MOSPEE - Tracking Active",
                speedText = "Speed: 0.0 km/h",
                summaryText = "Distance: 0.00 km • 00:00"
            )
        )
        startLocationUpdates()
        startElapsedTimer()

        _liveTripData.value = ServiceTripState(isTracking = true, tripId = tripId)
    }

    private fun startLocationUpdates() {
        locationJob?.cancel()
        locationJob = locationClient
            .getLocationUpdates(Constants.LOCATION_UPDATE_INTERVAL_MS)
            .onEach { location ->
                if (!isPaused) {
                    onNewLocation(location)
                }
            }
            .catch { e -> e.printStackTrace() }
            .launchIn(serviceScope)
    }

    private fun onNewLocation(location: Location) {
        // Filter noise
        if (!LocationUtils.isLocationValid(location, lastLocation)) return

        var speedKmh = LocationUtils.msToKmh(location.speed)
        if (speedKmh < 1.5f) speedKmh = 0f

        // Accumulate distance & Update stats
        lastLocation?.let { last ->
            val delta = location.distanceTo(last)
            // MINIMUM MOVEMENT FILTER (5 meters) for polyline smoothing as requested
            if (delta >= 5.0f && delta < Constants.MAX_CONSECUTIVE_DISTANCE_M) {
                totalDistanceMeters += delta
                
                // Save to DB ONLY when moved significantly to avoid "piles" of points
                ioScope.launch {
                    repository.saveLocationPoint(
                        LocationPoint(
                            tripId = currentTripId,
                            latitude = location.latitude,
                            longitude = location.longitude,
                            speedKmh = speedKmh,
                            accuracyMeters = location.accuracy,
                            timestamp = location.time
                        )
                    )
                }
            }
        } ?: run {
            // Initial point, always save
            ioScope.launch {
                repository.saveLocationPoint(
                    LocationPoint(
                        tripId = currentTripId,
                        latitude = location.latitude,
                        longitude = location.longitude,
                        speedKmh = speedKmh,
                        accuracyMeters = location.accuracy,
                        timestamp = location.time
                    )
                )
            }
        }

        // Update speed stats (even if didn't move 5m, speed can still change)
        if (speedKmh > topSpeedKmh) topSpeedKmh = speedKmh
        speedReadings.add(speedKmh)
        val avgSpeedKmh = if (speedReadings.isNotEmpty()) speedReadings.average().toFloat() else 0f

        lastLocation = location

        // Emit live update for UI
        val elapsed = (System.currentTimeMillis() - tripStartTime) / 1000L
        _liveTripData.value = _liveTripData.value.copy(
            currentSpeedKmh = speedKmh,
            avgSpeedKmh = avgSpeedKmh,
            topSpeedKmh = topSpeedKmh,
            distanceMeters = totalDistanceMeters,
            elapsedSeconds = elapsed,
            currentLat = location.latitude,
            currentLng = location.longitude
        )

        // Update notification
        updateNotification()
    }

    // ── Timer ─────────────────────────────────────────────────────────────────

    private fun startElapsedTimer() {
        serviceScope.launch {
            while (_liveTripData.value.isTracking) {
                delay(1000)
                if (!isPaused) {
                    val elapsed = (System.currentTimeMillis() - tripStartTime) / 1000L
                    _liveTripData.value = _liveTripData.value.copy(elapsedSeconds = elapsed)
                    updateNotification()
                }
            }
        }
    }

    // ── Controls ──────────────────────────────────────────────────────────────

    private fun togglePause() {
        isPaused = !isPaused
        _liveTripData.value = _liveTripData.value.copy(isPaused = isPaused)
        updateNotification()
    }

    private fun stopTracking() {
        val finalState = _liveTripData.value
        serviceScope.launch {
            if (currentTripId != -1L) {
                val avgSpeed = if (speedReadings.isNotEmpty())
                    speedReadings.average().toFloat() else 0f
                val duration = (System.currentTimeMillis() - tripStartTime) / 1000L

                repository.stopTrip(
                    tripId = currentTripId,
                    endTime = System.currentTimeMillis(),
                    distanceMeters = totalDistanceMeters,
                    avgSpeedKmh = avgSpeed,
                    topSpeedKmh = topSpeedKmh,
                    durationSeconds = duration
                )
            }
        }

        locationJob?.cancel()
        _liveTripData.value = ServiceTripState(isTracking = false, tripId = currentTripId)
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    // ── Notification ──────────────────────────────────────────────────────────

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            Constants.NOTIFICATION_CHANNEL_ID,
            Constants.NOTIFICATION_CHANNEL_NAME,
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "MOSPEE live trip tracking"
            setShowBadge(false)
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    private fun buildNotification(
        title: String,
        speedText: String,
        summaryText: String
    ): Notification {
        val openIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, openIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val stopIntent = Intent(this, LocationForegroundService::class.java).apply {
            action = Constants.ACTION_STOP_TRACKING
        }
        val stopPending = PendingIntent.getService(
            this, 1, stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val pauseIntent = Intent(this, LocationForegroundService::class.java).apply {
            action = Constants.ACTION_PAUSE_TRACKING
        }
        val pausePending = PendingIntent.getService(
            this, 2, pauseIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, Constants.NOTIFICATION_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(speedText)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$speedText\n$summaryText")
            )
            .setSmallIcon(R.drawable.ic_speed)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .addAction(
                R.drawable.ic_speed,
                if (isPaused) "Resume" else "Pause",
                pausePending
            )
            .addAction(R.drawable.ic_stop, "Stop", stopPending)
            .build()
    }

    private fun updateNotification() {
        val state = _liveTripData.value
        val speedValue = if (useKmh) state.currentSpeedKmh else LocationUtils.kmhToMph(state.currentSpeedKmh)
        val unit = if (useKmh) "km/h" else "mph"
        val speedText = "Speed: ${"%.1f".format(speedValue)} $unit"
        val summaryText = buildString {
            val distValue = if (useKmh) state.distanceMeters / 1000f else state.distanceMeters * Constants.METERS_TO_MILES
            val distUnit = if (useKmh) "km" else "mi"
            append("Distance: ${"%.2f".format(distValue)} $distUnit")
            append(" • ")
            append(LocationUtils.formatDuration(state.elapsedSeconds))
            if (state.isPaused) {
                append(" • Paused")
            }
        }
        val notification = buildNotification(
            title = "MOSPEE - Tracking Active",
            speedText = speedText,
            summaryText = summaryText
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(Constants.NOTIFICATION_ID, notification)
    }
}
