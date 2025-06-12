package com.example.regularitytracker

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.GnssStatus
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.regularitytracker.ui.theme.RegularityTrackerTheme

class MainActivity : ComponentActivity() {

    private lateinit var locationManager: LocationManager

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        super.onCreate(savedInstanceState)

        // Pedir permisos
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        }

        // Usamos rememberUpdatedState dentro de setContent, pero necesitamos un state global para satelliteCount
        val satelliteCountState = mutableStateOf(0)

        val locationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) initLocation(satelliteCountState)
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            initLocation(satelliteCountState)
        }

        setContent {
            RegularityTrackerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(
                        satelliteCount = satelliteCountState,
                        appContext = application
                    )
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun initLocation(satelliteCount: MutableState<Int>) {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        // Fuerza a mantener GPS activo
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            2000L,
            0f,
            object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    // Solo fuerza GPS
                }
            },
            Looper.getMainLooper()
        )

        // Registrar escucha de satélites
        locationManager.registerGnssStatusCallback(object : GnssStatus.Callback() {
            override fun onSatelliteStatusChanged(status: GnssStatus) {
                val usedSatellites = (0 until status.satelliteCount).count {
                    status.usedInFix(it)
                }
                satelliteCount.value = usedSatellites
            }
        })
    }
}
