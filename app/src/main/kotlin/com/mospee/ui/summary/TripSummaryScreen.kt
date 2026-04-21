package com.mospee.ui.summary

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mospee.domain.model.LocationPoint
import com.mospee.domain.model.Trip
import com.mospee.ui.components.OpenStreetMapView
import com.mospee.ui.theme.*
import com.mospee.utils.LocationUtils
import java.text.SimpleDateFormat
import java.util.*
import org.osmdroid.util.GeoPoint

@Composable
fun TripSummaryScreen(
    tripId: Long,
    onBack: () -> Unit,
    onOpenHistory: () -> Unit,
    viewModel: TripSummaryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val useKmh by viewModel.useKmh.collectAsStateWithLifecycle()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(tripId) { viewModel.loadTrip(tripId) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete trip?") },
            text = { Text("This trip will be permanently removed.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.deleteTrip(tripId) { onBack() }
                }) {
                    Text("Delete", color = MospeeRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = Color.White,
            titleContentColor = Color.Black,
            textContentColor = Color.Black.copy(alpha = 0.6f)
        )
    }

    Scaffold(containerColor = MospeeCream) { padding ->
        when (val state = uiState) {
            is SummaryUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MospeeTerracotta)
                }
            }
            is SummaryUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = state.message, color = MospeeRed)
                }
            }
            is SummaryUiState.Success -> {
                SummaryContent(
                    trip = state.trip,
                    points = state.points,
                    useKmh = useKmh,
                    onBack = onBack,
                    onDelete = { showDeleteDialog = true },
                    onOpenHistory = onOpenHistory,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
private fun SummaryContent(
    trip: Trip,
    points: List<LocationPoint>,
    useKmh: Boolean,
    onBack: () -> Unit,
    onDelete: () -> Unit,
    onOpenHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val shareText = remember(trip, useKmh) {
        "MOSPEE Trip Summary\nDistance: ${LocationUtils.formatDistance(trip.distanceMeters, useKmh)}\nDuration: ${LocationUtils.formatDuration(trip.durationSeconds)}\nAvg Speed: ${LocationUtils.formatSpeed(trip.avgSpeedKmh, useKmh)} ${if (useKmh) "km/h" else "mph"}"
    }

    val startText = remember(trip.startTime) { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(trip.startTime)) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(modifier = Modifier.height(300.dp)) {
            SummaryMap(points = points)
            Surface(
                modifier = Modifier.padding(16.dp).size(48.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.9f),
                shadowElevation = 4.dp
            ) {
                IconButton(onClick = onBack) { Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Color.Gray) }
            }
        }

        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "Trip Summary", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = Color.Black.copy(alpha = 0.8f))
            Text(text = startText, style = MaterialTheme.typography.bodyMedium, color = Color.Black.copy(alpha = 0.4f))

            Spacer(modifier = Modifier.height(24.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                item { SummaryStatItem("Distance", LocationUtils.formatDistance(trip.distanceMeters, useKmh), Icons.Rounded.Route) }
                item { SummaryStatItem("Time", LocationUtils.formatDuration(trip.durationSeconds), Icons.Rounded.Schedule) }
                item { SummaryStatItem("Avg Speed", "${LocationUtils.formatSpeed(trip.avgSpeedKmh, useKmh)} ${if (useKmh) "km/h" else "mph"}", Icons.Rounded.Speed) }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(text = "DETAILED METRICS", style = MaterialTheme.typography.labelMedium, color = MospeeTerracotta, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    MetricRow("Top Speed", "${LocationUtils.formatSpeed(trip.topSpeedKmh, useKmh)} ${if (useKmh) "km/h" else "mph"}", Icons.Rounded.Bolt)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.Black.copy(alpha = 0.05f))
                    MetricRow("Total Points", points.size.toString(), Icons.Rounded.LocationOn)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = {
                        context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }, "Share trip"))
                    },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MospeeTerracotta)
                ) {
                    Icon(Icons.Rounded.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share")
                }
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MospeeRed.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Rounded.Delete, contentDescription = null, tint = MospeeRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete", color = MospeeRed)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onOpenHistory, modifier = Modifier.fillMaxWidth()) {
                Text("Back to History", color = Color.Gray)
            }
        }
    }
}

@Composable
private fun SummaryStatItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        modifier = Modifier.width(140.dp),
        shape = RoundedCornerShape(24.dp),
        color = MospeeTerracottaLight
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = MospeeTerracotta, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Black.copy(alpha = 0.4f))
            Text(text = value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.Black.copy(alpha = 0.8f))
        }
    }
}

@Composable
private fun MetricRow(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = MospeeTerracotta, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = label, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium, color = Color.Black.copy(alpha = 0.6f))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.Black.copy(alpha = 0.8f))
    }
}

@Composable
private fun SummaryMap(points: List<LocationPoint>) {
    if (points.isEmpty()) return
    val geoPoints = remember(points) { points.map { GeoPoint(it.latitude, it.longitude) } }
    OpenStreetMapView(
        modifier = Modifier.fillMaxSize(),
        center = geoPoints.first(),
        zoom = 15.0,
        routePoints = geoPoints,
        showRouteMarkers = true,
        enableGestures = true
    )
}
