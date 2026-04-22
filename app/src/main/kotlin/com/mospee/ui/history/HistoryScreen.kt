package com.mospee.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mospee.domain.model.Trip
import com.mospee.ui.components.RouteThumbnail
import com.mospee.ui.theme.*
import com.mospee.utils.LocationUtils
import java.text.SimpleDateFormat
import java.util.*

private enum class HistoryFilter(val label: String) {
    All("All"),
    Today("Today"),
    ThisWeek("This Week"),
    ThisMonth("This Month")
}

@Composable
fun HistoryScreen(
    onTripClick: (Long) -> Unit,
    onBack: () -> Unit,
    onOpenHome: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val useKmh by viewModel.useKmh.collectAsStateWithLifecycle()
    var selectedFilter by remember { mutableStateOf(HistoryFilter.All) }

    Scaffold(
        bottomBar = {
            HistoryBottomNav(
                currentRoute = "history",
                onHomeClick = onOpenHome,
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
            HistoryTopBar(onBack = onBack)

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                items(HistoryFilter.entries.toTypedArray()) { filter ->
                    FilterChip(
                        label = filter.label,
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter }
                    )
                }
            }

            when (val state = uiState) {
                is HistoryUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MospeeTerracotta)
                    }
                }
                is HistoryUiState.Empty -> EmptyHistoryContent(modifier = Modifier.fillMaxSize())
                is HistoryUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = MospeeRed)
                    }
                }
                is HistoryUiState.Success -> {
                    val filteredTrips = remember(state.trips, selectedFilter) {
                        state.trips.filter { trip -> matchesFilter(trip, selectedFilter) }
                    }
                    if (filteredTrips.isEmpty()) {
                        EmptyHistoryContent(
                            modifier = Modifier.fillMaxSize(),
                            title = "No matching trips",
                            subtitle = "Try a wider filter to see more MOSPEE trips."
                        )
                    } else {
                        TripList(
                            trips = filteredTrips,
                            useKmh = useKmh,
                            onTripClick = onTripClick,
                            onDeleteTrip = { viewModel.deleteTrip(it) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(50.dp),
        color = if (selected) MospeeTerracottaLight else Color.White.copy(alpha = 0.5f),
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.Black.copy(alpha = 0.05f))
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) MospeeTerracotta else Color.Black.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun HistoryTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(48.dp),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.9f),
            shadowElevation = 2.dp
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = "My Trips",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Black.copy(alpha = 0.8f)
            )
            Text(
                text = "Revisit your tracked drives",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
private fun TripList(
    trips: List<Trip>,
    useKmh: Boolean,
    onTripClick: (Long) -> Unit,
    onDeleteTrip: (Long) -> Unit
) {
    var tripToDelete by remember { mutableStateOf<Trip?>(null) }

    tripToDelete?.let { trip ->
        AlertDialog(
            onDismissRequest = { tripToDelete = null },
            title = { Text("Delete trip?") },
            text = { Text("This trip will be permanently removed from MOSPEE history.") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteTrip(trip.id)
                    tripToDelete = null
                }) {
                    Text("Delete", color = MospeeRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { tripToDelete = null }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = Color.White,
            titleContentColor = Color.Black,
            textContentColor = Color.Black.copy(alpha = 0.6f)
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(trips, key = { it.id }) { trip ->
            HistoryTripCard(
                trip = trip,
                useKmh = useKmh,
                onClick = { onTripClick(trip.id) },
                onDelete = { tripToDelete = trip }
            )
        }
    }
}

@Composable
private fun HistoryTripCard(
    trip: Trip,
    useKmh: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val dateText = remember(trip.startTime) {
        SimpleDateFormat("EEE, dd MMM • hh:mm a", Locale.getDefault()).format(Date(trip.startTime))
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(24.dp),
        color = MospeeTerracottaLight
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Route,
                    contentDescription = null,
                    tint = MospeeTerracotta,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black.copy(alpha = 0.4f)
                )
                Text(
                    text = LocationUtils.formatDistance(trip.distanceMeters, useKmh),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black.copy(alpha = 0.8f)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Schedule, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(LocationUtils.formatDuration(trip.durationSeconds), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Speed, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("${LocationUtils.formatSpeed(trip.topSpeedKmh, useKmh)} ${if (useKmh) "km/h" else "mph"}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Cloud Sync Status
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (trip.isSynced) Icons.Rounded.CloudDone else Icons.Rounded.CloudUpload,
                        contentDescription = null,
                        tint = if (trip.isSynced) MospeeGreen else MospeeAmber,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (trip.isSynced) "Synced" else "Pending Sync",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (trip.isSynced) MospeeGreen else MospeeAmber
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(Icons.Rounded.DeleteOutline, contentDescription = "Delete", tint = MospeeRed.copy(alpha = 0.5f))
            }
        }
    }
}

@Composable
private fun EmptyHistoryContent(
    modifier: Modifier = Modifier,
    title: String = "No trips yet",
    subtitle: String = "Start your first trip to build your driving history."
) {
    Column(
        modifier = modifier.padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Rounded.History,
            contentDescription = null,
            tint = MospeeTerracotta.copy(alpha = 0.2f),
            modifier = Modifier.size(100.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.Black.copy(alpha = 0.7f)
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Black.copy(alpha = 0.4f),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun HistoryBottomNav(
    currentRoute: String,
    onHomeClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().height(80.dp),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem(Icons.Rounded.Home, "Home", currentRoute == "home", onHomeClick)
            BottomNavItem(Icons.Rounded.History, "History", currentRoute == "history", {})
            BottomNavItem(Icons.Rounded.Settings, "Settings", currentRoute == "settings", onSettingsClick)
        }
    }
}

@Composable
private fun BottomNavItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
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

private fun matchesFilter(trip: Trip, filter: HistoryFilter): Boolean {
    val tripCalendar = Calendar.getInstance().apply { timeInMillis = trip.startTime }
    val now = Calendar.getInstance()
    return when (filter) {
        HistoryFilter.All -> true
        HistoryFilter.Today -> tripCalendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) && tripCalendar.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR)
        HistoryFilter.ThisWeek -> tripCalendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) && tripCalendar.get(Calendar.WEEK_OF_YEAR) == now.get(Calendar.WEEK_OF_YEAR)
        HistoryFilter.ThisMonth -> tripCalendar.get(Calendar.YEAR) == now.get(Calendar.YEAR) && tripCalendar.get(Calendar.MONTH) == now.get(Calendar.MONTH)
    }
}
