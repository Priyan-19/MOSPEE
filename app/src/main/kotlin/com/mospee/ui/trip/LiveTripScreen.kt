package com.mospee.ui.trip

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mospee.ui.theme.*
import com.mospee.ui.components.*
import com.mospee.utils.LocationUtils
import org.osmdroid.util.GeoPoint
import com.mospee.domain.model.LocationPoint

@Composable
fun LiveTripScreen(
    onTripStopped: (Long) -> Unit,
    onBack: () -> Unit,
    viewModel: LiveTripViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val routePoints by viewModel.routePoints.collectAsState()
    val useKmh by viewModel.useKmh.collectAsState()
    val meterType by viewModel.meterType.collectAsState()

    // Handle navigation when trip is stopped
    LaunchedEffect(uiState) {
        if (uiState is TripUiState.Stopped) {
            onTripStopped((uiState as TripUiState.Stopped).tripId)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MospeeCream
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = uiState) {
                is TripUiState.Starting -> WarmupContent(state.initialLat, state.initialLng)
                is TripUiState.Tracking -> TrackingContent(
                    state = state,
                    routePoints = routePoints,
                    useKmh = useKmh,
                    meterType = meterType,
                    onPause = { viewModel.pauseTrip() },
                    onResume = { viewModel.resumeTrip() },
                    onStop = { viewModel.stopTrip() }
                )
                is TripUiState.Error -> ErrorContent(state.message, onBack)
                is TripUiState.PermissionsRequired -> PermissionsContent(onBack)
                is TripUiState.GpsDisabled -> GpsDisabledContent(onBack)
                else -> Unit
            }
        }
    }
}

@Composable
private fun WarmupContent(initialLat: Double?, initialLng: Double?) {
    val center = remember(initialLat, initialLng) {
        if (initialLat != null && initialLng != null) GeoPoint(initialLat, initialLng)
        else null
    }
    Box(modifier = Modifier.fillMaxSize()) {
        if (center != null) {
            OpenStreetMapView(modifier = Modifier.fillMaxSize(), center = center, zoom = 15.0)
        }
        
        // Semi-transparent overlay to keep map visible but show state
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.2f)))
        
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = Color.White.copy(alpha = 0.95f),
                shape = RoundedCornerShape(24.dp),
                tonalElevation = 8.dp,
                shadowElevation = 12.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    CircularProgressIndicator(
                        color = MospeeTerracotta,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(28.dp)
                    )
                    Text(
                        "Starting Trip...", 
                        color = Color.Black, 
                        fontSize = 20.sp, 
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun TrackingContent(
    state: TripUiState.Tracking,
    routePoints: List<LocationPoint>,
    useKmh: Boolean,
    meterType: String,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onStop: () -> Unit
) {
    val mappedRoutePoints = remember(routePoints) { routePoints.map { GeoPoint(it.latitude, it.longitude) } }
    val currentPosition = remember(state.currentLat, state.currentLng, mappedRoutePoints) {
        if (state.currentLat != null && state.currentLng != null) {
            GeoPoint(state.currentLat, state.currentLng)
        } else {
            mappedRoutePoints.lastOrNull()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Full Screen Map
        if (currentPosition != null) {
            OpenStreetMapView(
                modifier = Modifier.fillMaxSize(),
                center = currentPosition,
                userLocation = currentPosition,
                startPoint = mappedRoutePoints.firstOrNull(),
                zoom = 17.5,
                routePoints = mappedRoutePoints,
                followCenter = true
            )
        }

        // Top Controls: Live Badge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            LiveBadge(modifier = Modifier.align(Alignment.TopStart))
            
            // Speed indicator in small pill when overspeeding
            if (state.isOverspeed) {
                Surface(
                    modifier = Modifier.align(Alignment.TopCenter),
                    shape = RoundedCornerShape(50.dp),
                    color = MospeeRed,
                    tonalElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Warning, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("OVERSPEEDING", color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        }

        // Bottom Stats Panel
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Stats Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White.copy(alpha = 0.96f),
                shape = RoundedCornerShape(32.dp),
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Main Speed Display
                    val displaySpeed = if (useKmh) state.currentSpeedKmh else LocationUtils.kmhToMph(state.currentSpeedKmh)
                    val unit = if (useKmh) "km/h" else "mph"
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(bottom = 24.dp)
                    ) {
                        Text(
                            text = displaySpeed.toInt().toString(),
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 80.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-2).sp
                            ),
                            color = if (state.isOverspeed) MospeeRed else Color.Black.copy(alpha = 0.85f)
                        )
                        Text(
                            text = unit.uppercase(),
                            style = MaterialTheme.typography.labelLarge,
                            color = if (state.isOverspeed) MospeeRed else MospeeTerracotta,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp
                        )
                    }

                    // Secondary Stats Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        StatItem(
                            label = "Average", 
                            value = LocationUtils.formatSpeed(state.avgSpeedKmh, useKmh), 
                            icon = Icons.Rounded.Analytics
                        )
                        StatItem(
                            label = "Distance", 
                            value = LocationUtils.formatDistance(state.distanceMeters, useKmh), 
                            icon = Icons.Rounded.Route
                        )
                        StatItem(
                            label = "Time", 
                            value = LocationUtils.formatDuration(state.elapsedSeconds), 
                            icon = Icons.Rounded.Timer
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GlowButton(
                            onClick = if (state.isPaused) onResume else onPause,
                            modifier = Modifier.weight(1f),
                            containerColor = if (state.isPaused) MospeeGreen else Color(0xFFE9E9E9),
                            contentColor = if (state.isPaused) Color.White else Color.Black,
                            glowColor = if (state.isPaused) MospeeGreen.copy(alpha = 0.3f) else Color.Transparent
                        ) {
                            Icon(if (state.isPaused) Icons.Rounded.PlayArrow else Icons.Rounded.Pause, null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (state.isPaused) "RESUME" else "PAUSE", 
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false
                            )
                        }

                        GlowButton(
                            onClick = onStop,
                            modifier = Modifier.weight(1f),
                            containerColor = MospeeRed,
                            contentColor = Color.White,
                            glowColor = MospeeRed.copy(alpha = 0.4f)
                        ) {
                            Icon(Icons.Rounded.Stop, null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "STOP", 
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon, 
            contentDescription = null, 
            tint = MospeeTerracotta.copy(alpha = 0.7f), 
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value, 
            style = MaterialTheme.typography.titleMedium, 
            fontWeight = FontWeight.Bold, 
            color = Color.Black.copy(alpha = 0.8f)
        )
        Text(
            text = label.uppercase(), 
            style = MaterialTheme.typography.labelSmall, 
            color = Color.Gray,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun ErrorContent(message: String, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(MospeeCream),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Rounded.Error, null, tint = MospeeRed, modifier = Modifier.size(80.dp))
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Something went wrong", 
            style = MaterialTheme.typography.headlineSmall, 
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message, 
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(32.dp))
        GlowButton(onClick = onBack, modifier = Modifier.width(200.dp)) {
            Text("GO BACK")
        }
    }
}

@Composable
private fun PermissionsContent(onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(MospeeCream),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Rounded.LocationOn, null, tint = MospeeTerracotta, modifier = Modifier.size(80.dp))
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Location Required", 
            style = MaterialTheme.typography.headlineSmall, 
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "MOSPEE needs location access to track your trip and calculate speed.", 
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 48.dp),
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(32.dp))
        GlowButton(onClick = onBack, modifier = Modifier.width(200.dp)) {
            Text("GO BACK")
        }
    }
}

@Composable
private fun GpsDisabledContent(onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(MospeeCream),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(Icons.Rounded.LocationOff, null, tint = MospeeRed, modifier = Modifier.size(80.dp))
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "GPS is Disabled", 
            style = MaterialTheme.typography.headlineSmall, 
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Please enable GPS to start tracking your trip.", 
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 48.dp),
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(32.dp))
        GlowButton(onClick = onBack, modifier = Modifier.width(200.dp)) {
            Text("GO BACK")
        }
    }
}
