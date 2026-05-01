package com.mospee.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mospee.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * MOSPEE-style circular speedometer with a multi-colour arc:
 *  0-60 = green | 60-100 = yellow | 100-140 = orange | 140+ = red
 */
@Composable
fun CircularSpeedometer(
    speed: Float,
    maxSpeed: Float = 160f,
    unit: String = "km/h",
    isOverspeed: Boolean = false,
    modifier: Modifier = Modifier
) {
    val colorOnSurface = MaterialTheme.colorScheme.onSurface
    val colorOnSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val colorOutline = MaterialTheme.colorScheme.outline
    
    val animatedSpeed by animateFloatAsState(
        targetValue = speed,
        animationSpec = tween(600, easing = LinearOutSlowInEasing),
        label = "speed"
    )

    // Pulse for overspeed
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "pulseAlpha"
    )

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val radius = (size.minDimension / 2f) - 20.dp.toPx()
            val strokeWidth = 14.dp.toPx()
            val startAngle = 140f
            val totalSweep = 260f
            val speedFraction = (animatedSpeed / maxSpeed).coerceIn(0f, 1f)
            val currentSweep = speedFraction * totalSweep

            // 1. Futuristic Background Track
            drawArc(
                color = colorOnSurface.copy(alpha = 0.05f),
                startAngle = startAngle,
                sweepAngle = totalSweep,
                useCenter = false,
                style = Stroke(width = strokeWidth + 4.dp.toPx(), cap = StrokeCap.Round)
            )
            drawArc(
                color = colorOutline.copy(alpha = 0.15f),
                startAngle = startAngle,
                sweepAngle = totalSweep,
                useCenter = false,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )

            // 2. Neon Gradient Arc
            val gradientColors = if (isOverspeed) {
                listOf(SpeedRed.copy(pulseAlpha), SpeedRed)
            } else {
                listOf(SpeedGreen, SpeedYellow, SpeedOrange, SpeedRed)
            }
            
            // Outer Glow (Subtle)
            drawArc(
                brush = Brush.sweepGradient(
                    0.4f to gradientColors[0].copy(alpha = 0.3f),
                    1f to gradientColors.last().copy(alpha = 0.3f),
                    center = Offset(cx, cy)
                ),
                startAngle = startAngle,
                sweepAngle = currentSweep,
                useCenter = false,
                style = Stroke(width = strokeWidth + 8.dp.toPx(), cap = StrokeCap.Round)
            )

            // Main Progress Arc
            drawArc(
                brush = Brush.sweepGradient(
                    0.4f to gradientColors[0],
                    1f to gradientColors.last(),
                    center = Offset(cx, cy)
                ),
                startAngle = startAngle,
                sweepAngle = currentSweep,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // 3. Dynamic Tick System (Premium)
            val tickCount = 40
            for (i in 0..tickCount) {
                val fraction = i.toFloat() / tickCount
                val angle = startAngle + fraction * totalSweep
                val rad = Math.toRadians(angle.toDouble()).toFloat()
                val isMajor = i % 5 == 0
                
                val innerR = radius - (if (isMajor) 18.dp.toPx() else 12.dp.toPx())
                val outerR = radius - 8.dp.toPx()
                
                val isActive = fraction <= speedFraction
                val tickColor = if (isActive) {
                    if (isOverspeed) SpeedRed else SpeedGreen.lerp(SpeedRed, fraction)
                } else {
                    colorOnSurfaceVariant.copy(alpha = 0.15f)
                }

                drawLine(
                    color = tickColor,
                    start = Offset(cx + innerR * cos(rad), cy + innerR * sin(rad)),
                    end = Offset(cx + outerR * cos(rad), cy + outerR * sin(rad)),
                    strokeWidth = (if (isMajor) 2.5.dp.toPx() else 1.dp.toPx()),
                    cap = StrokeCap.Round
                )
                
                // Major labels (numerical)
                if (isMajor) {
                    val labelR = innerR - 14.dp.toPx()
                    // In a real production app, we'd use nativeCanvas.drawText here
                    // but for this UI demo, we'll keep the clean look
                }
            }

            // 4. Leading Edge Light Racer (Replaces traditional needle)
            val tipAngle = startAngle + currentSweep
            val tipRad = Math.toRadians(tipAngle.toDouble()).toFloat()
            val tipX = cx + radius * cos(tipRad)
            val tipY = cy + radius * sin(tipRad)

            drawCircle(
                color = Color.White,
                radius = 5.dp.toPx(),
                center = Offset(tipX, tipY)
            )
            drawCircle(
                color = if (isOverspeed) SpeedRed else SpeedGreen.lerp(SpeedRed, speedFraction),
                radius = 10.dp.toPx(),
                center = Offset(tipX, tipY),
                alpha = pulseAlpha
            )
        }

        // 5. High-Tech Center Display
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = animatedSpeed.toInt().toString(),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 84.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-2).sp
                ),
                color = if (isOverspeed) SpeedRed else colorOnSurface
            )
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(if (isOverspeed) SpeedRed else SpeedGreen, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = unit.uppercase(),
                    style = MaterialTheme.typography.labelLarge.copy(
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = colorOnSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

// Extension to lerp colors easily
private fun Color.lerp(other: Color, fraction: Float): Color {
    return Color(
        red = red + (other.red - red) * fraction,
        green = green + (other.green - green) * fraction,
        blue = blue + (other.blue - blue) * fraction,
        alpha = alpha + (other.alpha - alpha) * fraction
    )
}
/**
 * Route thumbnail drawn as a curved coloured line on a dark map background.
 */
@Composable
fun RouteThumbnail(modifier: Modifier = Modifier, useDarkStyle: Boolean = LocalDarkMode.current) {
    Canvas(
        modifier = modifier.clip(RoundedCornerShape(10.dp))
            .background(if (useDarkStyle) Color(0xFF1A2236) else Color(0xFFDCEEDC))
    ) {
        // Grid
        val step = 28.dp.toPx()
        val gridColor = if (useDarkStyle) Color.White.copy(alpha = 0.04f) else Color.Black.copy(alpha = 0.05f)
        for (x in 0..(size.width / step).toInt() + 1)
            drawLine(gridColor, Offset(x * step, 0f), Offset(x * step, size.height))
        for (y in 0..(size.height / step).toInt() + 1)
            drawLine(gridColor, Offset(0f, y * step), Offset(size.width, y * step))

        // Route line
        val path = Path().apply {
            moveTo(size.width * 0.15f, size.height * 0.82f)
            cubicTo(
                size.width * 0.3f, size.height * 0.7f,
                size.width * 0.5f, size.height * 0.4f,
                size.width * 0.85f, size.height * 0.15f
            )
        }
        drawPath(path, Brush.linearGradient(listOf(SpeedGreen, StSecondary)), style = Stroke(3.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))

        // Start dot (green)
        drawCircle(SpeedGreen,  5.dp.toPx(), Offset(size.width * 0.15f, size.height * 0.82f))
        // End dot (red pin)
        drawCircle(SpeedRed,    5.dp.toPx(), Offset(size.width * 0.85f, size.height * 0.15f))
        drawCircle(Color.White, 2.5.dp.toPx(), Offset(size.width * 0.85f, size.height * 0.15f))
    }
}

// Legacy alias used by HistoryScreen
@Composable
fun LightRouteThumbnail(modifier: Modifier = Modifier) = RouteThumbnail(modifier, useDarkStyle = false)

/**
 * Elevation spark-line (used in trip route card).
 */
@Composable
fun ElevationSparkline(
    modifier: Modifier = Modifier,
    color: Color = StPrimary,
    points: List<Float> = listOf(0.4f, 0.5f, 0.45f, 0.6f, 0.7f, 0.65f, 0.8f)
) {
    Canvas(modifier = modifier) {
        if (points.size < 2) return@Canvas
        val xStep = size.width / (points.size - 1).toFloat()
        val path = Path().apply {
            moveTo(0f, size.height * (1f - points[0]))
            points.forEachIndexed { i, v ->
                if (i > 0) lineTo(i * xStep, size.height * (1f - v))
            }
        }
        // Fill
        val fillPath = Path().apply {
            addPath(path)
            lineTo((points.size - 1) * xStep, size.height)
            lineTo(0f, size.height)
            close()
        }
        drawPath(fillPath, color.copy(alpha = 0.15f))
        drawPath(path, color, style = Stroke(2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
}

/**
 * Shared glow CTA button used across all screens.
 */
@Composable
fun GlowButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = StPrimary,
    contentColor: Color = Color.White,
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .height(54.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(27.dp),
                spotColor = containerColor.copy(alpha = 0.6f)
            ),
        shape = RoundedCornerShape(27.dp),
        color = containerColor,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 28.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            content = content
        )
    }
}
