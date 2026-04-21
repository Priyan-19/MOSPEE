package com.mospee.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.mospee.utils.Constants
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = Constants.PREFS_NAME
)

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    private object Keys {
        val USE_KMH             = booleanPreferencesKey(Constants.PREF_UNIT_KMH)
        val DARK_MODE           = booleanPreferencesKey(Constants.PREF_DARK_MODE)
        val OVERSPEED_ENABLED   = booleanPreferencesKey(Constants.PREF_OVERSPEED_ENABLED)
        val OVERSPEED_THRESHOLD = floatPreferencesKey(Constants.PREF_OVERSPEED_THRESHOLD)
    }

    val useKmh: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.USE_KMH] ?: true }

    val darkMode: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.DARK_MODE] ?: true }

    val overspeedEnabled: Flow<Boolean> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.OVERSPEED_ENABLED] ?: false }

    val overspeedThreshold: Flow<Float> = dataStore.data
        .catch { if (it is IOException) emit(emptyPreferences()) else throw it }
        .map { it[Keys.OVERSPEED_THRESHOLD] ?: Constants.DEFAULT_OVERSPEED_THRESHOLD_KMH }

    suspend fun setUseKmh(value: Boolean) {
        dataStore.edit { it[Keys.USE_KMH] = value }
    }

    suspend fun setDarkMode(value: Boolean) {
        dataStore.edit { it[Keys.DARK_MODE] = value }
    }

    suspend fun setOverspeedEnabled(value: Boolean) {
        dataStore.edit { it[Keys.OVERSPEED_ENABLED] = value }
    }

    suspend fun setOverspeedThreshold(value: Float) {
        dataStore.edit { it[Keys.OVERSPEED_THRESHOLD] = value }
    }
}
