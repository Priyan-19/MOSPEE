package com.mospee

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.mospee.data.repository.UserPreferencesRepository
import com.mospee.ui.navigation.AppNavGraph
import com.mospee.ui.theme.*
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var prefsRepository: UserPreferencesRepository

    @Inject
    lateinit var firebaseManager: com.mospee.data.remote.FirebaseManager

    @Inject
    lateinit var tripRepository: com.mospee.domain.repository.TripRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            val darkMode by prefsRepository.darkMode.collectAsStateWithLifecycle(initialValue = true)
            var startupError by remember { mutableStateOf<String?>(null) }

            MOSPEETheme(darkTheme = darkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (startupError != null) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("Startup Error: ${startupError}", color = MospeeRed, modifier = Modifier.padding(20.dp))
                        }
                    } else {
                        PermissionGate {
                            val navController = rememberNavController()
                            AppNavGraph(navController = navController)
                            
                            // Perform background tasks safely
                            LaunchedEffect(Unit) {
                                try {
                                    firebaseManager.signInAnonymously()
                                    tripRepository.syncPendingTrips()
                                } catch (e: Exception) {
                                    startupError = "Cloud Sync Error: ${e.message}"
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionGate(content: @Composable () -> Unit) {
    val requiredPermissions = buildList {
        add(Manifest.permission.ACCESS_FINE_LOCATION)
        add(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val permissionsState = rememberMultiplePermissionsState(requiredPermissions)

    LaunchedEffect(Unit) {
        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        }
    }

    if (permissionsState.allPermissionsGranted) {
        content()
    } else {
        // Show a premium loading indicator while waiting for permission or processing
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MospeeCream),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MospeeTerracotta)
        }
    }
}


