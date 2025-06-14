package com.example.regularitytracker

import android.app.Application
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*

sealed class Screen(val route: String) {
    object FakeEditor : Screen("fake_editor")
    object MainScreen : Screen("main")
    object MeasurementScreen : Screen("measurement")
}

@Composable
fun AppNavigation(
    satelliteCount: State<Int>,
    appContext: Application
) {
    val navController = rememberNavController()
    val measurementViewModel: MeasurementViewModel = viewModel()
    val idealTimes by measurementViewModel.idealTimes.collectAsState()

    NavHost(navController = navController, startDestination = Screen.FakeEditor.route) {
        // 🕵️ Pantalla inicial de "PhotoFilter"
        composable(Screen.FakeEditor.route) {
            FakeEditorScreen(
                onPinValidated = {
                    navController.navigate(Screen.MainScreen.route) {
                        popUpTo(Screen.FakeEditor.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.MainScreen.route) {
            MainScreen(
                satelliteCount = satelliteCount.value,
                selectedSpeed = measurementViewModel.currentSpeed,
                onSpeedChange = { measurementViewModel.setTargetSpeed(it) },
                onStartClick = {
                    navController.navigate(Screen.MeasurementScreen.route)
                }
            )
        }

        composable(Screen.MeasurementScreen.route) {
            val context = LocalContext.current
            MeasurementScreen(
                satelliteCount = satelliteCount.value,
                elapsedTime = measurementViewModel.elapsedTime,
                distanceKm = measurementViewModel.distanceKm,
                splitTimes = measurementViewModel.splitTimes,
                idealTimes = idealTimes,
                onStopClick = { measurementViewModel.pauseMeasurement() },
                onResetClick = { measurementViewModel.resetMeasurement(context) },
                onAddManualSplit = { measurementViewModel.addManualSplit() },
                viewModel = measurementViewModel,
                context = context
            )
        }
    }
}
