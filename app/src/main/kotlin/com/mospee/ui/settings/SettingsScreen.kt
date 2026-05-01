package com.mospee.ui.settings

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mospee.ui.home.HomeViewModel
import com.mospee.ui.home.SpeedoBottomNav
import com.mospee.ui.components.GlassyCard
import com.mospee.ui.theme.*

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenHome: () -> Unit,
    onOpenHistory: () -> Unit,
    onOpenStats: () -> Unit,
    onOpenLive: () -> Unit,
    viewModel: HomeViewModel = androidx.hilt.navigation.compose.hiltViewModel()
) {
    val useKmh          by viewModel.useKmh.collectAsStateWithLifecycle()
    val meterType       by viewModel.meterType.collectAsStateWithLifecycle()
    val overspeedEnabled by viewModel.overspeedEnabled.collectAsStateWithLifecycle()
    val darkMode        by viewModel.darkMode.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            SpeedoBottomNav(
                currentRoute    = "settings",
                onHomeClick     = onOpenHome,
                onLiveClick     = onOpenLive,
                onHistoryClick  = onOpenHistory,
                onStatsClick    = onOpenStats,
                onSettingsClick = { }
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack, modifier = Modifier.size(38.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))) {
                        Icon(Icons.Rounded.ChevronLeft, null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text("Settings", style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Unit Preference ──────────────────────────────────────────────
            SettingsSection("UNIT PREFERENCE") {
                // Speed unit toggle buttons (km/h | mph)
                SettingsContent(
                    title = "Speed Unit",
                    subtitle = "Choose between km/h and mph",
                    icon = Icons.Rounded.Speed,
                    iconColor = StPrimary
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        UnitToggleButton(label = "km/h", selected = useKmh,  onClick = { viewModel.setUseKmh(true) })
                        UnitToggleButton(label = "mph",  selected = !useKmh, onClick = { viewModel.setUseKmh(false) })
                    }
                }

                HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(horizontal = 16.dp))

                // Distance unit
                SettingsContent(
                    title = "Distance Unit",
                    subtitle = "km or miles display",
                    icon = Icons.Rounded.Route,
                    iconColor = StSecondary
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        UnitToggleButton(label = "km", selected = useKmh,  onClick = { viewModel.setUseKmh(true) })
                        UnitToggleButton(label = "mi", selected = !useKmh, onClick = { viewModel.setUseKmh(false) })
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Tracking ─────────────────────────────────────────────────────
            SettingsSection("TRACKING") {
                SettingsToggleRow(
                    title = "Overspeed Alerts",
                    subtitle = "Visual warning when exceeding limit",
                    icon = Icons.Rounded.NotificationImportant,
                    iconColor = StError,
                    checked = overspeedEnabled,
                    onCheckedChange = { viewModel.setOverspeedEnabled(it) },
                    thumbColor = StError
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(horizontal = 16.dp))
                SettingsContent(
                    title = "Meter Style",
                    subtitle = "Current: ${meterType.replaceFirstChar { it.uppercase() }}",
                    icon = Icons.Rounded.DialerSip,
                    iconColor = StAccent
                ) {
                    Button(
                        onClick = { viewModel.toggleMeterType() },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurface),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Text("Switch", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Appearance ───────────────────────────────────────────────────
            SettingsSection("APPEARANCE") {
                SettingsToggleRow(
                    title = "Dark Mode",
                    subtitle = "Dark background for night driving",
                    icon = Icons.Rounded.DarkMode,
                    iconColor = StPurple,
                    checked = darkMode,
                    onCheckedChange = { viewModel.setDarkMode(it) },
                    thumbColor = StPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── About ────────────────────────────────────────────────────────
            SettingsSection("ABOUT") {
                SettingsContent(
                    title = "App Version",
                    subtitle = "MOSPEE v2.5.0",
                    icon = Icons.Rounded.Info,
                    iconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                ) {
                    Text("LATEST", style = MaterialTheme.typography.labelSmall, color = StPrimary, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            title, style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f), fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp, modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
        )
        GlassyCard(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}

@Composable
private fun SettingsToggleRow(
    title: String, subtitle: String,
    icon: ImageVector, iconColor: Color,
    checked: Boolean, onCheckedChange: (Boolean) -> Unit,
    thumbColor: Color
) {
    SettingsContent(title, subtitle, icon, iconColor) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor  = thumbColor,
                checkedTrackColor  = thumbColor.copy(alpha = 0.3f),
                uncheckedThumbColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )
    }
}

@Composable
private fun SettingsContent(
    title: String, subtitle: String,
    icon: ImageVector, iconColor: Color,
    action: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(38.dp).background(iconColor.copy(0.12f), CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = iconColor, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        action()
    }
}

@Composable
private fun UnitToggleButton(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(8.dp),
        color = if (selected) StPrimary else MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, if (selected) StPrimary else MaterialTheme.colorScheme.outline),
        modifier = Modifier.height(32.dp)
    ) {
        Box(modifier = Modifier.padding(horizontal = 14.dp), contentAlignment = Alignment.Center) {
            Text(
                label,
                style = MaterialTheme.typography.labelMedium,
                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}
