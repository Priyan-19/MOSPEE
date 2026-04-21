package com.mospee.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mospee.data.repository.UserPreferencesRepository
import com.mospee.domain.model.Trip
import com.mospee.domain.usecase.DeleteTripUseCase
import com.mospee.domain.usecase.GetAllTripsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class HistoryUiState {
    data object Loading : HistoryUiState()
    data class  Success(val trips: List<Trip>) : HistoryUiState()
    data class  Empty(val message: String = "No trips yet") : HistoryUiState()
    data class  Error(val message: String) : HistoryUiState()
}

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getAllTripsUseCase: GetAllTripsUseCase,
    private val deleteTripUseCase: DeleteTripUseCase,
    private val prefsRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<HistoryUiState>(HistoryUiState.Loading)
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    val useKmh: StateFlow<Boolean> = prefsRepository.useKmh
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    init {
        viewModelScope.launch {
            getAllTripsUseCase()
                .catch { e -> _uiState.value = HistoryUiState.Error(e.message ?: "Error") }
                .collect { trips ->
                    _uiState.value = if (trips.isEmpty()) {
                        HistoryUiState.Empty()
                    } else {
                        HistoryUiState.Success(trips)
                    }
                }
        }
    }

    fun deleteTrip(tripId: Long) {
        viewModelScope.launch {
            deleteTripUseCase(tripId)
        }
    }
}
