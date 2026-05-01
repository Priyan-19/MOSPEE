package com.mospee.ui.statistics

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mospee.ui.components.GlassyCard
import com.mospee.ui.home.SpeedoBottomNav
import com.mospee.ui.theme.*
import com.mospee.utils.LocationUtils
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun StatisticsScreen(
    onBack: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenLive: () -> Unit,
    viewModel: StatisticsViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val useKmh  by viewModel.useKmh.collectAsStateWithLifecycle()
    val darkMode by viewModel.darkMode.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            SpeedoBottomNav(
                currentRoute    = "statistics",
                onHomeClick     = onOpenHome,
                onLiveClick     = onOpenLive,
                onHistoryClick  = onOpenHistory,
                onStatsClick    = { },
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Surface(color = Color.Transparent, modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.statusBarsPadding().padding(horizontal = 20.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("ANALYTICS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                        Text("Trip Analytics", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                    }
                    if (uiState.isLoading) CircularProgressIndicator(modifier = Modifier.size(22.dp), color = StPrimary, strokeWidth = 2.dp)
                }
            }

            if (uiState.isLoading) {
                Box(Modifier.fillMaxWidth().height(300.dp), Alignment.Center) {
                    CircularProgressIndicator(color = StPrimary)
                }
                return@Column
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Spacer(modifier = Modifier.height(4.dp))

                // ── 4 summary tiles ──────────────────────────────────────────
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SummaryTile(modifier = Modifier.weight(1f), label = "Total Trips", value = uiState.totalTrips.toString(),
                        icon = Icons.Rounded.Route, color = StSecondary)
                    SummaryTile(modifier = Modifier.weight(1f),
                        label = "Total Distance",
                        value = LocationUtils.formatDistance(uiState.totalDistanceMeters.toFloat(), useKmh),
                        icon = Icons.Rounded.SocialDistance, color = StPrimary)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SummaryTile(modifier = Modifier.weight(1f),
                        label = "Total Time",
                        value = LocationUtils.formatDuration(uiState.totalDurationSeconds),
                        icon = Icons.Rounded.Timer, color = StWarning)
                    SummaryTile(modifier = Modifier.weight(1f),
                        label = "Top Speed",
                        value = LocationUtils.formatSpeed(uiState.topSpeedKmh, useKmh),
                        icon = Icons.Rounded.Speed, color = StError)
                }

                // ── Speed over time chart ────────────────────────────────────
                if (uiState.speedTrend.isNotEmpty()) {
                    SpeedTrendCard(trend = uiState.speedTrend, useKmh = useKmh)
                }

                // ── Speed distribution donut ─────────────────────────────────
                SpeedDistributionCard(trips = uiState.trips)

                // ── Current trip stats ───────────────────────────────────────
                uiState.trips.lastOrNull()?.let { last ->
                    CurrentTripStatsCard(trip = last, useKmh = useKmh)
                }

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}
}

// ── Summary tile ──────────────────────────────────────────────────────────────
@Composable
private fun SummaryTile(modifier: Modifier, label: String, value: String, icon: ImageVector, color: Color) {
    GlassyCard(modifier = modifier, shape = RoundedCornerShape(14.dp), contentPadding = PaddingValues(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(38.dp).background(color.copy(0.15f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(value, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
            }
        }
    }
}

// ── Speed trend chart ─────────────────────────────────────────────────────────
@Composable
private fun SpeedTrendCard(trend: List<Float>, useKmh: Boolean) {
    GlassyCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), contentPadding = PaddingValues(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("SPEED OVER TIME", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Text(if (useKmh) "km/h" else "mph", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            SpeedChart(trend = trend, useKmh = useKmh, modifier = Modifier.fillMaxWidth().height(100.dp))
    }
}

@Composable
private fun SpeedChart(trend: List<Float>, useKmh: Boolean, modifier: Modifier) {
    val converted = if (useKmh) trend else trend.map { LocationUtils.kmhToMph(it) }
    val maxV = (converted.maxOrNull() ?: 1f).coerceAtLeast(1f)
    Canvas(modifier = modifier) {
        if (converted.size < 2) return@Canvas
        val xStep = size.width / (converted.size - 1).toFloat()
        val path = Path().apply {
            converted.forEachIndexed { i, v ->
                val x = i * xStep
                val y = size.height - (v / maxV) * size.height
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
        }
        // Fill
        val fill = Path().apply {
            addPath(path)
            lineTo(size.width, size.height); lineTo(0f, size.height); close()
        }
        drawPath(fill, Brush.verticalGradient(listOf(StPrimary.copy(0.3f), Color.Transparent)))
        drawPath(path, StPrimary, style = Stroke(2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
        // Peak label
        val peakI = converted.indices.maxByOrNull { converted[it] } ?: return@Canvas
        val px = peakI * xStep
        val py = size.height - (converted[peakI] / maxV) * size.height
        drawCircle(StPrimary, 4.dp.toPx(), Offset(px, py))
    }
}

// ── Speed distribution donut ──────────────────────────────────────────────────
@Composable
private fun SpeedDistributionCard(trips: List<com.mospee.domain.model.Trip>) {
    val avgSpeed = trips.map { it.avgSpeedKmh }.average().toFloat()
    // Mock distribution bands
    val bands = listOf(
        Triple("0–20 km/h",  0.20f, StPrimary),
        Triple("20–40 km/h", 0.45f, StSecondary),
        Triple("40–60 km/h", 0.25f, StWarning),
        Triple("60+ km/h",   0.10f, StError)
    )
    GlassyCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), contentPadding = PaddingValues(16.dp)) {
            Text("TRIP ANALYTICS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Donut
                Box(modifier = Modifier.size(90.dp), contentAlignment = Alignment.Center) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val sweep = 360f
                        var start = -90f
                        bands.forEach { (_, fraction, color) ->
                            val s = fraction * sweep
                            drawArc(color, start, s, false, style = Stroke(12.dp.toPx()))
                            start += s
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${avgSpeed.toInt()}", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                        Text("Avg km/h", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 8.sp)
                    }
                }
                Spacer(modifier = Modifier.width(20.dp))
                // Legend
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    bands.forEach { (label, fraction, color) ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                            Text("${(fraction * 100).toInt()}%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
    }
}

// ── Current trip stats card ───────────────────────────────────────────────────
@Composable
private fun CurrentTripStatsCard(trip: com.mospee.domain.model.Trip, useKmh: Boolean) {
    GlassyCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), contentPadding = PaddingValues(16.dp)) {
            Text("LAST TRIP STATS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(14.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                TripStatCard(modifier = Modifier.weight(1f), label = "Top Speed", value = "${trip.topSpeedKmh.toInt()}", unit = if (useKmh) "km/h" else "mph", color = StError, icon = Icons.Rounded.Speed)
                Spacer(modifier = Modifier.width(10.dp))
                TripStatCard(modifier = Modifier.weight(1f), label = "Avg Speed", value = "${trip.avgSpeedKmh.toInt()}", unit = if (useKmh) "km/h" else "mph", color = StSecondary, icon = Icons.Rounded.TrendingUp)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                TripStatCard(modifier = Modifier.weight(1f), label = "Distance", value = LocationUtils.formatDistance(trip.distanceMeters, useKmh), unit = "", color = StPrimary, icon = Icons.Rounded.Route)
                Spacer(modifier = Modifier.width(10.dp))
                TripStatCard(modifier = Modifier.weight(1f), label = "Time", value = LocationUtils.formatDuration(trip.durationSeconds), unit = "", color = StWarning, icon = Icons.Rounded.Timer)
            }
    }
}

@Composable
private fun TripStatCard(modifier: Modifier, label: String, value: String, unit: String, color: Color, icon: ImageVector) {
    GlassyCard(modifier = modifier, shape = RoundedCornerShape(12.dp), contentPadding = PaddingValues(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(32.dp).background(color.copy(0.15f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(15.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f))
                Text(if (unit.isEmpty()) value else "${value} ${unit}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
            }
        }
    }
}
