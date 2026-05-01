package com.mospee.ui.statistics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mospee.data.repository.UserPreferencesRepository
import com.mospee.domain.model.Trip
import com.mospee.domain.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

data class StatisticsUiState(
    val isLoading: Boolean = true,
    val totalTrips: Int = 0,
    val totalDistanceMeters: Double = 0.0,
    val totalDurationSeconds: Long = 0,
    val topSpeedKmh: Float = 0f,
    val speedTrend: List<Float> = emptyList(),
    val trips: List<Trip> = emptyList()
)

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val prefsRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState = _uiState.asStateFlow()

    val useKmh = prefsRepository.useKmh.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val darkMode = prefsRepository.darkMode.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    init {
        loadStatistics()
    }

    fun loadStatistics() {
        viewModelScope.launch {
            tripRepository.getAllTrips().collect { trips ->
                val totalDistance = trips.sumOf { it.distanceMeters.toDouble() }
                val totalDuration = trips.sumOf { it.durationSeconds }
                val topSpeed = trips.maxOfOrNull { it.topSpeedKmh } ?: 0f
                
                // Simplified trend: avg speed of last 7 trips
                val trend = trips.takeLast(7).map { it.avgSpeedKmh }

                _uiState.value = StatisticsUiState(
                    isLoading = false,
                    totalTrips = trips.size,
                    totalDistanceMeters = totalDistance,
                    totalDurationSeconds = totalDuration,
                    topSpeedKmh = topSpeed,
                    speedTrend = trend,
                    trips = trips
                )
            }
        }
    }
}
