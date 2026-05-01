package com.mospee.ui.history

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mospee.domain.model.Trip
import com.mospee.ui.components.GlassyCard
import com.mospee.ui.components.RouteThumbnail
import com.mospee.ui.home.SpeedoBottomNav
import com.mospee.ui.theme.*
import com.mospee.utils.LocationUtils
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HistoryScreen(
    onTripClick: (Long) -> Unit,
    onBack: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenStats: () -> Unit,
    onOpenLive: () -> Unit,
    viewModel: HistoryViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val useKmh  by viewModel.useKmh.collectAsStateWithLifecycle()
    val darkMode by viewModel.darkMode.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            SpeedoBottomNav(
                currentRoute   = "history",
                onHomeClick    = onOpenHome,
                onLiveClick    = onOpenLive,
                onHistoryClick = { },
                onStatsClick   = onOpenStats,
                onSettingsClick = onOpenSettings
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
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

        Column(modifier = Modifier.fillMaxSize().padding(padding)) {

            // ── Header ───────────────────────────────────────────────────────
            Surface(color = Color.Transparent, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.statusBarsPadding().padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("ACTIVITY", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                        Text("Trip History", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState is HistoryUiState.Loading)
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = StPrimary, strokeWidth = 2.dp)
                        else
                            Icon(Icons.Rounded.History, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // ── Content ──────────────────────────────────────────────────────
            when (val state = uiState) {
                is HistoryUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    CircularProgressIndicator(color = StPrimary)
                }
                is HistoryUiState.Success -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(state.trips, key = { it.id }) { trip ->
                        HistoryTripCard(trip, useKmh, { onTripClick(trip.id) }, { viewModel.deleteTrip(trip.id) })
                    }
                }
                is HistoryUiState.Empty -> EmptyHistoryState()
                is HistoryUiState.Error -> ErrorHistoryState(state.message)
            }
        }
    }
}
}

@Composable
private fun HistoryTripCard(trip: Trip, useKmh: Boolean, onClick: () -> Unit, onDelete: () -> Unit) {
    val dateText = remember(trip.startTime) {
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(trip.startTime))
    }
    val timeText = remember(trip.startTime) {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(trip.startTime))
    }

    GlassyCard(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        borderColor = MaterialTheme.colorScheme.outline
    ) {
        Column {
            // Date + delete row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(32.dp).background(StPrimary.copy(0.12f), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Rounded.DirectionsCar, null, tint = StPrimary, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(dateText, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                        Text(timeText, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!trip.isSynced)
                        Icon(Icons.Rounded.CloudOff, null, tint = StWarning, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Rounded.Delete, null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Mini route sparkline
            RouteThumbnail(modifier = Modifier.fillMaxWidth().height(72.dp))

            Spacer(modifier = Modifier.height(14.dp))

            // Stats row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                HistoryStat("DIST", LocationUtils.formatDistance(trip.distanceMeters, useKmh), StPrimary)
                HistoryStat("TIME", LocationUtils.formatDuration(trip.durationSeconds), MaterialTheme.colorScheme.onSurface)
                HistoryStat("AVG", LocationUtils.formatSpeed(trip.avgSpeedKmh, useKmh), StSecondary)
                HistoryStat("TOP", LocationUtils.formatSpeed(trip.topSpeedKmh, useKmh), StWarning)
            }
        }
    }
}

@Composable
private fun HistoryStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(value, style = MaterialTheme.typography.labelMedium, color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun EmptyHistoryState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Rounded.HistoryToggleOff, null, tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), modifier = Modifier.size(56.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("No Trips Yet", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
            Text("Your tracked sessions will appear here.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
        }
    }
}

@Composable
private fun ErrorHistoryState(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Rounded.Error, null, tint = StError, modifier = Modifier.size(56.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        }
    }
}
