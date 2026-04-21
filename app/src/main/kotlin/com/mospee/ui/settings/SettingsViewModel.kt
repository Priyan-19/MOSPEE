package com.mospee.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mospee.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefsRepository: UserPreferencesRepository
) : ViewModel() {

    val useKmh: StateFlow<Boolean> = prefsRepository.useKmh
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val darkMode: StateFlow<Boolean> = prefsRepository.darkMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val overspeedEnabled: StateFlow<Boolean> = prefsRepository.overspeedEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val overspeedThreshold: StateFlow<Float> = prefsRepository.overspeedThreshold
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 100f)

    fun setUseKmh(value: Boolean) = viewModelScope.launch { prefsRepository.setUseKmh(value) }
    fun setDarkMode(value: Boolean) = viewModelScope.launch { prefsRepository.setDarkMode(value) }
    fun setOverspeedEnabled(value: Boolean) = viewModelScope.launch { prefsRepository.setOverspeedEnabled(value) }
    fun setOverspeedThreshold(value: Float) = viewModelScope.launch { prefsRepository.setOverspeedThreshold(value) }
}
