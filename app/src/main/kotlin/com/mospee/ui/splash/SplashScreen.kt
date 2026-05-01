package com.mospee.ui.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mospee.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {

    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing)),
        label = "rotate"
    )
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label = "glow"
    )

    var showTagline by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(500); showTagline = true
        delay(2000); onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Color(0xFF0D1117), Color(0xFF0A1628), Color(0xFF091A20)))
            ),
        contentAlignment = Alignment.Center
    ) {
        // Background constellation dots
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stars = listOf(
                Offset(size.width * 0.08f, size.height * 0.1f),
                Offset(size.width * 0.92f, size.height * 0.12f),
                Offset(size.width * 0.15f, size.height * 0.85f),
                Offset(size.width * 0.88f, size.height * 0.8f),
                Offset(size.width * 0.5f, size.height * 0.05f),
                Offset(size.width * 0.3f, size.height * 0.92f),
                Offset(size.width * 0.72f, size.height * 0.95f),
            )
            stars.forEach { s -> drawCircle(Color.White.copy(alpha = 0.25f), 1.5.dp.toPx(), s) }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated speedometer ring
            Box(
                modifier = Modifier.size(140.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cx = size.width / 2; val cy = size.height / 2
                    val r = size.minDimension / 2 - 10.dp.toPx()

                    // Outer glow ring
                    drawCircle(StPrimary.copy(alpha = glowAlpha * 0.15f), r + 12.dp.toPx(), Offset(cx, cy))

                    // Track
                    drawArc(StOutline.copy(alpha = 0.5f), 135f, 270f, false, style = Stroke(10.dp.toPx(), cap = StrokeCap.Round))

                    // Animated coloured arc
                    drawArc(
                        Brush.sweepGradient(listOf(SpeedGreen, SpeedYellow, SpeedOrange, SpeedRed, SpeedGreen), Offset(cx, cy)),
                        135f, 270f, false, style = Stroke(10.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Rotating needle dot
                    val rad = Math.toRadians((rotationAngle + 135).toDouble())
                    val dotPos = Offset(cx + (r - 14.dp.toPx()) * cos(rad).toFloat(), cy + (r - 14.dp.toPx()) * sin(rad).toFloat())
                    drawCircle(Color.White, 5.dp.toPx(), dotPos)
                    drawCircle(StPrimary, 3.dp.toPx(), dotPos)
                }

                // Icon in centre
                Box(modifier = Modifier.size(56.dp).background(StSurface, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Rounded.Speed, null, tint = StPrimary, modifier = Modifier.size(28.dp))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Brand name
                Text(
                    "MOSPEE",
                    fontWeight = FontWeight.Black,
                    fontSize = 32.sp,
                    color = StPrimary,
                    letterSpacing = (-0.5).sp
                )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Track. Analyze. Improve.",
                style = MaterialTheme.typography.bodyMedium,
                color = StTextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Loading dots
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) { i ->
                    val a by infiniteTransition.animateFloat(
                        initialValue = 0.3f, targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            tween(600, delayMillis = i * 150),
                            RepeatMode.Reverse
                        ),
                        label = "dot$i"
                    )
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(StPrimary.copy(alpha = a), CircleShape)
                    )
                }
            }
        }
    }
}
