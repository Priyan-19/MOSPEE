package com.mospee.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
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
    onOpenSettings: () -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isTracking by viewModel.isTracking.collectAsStateWithLifecycle()
    val elapsedSeconds by viewModel.elapsedSeconds.collectAsStateWithLifecycle()

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
                    center = GeoPoint(12.9716, 77.5946),
                    zoom = 15.0,
                    enableGestures = true
                )

                // Map UI Elements
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MapActionButton(Icons.Rounded.Layers)
                    MapActionButton(Icons.Rounded.Search)
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MapActionButton(Icons.Rounded.Add, size = 40.dp)
                    MapActionButton(Icons.Rounded.Remove, size = 40.dp)
                }

                if (isTracking) {
                    TripActiveOverlay(
                        elapsedTime = LocationUtils.formatDuration(elapsedSeconds),
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 24.dp)
                    )
                }
            }

            // Controls Section (Bottom 50%)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = MospeeCream,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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
                            is HomeUiState.Ready -> LastTripCard(state.lastTrip, onOpenHistory)
                            is HomeUiState.Loading -> CircularProgressIndicator(color = MospeeTerracotta)
                            else -> LastTripCard(null, onOpenHistory)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MapActionButton(icon: ImageVector, size: androidx.compose.ui.unit.Dp = 44.dp) {
    Surface(
        modifier = Modifier.size(size),
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.9f),
        shadowElevation = 4.dp
    ) {
        IconButton(onClick = {}) {
            Icon(imageVector = icon, contentDescription = null, tint = Color.Gray)
        }
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
