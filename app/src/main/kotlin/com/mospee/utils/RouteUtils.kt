package com.mospee.utils

import com.google.maps.android.PolyUtil
import com.mospee.data.local.entity.LocationPointEntity
import org.osmdroid.util.GeoPoint

object RouteUtils {

    /**
     * Encodes a list of location points into a Google Polyline string.
     */
    fun encodeRoute(points: List<LocationPointEntity>): String {
        if (points.isEmpty()) return ""
        val latLngs = points.map { 
            com.google.android.gms.maps.model.LatLng(it.latitude, it.longitude) 
        }
        return PolyUtil.encode(latLngs)
    }

    /**
     * Decodes a Google Polyline string back into a list of GeoPoints.
     */
    fun decodeRoute(encoded: String): List<GeoPoint> {
        if (encoded.isEmpty()) return emptyList()
        return PolyUtil.decode(encoded).map { 
            GeoPoint(it.latitude, it.longitude)
        }
    }
}
