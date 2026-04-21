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
    zoom: Double = 14.0,
    routePoints: List<GeoPoint> = emptyList(),
    showRouteMarkers: Boolean = false,
    followCenter: Boolean = false,
    enableGestures: Boolean = true
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mapView = remember {
        MapView(context).apply {
            setTileSource(TileSourceFactory.MAPNIK)
            setMultiTouchControls(enableGestures)
            controller.setZoom(zoom)
            controller.setCenter(center)
            isTilesScaledToDpi = true
            zoomController.setVisibility(org.osmdroid.views.CustomZoomButtonsController.Visibility.NEVER)
        }
    }

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                mapView.onResume()
            }

            override fun onPause(owner: LifecycleOwner) {
                mapView.onPause()
            }

            override fun onDestroy(owner: LifecycleOwner) {
                mapView.onDetach()
            }
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
            view.setMultiTouchControls(enableGestures)
            view.overlays.clear()

            if (routePoints.isNotEmpty()) {
                val polyline = Polyline().apply {
                    setPoints(routePoints)
                    outlinePaint.color = android.graphics.Color.parseColor("#1A73E8")
                    outlinePaint.strokeWidth = 14f
                }
                view.overlays.add(polyline)

                if (showRouteMarkers) {
                    val startMarker = Marker(view).apply {
                        position = routePoints.first()
                        title = "Start"
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    }
                    val endMarker = Marker(view).apply {
                        position = routePoints.last()
                        title = "Current location"
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    }
                    view.overlays.add(startMarker)
                    view.overlays.add(endMarker)
                }

                if (followCenter) {
                    view.controller.animateTo(routePoints.last())
                } else if (routePoints.size > 1) {
                    val boundingBox = BoundingBox.fromGeoPoints(routePoints)
                    view.zoomToBoundingBox(boundingBox, true, 96)
                }
            } else {
                view.controller.setZoom(zoom)
                view.controller.setCenter(center)
            }

            view.invalidate()
        }
    )
}
