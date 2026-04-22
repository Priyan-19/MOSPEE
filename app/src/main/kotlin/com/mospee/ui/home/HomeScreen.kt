package com.mospee.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.border
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mospee.domain.model.Trip
import com.mospee.ui.components.*
import com.mospee.ui.theme.*
import com.mospee.utils.LocationUtils
import org.osmdroid.util.GeoPoint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    onStartTrip: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenTripDetails: (Long) -> Unit,
    onOpenSettings: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val userLocation by viewModel.userLocation.collectAsStateWithLifecycle()
    val zoomLevel by viewModel.zoomLevel.collectAsStateWithLifecycle()
    val mapType by viewModel.mapType.collectAsStateWithLifecycle()
    val showTraffic by viewModel.showTraffic.collectAsStateWithLifecycle()
    val showTransit by viewModel.showTransit.collectAsStateWithLifecycle()
    val showBicycling by viewModel.showBicycling.collectAsStateWithLifecycle()
    val meterType by viewModel.meterType.collectAsStateWithLifecycle()
    val useKmh by viewModel.useKmh.collectAsStateWithLifecycle()
    val isTracking by viewModel.isTracking.collectAsStateWithLifecycle() // Kept for START/RESUME button text

    var showMapTypeSheet by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            HomeBottomNav(
                currentRoute = "home",
                onHistoryClick = onOpenHistory,
                onSettingsClick = onOpenSettings
            )
        },
        containerColor = MospeeCream
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Map Section (Top 50%)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                OpenStreetMapView(
                    modifier = Modifier.fillMaxSize(),
                    center = userLocation ?: GeoPoint(12.9716, 77.5946),
                    userLocation = userLocation,
                    zoom = zoomLevel,
                    mapType = mapType,
                    followCenter = true,
                    enableGestures = true
                )

                // Map UI Elements (All in one column to prevent overlap)
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MapActionButton(Icons.Rounded.MyLocation, onClick = { viewModel.refreshLocation() })
                    MapActionButton(
                        icon = Icons.Rounded.Layers,
                        onClick = { showMapTypeSheet = true }
                    )
                    MapActionButton(
                        icon = if (meterType == "digital") Icons.Rounded.Speed else Icons.Rounded.AvTimer,
                        onClick = { viewModel.toggleMeterType() }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    MapActionButton(Icons.Rounded.Add, size = 42.dp, onClick = { viewModel.zoomIn() })
                    MapActionButton(Icons.Rounded.Remove, size = 42.dp, onClick = { viewModel.zoomOut() })
                }

                // GPS Disabled Warning
                if (!viewModel.isGpsEnabled()) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = MospeeRed.copy(alpha = 0.9f),
                        tonalElevation = 4.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.LocationOff, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("GPS is turned off", style = MaterialTheme.typography.labelMedium, color = Color.White)
                        }
                    }
                }

                ActiveTripOverlaySection(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 24.dp))
            }


            // Controls Section (Bottom 50%)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.2f),
                color = MospeeCream,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SpeedometerSection(meterType = meterType, useKmh = useKmh)

                    Text(
                        text = "Ready for your next journey?",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Black.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    GlowButton(
                        onClick = onStartTrip,
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = if (isTracking) "RESUME TRIP" else "START TRIP",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "LAST TRIP SUMMARY",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.Black.copy(alpha = 0.5f),
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        when (val state = uiState) {
                            is HomeUiState.Ready -> LastTripCard(
                                state.lastTrip,
                                onClick = { 
                                    state.lastTrip?.id?.let { onOpenTripDetails(it) } ?: onOpenHistory() 
                                }
                            )
                            is HomeUiState.Loading -> CircularProgressIndicator(color = MospeeTerracotta)
                            else -> LastTripCard(null, onOpenHistory)
                        }
                    }
                }
            }
        }

        if (showMapTypeSheet) {
            MapTypeBottomSheet(
                onDismiss = { showMapTypeSheet = false },
                currentMapType = mapType,
                onSetMapType = { viewModel.setMapType(it) },
                showTraffic = showTraffic,
                onToggleTraffic = { viewModel.toggleTraffic() },
                showTransit = showTransit,
                onToggleTransit = { viewModel.toggleTransit() },
                showBicycling = showBicycling,
                onToggleBicycling = { viewModel.toggleBicycling() }
            )
        }
    }
}

@Composable
private fun MapActionButton(
    icon: ImageVector, 
    size: androidx.compose.ui.unit.Dp = 44.dp,
    onClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.size(size),
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.9f),
        shadowElevation = 4.dp
    ) {
        IconButton(onClick = onClick) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.Gray)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MapTypeBottomSheet(
    onDismiss: () -> Unit,
    currentMapType: String,
    onSetMapType: (String) -> Unit,
    showTraffic: Boolean,
    onToggleTraffic: () -> Unit,
    showTransit: Boolean,
    onToggleTransit: () -> Unit,
    showBicycling: Boolean,
    onToggleBicycling: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color.LightGray) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 40.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Map type",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Rounded.Close, contentDescription = "Close", modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MapTypeOption(
                    label = "Default",
                    isSelected = currentMapType == "default",
                    onClick = { onSetMapType("default") },
                    iconRes = Icons.Rounded.Map
                )
                MapTypeOption(
                    label = "Satellite",
                    isSelected = currentMapType == "satellite",
                    onClick = { onSetMapType("satellite") },
                    iconRes = Icons.Rounded.Satellite
                )
                MapTypeOption(
                    label = "Terrain",
                    isSelected = currentMapType == "terrain",
                    onClick = { onSetMapType("terrain") },
                    iconRes = Icons.Rounded.Terrain
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Map details",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                MapDetailOption(
                    label = "Public transport", 
                    icon = Icons.Rounded.DirectionsBus,
                    isSelected = showTransit,
                    onClick = onToggleTransit
                )
                MapDetailOption(
                    label = "Traffic", 
                    icon = Icons.Rounded.Traffic,
                    isSelected = showTraffic,
                    onClick = onToggleTraffic
                )
                MapDetailOption(
                    label = "Bicycling", 
                    icon = Icons.Rounded.DirectionsBike,
                    isSelected = showBicycling,
                    onClick = onToggleBicycling
                )
                MapDetailOption(
                    label = "3D", 
                    icon = Icons.Rounded.ViewInAr,
                    isSelected = false,
                    isEnabled = false
                )
            }
        }
    }
}

@Composable
private fun MapTypeOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    iconRes: ImageVector,
    isEnabled: Boolean = true
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .alpha(if (isEnabled) 1f else 0.4f)
            .clickable(enabled = isEnabled) { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) MospeeTerracottaLight else Color(0xFFF5F5F5))
                .border(
                    width = 2.dp,
                    color = if (isSelected) MospeeTerracotta else Color.Transparent,
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconRes,
                contentDescription = label,
                tint = if (isSelected) MospeeTerracotta else Color.Gray,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) MospeeTerracotta else Color.Black.copy(alpha = 0.6f),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun MapDetailOption(
    label: String, 
    icon: ImageVector,
    isSelected: Boolean = false,
    isEnabled: Boolean = true,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .alpha(if (isEnabled) 1f else 0.4f)
            .clickable(enabled = isEnabled) { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isSelected) MospeeTerracottaLight else Color(0xFFF5F5F5))
                .border(
                    width = 1.5.dp,
                    color = if (isSelected) MospeeTerracotta else Color.Transparent,
                    shape = RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon, 
                contentDescription = label, 
                tint = if (isSelected) MospeeTerracotta else Color.Gray, 
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) MospeeTerracotta else Color.Black.copy(alpha = 0.6f),
            textAlign = TextAlign.Center,
            modifier = Modifier.width(60.dp)
        )
    }
}

@Composable
private fun LastTripCard(trip: Trip?, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        color = MospeeTerracottaLight
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (trip != null) {
                        SimpleDateFormat("EEEE, h:mm a", Locale.getDefault()).format(Date(trip.startTime))
                    } else "No recent trips",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = Color.Black.copy(alpha = 0.3f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                TripMetricItem(
                    icon = Icons.Rounded.LocationOn,
                    label = "Distance:",
                    value = trip?.let { LocationUtils.formatDistance(it.distanceMeters, true) } ?: "0 km"
                )
                TripMetricItem(
                    icon = Icons.Rounded.Speed,
                    label = "Avg Speed:",
                    value = trip?.let { "%.0f km/h".format(it.avgSpeedKmh) } ?: "0 km/h"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "tap for details",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Black.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun TripMetricItem(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MospeeTerracotta,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Black.copy(alpha = 0.4f)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun HomeBottomNav(
    currentRoute: String,
    onHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(Icons.Rounded.Home, "Home", currentRoute == "home", {})
            BottomNavItem(Icons.Rounded.History, "History", currentRoute == "history", onHistoryClick)
            BottomNavItem(Icons.Rounded.Settings, "Settings", currentRoute == "settings", onSettingsClick)
        }
    }
}

@Composable
private fun BottomNavItem(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(width = 56.dp, height = 32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(if (selected) MospeeTerracottaLight else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) MospeeTerracotta else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) MospeeTerracotta else Color.Gray,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun SpeedometerSection(meterType: String, useKmh: Boolean) {
    val liveTripData by com.mospee.service.LocationForegroundService.liveTripData.collectAsStateWithLifecycle()
    val currentSpeed = liveTripData.currentSpeedKmh
    val displaySpeed = if (useKmh) currentSpeed else LocationUtils.kmhToMph(currentSpeed)
    val unit = if (useKmh) "km/h" else "mph"

    if (meterType == "analog") {
        Speedometer3D(
            speed = displaySpeed,
            unit = unit,
            modifier = Modifier
                .size(210.dp)
                .padding(bottom = 12.dp)
        )
    } else {
        DigitalSpeedometer(
            speed = displaySpeed,
            unit = unit,
            modifier = Modifier.height(160.dp)
        )
    }
}

@Composable
private fun ActiveTripOverlaySection(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val isTracking by viewModel.isTracking.collectAsStateWithLifecycle()
    val elapsedSeconds by viewModel.elapsedSeconds.collectAsStateWithLifecycle()

    if (isTracking) {
        TripActiveOverlay(
            elapsedTime = LocationUtils.formatDuration(elapsedSeconds),
            modifier = modifier
        )
    }
}
