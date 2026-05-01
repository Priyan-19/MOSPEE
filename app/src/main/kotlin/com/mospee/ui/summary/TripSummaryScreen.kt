package com.mospee.ui.summary

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mospee.ui.components.OpenStreetMapView
import com.mospee.ui.components.RouteThumbnail
import com.mospee.ui.theme.*
import com.mospee.utils.LocationUtils
import org.osmdroid.util.GeoPoint
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TripSummaryScreen(
    tripId: Long,
    onBack: () -> Unit,
    onOpenHistory: () -> Unit,
    viewModel: TripSummaryViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val useKmh  by viewModel.useKmh.collectAsStateWithLifecycle()
    val weather by viewModel.weather.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(tripId) { viewModel.loadTrip(tripId) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Surface(color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth(), border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
                Row(
                    modifier = Modifier.statusBarsPadding().padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.size(38.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                    ) {
                        Icon(Icons.Rounded.ChevronLeft, null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp))
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("TRIP SUMMARY", style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                        Text("Performance Analytics", style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                    }
                    // Share button
                    IconButton(
                        onClick = {
                            val state = uiState
                            if (state is SummaryUiState.Success) {
                                val t = state.trip
                                val txt = "🚗 Trip recorded with MOSPEE!\n" +
                                    "📏 Distance: ${LocationUtils.formatDistance(t.distanceMeters, useKmh)}\n" +
                                    "⚡ Top Speed: ${LocationUtils.formatSpeed(t.topSpeedKmh, useKmh)}\n" +
                                    "⏱ Duration: ${LocationUtils.formatDuration(t.durationSeconds)}"
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, txt)
                                }
                                context.startActivity(Intent.createChooser(intent, "Share Trip"))
                            }
                        },
                        modifier = Modifier.size(38.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                    ) {
                        Icon(Icons.Rounded.Share, null, tint = StPrimary, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    ) { padding ->
        when (val state = uiState) {
            is SummaryUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator(color = StPrimary)
            }
            is SummaryUiState.Success -> {
                val t = state.trip
                val geoPoints = state.points.map { GeoPoint(it.latitude, it.longitude) }

                Column(
                    modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // ── Map or Route Thumbnail ──────────────────────────────────
                    Box(
                        modifier = Modifier.fillMaxWidth().height(240.dp).background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        if (geoPoints.isNotEmpty()) {
                            OpenStreetMapView(
                                modifier = Modifier.fillMaxSize(),
                                center = geoPoints[geoPoints.size / 2],
                                startPoint = geoPoints.first(),
                                endPoint = geoPoints.last(),
                                routePoints = geoPoints,
                                zoom = 15.0
                            )
                        } else {
                            RouteThumbnail(modifier = Modifier.fillMaxSize())
                        }
                        // Floating badge
                        Surface(
                            modifier = Modifier.padding(12.dp).align(Alignment.TopEnd),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.Place, null, tint = StPrimary, modifier = Modifier.size(12.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Route Recorded", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        // ── 4 stat cards ───────────────────────────────────────
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            SummaryStatCard(Modifier.weight(1f), "Distance",
                                LocationUtils.formatDistance(t.distanceMeters, useKmh),
                                Icons.Rounded.Route, StPrimary)
                            SummaryStatCard(Modifier.weight(1f), "Top Speed",
                                LocationUtils.formatSpeed(t.topSpeedKmh, useKmh),
                                Icons.Rounded.Speed, SpeedRed)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            SummaryStatCard(Modifier.weight(1f), "Avg Speed",
                                LocationUtils.formatSpeed(t.avgSpeedKmh, useKmh),
                                Icons.Rounded.TrendingUp, StSecondary)
                            SummaryStatCard(Modifier.weight(1f), "Duration",
                                LocationUtils.formatDuration(t.durationSeconds),
                                Icons.Rounded.Timer, StWarning)
                        }

                        // ── Session info card ───────────────────────────────────
                        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("SESSION INFO", style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                                val sdf = SimpleDateFormat("MMM dd, yyyy  hh:mm a", Locale.getDefault())
                                InfoRow("Start Time", sdf.format(Date(t.startTime)))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                                InfoRow("End Time", sdf.format(Date(t.endTime)))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outline)
                                InfoRow("Cloud Sync", if (t.isSynced) "Synced ✓" else "Local Only",
                                    if (t.isSynced) StPrimary else StWarning)
                            }
                        }

                        // ── Quick actions ───────────────────────────────────────
                        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
                            Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceAround) {
                                QuickActionButton(Icons.Rounded.LocalGasStation, "Nearby\nFuel", StWarning) {
                                    val uri = android.net.Uri.parse("geo:0,0?q=gas+station")
                                    val intent = Intent(Intent.ACTION_VIEW, uri)
                                    context.startActivity(intent)
                                }
                                QuickActionButton(
                                    Icons.Rounded.WbSunny, 
                                    "Weather\n${weather?.temperature ?: "--"}°C", 
                                    StSecondary
                                ) {
                                    // Could show details or refresh
                                    viewModel.loadTrip(tripId)
                                }
                                QuickActionButton(Icons.Rounded.Share, "Share\nTrip", StPrimary) {
                                    if (state is SummaryUiState.Success) {
                                        val t = state.trip
                                        val txt = "🚗 Trip recorded with MOSPEE!\n" +
                                            "📏 Distance: ${LocationUtils.formatDistance(t.distanceMeters, useKmh)}\n" +
                                            "⏱ Duration: ${LocationUtils.formatDuration(t.durationSeconds)}"
                                        val intent = Intent(Intent.ACTION_SEND).apply {
                                            type = "text/plain"
                                            putExtra(Intent.EXTRA_TEXT, txt)
                                        }
                                        context.startActivity(Intent.createChooser(intent, "Share Trip"))
                                    }
                                }
                                QuickActionButton(Icons.Rounded.Sos, "SOS", StError, isEmergency = true) {
                                    val intent = Intent(Intent.ACTION_DIAL).apply {
                                        data = android.net.Uri.parse("tel:911")
                                    }
                                    context.startActivity(intent)
                                }
                            }
                        }

                        // ── View history button ─────────────────────────────────
                        Button(
                            onClick = onOpenHistory,
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Icon(Icons.Rounded.History, null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("View All History", fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
            is SummaryUiState.Error -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Rounded.Error, null, tint = StError, modifier = Modifier.size(52.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(state.message, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = onBack) { Text("Go Back", color = StPrimary) }
                }
            }
        }
    }
}

@Composable
private fun SummaryStatCard(modifier: Modifier, label: String, value: String, icon: ImageVector, color: Color) {
    Surface(modifier = modifier, shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surface, border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(modifier = Modifier.size(34.dp).background(color.copy(0.14f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(value, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
            Text(label.uppercase(), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String, color: Color = Color.Unspecified) {
    val finalColor = if (color == Color.Unspecified) MaterialTheme.colorScheme.onSurface else color
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodySmall, color = finalColor, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector, 
    label: String, 
    color: Color, 
    isEmergency: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(
            interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
            indication = rememberRipple(bounded = false, radius = 32.dp),
            onClick = onClick
        )
    ) {
        Box(
            modifier = Modifier.size(if (isEmergency) 52.dp else 46.dp)
                .background(if (isEmergency) StError else color.copy(0.13f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = if (isEmergency) Color.White else color, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center, lineHeight = 14.sp)
    }
}
