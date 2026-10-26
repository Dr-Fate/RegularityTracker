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
import android.media.AudioAttributes
import android.media.SoundPool

class MeasurementViewModel : ViewModel() {
    private var soundPool: SoundPool? = null
    private var beepSoundId: Int? = null

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

    private var targetSpeedKmh: Int = 60
    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    val currentSpeed: Int
        get() = targetSpeedKmh

    // --- Lógica de estabilización inicial ---
    private var consecutiveGoodFixes = 0
    private val requiredGoodFixes = 5
    private val minStableSpeedKmh = 12.0
    private val maxAccuracyMeters = 18f

    private val _isStabilized = MutableStateFlow(false)
    val isStabilized: StateFlow<Boolean> = _isStabilized

    private val _stabilizationProgress = MutableStateFlow(0)
    val stabilizationProgress: StateFlow<Int> = _stabilizationProgress

    fun setTargetSpeed(speed: Int) {
        targetSpeedKmh = speed
        _idealTimes.value = emptyList()
    }

    fun startMeasurement(context: Context) {
        soundPool = SoundPool.Builder()
            .setMaxStreams(3)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .build()

        beepSoundId = soundPool?.load(context, R.raw.beep, 1)

        if (_isRunning.value) return
        _isRunning.value = true

        measurementStartTime = null // Aún no arranca el conteo real
        lastRecordedKm = _splitTimes.value.size
        lastLocation = null
        consecutiveGoodFixes = 0
        _isStabilized.value = false
        _stabilizationProgress.value = 0

        // Cronómetro general (solo para mostrar tiempo en pantalla una vez que arranque)
        timerJob = viewModelScope.launch {
            while (_isRunning.value) {
                delay(1000)
                measurementStartTime?.let {
                    _elapsedTime.value = System.currentTimeMillis() - it
                }
            }
        }

        // Registro GPS
        locationTracker = LocationTracker(context) { location ->
            val previous = lastLocation
            lastLocation = location

            val speedKmh = (location.speed * 3.6).toDouble()

            // Filtros de estabilidad y calidad
            val goodFix = speedKmh >= minStableSpeedKmh &&
                    (!location.hasAccuracy() || location.accuracy <= maxAccuracyMeters)

            if (measurementStartTime == null) {
                if (goodFix) {
                    consecutiveGoodFixes++
                } else {
                    consecutiveGoodFixes = 0
                }
                _stabilizationProgress.value = consecutiveGoodFixes

                if (consecutiveGoodFixes >= requiredGoodFixes) {
                    // ✅ Inicia el conteo real
                    measurementStartTime = System.currentTimeMillis()
                    _distanceKm.value = 0f
                    lastRecordedKm = 0
                    _isStabilized.value = true
                }
                return@LocationTracker
            }

            if (previous != null && measurementStartTime != null) {
                val distanceDelta = previous.distanceTo(location).toDouble()
                val timeDelta = (location.time - previous.time).toDouble()

                if (timeDelta <= 0) return@LocationTracker

                val instSpeed = (distanceDelta / (timeDelta / 1000.0)) * 3.6

                // 🧩 Filtros suaves
                if (instSpeed > 160) return@LocationTracker
                if (distanceDelta > 200) return@LocationTracker
                if (location.hasAccuracy() && location.accuracy > 25) return@LocationTracker

                // Distancia acumulada
                val prevDistanceKm = _distanceKm.value.toDouble()
                val totalDistance = prevDistanceKm + (distanceDelta / 1000.0)
                _distanceKm.value = totalDistance.toFloat()

                val fullKm = totalDistance.toInt()

                // 🚀 Cruce de nuevo kilómetro
                if (fullKm > lastRecordedKm) {
                    val kmTarget = fullKm.toDouble()
                    val fraction = (kmTarget - prevDistanceKm) / (totalDistance - prevDistanceKm)
                    val interpolatedTimeMillis =
                        (previous.time + (timeDelta * fraction)).toLong()

                    val interpElapsed =
                        (interpolatedTimeMillis - (measurementStartTime ?: 0L))

                    // 🌟 Corrección sistemática de 0,3 s
                    val correctedElapsed = interpElapsed - 300

                    lastRecordedKm = fullKm
                    _splitTimes.value = _splitTimes.value + correctedElapsed

                    val secondsPerKm = 3600_000.0 / targetSpeedKmh
                    val idealTime = (fullKm * secondsPerKm).toLong()
                    _idealTimes.value = _idealTimes.value + idealTime

                    // 🔊 Beeps diferenciados:
                    // diff = ideal - split
                    // diff > 100 (tardó menos, o sea split < ideal) -> Rápido/Adelantado -> 2 beeps
                    // diff < -100 (tardó más, o sea split > ideal) -> Lento/Atrasado -> 3 beeps
                    // en tolerancia (-100..100) -> Perfecto -> 1 beep
                    val diff = idealTime - correctedElapsed
                    val beepCount = when {
                        diff > 100 -> 2   // ¡Desacelerar!
                        diff < -100 -> 3  // ¡Acelerar!
                        else -> 1         // ¡Perfecto!
                    }
                    playBeeps(beepCount)
                }
            }
        }

        locationTracker?.start()
    }

    private fun playBeeps(count: Int) {
        val soundId = beepSoundId ?: return
        val pool = soundPool ?: return
        viewModelScope.launch {
            for (i in 0 until count) {
                pool.play(soundId, 0.7f, 0.7f, 1, 0, 1f)
                if (i < count - 1) {
                    delay(220) // Separación rítmica entre beeps
                }
            }
        }
    }

    fun stopMeasurement() {
        _isRunning.value = false
        timerJob?.cancel()
        locationTracker?.stop()
        soundPool?.release()
        soundPool = null
    }

    fun resetMeasurement() {
        stopMeasurement()
        _elapsedTime.value = 0L
        _distanceKm.value = 0f
        _splitTimes.value = emptyList()
        _idealTimes.value = emptyList()
        measurementStartTime = null
        lastRecordedKm = 0
        lastLocation = null
        consecutiveGoodFixes = 0
        _isStabilized.value = false
        _stabilizationProgress.value = 0
    }

    fun addManualSplit() {
        measurementStartTime?.let { startTime ->
            val currentTime = System.currentTimeMillis() - startTime
            _splitTimes.value = _splitTimes.value + currentTime

            val nextKm = _splitTimes.value.size
            val secondsPerKm = 3600_000.0 / targetSpeedKmh
            val idealTime = (nextKm * secondsPerKm).toLong()
            _idealTimes.value = _idealTimes.value + idealTime

            val diff = idealTime - currentTime
            val beepCount = when {
                diff > 100 -> 2
                diff < -100 -> 3
                else -> 1
            }
            playBeeps(beepCount)
        }
    }

    fun pauseMeasurement() = stopMeasurement()
    fun resumeMeasurement(context: Context) = startMeasurement(context)

    fun exportToCsv(context: Context) {
        val headers = "Kilómetro,Medido,Ideal,Diferencia"
        val rows = _splitTimes.value.mapIndexed { index, split ->
            val ideal = _idealTimes.value.getOrNull(index)
            val difference = ideal?.let { split - it }

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
