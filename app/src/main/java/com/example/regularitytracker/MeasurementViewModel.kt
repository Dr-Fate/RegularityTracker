package com.example.regularitytracker

import android.content.Context
import android.location.Location
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.abs

class MeasurementViewModel : ViewModel() {
    private var measurementStartTime: Long? = null
    private var timerJob: Job? = null
    private var locationTracker: LocationTracker? = null

    private val _elapsedTime = MutableStateFlow(0L)
    val elapsedTime: StateFlow<Long> = _elapsedTime

    private val _distanceKm = MutableStateFlow(0f)
    val distanceKm: StateFlow<Float> = _distanceKm

    private val _splitTimes = MutableStateFlow<List<Long>>(emptyList())
    val splitTimes: StateFlow<List<Long>> = _splitTimes

    private val _idealTimes = MutableStateFlow<List<Long>>(emptyList())
    val idealTimes: StateFlow<List<Long>> = _idealTimes

    private var lastLocation: Location? = null
    private var lastRecordedKm = 0

    private var targetSpeedKmh: Int = 60 // default
    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    val currentSpeed: Int
        get() = targetSpeedKmh

    fun setTargetSpeed(speed: Int) {
        targetSpeedKmh = speed
        _idealTimes.value = emptyList()
    }

    fun startMeasurement(context: Context) {
        if (_isRunning.value) return  // evitar reinicio si ya está corriendo
        _isRunning.value = true

        measurementStartTime = System.currentTimeMillis() - _elapsedTime.value
        lastRecordedKm = _splitTimes.value.size
        lastLocation = null

        timerJob = viewModelScope.launch {
            while (_isRunning.value) {
                delay(1000)
                measurementStartTime?.let {
                    _elapsedTime.value = System.currentTimeMillis() - it
                }
            }
        }

        locationTracker = LocationTracker(context) { location ->
            val previous = lastLocation
            lastLocation = location

            if (previous != null) {
                val distanceDelta = previous.distanceTo(location)
                val speedKmh = (location.speed * 3.6).toFloat()

                if (distanceDelta > 3f && speedKmh >= 12f) {
                    val totalDistance = _distanceKm.value + distanceDelta / 1000f
                    _distanceKm.value = totalDistance

                    val fullKm = totalDistance.toInt()
                    if (fullKm > lastRecordedKm) {
                        lastRecordedKm = fullKm
                        val currentTime = System.currentTimeMillis() - (measurementStartTime ?: 0L)
                        _splitTimes.value = _splitTimes.value + currentTime

                        val secondsPerKm = 3600_000.0 / targetSpeedKmh
                        _idealTimes.value = _idealTimes.value + (fullKm * secondsPerKm).toLong()
                    }
                }
            }
        }
        locationTracker?.start()
    }

    fun stopMeasurement() {
        _isRunning.value = false
        timerJob?.cancel()
        locationTracker?.stop()
    }

    fun resetMeasurement(context: Context) {
        stopMeasurement()
        _elapsedTime.value = 0L
        _distanceKm.value = 0f
        _splitTimes.value = emptyList()
        _idealTimes.value = emptyList()
        measurementStartTime = null
        lastRecordedKm = 0
        lastLocation = null
        startMeasurement(context)
    }

    fun addManualSplit() {
        measurementStartTime?.let { startTime ->
            val currentTime = System.currentTimeMillis() - startTime
            _splitTimes.value = _splitTimes.value + currentTime

            val nextKm = _splitTimes.value.size
            val secondsPerKm = 3600_000.0 / targetSpeedKmh
            _idealTimes.value = _idealTimes.value + (nextKm * secondsPerKm).toLong()
        }
    }

    fun pauseMeasurement() {
        stopMeasurement()
    }

    fun resumeMeasurement(context: Context) {
        startMeasurement(context)
    }
    fun exportToCsv(context: Context) {
        val headers = "Kilómetro,Medido,Ideal,Diferencia"
        val rows = _splitTimes.value.mapIndexed { index, split ->
            val ideal = _idealTimes.value.getOrNull(index)
            val difference = if (ideal != null) split - ideal else null

            val diffFormatted = when {
                difference == null -> "-"
                difference == 0L -> "00:00,0"
                difference > 0L -> "+${formatTime(difference)}"
                else -> "-${formatTime(abs(difference))}"
            }

            val splitFormatted = formatTime(split)
            val idealFormatted = ideal?.let { formatTime(it) } ?: "-"

            "${index + 1},$splitFormatted,$idealFormatted,$diffFormatted"
        }

        val csvContent = (listOf(headers) + rows).joinToString("\n")

        val filename = "medicion_${System.currentTimeMillis()}.csv"
        val file = File(context.getExternalFilesDir(null), filename)

        file.writeText(csvContent)

        Toast.makeText(context, "Exportado como $filename", Toast.LENGTH_SHORT).show()
    }

}
