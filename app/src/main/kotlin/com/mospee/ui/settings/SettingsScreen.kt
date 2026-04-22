package com.mospee.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mospee.ui.theme.*
import com.mospee.utils.Constants

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenHome: () -> Unit = {},
    onOpenHistory: () -> Unit = {},
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val useKmh            by viewModel.useKmh.collectAsStateWithLifecycle()
    val darkMode          by viewModel.darkMode.collectAsStateWithLifecycle()
    val overspeedEnabled  by viewModel.overspeedEnabled.collectAsStateWithLifecycle()
    val overspeedThreshold by viewModel.overspeedThreshold.collectAsStateWithLifecycle()
    val gpsSmoothing      by viewModel.gpsSmoothing.collectAsStateWithLifecycle()
    val wifiSyncOnly      by viewModel.wifiSyncOnly.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            SettingsBottomNav(
                currentRoute = "settings",
                onHomeClick = onOpenHome,
                onHistoryClick = onOpenHistory
            )
        },
        containerColor = MospeeCream
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            SettingsTopBar(onBack = onBack)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ── Account ──────────────────────────────────────────────────────
                SettingsSectionHeader("Account")
                AccountInfoCard()

                // ── Display ──────────────────────────────────────────────────────
                SettingsSectionHeader("Display")

                SettingsToggleRow(
                    icon = Icons.Rounded.DarkMode,
                    title = "Dark Mode",
                    subtitle = "Use dark theme throughout the app",
                    checked = darkMode,
                    onToggle = { viewModel.setDarkMode(it) }
                )

                SettingsToggleRow(
                    icon = Icons.Rounded.Speed,
                    title = "Speed Units",
                    subtitle = if (useKmh) "Currently showing km/h" else "Currently showing mph",
                    checked = useKmh,
                    onToggle = { viewModel.setUseKmh(it) }
                )

                // ── Tracking ──────────────────────────────────────────────────────
                SettingsSectionHeader("Tracking")

                SettingsToggleRow(
                    icon = Icons.Rounded.MyLocation,
                    title = "GPS Smoothing",
                    subtitle = "Reduce jitter for a smoother path",
                    checked = gpsSmoothing,
                    onToggle = { viewModel.setGpsSmoothing(it) }
                )

                SettingsToggleRow(
                    icon = Icons.Rounded.NotificationsActive,
                    title = "Overspeed Alert",
                    subtitle = "Vibrate when over threshold",
                    checked = overspeedEnabled,
                    onToggle = { viewModel.setOverspeedEnabled(it) }
                )

                if (overspeedEnabled) {
                    OverspeedThresholdSlider(
                        threshold = overspeedThreshold,
                        useKmh = useKmh,
                        onValueChange = { viewModel.setOverspeedThreshold(it) }
                    )
                }

                // ── Cloud & Storage ────────────────────────────────────────────────
                SettingsSectionHeader("Cloud Storage")

                SettingsToggleRow(
                    icon = Icons.Rounded.Wifi,
                    title = "Sync over Wi-Fi only",
                    subtitle = "Save mobile data usage",
                    checked = wifiSyncOnly,
                    onToggle = { viewModel.setWifiSyncOnly(it) }
                )

                // ── About ────────────────────────────────────────────────────────
                SettingsSectionHeader("About")
                AboutCard()
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun AccountInfoCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(MospeeTerracotta),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    "Anonymous Pilot",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black.copy(alpha = 0.8f)
                )
                Text(
                    "Securely synced to Cloud",
                    style = MaterialTheme.typography.bodySmall,
                    color = MospeeTerracotta
                )
            }
        }
    }
}

@Composable
private fun AboutCard() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = MospeeTerracottaLight
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                "MOSPEE Premium v1.3.5",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "A premium GPS speedometer and trip tracker designed for high-performance automotive experiences.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Black.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                TextButton(onClick = {}) { Text("Rate App", color = MospeeTerracotta) }
                TextButton(onClick = {}) { Text("Share App", color = MospeeTerracotta) }
            }
        }
    }
}

@Composable
private fun SettingsTopBar(onBack: () -> Unit) {
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
                text = "Settings",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.Black.copy(alpha = 0.8f)
            )
            Text(
                text = "Customize your MOSPEE experience",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Black.copy(alpha = 0.4f)
            )
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MospeeTerracotta,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        letterSpacing = androidx.compose.ui.unit.TextUnit.Unspecified,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MospeeTerracottaLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MospeeTerracotta,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = Color.Black.copy(alpha = 0.8f))
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall,
                    color = Color.Black.copy(alpha = 0.4f))
            }
            Switch(
                checked = checked,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = MospeeTerracotta,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.LightGray.copy(alpha = 0.5f)
                )
            )
        }
    }
}

@Composable
private fun OverspeedThresholdSlider(
    threshold: Float,
    useKmh: Boolean,
    onValueChange: (Float) -> Unit
) {
    val displayValue = if (useKmh) threshold else threshold * Constants.KMH_TO_MPH
    val unit = if (useKmh) "km/h" else "mph"
    val range = if (useKmh) 30f..200f else 20f..124f

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.6f)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Overspeed Threshold", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color.Black.copy(alpha = 0.8f))
                Text(
                    text = "%.0f %s".format(displayValue, unit),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MospeeTerracotta
                )
            }
            Slider(
                value = displayValue,
                onValueChange = { newVal ->
                    val kmh = if (useKmh) newVal else newVal / Constants.KMH_TO_MPH
                    onValueChange(kmh)
                },
                valueRange = range,
                steps = 0,
                colors = SliderDefaults.colors(
                    thumbColor = MospeeTerracotta,
                    activeTrackColor = MospeeTerracotta,
                    inactiveTrackColor = MospeeTerracotta.copy(alpha = 0.1f)
                )
            )
        }
    }
}

@Composable
private fun SettingsBottomNav(
    currentRoute: String,
    onHomeClick: () -> Unit,
    onHistoryClick: () -> Unit
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
            BottomNavItem(Icons.Rounded.History, "History", currentRoute == "history", onHistoryClick)
            BottomNavItem(Icons.Rounded.Settings, "Settings", currentRoute == "settings", {})
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
