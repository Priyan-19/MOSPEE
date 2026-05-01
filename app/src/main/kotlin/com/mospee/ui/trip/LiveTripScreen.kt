package com.mospee.ui.trip

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import com.mospee.domain.model.LocationPoint
import com.mospee.ui.components.*
import com.mospee.ui.theme.*
import com.mospee.utils.LocationUtils
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import android.app.Activity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import org.osmdroid.util.GeoPoint

@Composable
fun LiveTripScreen(
    onTripStopped: (Long) -> Unit,
    onBack: () -> Unit,
    onOpenHome: () -> Unit = {},
    onOpenHistory: () -> Unit = {},
    onOpenStats: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    viewModel: LiveTripViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val routePoints by viewModel.routePoints.collectAsState()
    val useKmh by viewModel.useKmh.collectAsState()
    val overspeedEnabled by viewModel.overspeedEnabled.collectAsState()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsState()
    val soundEnabled by viewModel.soundEnabled.collectAsState()
    val autoPauseEnabled by viewModel.autoPauseEnabled.collectAsState()
    val darkMode by viewModel.darkMode.collectAsState()

    val context = LocalContext.current

    val gpsSettingLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.retryGpsCheck()
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is TripUiState.Stopped) {
            onTripStopped((uiState as TripUiState.Stopped).tripId)
        } else if (uiState is TripUiState.GpsDisabled) {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
            val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
            val client = LocationServices.getSettingsClient(context)
            val task = client.checkLocationSettings(builder.build())

            task.addOnFailureListener { exception ->
                if (exception is ResolvableApiException) {
                    try {
                        val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                        gpsSettingLauncher.launch(intentSenderRequest)
                    } catch (sendEx: Exception) {
                        // Ignore the error.
                    }
                }
            }
        }
    }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            if (uiState !is TripUiState.Tracking) {
                com.mospee.ui.home.SpeedoBottomNav(
                    currentRoute = "live",
                    onHomeClick = onOpenHome,
                    onLiveClick = { },
                    onHistoryClick = onOpenHistory,
                    onStatsClick = onOpenStats,
                    onSettingsClick = onOpenSettings
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Global Background for the screen
            if (darkMode) {
                Image(
                    painter = painterResource(id = com.mospee.R.drawable.mospee_bg),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)))
            } else {
                Image(
                    painter = painterResource(id = com.mospee.R.drawable.mospee_light_bg),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.4f)))
            }

            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is TripUiState.Starting           -> ReadyContent(
                    state = state, 
                    onStart = { viewModel.startTrip() },
                    onOpenHome = onOpenHome,
                    onOpenHistory = onOpenHistory,
                    onOpenStats = onOpenStats,
                    onOpenSettings = onOpenSettings
                )
                is TripUiState.Tracking           -> TrackingContent(
                    state = state,
                    routePoints = routePoints,
                    useKmh = useKmh,
                    overspeedEnabled = overspeedEnabled,
                    vibrationEnabled = vibrationEnabled,
                    soundEnabled = soundEnabled,
                    autoPauseEnabled = autoPauseEnabled,
                    onPause = { viewModel.pauseTrip() },
                    onResume = { viewModel.resumeTrip() },
                    onStop = { viewModel.stopTrip() },
                    onToggleOverspeed = { viewModel.toggleOverspeed(!overspeedEnabled) },
                    onToggleVibration = { viewModel.toggleVibration(!vibrationEnabled) },
                    onToggleSound = { viewModel.toggleSound(!soundEnabled) },
                    onToggleAutoPause = { viewModel.toggleAutoPause(!autoPauseEnabled) },
                    onBack = onBack
                )
                is TripUiState.Error              -> SimpleStateContent(Icons.Rounded.Error, StError, "Tracking Failed", state.message, "Dismiss", onBack)
                is TripUiState.PermissionsRequired -> SimpleStateContent(Icons.Rounded.Security, StSecondary, "Permissions Required", "GPS access is needed.", "Grant Access", onBack)
                is TripUiState.GpsDisabled        -> SimpleStateContent(
                    icon = Icons.Rounded.GpsOff, 
                    color = StWarning, 
                    title = "GPS Disabled", 
                    body = "Please enable location services for live tracking.", 
                    btnLabel = "Retry", 
                    onAction = { viewModel.retryGpsCheck() }
                )
                else -> Unit
            }
        }
    }
}
}

@Composable
private fun ReadyContent(
    state: TripUiState.Starting, 
    onStart: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenStats: () -> Unit,
    onOpenSettings: () -> Unit
) {
    if (state.initialLat != null && state.initialLng != null) {
        val geoPoint = GeoPoint(state.initialLat, state.initialLng)
        Column(modifier = Modifier.fillMaxSize()) {
            // ── Header ───────────────────────────────────────────────────────
            Surface(color = Color.Transparent, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.statusBarsPadding().padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("LIVE", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                        Text("Live Tracking", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.GpsFixed, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    }
                }
            }
            
            // Map Card with rounded corners
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(32.dp))
                    .border(BorderStroke(2.dp, MaterialTheme.colorScheme.outline), RoundedCornerShape(32.dp))
            ) {
                OpenStreetMapView(
                    modifier = Modifier.fillMaxSize(),
                    center = geoPoint,
                    userLocation = geoPoint,
                    routePoints = emptyList(),
                    zoom = 18.0,
                    followCenter = false,
                    enableGestures = true
                )
                
                // Start Trip Button inside the map
                Button(
                    onClick = onStart,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 24.dp)
                        .fillMaxWidth(0.8f)
                        .height(64.dp),
                    shape = RoundedCornerShape(32.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = StPrimary),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp)
                ) {
                    Text("START TRIP", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = Color.White, letterSpacing = 2.sp)
                }
            }
            

        }
    } else {
        Column(
            modifier = Modifier.fillMaxSize().background(Color.Transparent),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(72.dp), color = StPrimary, strokeWidth = 3.dp)
                Icon(Icons.Rounded.GpsFixed, null, tint = StPrimary, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text("Acquiring Signal", style = MaterialTheme.typography.headlineSmall, color = Color.White, fontWeight = FontWeight.Bold)
            Text("Establishing precision GPS lock…", color = Color.White.copy(alpha = 0.6f), style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun TrackingContent(
    state: TripUiState.Tracking,
    routePoints: List<LocationPoint>,
    useKmh: Boolean,
    overspeedEnabled: Boolean,
    vibrationEnabled: Boolean,
    soundEnabled: Boolean,
    autoPauseEnabled: Boolean,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit,
    onToggleOverspeed: () -> Unit,
    onToggleVibration: () -> Unit,
    onToggleSound: () -> Unit,
    onToggleAutoPause: () -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        // ── TOP SPEEDOMETER CARD ───────────────────────────────────────────
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            shape = RoundedCornerShape(20.dp),
            color = if (LocalDarkMode.current) Color(0xFF131821) else Color.White.copy(alpha = 0.9f),
            border = BorderStroke(1.dp, if (LocalDarkMode.current) Color(0xFF1E2632) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Column(
                modifier = Modifier.padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val speed = if (useKmh) state.currentSpeedKmh else LocationUtils.kmhToMph(state.currentSpeedKmh)
                val top   = if (useKmh) state.topSpeedKmh    else LocationUtils.kmhToMph(state.topSpeedKmh)
                val unit  = if (useKmh) "KM/H" else "MPH"
                
                CircularSpeedometer(
                    speed = speed,
                    maxSpeed = if (useKmh) 160f else 100f,
                    isOverspeed = state.isOverspeed,
                    unit = unit,
                    modifier = Modifier.size(260.dp)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("MAX SPEED", style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.4f), letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("${top.toInt()} ${unit.lowercase()}", style = MaterialTheme.typography.labelLarge, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        // ── STATS ROW ──────────────────────────────────────────────────────
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            shape = RoundedCornerShape(16.dp),
            color = if (LocalDarkMode.current) Color(0xFF131821) else Color.White.copy(alpha = 0.9f),
            border = BorderStroke(1.dp, if (LocalDarkMode.current) Color(0xFF1E2632) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LiveStatItem("DISTANCE", LocationUtils.formatDistance(state.distanceMeters, useKmh))
                Divider(modifier = Modifier.height(30.dp).width(1.dp), color = if (LocalDarkMode.current) Color(0xFF1E2632) else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                LiveStatItem("DURATION", LocationUtils.formatDuration(state.elapsedSeconds))
                Divider(modifier = Modifier.height(30.dp).width(1.dp), color = if (LocalDarkMode.current) Color(0xFF1E2632) else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f))
                LiveStatItem("AVG SPEED", LocationUtils.formatSpeed(state.avgSpeedKmh, useKmh))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── CONTROL BUTTONS ────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = if (state.isPaused) onResume else onPause,
                modifier = Modifier.weight(1f).height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (LocalDarkMode.current) Color(0xFF131821) else Color.White.copy(alpha = 0.9f),
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                border = BorderStroke(1.dp, if (LocalDarkMode.current) Color(0xFF1E2632) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Icon(if (state.isPaused) Icons.Rounded.PlayArrow else Icons.Rounded.Pause, null, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(if (state.isPaused) "Resume" else "Pause", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            
            Button(
                onClick = onStop,
                modifier = Modifier.weight(1f).height(60.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF44336)
                )
            ) {
                Icon(Icons.Rounded.Stop, null, tint = Color.White, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Stop Trip", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── SETTINGS TOGGLES ───────────────────────────────────────────────
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            shape = RoundedCornerShape(16.dp),
            color = if (LocalDarkMode.current) Color(0xFF131821) else Color.White.copy(alpha = 0.9f),
            border = BorderStroke(1.dp, if (LocalDarkMode.current) Color(0xFF1E2632) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                AlertIcon(Icons.Rounded.Speed, "Overspeed", overspeedEnabled, Color(0xFFF44336), onToggleOverspeed)
                AlertIcon(Icons.Rounded.Vibration, "Vibration", vibrationEnabled, Color(0xFF2196F3), onToggleVibration)
                AlertIcon(Icons.Rounded.VolumeUp, "Sound", soundEnabled, Color(0xFFFFC107), onToggleSound)
                AlertIcon(Icons.Rounded.PauseCircle, "Auto Pause", autoPauseEnabled, Color(0xFF4CAF50), onToggleAutoPause)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ── LIVE MAP ───────────────────────────────────────────────────────
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 12.dp)
                .padding(bottom = 12.dp),
            shape = RoundedCornerShape(24.dp),
            color = if (LocalDarkMode.current) Color(0xFF131821) else Color.White.copy(alpha = 0.9f),
            border = BorderStroke(1.dp, if (LocalDarkMode.current) Color(0xFF1E2632) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
        ) {
            val geoPoints = routePoints.map { GeoPoint(it.latitude, it.longitude) }
            if (geoPoints.isNotEmpty()) {
                OpenStreetMapView(
                    modifier = Modifier.fillMaxSize(),
                    center = geoPoints.last(),
                    userLocation = geoPoints.last(),
                    routePoints = geoPoints,
                    zoom = 17.0,
                    followCenter = true,
                    enableGestures = true
                )
            } else {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF4CAF50))
                }
            }
        }
    }
}

@Composable
private fun LiveStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val onSurface = MaterialTheme.colorScheme.onSurface
        Text(label, style = MaterialTheme.typography.labelSmall, color = onSurface.copy(alpha = 0.4f), letterSpacing = 0.8.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.titleMedium, color = onSurface, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun AlertIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    active: Boolean,
    color: Color,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(if (active) color.copy(0.2f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), CircleShape)
                .border(1.dp, if (active) color.copy(0.4f) else Color.Transparent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, label, tint = if (active) color else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f), modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            label, 
            style = MaterialTheme.typography.labelSmall, 
            color = if (active) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            fontSize = 10.sp
        )
    }
}

@Composable
private fun SimpleStateContent(icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, title: String, body: String, btnLabel: String, onAction: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Icon(icon, null, tint = color, modifier = Modifier.size(56.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(title, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(body, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(24.dp))
            GlowButton(onClick = onAction) { Text(btnLabel, fontWeight = FontWeight.SemiBold, color = Color.White) }
        }
    }
}
