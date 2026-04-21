package com.mospee.ui.home

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
import kotlinx.coroutines.launch
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
    private val prefsRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val isTracking: StateFlow<Boolean> = LocationForegroundService.liveTripData
        .map { it.isTracking }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val elapsedSeconds: StateFlow<Long> = LocationForegroundService.liveTripData
        .map { it.elapsedSeconds }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val useKmh: StateFlow<Boolean> = prefsRepository.useKmh
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val overspeedEnabled: StateFlow<Boolean> = prefsRepository.overspeedEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val darkMode: StateFlow<Boolean> = prefsRepository.darkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    init {
        loadLastTrip()
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
