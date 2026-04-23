package com.mospee.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

@Composable
fun OpenStreetMapView(
    modifier: Modifier = Modifier,
    center: GeoPoint = GeoPoint(12.9716, 77.5946),
    zoom: Double = 15.0,
    mapType: String = "default", // default, satellite, terrain
    userLocation: GeoPoint? = null,
    startPoint: GeoPoint? = null,
    endPoint: GeoPoint? = null,
    routePoints: List<GeoPoint> = emptyList(),
    showRouteMarkers: Boolean = false,
    followCenter: Boolean = false,
    enableGestures: Boolean = true
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    // Remember overlays to avoid recreation
    val userMarker = remember { 
        Marker(MapView(context)).apply {
            icon = context.getDrawable(com.mospee.R.drawable.ic_location_marker)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "Current Location"
        }
    }

    val startMarker = remember {
        Marker(MapView(context)).apply {
            icon = context.getDrawable(com.mospee.R.drawable.ic_location_marker)?.apply {
                setTint(android.graphics.Color.parseColor("#00C853")) // Green for Start
            }
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "Start Point"
        }
    }

    val endMarker = remember {
        Marker(MapView(context)).apply {
            icon = context.getDrawable(com.mospee.R.drawable.ic_location_marker)?.apply {
                setTint(android.graphics.Color.parseColor("#E53935")) // Red for End
            }
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            title = "End Point"
        }
    }
    
    val routePolyline = remember {
        Polyline().apply {
            outlinePaint.color = android.graphics.Color.parseColor("#1A73E8")
            outlinePaint.strokeWidth = 14f
        }
    }

    val mapView = remember {
        MapView(context).apply {
            setMultiTouchControls(enableGestures)
            isTilesScaledToDpi = true
            zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER)
            
            // Add persistent overlays once
            overlays.add(routePolyline)
            overlays.add(startMarker)
            overlays.add(endMarker)
            overlays.add(userMarker)
        }
    }

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) { mapView.onResume() }
            override fun onPause(owner: LifecycleOwner) { mapView.onPause() }
            override fun onDestroy(owner: LifecycleOwner) { mapView.onDetach() }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { mapView },
        update = { view ->
            // 1. Update Tile Source ONLY if changed
            val targetSource = when (mapType) {
                "satellite" -> TileSourceFactory.USGS_SAT
                "terrain" -> TileSourceFactory.USGS_TOPO
                else -> TileSourceFactory.MAPNIK
            }
            if (view.tileProvider.tileSource.name() != targetSource.name()) {
                view.setTileSource(targetSource)
            }

            // 2. Update Map State
            if (view.zoomLevelDouble != zoom) {
                view.controller.setZoom(zoom)
            }
            view.setMultiTouchControls(enableGestures)

            // 3. Update Route Polyline
            if (routePoints.isNotEmpty()) {
                routePolyline.setPoints(routePoints)
                routePolyline.isEnabled = true
            } else {
                routePolyline.isEnabled = false
            }

            // 4. Update Markers
            
            // Start Marker
            if (startPoint != null) {
                startMarker.position = startPoint
                startMarker.isEnabled = true
            } else {
                startMarker.isEnabled = false
            }

            // End Marker
            if (endPoint != null) {
                endMarker.position = endPoint
                endMarker.isEnabled = true
            } else {
                endMarker.isEnabled = false
            }

            // User Location Marker (Live)
            if (userLocation != null) {
                userMarker.position = userLocation
                userMarker.isEnabled = true
                
                if (followCenter) {
                    view.controller.animateTo(userLocation)
                }
            } else {
                userMarker.isEnabled = false
                if (routePoints.isEmpty() && center != null) {
                    view.controller.setCenter(center)
                }
            }

            // 5. Handle Bounding Box to fit route
            if (routePoints.size > 1 && !followCenter) {
                view.post {
                    try {
                        val boundingBox = BoundingBox.fromGeoPoints(routePoints)
                        view.zoomToBoundingBox(boundingBox, true, 100)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            view.invalidate()
        }
    )
}
