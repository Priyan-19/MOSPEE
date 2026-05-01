package com.mospee.ui.components

import kotlin.math.*

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mospee.ui.theme.*
import com.mospee.ui.theme.LocalDarkMode
import kotlin.math.cos
import kotlin.math.sin

/**
 * Base Glassy Card with subtle borders and shadow.
 */
@Composable
fun GlassyCard(
    modifier: Modifier = Modifier,
    containerColor: Color = if (LocalDarkMode.current) Color.Black.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.85f),
    borderColor: Color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f),
    shape: RoundedCornerShape = RoundedCornerShape(24.dp),
    contentPadding: PaddingValues = PaddingValues(16.dp),
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val isDark = LocalDarkMode.current
    val cardModifier = if (onClick != null) modifier.clickable { onClick() } else modifier
    
    Box(
        modifier = cardModifier
            .shadow(
                elevation = if (isDark) 16.dp else 24.dp,
                shape = shape,
                spotColor = Color.Black.copy(alpha = if (isDark) 0.5f else 0.15f),
                ambientColor = Color.Black.copy(alpha = if (isDark) 0.3f else 0.05f)
            )
            .clip(shape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        containerColor.copy(alpha = (containerColor.alpha + 0.15f).coerceAtMost(1f)),
                        containerColor,
                        containerColor.copy(alpha = (containerColor.alpha - 0.15f).coerceAtLeast(0f))
                    )
                )
            )
            .border(
                width = 1.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        borderColor.copy(alpha = (borderColor.alpha + 0.4f).coerceAtMost(1f)), 
                        borderColor.copy(alpha = (borderColor.alpha - 0.1f).coerceAtLeast(0f))
                    )
                ),
                shape = shape
            )
    ) {
        // Premium Glossy Reflection Overlay
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = if (isDark) 0.05f else 0.4f),
                            Color.Transparent
                        ),
                        center = Offset(0f, 0f),
                        radius = 1000f
                    )
                )
        )
        Column(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}

/**
 * SpeedoTrack Logo matching the branding in the image.
 */
@Composable
fun SpeedoTrackLogo(modifier: Modifier = Modifier) {
    val isDark = LocalDarkMode.current
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(12.dp))
        Box(
            modifier = Modifier
                .size(48.dp)
                .shadow(
                    elevation = 16.dp, 
                    shape = CircleShape, 
                    spotColor = StPrimary.copy(alpha = 0.3f),
                    ambientColor = StPrimary.copy(alpha = 0.1f)
                )
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = if (isDark) {
                            listOf(Color(0xFF2A2D35), Color(0xFF1E2026))
                        } else {
                            listOf(Color.White, Color.White.copy(alpha = 0.8f))
                        }
                    )
                )
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        colors = if (isDark) {
                            listOf(Color.White.copy(alpha = 0.3f), Color.Transparent)
                        } else {
                            listOf(Color.White, Color.White.copy(alpha = 0.5f))
                        }
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.White.copy(alpha = if (isDark) 0.1f else 0.5f),
                                Color.Transparent
                            ),
                            center = Offset.Zero,
                            radius = 150f
                        )
                    )
            )
            Image(
                painter = painterResource(id = com.mospee.R.drawable.ic_logo_premium),
                contentDescription = "MOSPEE App Logo",
                modifier = Modifier.fillMaxSize().padding(8.dp),
                contentScale = ContentScale.Fit
            )
        }
        
        Spacer(modifier = Modifier.width(6.dp))
        
        Column {
            Text(
                text = "MOSPEE",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.5).sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = "Track. Analyze. Improve.",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    letterSpacing = 0.2.sp
                )
            )
        }
    }
}

@Composable
fun AdvancedSpeedGauge(
    speed: Float,
    maxSpeedValue: Float = 160f,
    modifier: Modifier = Modifier
) {
    val animatedSpeed by animateFloatAsState(
        targetValue = speed,
        animationSpec = tween(800, easing = LinearOutSlowInEasing),
        label = "speed"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        val onSurface = MaterialTheme.colorScheme.onSurface
        val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
        
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val radius = (size.minDimension / 2f) - 16.dp.toPx()
            val strokeWidth = 12.dp.toPx()
            
            val startAngle = 145f
            val sweepAngle = 250f
            
            // 1. Background Arc (Shadow/Track)
            drawArc(
                color = onSurface.copy(alpha = 0.05f),
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            
            // 2. Colored Progress Arc
            val progressSweep = (animatedSpeed / maxSpeedValue).coerceIn(0f, 1f) * sweepAngle
            val colors = listOf(
                Color(0xFF00D2FF), // Cyan
                Color(0xFF00E676), // Green
                Color(0xFFFFEA00), // Yellow
                Color(0xFFFF9100), // Orange
                Color(0xFFFF1744), // Red
                Color(0xFFD500F9)  // Purple
            )
            
            drawArc(
                brush = Brush.sweepGradient(
                    colors = colors,
                    center = Offset(cx, cy)
                ),
                startAngle = startAngle,
                sweepAngle = progressSweep,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            
            // 3. Ticks and Labels
            val tickColor = onSurface.copy(alpha = 0.2f)
            val totalTicks = 8 // 0, 20, 40, 60, 80, 100, 120, 140, 160
            
            for (i in 0..totalTicks) {
                val angle = startAngle + (i * sweepAngle / totalTicks)
                val rad = Math.toRadians(angle.toDouble()).toFloat()
                
                // Main Ticks
                val tickStartR = radius - 8.dp.toPx()
                val tickEndR = radius - 18.dp.toPx()
                drawLine(
                    color = tickColor,
                    start = Offset(cx + tickStartR * cos(rad), cy + tickStartR * sin(rad)),
                    end = Offset(cx + tickEndR * cos(rad), cy + tickEndR * sin(rad)),
                    strokeWidth = 1.5.dp.toPx()
                )
                
                // Smaller Sub-ticks
                if (i < totalTicks) {
                    for (j in 1..4) {
                        val subAngle = angle + (j * (sweepAngle / totalTicks) / 5f)
                        val subRad = Math.toRadians(subAngle.toDouble()).toFloat()
                        val sStartR = radius - 8.dp.toPx()
                        val sEndR = radius - 12.dp.toPx()
                        drawLine(
                            color = tickColor.copy(alpha = 0.1f),
                            start = Offset(cx + sStartR * cos(subRad), cy + sStartR * sin(subRad)),
                            end = Offset(cx + sEndR * cos(subRad), cy + sEndR * sin(subRad)),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                }
                
                // Labels (Numerical)
                val labelR = radius - 42.dp.toPx()
                val labelX = cx + labelR * cos(rad)
                val labelY = cy + labelR * sin(rad)
                
                // Using drawIntoCanvas for precision text if needed, but for now let's assume labels are handled
            }
        }
        
        // Use Overlay for Numbers and Labels to keep it clean
        Box(modifier = Modifier.fillMaxSize()) {
            val totalTicks = 8
            val startAngle = 145f
            val sweepAngle = 250f
            
            (0..totalTicks).forEach { i ->
                val angle = startAngle + (i * sweepAngle / totalTicks)
                val rad = Math.toRadians(angle.toDouble()).toFloat()
                val label = (i * 20).toString()
                
                // Position numbers approximately
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = onSurfaceVariant.copy(alpha = 0.8f)
                        ),
                        modifier = Modifier
                            .align(Alignment.Center)
                            .graphicsLayer {
                                val radiusPx = (size.minDimension / 2f) - 58.dp.toPx()
                                translationX = radiusPx * cos(rad)
                                translationY = radiusPx * sin(rad)
                            }
                    )
                }
            }
        }

        // Central Speed Display
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 10.dp)
        ) {
            Text(
                text = animatedSpeed.toInt().toString(),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 84.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurface,
                    letterSpacing = (-2).sp
                )
            )
            Text(
                text = "km/h",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Medium
                )
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Bottom Max Speed Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "MAX SPEED",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "128",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "km/h",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }
        }
    }
}

/**
 * Trip Route Card with Map and Elevation charts.
 */
@Composable
fun TripRouteCard(modifier: Modifier = Modifier) {
    GlassyCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "TRIP ROUTE",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Icon(
                Icons.Rounded.Fullscreen,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Map Placeholder
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF151921))
        ) {
            // Draw dummy map route
            Canvas(modifier = Modifier.fillMaxSize()) {
                val path = Path().apply {
                    moveTo(size.width * 0.1f, size.height * 0.8f)
                    quadraticBezierTo(size.width * 0.4f, size.height * 0.7f, size.width * 0.5f, size.height * 0.4f)
                    quadraticBezierTo(size.width * 0.6f, size.height * 0.1f, size.width * 0.9f, size.height * 0.1f)
                }
                drawPath(
                    path = path,
                    color = StPrimary,
                    style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                )
                
                // Markers
                drawCircle(StPrimary, radius = 6.dp.toPx(), center = Offset(size.width * 0.1f, size.height * 0.8f))
                drawCircle(StError, radius = 6.dp.toPx(), center = Offset(size.width * 0.9f, size.height * 0.1f))
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth()) {
            TripStatColumn("12.45 km", "Total Distance", modifier = Modifier.weight(1f))
            TripStatColumn("00:25:30", "Total Time", modifier = Modifier.weight(1f))
            TripStatColumn("28.7 km/h", "Avg Speed", modifier = Modifier.weight(1f))
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Elevation Gain/Loss
        Row(modifier = Modifier.fillMaxWidth()) {
            ElevationItem("Elevation Gain", "120 m", SpeedGreen, modifier = Modifier.weight(1f))
            ElevationItem("Elevation Loss", "98 m", StPurple, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun TripStatColumn(value: String, label: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ElevationItem(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(8.dp))
            // Sparkline
            Canvas(modifier = Modifier.size(40.dp, 20.dp)) {
                val path = Path().apply {
                    moveTo(0f, size.height * 0.8f)
                    lineTo(size.width * 0.3f, size.height * 0.4f)
                    lineTo(size.width * 0.6f, size.height * 0.6f)
                    lineTo(size.width, size.height * 0.2f)
                }
                drawPath(path, color, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round))
            }
        }
    }
}

/**
 * Speed Over Time Line Chart.
 */
@Composable
fun SpeedOverTimeCard(modifier: Modifier = Modifier) {
    GlassyCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "SPEED OVER TIME",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "km/h",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        ) {
            val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Grid Lines
                for (i in 0..3) {
                    val y = size.height * (i / 3f)
                    drawLine(gridColor, Offset(0f, y), Offset(size.width, y))
                }
                
                // Chart Line
                val points = listOf(20f, 40f, 35f, 60f, 80f, 75f, 90f, 85f, 110f, 100f)
                val path = Path().apply {
                    points.forEachIndexed { i, p ->
                        val x = i * (size.width / (points.size - 1))
                        val y = size.height - (p / 120f * size.height)
                        if (i == 0) moveTo(x, y) else lineTo(x, y)
                    }
                }
                
                drawPath(
                    path = path,
                    color = StPrimary,
                    style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                )
                
                // Gradient Fill
                val fillPath = Path().apply {
                    addPath(path)
                    lineTo(size.width, size.height)
                    lineTo(0f, size.height)
                    close()
                }
                drawPath(
                    fillPath,
                    Brush.verticalGradient(
                        colors = listOf(StPrimary.copy(alpha = 0.3f), Color.Transparent)
                    )
                )
                
                // Current Point
                val lastX = size.width
                val lastY = size.height - (points.last() / 120f * size.height)
                drawCircle(StPrimary, radius = 4.dp.toPx(), center = Offset(lastX, lastY))
            }
        }
    }
}

/**
 * Current Trip Stats Grid.
 */
@Composable
fun CurrentTripStatsCard(
    modifier: Modifier = Modifier,
    topSpeed: String = "0.0 km/h",
    avgSpeed: String = "0.0 km/h",
    distance: String = "0.00 km",
    time: String = "00:00:00"
) {
    GlassyCard(modifier = modifier) {
        Text(
            "CURRENT TRIP STATS",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth()) {
            StatGridItem(Icons.Rounded.Speed, "Top Speed", topSpeed, SpeedRed, Modifier.weight(1f))
            StatGridItem(Icons.Rounded.AvTimer, "Avg Speed", avgSpeed, StSecondary, Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            StatGridItem(Icons.Rounded.Route, "Distance", distance, StPrimary, Modifier.weight(1f))
            StatGridItem(Icons.Rounded.Schedule, "Time", time, StWarning, Modifier.weight(1f))
        }
    }
}

@Composable
fun StatGridItem(icon: ImageVector, label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(color.copy(alpha = 0.12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), fontSize = 9.sp)
            Text(value, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, maxLines = 1)
        }
    }
}

/**
 * Bottom Actions Grid: Nearby Fuel, Weather, Share, SOS.
 */
@Composable
fun BottomActionsGrid(
    modifier: Modifier = Modifier,
    weatherLabel: String = "Weather\n--",
    onNearbyFuel: () -> Unit = {},
    onWeather: () -> Unit = {},
    onShareTrip: () -> Unit = {},
    onEmergency: () -> Unit = {}
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        ActionIconButton(Icons.Rounded.LocalGasStation, "Nearby\nFuel", Modifier.weight(1f), onClick = onNearbyFuel)
        ActionIconButton(Icons.Rounded.Cloud, weatherLabel, Modifier.weight(1f), onClick = onWeather)
        ActionIconButton(Icons.Rounded.Share, "Share\nTrip", Modifier.weight(1f), onClick = onShareTrip)
        ActionIconButton(Icons.Rounded.Sos, "Emerg", Modifier.weight(1f), isEmergency = true, onClick = onEmergency)
    }
}

@Composable
fun ActionIconButton(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    isEmergency: Boolean = false,
    onClick: () -> Unit
) {
    GlassyCard(
        modifier = modifier.height(95.dp),
        onClick = onClick,
        contentPadding = PaddingValues(8.dp),
        containerColor = when {
            isEmergency && LocalDarkMode.current -> SpeedRed.copy(alpha = 0.15f)
            isEmergency -> Color(0xFFFFEBEE)
            LocalDarkMode.current -> Color(0xFF1A1C1E).copy(alpha = 0.6f)
            else -> Color.White
        },
        borderColor = if (isEmergency) SpeedRed.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.1f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isEmergency) {
                Surface(
                    modifier = Modifier.size(38.dp),
                    shape = CircleShape,
                    color = SpeedRed,
                    shadowElevation = 6.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("SOS", color = Color.White, fontWeight = FontWeight.Black, fontSize = 10.sp)
                    }
                }
            } else {
                Icon(
                    icon,
                    null,
                    tint = if (LocalDarkMode.current) Color.White else Color(0xFF2D323D),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                lineHeight = 12.sp,
                color = if (isEmergency) SpeedRed else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Unit Preference and Dark Mode Toggle.
 */
@Composable
fun SettingsQuickCard(modifier: Modifier = Modifier) {
    GlassyCard(modifier = modifier) {
        Text(
            "Unit Preference",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        // Speed Unit
        SegmentedToggle(options = listOf("km/h", "mph"), selectedIndex = 0)
        
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "Distance Unit",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))
        SegmentedToggle(options = listOf("km", "mi"), selectedIndex = 0)
        
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Dark Mode", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
            Switch(
                checked = true,
                onCheckedChange = {},
                colors = SwitchDefaults.colors(checkedThumbColor = StPrimary)
            )
        }
    }
}

@Composable
fun SegmentedToggle(options: List<String>, selectedIndex: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .padding(2.dp)
    ) {
        options.forEachIndexed { index, text ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(6.dp))
                    .background(if (index == selectedIndex) StPrimary else Color.Transparent)
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (index == selectedIndex) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
