package com.mospee.ui.home

import android.annotation.SuppressLint
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mospee.data.repository.UserPreferencesRepository
import com.mospee.domain.model.Trip
import com.mospee.domain.usecase.GetLastTripUseCase
import com.mospee.service.LocationForegroundService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint
import javax.inject.Inject

sealed class HomeUiState {
    data object Idle    : HomeUiState()
    data object Loading : HomeUiState()
    data class  Ready(val lastTrip: Trip?) : HomeUiState()
    data class  Error(val message: String) : HomeUiState()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getLastTripUseCase: GetLastTripUseCase,
    private val prefsRepository: UserPreferencesRepository,
    private val locationManager: LocationManager,
    private val locationClient: com.mospee.location.LocationClient
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _userLocation = MutableStateFlow<GeoPoint?>(null)
    val userLocation = _userLocation.asStateFlow()

    private val _zoomLevel = MutableStateFlow(15.0)
    val zoomLevel = _zoomLevel.asStateFlow()

    private val _isSatelliteMode = MutableStateFlow(false)
    val isSatelliteMode = _isSatelliteMode.asStateFlow()

    private val _mapType = MutableStateFlow("default") // default, satellite, terrain
    val mapType = _mapType.asStateFlow()

    private val _showTraffic = MutableStateFlow(false)
    val showTraffic = _showTraffic.asStateFlow()

    private val _showTransit = MutableStateFlow(false)
    val showTransit = _showTransit.asStateFlow()

    private val _showBicycling = MutableStateFlow(false)
    val showBicycling = _showBicycling.asStateFlow()

    val isTracking: StateFlow<Boolean> = LocationForegroundService.liveTripData
        .map { it.isTracking }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val elapsedSeconds: StateFlow<Long> = LocationForegroundService.liveTripData
        .map { it.elapsedSeconds }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val useKmh: StateFlow<Boolean> = prefsRepository.useKmh
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val meterType: StateFlow<String> = prefsRepository.meterType
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "digital")

    val overspeedEnabled: StateFlow<Boolean> = prefsRepository.overspeedEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val darkMode: StateFlow<Boolean> = prefsRepository.darkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    init {
        loadLastTrip()
        startLocationUpdates()
        // Automatically refresh last trip when tracking stops
        viewModelScope.launch {
            LocationForegroundService.liveTripData.collect { state ->
                if (!state.isTracking) {
                    loadLastTrip()
                }
            }
        }
    }
 
    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {
        locationClient
            .getLocationUpdates(5000L) // 5 seconds for Home screen is enough
            .onEach { location ->
                _userLocation.value = GeoPoint(location.latitude, location.longitude)
            }
            .catch { e -> e.printStackTrace() }
            .launchIn(viewModelScope)
    }

    fun loadLastTrip() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val trip = getLastTripUseCase()
                _uiState.value = HomeUiState.Ready(trip)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun refreshLocation() {
        startLocationUpdates()
    }

    fun isGpsEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun zoomIn() {
        _zoomLevel.value = (_zoomLevel.value + 1.0).coerceAtMost(20.0)
    }

    fun zoomOut() {
        _zoomLevel.value = (_zoomLevel.value - 1.0).coerceAtLeast(3.0)
    }

    fun toggleSatelliteMode() {
        _isSatelliteMode.value = !_isSatelliteMode.value
        _mapType.value = if (_isSatelliteMode.value) "satellite" else "default"
    }

    fun setMapType(type: String) {
        _mapType.value = type
        _isSatelliteMode.value = (type == "satellite")
    }

    fun toggleTraffic() { _showTraffic.value = !_showTraffic.value }
    fun toggleTransit() { _showTransit.value = !_showTransit.value }
    fun toggleBicycling() { _showBicycling.value = !_showBicycling.value }

    fun toggleMeterType() = viewModelScope.launch {
        val nextType = if (meterType.value == "digital") "analog" else "digital"
        prefsRepository.setMeterType(nextType)
    }

    fun setUseKmh(value: Boolean) = viewModelScope.launch {
        prefsRepository.setUseKmh(value)
    }

    fun setOverspeedEnabled(value: Boolean) = viewModelScope.launch {
        prefsRepository.setOverspeedEnabled(value)
    }

    fun setDarkMode(value: Boolean) = viewModelScope.launch {
        prefsRepository.setDarkMode(value)
    }
}
