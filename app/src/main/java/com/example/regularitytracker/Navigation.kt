package com.example.regularitytracker

import androidx.compose.runtime.*
import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*

@Composable
fun AppNavigation(
    satelliteCount: State<Int>,
    appContext: Application
) {
    val navController = rememberNavController()
    val measurementViewModel: MeasurementViewModel = viewModel()

    val idealTimes by measurementViewModel.idealTimes.collectAsState()

    NavHost(navController = navController, startDestination = "main") {
        composable("main") {
            MainScreen(
                satelliteCount = satelliteCount.value,
                selectedSpeed = measurementViewModel.currentSpeed, // ✅ getter público
                onSpeedChange = { measurementViewModel.setTargetSpeed(it) },
                onStartClick = {
                    navController.navigate("measurement")
                }
            )
        }
        composable("measurement") {
            val context = LocalContext.current
            MeasurementScreen(
                satelliteCount = satelliteCount.value,
                elapsedTime = measurementViewModel.elapsedTime,
                distanceKm = measurementViewModel.distanceKm,
                splitTimes = measurementViewModel.splitTimes,
                idealTimes = idealTimes, // ✅ sin .value directamente
                onStopClick = { measurementViewModel.pauseMeasurement() },
                onResetClick = { measurementViewModel.resetMeasurement(context) },
                onAddManualSplit = { measurementViewModel.addManualSplit() },
                viewModel = measurementViewModel,
                context = context
            )
        }
    }
}