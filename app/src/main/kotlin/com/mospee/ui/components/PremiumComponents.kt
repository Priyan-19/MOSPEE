package com.mospee.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.fillMaxHeight
import com.mospee.ui.theme.MapBackdropGradient
import com.mospee.ui.theme.MospeeAmber
import com.mospee.ui.theme.MospeeBlue
import com.mospee.ui.theme.MospeeBlueBright
import com.mospee.ui.theme.MospeeGreen
import com.mospee.ui.theme.MospeeOutline
import com.mospee.ui.theme.MospeeRed
import com.mospee.ui.theme.MospeeSurface
import com.mospee.ui.theme.MospeeSurfaceAlt
import com.mospee.ui.theme.MospeeTerracotta
import com.mospee.ui.theme.MospeeTextPrimary
import com.mospee.ui.theme.MospeeTextSecondary
import com.mospee.ui.theme.MospeeWhite

val DarkMapStyleJson = """
[
  {
    "elementType": "geometry",
    "stylers": [{ "color": "#141824" }]
  },
  {
    "elementType": "labels.text.fill",
    "stylers": [{ "color": "#8b95a7" }]
  },
  {
    "elementType": "labels.text.stroke",
    "stylers": [{ "color": "#141824" }]
  },
  {
    "featureType": "administrative",
    "elementType": "geometry.stroke",
    "stylers": [{ "color": "#2d3443" }]
  },
  {
    "featureType": "poi",
    "elementType": "geometry",
    "stylers": [{ "color": "#1a2030" }]
  },
  {
    "featureType": "poi.park",
    "elementType": "geometry",
    "stylers": [{ "color": "#14271f" }]
  },
  {
    "featureType": "road",
    "elementType": "geometry",
    "stylers": [{ "color": "#1f2737" }]
  },
  {
    "featureType": "road",
    "elementType": "geometry.stroke",
    "stylers": [{ "color": "#111722" }]
  },
  {
    "featureType": "road.highway",
    "elementType": "geometry",
    "stylers": [{ "color": "#273246" }]
  },
  {
    "featureType": "transit",
    "elementType": "geometry",
    "stylers": [{ "color": "#1d2532" }]
  },
  {
    "featureType": "water",
    "elementType": "geometry",
    "stylers": [{ "color": "#0d1728" }]
  }
]
""".trimIndent()

@Composable
fun Speedometer3D(
    speed: Float,
    maxSpeed: Float = 160f,
    unit: String = "km/h",
    modifier: Modifier = Modifier
) {
    val transition = updateTransition(targetState = speed.coerceIn(0f, maxSpeed), label = "speed")
    val animatedSpeed by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 600, easing = FastOutSlowInEasing) },
        label = "animated_speed"
    ) { it }

    Box(
        modifier = modifier.size(240.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer Glow Circle
        Surface(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.05f),
            border = BorderStroke(2.dp, Color.White.copy(alpha = 0.1f))
        ) {}

        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 12.dp.toPx()
            val startAngle = 135f
            val sweep = 270f
            val topLeft = Offset(stroke * 2, stroke * 2)
            val arcSize = Size(size.width - stroke * 4, size.height - stroke * 4)

            // Background Arc
            drawArc(
                color = Color.Black.copy(alpha = 0.05f),
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            // Speed Arc
            drawArc(
                brush = Brush.sweepGradient(
                    0.0f to MospeeTerracotta.copy(alpha = 0.3f),
                    0.5f to MospeeTerracotta,
                    1.0f to MospeeTerracotta
                ),
                startAngle = startAngle,
                sweepAngle = sweep * (animatedSpeed / maxSpeed),
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )

            // Indicator Dots
            val radius = arcSize.width / 2
            val center = Offset(size.width / 2, size.height / 2)
            for (i in 0..10) {
                val angle = startAngle + (sweep / 10) * i
                val rad = Math.toRadians(angle.toDouble())
                val x = center.x + (radius + 20f) * Math.cos(rad).toFloat()
                val y = center.y + (radius + 20f) * Math.sin(rad).toFloat()
                drawCircle(
                    color = if (animatedSpeed / maxSpeed >= i / 10f) MospeeTerracotta else Color.LightGray.copy(alpha = 0.3f),
                    radius = 2.dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = speed.toInt().toString(),
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 72.sp),
                color = Color.Black.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = unit.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = MospeeTerracotta,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        }
    }
}

@Composable
fun DigitalSpeedometer(
    speed: Float,
    unit: String = "km/h",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = speed.toInt().toString(),
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 120.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-4).sp
                    ),
                    color = Color.Black.copy(alpha = 0.85f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = unit,
                    style = MaterialTheme.typography.titleLarge,
                    color = MospeeTerracotta,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }
            
            // Speed bar
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.05f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth( (speed / 160f).coerceIn(0f, 1f) )
                        .fillMaxHeight()
                        .background(MospeeTerracotta)
                )
            }
        }
    }
}

@Composable
fun GlassmorphicCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(20.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.shadow(
            elevation = 14.dp,
            shape = shape,
            ambientColor = MospeeBlue.copy(alpha = 0.18f),
            spotColor = Color.Black
        ),
        shape = shape,
        color = MospeeSurface.copy(alpha = 0.94f),
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .clip(shape)
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.06f),
                            Color.Transparent
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.06f),
                    shape = shape
                )
                .padding(20.dp),
            content = content
        )
    }
}

@Composable
fun PremiumButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = MospeeBlue,
    contentColor: Color = MospeeWhite,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(56.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(50.dp),
                ambientColor = containerColor.copy(alpha = 0.3f),
                spotColor = containerColor.copy(alpha = 0.4f)
            ),
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        contentPadding = ButtonDefaults.ContentPadding
    ) {
        content()
    }
}

@Composable
fun GlowButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = com.mospee.ui.theme.MospeeTerracotta,
    contentColor: Color = Color.White,
    glowColor: Color = com.mospee.ui.theme.MospeeTerracotta.copy(alpha = 0.4f),
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .height(64.dp)
            .shadow(
                elevation = 16.dp,
                shape = RoundedCornerShape(32.dp),
                spotColor = glowColor,
                ambientColor = glowColor
            ),
        shape = RoundedCornerShape(32.dp),
        color = containerColor,
        contentColor = contentColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            content = content
        )
    }
}

@Composable
fun TripActiveOverlay(
    elapsedTime: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = com.mospee.ui.theme.MospeeBadgeOrange.copy(alpha = 0.95f),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "TRIP ACTIVE",
                style = MaterialTheme.typography.labelMedium,
                color = com.mospee.ui.theme.MospeeTerracotta,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Time: $elapsedTime",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Black.copy(alpha = 0.8f),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PremiumStatItem(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = MospeeTextPrimary,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MospeeTextSecondary
        )
    }
}

@Composable
fun LiveBadge(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "live")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "live_alpha"
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .background(MospeeRed.copy(alpha = 0.16f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(MospeeRed.copy(alpha = alpha))
        )
        Text(
            text = "LIVE",
            style = MaterialTheme.typography.labelMedium,
            color = MospeeWhite
        )
    }
}

@Composable
fun RouteThumbnail(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Brush.verticalGradient(listOf(Color(0xFF101724), Color(0xFF1A2434))))
    ) {
        drawRoundRect(
            color = Color.White.copy(alpha = 0.03f),
            size = size,
            cornerRadius = CornerRadius(18.dp.toPx(), 18.dp.toPx())
        )

        val routePath = Path().apply {
            moveTo(size.width * 0.18f, size.height * 0.72f)
            cubicTo(
                size.width * 0.26f,
                size.height * 0.48f,
                size.width * 0.42f,
                size.height * 0.86f,
                size.width * 0.56f,
                size.height * 0.46f
            )
            cubicTo(
                size.width * 0.64f,
                size.height * 0.28f,
                size.width * 0.82f,
                size.height * 0.34f,
                size.width * 0.86f,
                size.height * 0.18f
            )
        }

        drawPath(
            path = routePath,
            color = MospeeBlueBright,
            style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
        )

        drawCircle(
            color = MospeeGreen,
            radius = 6.dp.toPx(),
            center = Offset(size.width * 0.18f, size.height * 0.72f)
        )
        drawCircle(
            color = MospeeRed,
            radius = 6.dp.toPx(),
            center = Offset(size.width * 0.86f, size.height * 0.18f)
        )
    }
}

@Composable
fun SpeedWatermark(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val stroke = 14.dp.toPx()
        drawArc(
            color = MospeeBlue.copy(alpha = 0.18f),
            startAngle = 155f,
            sweepAngle = 230f,
            useCenter = false,
            style = Stroke(width = stroke, cap = StrokeCap.Round)
        )
        drawArc(
            color = MospeeGreen.copy(alpha = 0.14f),
            startAngle = 210f,
            sweepAngle = 90f,
            useCenter = false,
            style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
fun MapScrim(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.background(MapBackdropGradient)
    )
}

@Composable
fun StatusPill(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MospeeSurface.copy(alpha = 0.88f),
    contentColor: Color = MospeeTextPrimary
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50.dp))
            .background(containerColor)
            .border(1.dp, MospeeOutline.copy(alpha = 0.6f), RoundedCornerShape(50.dp))
            .padding(horizontal = 14.dp, vertical = 9.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = contentColor
        )
    }
}

@Composable
fun MetricCardShell(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MospeeSurfaceAlt.copy(alpha = 0.96f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            content = content
        )
    }
}

@Composable
fun SmallLegendDot(color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
fun AppBrandBadge(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(40.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(com.mospee.ui.theme.MospeeSurface),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.size(24.dp)) {
            val pinPath = Path().apply {
                moveTo(size.width / 2, size.height)
                cubicTo(0f, size.height * 0.4f, 0f, 0f, size.width / 2, 0f)
                cubicTo(size.width, 0f, size.width, size.height * 0.4f, size.width / 2, size.height)
            }
            drawPath(
                path = pinPath,
                color = com.mospee.ui.theme.MospeeTerracotta
            )
            drawCircle(
                color = Color.White,
                radius = 4.dp.toPx(),
                center = Offset(size.width / 2, size.height * 0.35f)
            )
        }
    }
}

@Composable
fun AccentText(value: String, modifier: Modifier = Modifier, color: Color = MospeeBlueBright) {
    Text(
        text = value,
        modifier = modifier,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = color
    )
}
