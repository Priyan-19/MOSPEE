package com.mospee.utils

object Constants {

    // Location update intervals
    const val LOCATION_UPDATE_INTERVAL_MS = 1000L       // 1 second for live feel
    const val LOCATION_FASTEST_INTERVAL_MS = 500L       // 0.5 seconds
    const val LOCATION_MIN_DISPLACEMENT_M = 0f          // Get updates even for small movements

    // Speed filter thresholds
    const val MAX_VALID_SPEED_KMH = 240f                // Filter spikes above 240 km/h
    const val MAX_VALID_SPEED_MPH = 149.1f
    const val MIN_ACCURACY_METERS = 20f                 // Be stricter with accuracy (20m instead of 30m)

    // Distance jump filter: ignore teleports > 300m between two consecutive points
    const val MAX_CONSECUTIVE_DISTANCE_M = 300f

    // Overspeed alert
    const val DEFAULT_OVERSPEED_THRESHOLD_KMH = 100f
    const val DEFAULT_OVERSPEED_THRESHOLD_MPH = 62f

    // Notification
    const val NOTIFICATION_CHANNEL_ID = "mospee_channel"
    const val NOTIFICATION_CHANNEL_NAME = "MOSPEE Tracking"
    const val NOTIFICATION_ID = 1001

    // Service actions
    const val ACTION_START_TRACKING = "com.mospee.START_TRACKING"
    const val ACTION_STOP_TRACKING  = "com.mospee.STOP_TRACKING"
    const val ACTION_PAUSE_TRACKING = "com.mospee.PAUSE_TRACKING"

    // Broadcast extras
    const val EXTRA_TRIP_ID         = "extra_trip_id"
    const val EXTRA_CURRENT_SPEED   = "extra_current_speed"
    const val EXTRA_DISTANCE        = "extra_distance"
    const val EXTRA_ELAPSED_TIME    = "extra_elapsed_time"
    const val EXTRA_AVG_SPEED       = "extra_avg_speed"
    const val EXTRA_TOP_SPEED       = "extra_top_speed"

    // DataStore preferences keys
    const val PREFS_NAME                   = "mospee_prefs"
    const val PREF_UNIT_KMH                = "pref_unit_kmh"
    const val PREF_DARK_MODE               = "pref_dark_mode"
    const val PREF_OVERSPEED_ENABLED       = "pref_overspeed_enabled"
    const val PREF_OVERSPEED_THRESHOLD     = "pref_overspeed_threshold"

    // Conversion
    const val MS_TO_KMH = 3.6f
    const val KMH_TO_MPH = 0.621371f
    const val METERS_TO_KM = 0.001f
    const val METERS_TO_MILES = 0.000621371f
}
