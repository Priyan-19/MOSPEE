package com.mospee.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mospee.domain.model.Trip
import com.mospee.ui.components.*
import com.mospee.ui.theme.*
import com.mospee.utils.LocationUtils
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(
    onStartTrip: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenTripDetails: (Long) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenStats: () -> Unit,
    viewModel: HomeViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isTracking by viewModel.isTracking.collectAsStateWithLifecycle()
    val liveData by viewModel.liveTripState.collectAsStateWithLifecycle()
    val useKmh by viewModel.useKmh.collectAsStateWithLifecycle()
    val overspeedEnabled by viewModel.overspeedEnabled.collectAsStateWithLifecycle()
    val overspeedThreshold by viewModel.overspeedThreshold.collectAsStateWithLifecycle()
    val darkMode by viewModel.darkMode.collectAsStateWithLifecycle()
    val allTrips by viewModel.allTrips.collectAsStateWithLifecycle()
    val heading  by viewModel.heading.collectAsStateWithLifecycle()
    val totalDistMeters by viewModel.totalDistanceMeters.collectAsStateWithLifecycle()
    val totalTimeSeconds by viewModel.totalDurationSeconds.collectAsStateWithLifecycle()
    val overallAvgKmh by viewModel.overallAvgSpeedKmh.collectAsStateWithLifecycle()

    val displaySpeed = if (useKmh) liveData.currentSpeedKmh else LocationUtils.kmhToMph(liveData.currentSpeedKmh)
    val displayDist = if (useKmh) liveData.distanceMeters / 1000f else liveData.distanceMeters * 0.000621371f
    val displayAvg = if (useKmh) liveData.avgSpeedKmh else LocationUtils.kmhToMph(liveData.avgSpeedKmh)
    val displayTop = if (useKmh) liveData.topSpeedKmh else LocationUtils.kmhToMph(liveData.topSpeedKmh)
    val unit = if (useKmh) "km/h" else "mph"
    val distUnit = if (useKmh) "km" else "mi"

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            SpeedoBottomNav(
                currentRoute = "home",
                onHomeClick = { },
                onLiveClick = onStartTrip,
                onHistoryClick = onOpenHistory,
                onStatsClick = onOpenStats,
                onSettingsClick = onOpenSettings
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (darkMode) {
                Image(
                    painter = painterResource(id = com.mospee.R.drawable.mospee_bg),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Dark overlay for dark mode
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                )
            } else {
                Image(
                    painter = painterResource(id = com.mospee.R.drawable.mospee_light_bg),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Light overlay for light mode to ensure text contrast
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White.copy(alpha = 0.4f))
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
            // ── Hero Section: MOSPEE Branding & Primary Actions ────────────────
            GlassyCard(
                modifier = Modifier.fillMaxWidth(),
                containerColor = if (darkMode) Color.Black.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.7f)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SpeedoTrackLogo()
                    
                    Spacer(modifier = Modifier.height(48.dp))
                    
                    // Large Primary Action Button
                    Button(
                        onClick = onStartTrip,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .shadow(12.dp, RoundedCornerShape(32.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent
                        ),
                        contentPadding = PaddingValues(0.dp),
                        shape = RoundedCornerShape(32.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF00E676), Color(0xFF00C853))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Rounded.PlayArrow, null, tint = Color.White, modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    "START NEW TRIP",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Secondary Outlined Action
                    OutlinedButton(
                        onClick = onOpenHistory,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(28.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.History, null, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("VIEW HISTORY", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // ── Live Trip Snapshot (Only when tracking) ────────────────────────
            if (isTracking) {
                GlassyCard(modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        BigStatItem("%.1f".format(displaySpeed), "CURRENT", unit)
                        BigStatItem("%.2f".format(displayDist), "DISTANCE", distUnit)
                        BigStatItem(LocationUtils.formatDuration(liveData.elapsedSeconds), "TIME", "")
                    }
                }
            }

            // ── Secondary Actions Row ────────────────────────────────────────
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OverspeedAlertCard(
                    modifier = Modifier.weight(1f),
                    enabled = overspeedEnabled,
                    threshold = overspeedThreshold,
                    unit = unit,
                    onToggle = { viewModel.setOverspeedEnabled(it) },
                    onThresholdChange = { viewModel.setOverspeedThreshold(it) }
                )
                SettingsQuickCard(
                    modifier = Modifier.weight(1f),
                    useKmh = useKmh,
                    onUnitChange = { viewModel.setUseKmh(it) },
                    darkMode = darkMode,
                    onThemeChange = { viewModel.setDarkMode(it) }
                )
            }

            // ── Analytics & History Section ──────────────────────────────────
            LifetimeStatsCard(
                totalDist = LocationUtils.formatDistance(totalDistMeters, useKmh),
                totalTime = LocationUtils.formatDuration(totalTimeSeconds),
                avgSpeed = LocationUtils.formatSpeed(overallAvgKmh, useKmh),
                trips = allTrips
            )

            val lastTrip = (uiState as? HomeUiState.Ready)?.lastTrip
            if (lastTrip != null) {
                LastTripSummaryCompactCard(lastTrip, useKmh) {
                    onOpenTripDetails(lastTrip.id)
                }
            }
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                AnalyticsCompactCard(modifier = Modifier.weight(1.2f), trips = allTrips)
                CompassCard(modifier = Modifier.weight(0.8f), heading = heading)
            }

            CurrentTripStatsCard(
                modifier = Modifier.fillMaxWidth(),
                topSpeed = "%.1f %s".format(displayTop, unit),
                avgSpeed = "%.1f %s".format(displayAvg, unit),
                distance = "%.2f %s".format(displayDist, distUnit),
                time = LocationUtils.formatDuration(liveData.elapsedSeconds)
            )

            TripHistoryCompactCard(
                modifier = Modifier.fillMaxWidth(), 
                trips = allTrips.take(3), 
                useKmh = useKmh,
                onOpenHistory = onOpenHistory,
                onOpenTrip = onOpenTripDetails
            )

            // ── Quick Action Grid ───────────────────────────────────────────
            val context = LocalContext.current
            val weatherInfo by viewModel.weather.collectAsState()
            val weatherLabel = weatherInfo?.let { "Weather\n${it.temperature}°C" } ?: "Weather\n--"
            
            Spacer(modifier = Modifier.height(6.dp))
            BottomActionsGrid(
                modifier = Modifier.fillMaxWidth(),
                weatherLabel = weatherLabel,
                onNearbyFuel = {
                    val gmmIntentUri = Uri.parse("geo:0,0?q=fuel")
                    val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                    mapIntent.setPackage("com.google.android.apps.maps")
                    context.startActivity(mapIntent)
                },
                onWeather = {
                    Toast.makeText(context, "Fetching real-time weather...", Toast.LENGTH_SHORT).show()
                },
                onShareTrip = {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "I'm tracking my trip with MOSPEE! Current speed: %.1f %s".format(displaySpeed, unit))
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                },
                onEmergency = {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:911")
                    context.startActivity(intent)
                }
            )
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
}

@Composable
fun LastTripSummaryCompactCard(trip: Trip, useKmh: Boolean, onClick: () -> Unit) {
    GlassyCard(modifier = Modifier.fillMaxWidth()) {
        Text("LAST TRIP SUMMARY", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(12.dp))
        // Small map path (Simplified visual)
        Canvas(modifier = Modifier.fillMaxWidth().height(60.dp)) {
            val path = Path().apply {
                moveTo(0f, size.height * 0.7f)
                lineTo(size.width * 0.2f, size.height * 0.4f)
                lineTo(size.width * 0.5f, size.height * 0.6f)
                lineTo(size.width * 0.8f, size.height * 0.2f)
                lineTo(size.width, size.height * 0.3f)
            }
            drawPath(path, StAccent, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round))
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            StatText(LocationUtils.formatDistance(trip.distanceMeters, useKmh), "DISTANCE")
            StatText(LocationUtils.formatDuration(trip.durationSeconds), "TIME")
            StatText(LocationUtils.formatSpeed(trip.avgSpeedKmh, useKmh), "AVG SPEED")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth().height(40.dp),
            colors = ButtonDefaults.buttonColors(containerColor = StAccent),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("View Detailed Summary", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
            Icon(Icons.Rounded.ChevronRight, null, modifier = Modifier.size(16.dp), tint = Color.White)
        }
    }
}

@Composable
fun StatText(value: String, label: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 8.sp)
        Text(value, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun BigStatItem(value: String, label: String, unit: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f), letterSpacing = 1.sp)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
            if (unit.isNotEmpty()) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(unit, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 2.dp))
            }
        }
    }
}

@Composable
fun SmallCustomSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val trackColor by androidx.compose.animation.animateColorAsState(if (checked) StPrimary else MaterialTheme.colorScheme.surfaceVariant)
    val thumbOffset by androidx.compose.animation.core.animateDpAsState(if (checked) 16.dp else 2.dp)
    
    Box(
        modifier = Modifier
            .width(36.dp)
            .height(20.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(trackColor)
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.CenterStart
    ) {
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .size(16.dp)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}

@Composable
fun OverspeedAlertCard(
    modifier: Modifier = Modifier, 
    enabled: Boolean, 
    threshold: Float,
    unit: String, 
    onToggle: (Boolean) -> Unit,
    onThresholdChange: (Float) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        var inputValue by remember { mutableStateOf(threshold.toInt().toString()) }
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Set Overspeed Alert") },
            text = {
                OutlinedTextField(
                    value = inputValue,
                    onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) inputValue = it },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                    label = { Text("Limit ($unit)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StPrimary,
                        focusedLabelColor = StPrimary,
                        cursorColor = StPrimary
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = { 
                    val newVal = inputValue.toFloatOrNull()
                    if (newVal != null && newVal > 0) {
                        onThresholdChange(newVal)
                    }
                    showDialog = false 
                }) { Text("Save", color = StPrimary) }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel", color = MaterialTheme.colorScheme.onSurface) }
            },
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }

    GlassyCard(
        modifier = modifier,
        borderColor = if (enabled) StError.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Warning, null, tint = if (enabled) StError else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("ALERT", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
            SmallCustomSwitch(
                checked = enabled,
                onCheckedChange = onToggle
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable { showDialog = true }
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Thres: ${threshold.toInt()} $unit", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
            Icon(Icons.Rounded.Edit, null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun AnalyticsCompactCard(modifier: Modifier = Modifier, trips: List<Trip>) {
    // Calculate simple speed brackets for analytics
    val total = trips.size.coerceAtLeast(1)
    val slow = trips.count { it.avgSpeedKmh < 20 }.toFloat() / total
    val mid = trips.count { it.avgSpeedKmh in 20.0..45.0 }.toFloat() / total
    val fast = trips.count { it.avgSpeedKmh > 45 }.toFloat() / total

    GlassyCard(modifier = modifier) {
        Text("SPEED ANALYTICS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            AnalyticsRow(SpeedGreen, "City (0-20)", "${(slow * 100).toInt()}%")
            AnalyticsRow(StSecondary, "Urban (20-45)", "${(mid * 100).toInt()}%")
            AnalyticsRow(StWarning, "High (45+)", "${(fast * 100).toInt()}%")
        }
    }
}

@Composable
fun SettingsQuickCard(
    modifier: Modifier = Modifier,
    useKmh: Boolean,
    onUnitChange: (Boolean) -> Unit,
    darkMode: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val isDark = LocalDarkMode.current
    GlassyCard(
        modifier = modifier,
        contentPadding = PaddingValues(10.dp),
        containerColor = if (isDark) Color.Black.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.8f)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Unit", style = MaterialTheme.typography.labelSmall)
                Row(modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant)) {
                    Text(
                        "KM",
                        modifier = Modifier.clickable { onUnitChange(true) }.background(if (useKmh) StPrimary else Color.Transparent).padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (useKmh) Color.Black else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        "MI",
                        modifier = Modifier.clickable { onUnitChange(false) }.background(if (!useKmh) StPrimary else Color.Transparent).padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (!useKmh) Color.Black else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Dark", style = MaterialTheme.typography.labelSmall)
                Switch(checked = darkMode, onCheckedChange = onThemeChange, modifier = Modifier.scale(0.6f))
            }
        }
    }
}

@Composable
fun AnalyticsRow(color: Color, label: String, value: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TripHistoryCompactCard(
    modifier: Modifier = Modifier, 
    trips: List<Trip>, 
    useKmh: Boolean,
    onOpenHistory: () -> Unit,
    onOpenTrip: (Long) -> Unit
) {
    GlassyCard(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("RECENT TRIPS", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Text("VIEW ALL", style = MaterialTheme.typography.labelSmall, color = StAccent, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { onOpenHistory() })
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        if (trips.isEmpty()) {
            Text("No trips recorded yet", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            trips.forEachIndexed { index, trip ->
                val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                HistoryRow(
                    date = sdf.format(Date(trip.startTime)),
                    dist = LocationUtils.formatDistance(trip.distanceMeters, useKmh),
                    time = LocationUtils.formatDuration(trip.durationSeconds),
                    color = if (index % 2 == 0) StPrimary else StSecondary,
                    onClick = { onOpenTrip(trip.id) }
                )
                if (index < trips.size - 1) Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun HistoryRow(date: String, dist: String, time: String, color: Color, onClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onClick() }) {
        Icon(Icons.Rounded.Route, null, tint = color, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(date, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            Text("$dist • $time", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Rounded.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
    }
}

@Composable
fun CompassCard(modifier: Modifier = Modifier, heading: Float) {
    val outlineColor = MaterialTheme.colorScheme.outline
    GlassyCard(modifier = modifier) {
        Box(modifier = Modifier.fillMaxWidth().height(60.dp), contentAlignment = Alignment.Center) {
            Canvas(modifier = Modifier.size(44.dp)) {
                drawCircle(outlineColor.copy(alpha = 0.3f), style = Stroke(1.dp.toPx()))
                rotate(heading) {
                    val path = Path().apply {
                        moveTo(size.width / 2, 0f)
                        lineTo(size.width * 0.3f, size.height)
                        lineTo(size.width / 2, size.height * 0.8f)
                        lineTo(size.width * 0.7f, size.height)
                        close()
                    }
                    drawPath(path, StPrimary)
                }
            }
        }
        val direction = LocationUtils.getDirectionString(heading)
        Text("$direction ${heading.toInt()}°", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
    }
}

@Composable
fun SpeedoBottomNav(
    currentRoute: String,
    onHomeClick: () -> Unit,
    onLiveClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onStatsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (LocalDarkMode.current) MaterialTheme.colorScheme.surface else Color.White.copy(alpha = 0.9f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SpeedoNavItem(
                icon = Icons.Rounded.Home,
                label = "Home",
                isSelected = currentRoute == "home",
                onClick = onHomeClick
            )
            SpeedoNavItem(
                icon = Icons.Rounded.Speed,
                label = "Live",
                isSelected = currentRoute == "live",
                onClick = onLiveClick
            )
            SpeedoNavItem(
                icon = Icons.Rounded.History,
                label = "History",
                isSelected = currentRoute == "history",
                onClick = onHistoryClick
            )
            SpeedoNavItem(
                icon = Icons.Rounded.BarChart,
                label = "Analytics",
                isSelected = currentRoute == "statistics",
                onClick = onStatsClick
            )
            SpeedoNavItem(
                icon = Icons.Rounded.Settings,
                label = "Settings",
                isSelected = currentRoute == "settings",
                onClick = onSettingsClick
            )
        }
    }
}

@Composable
private fun SpeedoNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) StPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) StPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun LifetimeStatsCard(totalDist: String, totalTime: String, avgSpeed: String, trips: List<Trip>) {
    GlassyCard(modifier = Modifier.fillMaxWidth()) {
        // Visual Activity Graph (Procedural based on real trip count)
        Box(modifier = Modifier.fillMaxWidth().height(100.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val path = Path()
                if (trips.isEmpty()) {
                    path.moveTo(0f, size.height * 0.8f)
                    path.lineTo(size.width, size.height * 0.8f)
                } else {
                    val step = size.width / (trips.size.coerceAtLeast(2) - 1).toFloat()
                    trips.reversed().forEachIndexed { i, trip ->
                        val x = i * step
                        val y = size.height - (trip.distanceMeters / 1000f).coerceAtMost(size.height)
                        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
                    }
                }
                drawPath(
                    path,
                    brush = Brush.verticalGradient(listOf(StPrimary, Color.Transparent)),
                    style = Stroke(3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text(totalDist, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                Text("Total Distance", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(totalTime, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Total Time", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(avgSpeed, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Avg Speed", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            ElevationItem("Elevation Gain", "0 m", SpeedGreen)
            ElevationItem("Elevation Loss", "0 m", StSecondary)
        }
    }
}

@Composable
fun ElevationItem(label: String, value: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 8.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(value, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Canvas(modifier = Modifier.size(20.dp, 8.dp)) {
                    drawPath(Path().apply {
                        moveTo(0f, size.height)
                        lineTo(size.width * 0.4f, size.height * 0.3f)
                        lineTo(size.width * 0.7f, size.height * 0.8f)
                        lineTo(size.width, 0f)
                    }, color, style = Stroke(1.5.dp.toPx()))
                }
            }
        }
    }
}
