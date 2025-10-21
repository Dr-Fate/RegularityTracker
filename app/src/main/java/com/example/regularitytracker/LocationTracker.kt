package com.example.regularitytracker

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle

class LocationTracker(
    private val context: Context,
    private val onNewLocation: (Location) -> Unit
) {
    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            onNewLocation(location)
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    private var isTracking = false

    @SuppressLint("MissingPermission")
    fun start() {
        if (!isTracking) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000L, // cada 1 segundo
                0f,    // al menos 0 metros
                locationListener
            )
            isTracking = true
        }
    }

    fun stop() {
        if (isTracking) {
            locationManager.removeUpdates(locationListener)
            isTracking = false
        }
    }
}

