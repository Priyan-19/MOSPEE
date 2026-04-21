package com.mospee.ui.summary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mospee.data.repository.UserPreferencesRepository
import com.mospee.domain.model.LocationPoint
import com.mospee.domain.model.Trip
import com.mospee.domain.usecase.DeleteTripUseCase
import com.mospee.domain.usecase.GetTripDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SummaryUiState {
    data object Loading : SummaryUiState()
    data class  Success(val trip: Trip, val points: List<LocationPoint>) : SummaryUiState()
    data class  Error(val message: String) : SummaryUiState()
}

@HiltViewModel
class TripSummaryViewModel @Inject constructor(
    private val getTripDetailsUseCase: GetTripDetailsUseCase,
    private val deleteTripUseCase: DeleteTripUseCase,
    private val prefsRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SummaryUiState>(SummaryUiState.Loading)
    val uiState: StateFlow<SummaryUiState> = _uiState.asStateFlow()

    val useKmh: StateFlow<Boolean> = prefsRepository.useKmh
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun loadTrip(tripId: Long) {
        viewModelScope.launch {
            _uiState.value = SummaryUiState.Loading
            try {
                val (trip, points) = getTripDetailsUseCase(tripId)
                if (trip != null) {
                    _uiState.value = SummaryUiState.Success(trip, points)
                } else {
                    _uiState.value = SummaryUiState.Error("Trip not found")
                }
            } catch (e: Exception) {
                _uiState.value = SummaryUiState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun deleteTrip(tripId: Long, onDeleted: () -> Unit) {
        viewModelScope.launch {
            deleteTripUseCase(tripId)
            onDeleted()
        }
    }
}
