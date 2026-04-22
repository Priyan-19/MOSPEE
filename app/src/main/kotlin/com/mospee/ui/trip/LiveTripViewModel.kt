package com.mospee.ui.trip

import android.content.Context
import android.content.Intent
import android.media.ToneGenerator
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mospee.data.repository.UserPreferencesRepository
import com.mospee.domain.model.LocationPoint
import com.mospee.domain.repository.TripRepository
import com.mospee.domain.usecase.StartTripUseCase
import com.mospee.service.LocationForegroundService
import com.mospee.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class TripUiState {
    data object PermissionsRequired : TripUiState()
    data object GpsDisabled         : TripUiState()
    data object Starting            : TripUiState()
    data class  Tracking(
        val currentSpeedKmh: Float,
        val avgSpeedKmh: Float,
        val topSpeedKmh: Float,
        val distanceMeters: Float,
        val elapsedSeconds: Long,
        val isPaused: Boolean,
        val tripId: Long,
        val isOverspeed: Boolean
    ) : TripUiState()
    data class  Stopped(val tripId: Long) : TripUiState()
    data class  Error(val message: String) : TripUiState()
}

@HiltViewModel
class LiveTripViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val startTripUseCase: StartTripUseCase,
    private val tripRepository: TripRepository,
    private val prefsRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<TripUiState>(TripUiState.Starting)
    val uiState: StateFlow<TripUiState> = _uiState.asStateFlow()

    val useKmh: StateFlow<Boolean> = prefsRepository.useKmh
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val overspeedEnabled: StateFlow<Boolean> = prefsRepository.overspeedEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val overspeedThreshold: StateFlow<Float> = prefsRepository.overspeedThreshold
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Constants.DEFAULT_OVERSPEED_THRESHOLD_KMH)

    val meterType: StateFlow<String> = prefsRepository.meterType
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "digital")

    private val _routePoints = MutableStateFlow<List<LocationPoint>>(emptyList())
    val routePoints: StateFlow<List<LocationPoint>> = _routePoints.asStateFlow()

    private var currentTripId: Long = -1L
    private var overspeedAlertActive = false
    private var routeJob: Job? = null

    init {
        observeServiceState()
    }

    private fun observeServiceState() {
        viewModelScope.launch {
            LocationForegroundService.liveTripData.collect { serviceState ->
                if (!serviceState.isTracking && currentTripId != -1L) {
                    _uiState.value = TripUiState.Stopped(currentTripId)
                    return@collect
                }
                if (serviceState.isTracking) {
                    if (serviceState.tripId != -1L && serviceState.tripId != currentTripId) {
                        currentTripId = serviceState.tripId
                        observeRoute(serviceState.tripId)
                    }
                    val threshold = overspeedThreshold.value
                    val isOver = overspeedEnabled.value && serviceState.currentSpeedKmh > threshold
                    if (isOver && !overspeedAlertActive) {
                        triggerOverspeedAlert()
                        overspeedAlertActive = true
                    } else if (!isOver) {
                        overspeedAlertActive = false
                    }
                    _uiState.value = TripUiState.Tracking(
                        currentSpeedKmh = serviceState.currentSpeedKmh,
                        avgSpeedKmh     = serviceState.avgSpeedKmh,
                        topSpeedKmh     = serviceState.topSpeedKmh,
                        distanceMeters  = serviceState.distanceMeters,
                        elapsedSeconds  = serviceState.elapsedSeconds,
                        isPaused        = serviceState.isPaused,
                        tripId          = serviceState.tripId,
                        isOverspeed     = isOver
                    )
                }
            }
        }
    }

    fun startTrip() {
        viewModelScope.launch {
            try {
                val tripId = startTripUseCase()
                currentTripId = tripId
                observeRoute(tripId)

                val serviceIntent = Intent(context, LocationForegroundService::class.java).apply {
                    action = Constants.ACTION_START_TRACKING
                    putExtra(Constants.EXTRA_TRIP_ID, tripId)
                }
                context.startForegroundService(serviceIntent)

                _uiState.value = TripUiState.Tracking(
                    currentSpeedKmh = 0f,
                    avgSpeedKmh     = 0f,
                    topSpeedKmh     = 0f,
                    distanceMeters  = 0f,
                    elapsedSeconds  = 0L,
                    isPaused        = false,
                    tripId          = tripId,
                    isOverspeed     = false
                )
            } catch (e: Exception) {
                _uiState.value = TripUiState.Error(e.message ?: "Failed to start trip")
            }
        }
    }

    fun pauseTrip() {
        val intent = Intent(context, LocationForegroundService::class.java).apply {
            action = Constants.ACTION_PAUSE_TRACKING
        }
        context.startService(intent)
    }

    fun stopTrip() {
        val intent = Intent(context, LocationForegroundService::class.java).apply {
            action = Constants.ACTION_STOP_TRACKING
        }
        context.startService(intent)
    }

    private fun observeRoute(tripId: Long) {
        routeJob?.cancel()
        routeJob = viewModelScope.launch {
            tripRepository.observeLocationPointsForTrip(tripId).collect { points ->
                _routePoints.value = points
            }
        }
    }

    private fun triggerOverspeedAlert() {
        viewModelScope.launch {
            try {
                val toneGen = ToneGenerator(AudioManager.STREAM_ALARM, 80)
                toneGen.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 500)
            } catch (_: Exception) {}

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val vm = context.getSystemService(VibratorManager::class.java)
                    vm.defaultVibrator.vibrate(
                        VibrationEffect.createWaveform(longArrayOf(0, 200, 100, 200), -1)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    val vibrator = context.getSystemService(Vibrator::class.java)
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(longArrayOf(0, 200, 100, 200), -1)
                }
            } catch (_: Exception) {}
        }
    }
}
