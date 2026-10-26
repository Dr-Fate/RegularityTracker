package com.example.regularitytracker

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.*

class LocationTracker(
    private val context: Context,
    private val onNewLocation: (Location) -> Unit
) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.lastLocation?.let { location ->
                onNewLocation(location)
            }
        }
    }

    private var isTracking = false

    @SuppressLint("MissingPermission")
    fun start() {
        if (!isTracking) {
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                1000L // Intervalo exacto de 1 segundo
            ).apply {
                setMinUpdateIntervalMillis(1000L)
                setWaitForAccurateLocation(true)
            }.build()

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            isTracking = true
        }
    }

    fun stop() {
        if (isTracking) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            isTracking = false
        }
    }
}
