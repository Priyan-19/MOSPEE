package com.mospee.ui.trip

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mospee.domain.model.LocationPoint
import com.mospee.ui.components.*
import com.mospee.ui.theme.*
import com.mospee.utils.LocationUtils
import org.osmdroid.util.GeoPoint

@Composable
fun LiveTripScreen(
    onTripStopped: (Long) -> Unit,
    onBack: () -> Unit,
    viewModel: LiveTripViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val useKmh by viewModel.useKmh.collectAsStateWithLifecycle()
    val overspeedThreshold by viewModel.overspeedThreshold.collectAsStateWithLifecycle()
    val meterType by viewModel.meterType.collectAsStateWithLifecycle()
    val routePoints by viewModel.routePoints.collectAsStateWithLifecycle()

    var showStopDialog by remember { mutableStateOf(false) }
    var isHudMode by remember { mutableStateOf(false) }
    var countdown by remember { mutableIntStateOf(3) }
    var warmupComplete by remember { mutableStateOf(false) }

    LaunchedEffect(uiState) {
        if (uiState is TripUiState.Starting && !warmupComplete) {
            countdown = 3
            repeat(3) {
                kotlinx.coroutines.delay(1000)
                countdown -= 1
            }
            warmupComplete = true
            viewModel.startTrip()
        }
        if (uiState !is TripUiState.Starting) {
            warmupComplete = false
        }
    }

    LaunchedEffect(uiState) {
        if (uiState is TripUiState.Stopped) {
            onTripStopped((uiState as TripUiState.Stopped).tripId)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MospeeCream)
            .graphicsLayer { if (isHudMode) scaleX = -1f }
    ) {
        when (val state = uiState) {
            is TripUiState.Tracking -> {
                TrackingContent(
                    state = state,
                    routePoints = routePoints,
                    useKmh = useKmh,
                    overspeedThreshold = overspeedThreshold,
                    meterType = meterType,
                    isHudMode = isHudMode,
                    onBack = onBack,
                    onToggleHud = { isHudMode = !isHudMode },
                    onPause = { viewModel.pauseTrip() },
                    onStop = { showStopDialog = true }
                )
            }
            is TripUiState.Starting -> WarmupContent(countdown = countdown)
            is TripUiState.Error -> ErrorContent(message = state.message, onRetry = { viewModel.startTrip() })
            else -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MospeeTerracotta)
            }
        }
    }

    if (showStopDialog) {
        AlertDialog(
            onDismissRequest = { showStopDialog = false },
            title = { Text("Stop trip?") },
            text = { Text("Your trip data will be saved to history.") },
            confirmButton = {
                TextButton(onClick = {
                    showStopDialog = false
                    viewModel.stopTrip()
                }) {
                    Text("Stop", color = MospeeRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showStopDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = Color.White,
            titleContentColor = Color.Black,
            textContentColor = Color.Black.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun WarmupContent(countdown: Int) {
    Box(modifier = Modifier.fillMaxSize()) {
        OpenStreetMapView(modifier = Modifier.fillMaxSize(), center = GeoPoint(12.9716, 77.5946), zoom = 14.0)
        Box(modifier = Modifier.fillMaxSize().background(MospeeCream.copy(alpha = 0.7f)))
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = if (countdown > 0) countdown.toString() else "GO!", style = MaterialTheme.typography.displayLarge, fontWeight = FontWeight.Black, color = MospeeTerracotta)
            Text(text = "Preparing GPS Tracking", style = MaterialTheme.typography.titleMedium, color = Color.Black.copy(alpha = 0.5f))
        }
    }
}

@Composable
private fun TrackingContent(
    state: TripUiState.Tracking,
    routePoints: List<LocationPoint>,
    useKmh: Boolean,
    overspeedThreshold: Float,
    meterType: String,
    isHudMode: Boolean,
    onBack: () -> Unit,
    onToggleHud: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit
) {
    val speedValue = if (useKmh) state.currentSpeedKmh else LocationUtils.kmhToMph(state.currentSpeedKmh)
    val unit = if (useKmh) "km/h" else "mph"

    val flashAlpha = remember { Animatable(0f) }
    LaunchedEffect(state.isOverspeed) {
        if (state.isOverspeed) {
            flashAlpha.animateTo(0.2f, tween(100))
            flashAlpha.animateTo(0f, tween(200))
        }
    }

    val mappedRoutePoints = remember(routePoints) { routePoints.map { it.toGeoPoint() } }
    val lastPoint = remember(mappedRoutePoints) { mappedRoutePoints.lastOrNull() ?: GeoPoint(12.9716, 77.5946) }

    Box(modifier = Modifier.fillMaxSize()) {
        OpenStreetMapView(
            modifier = Modifier.fillMaxSize(),
            center = lastPoint,
            zoom = 16.0,
            routePoints = mappedRoutePoints,
            showRouteMarkers = true,
            followCenter = true
        )
        
        Box(modifier = Modifier.fillMaxSize().background(MospeeRed.copy(alpha = flashAlpha.value)))

        Column(modifier = Modifier.fillMaxSize()) {
            // Top UI
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp).statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.9f),
                    shadowElevation = 4.dp
                ) {
                    IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Color.Gray) }
                }

                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = Color.White.copy(alpha = 0.9f),
                    shadowElevation = 4.dp
                ) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (state.isPaused) Color.Gray else MospeeGreen))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = LocationUtils.formatDuration(state.elapsedSeconds), fontWeight = FontWeight.Bold)
                    }
                }

                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = CircleShape,
                    color = if (isHudMode) MospeeTerracotta else Color.White.copy(alpha = 0.9f),
                    shadowElevation = 4.dp
                ) {
                    IconButton(onClick = onToggleHud) { Icon(Icons.Rounded.CenterFocusStrong, contentDescription = "HUD", tint = if (isHudMode) Color.White else Color.Gray) }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Bottom Panel
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = Color.White,
                shadowElevation = 16.dp
            ) {
                Column(modifier = Modifier.padding(24.dp).navigationBarsPadding()) {
                    if (meterType == "analog") {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Speedometer3D(
                                speed = speedValue,
                                unit = unit,
                                modifier = Modifier.size(180.dp)
                            )
                            
                            // Speed Limit Overlay for Analog
                            Surface(
                                modifier = Modifier.align(Alignment.TopEnd),
                                shape = CircleShape,
                                color = MospeeTerracottaLight,
                                border = BorderStroke(1.dp, MospeeTerracotta.copy(alpha = 0.2f))
                            ) {
                                Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "LIMIT", style = MaterialTheme.typography.labelSmall, color = MospeeTerracotta, fontSize = 8.sp)
                                    Text(text = overspeedThreshold.toInt().toString(), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MospeeTerracotta)
                                }
                            }
                        }
                    } else {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(text = "CURRENT SPEED", style = MaterialTheme.typography.labelSmall, color = Color.Black.copy(alpha = 0.4f))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(text = "%.0f".format(speedValue), style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Black, color = if (state.isOverspeed) MospeeRed else Color.Black)
                                    Text(text = unit, modifier = Modifier.padding(bottom = 12.dp, start = 4.dp), style = MaterialTheme.typography.titleMedium, color = Color.Black.copy(alpha = 0.4f))
                                }
                            }
                            
                            Box(
                                modifier = Modifier.size(80.dp).clip(CircleShape).background(MospeeTerracottaLight),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "LIMIT", style = MaterialTheme.typography.labelSmall, color = MospeeTerracotta)
                                    Text(text = overspeedThreshold.toInt().toString(), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MospeeTerracotta)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        LiveMetric(label = "Distance", value = LocationUtils.formatDistance(state.distanceMeters, useKmh), modifier = Modifier.weight(1f))
                        LiveMetric(label = "Avg Speed", value = "${state.avgSpeedKmh.toInt()} $unit", modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(
                            onClick = onPause,
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if (state.isPaused) MospeeGreen else MospeeTerracottaLight, contentColor = if (state.isPaused) Color.White else MospeeTerracotta)
                        ) {
                            Icon(if (state.isPaused) Icons.Rounded.PlayArrow else Icons.Rounded.Pause, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (state.isPaused) "Resume" else "Pause")
                        }
                        
                        Button(
                            onClick = onStop,
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MospeeRed)
                        ) {
                            Icon(Icons.Rounded.Stop, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Stop")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LiveMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = label.uppercase(), style = MaterialTheme.typography.labelSmall, color = Color.Black.copy(alpha = 0.4f))
        Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Icon(Icons.Rounded.Warning, contentDescription = null, tint = MospeeRed, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Tracking Error", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(text = message, textAlign = TextAlign.Center, color = Color.Gray)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRetry, colors = ButtonDefaults.buttonColors(containerColor = MospeeTerracotta)) {
                Text("Retry Tracking")
            }
        }
    }
}

private fun LocationPoint.toGeoPoint(): GeoPoint = GeoPoint(latitude, longitude)
