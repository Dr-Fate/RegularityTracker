package com.example.regularitytracker

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.abs
import kotlin.math.roundToInt
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeasurementScreen(
    satelliteCount: Int,
    elapsedTime: StateFlow<Long>,
    distanceKm: StateFlow<Float>,
    splitTimes: StateFlow<List<Long>>,
    idealTimes: List<Long>,
    onStopClick: () -> Unit,
    onResetClick: () -> Unit,
    onAddManualSplit: () -> Unit,
    viewModel: MeasurementViewModel,
    context: Context
) {
    LaunchedEffect(Unit) {
        if (!viewModel.isRunning.value) {
            viewModel.startMeasurement(context)
        }
    }

    val time by elapsedTime.collectAsState()
    val distance by distanceKm.collectAsState()
    val splits by splitTimes.collectAsState()
    var guidanceMessage by remember { mutableStateOf<String?>(null) }

    // Mensaje por diferencia de tiempos
    LaunchedEffect(splits.size) {
        val index = splits.lastIndex
        val split = splits.getOrNull(index)
        val ideal = idealTimes.getOrNull(index)
        if (split != null && ideal != null) {
            val diff = ideal - split
            guidanceMessage = when {
                diff > 100 -> "¡Desacelerar!"
                diff < -100 -> "¡Acelerar!"
                else -> "¡Perfecto!"
            }
            delay(15000)
            guidanceMessage = null
        }
    }


    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF1B1B1B)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.statusBars.asPaddingValues())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Tiempo total: ${formatTimeTitle(time)}",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Distancia: ${"%.2f".format(distance)} km",
                        color = Color.White
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    val gpsQuality = when {
                        satelliteCount >= 9 -> "GPS: Excelente"
                        satelliteCount >= 6 -> "GPS: Medio"
                        satelliteCount >= 1 -> "GPS: Débil"
                        else -> "Sin señal"
                    }

                    Text(
                        text = gpsQuality,
                        color = Color.LightGray,
                        modifier = Modifier
                            .background(Color(0xFF313131), shape = RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    var expanded by remember { mutableStateOf(false) }

                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menú",
                            tint = Color.White
                        )
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Exportar a CSV") },
                            onClick = {
                                viewModel.exportToCsv(context)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val stopInteractionSource = remember { MutableInteractionSource() }
                val isStopPressed by stopInteractionSource.collectIsPressedAsState()

                val stopColor = if (isStopPressed) Color(0xFF004F6A) else Color(0xFF00668A)
                val scale by animateFloatAsState(targetValue = if (isStopPressed) 0.95f else 1f)

                Button(
                    onClick = onStopClick,
                    interactionSource = stopInteractionSource,
                    colors = ButtonDefaults.buttonColors(containerColor = stopColor),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .graphicsLayer(scaleX = scale, scaleY = scale)
                ) {
                    Text("Stop")
                }

                Spacer(modifier = Modifier.width(48.dp))

                val resetInteractionSource = remember { MutableInteractionSource() }
                val isResetPressed by resetInteractionSource.collectIsPressedAsState()

                val resetColor = if (isResetPressed) Color(0xFFB03030) else Color(0xFFEB4040)
                val scale2 by animateFloatAsState(targetValue = if (isResetPressed) 0.95f else 1f)

                Button(
                    onClick = onResetClick,
                    interactionSource = resetInteractionSource,
                    colors = ButtonDefaults.buttonColors(containerColor = resetColor),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .graphicsLayer(scaleX = scale2, scaleY = scale2)
                ) {
                    Text("Reset")
                }


                // ✅ Botón auxiliar de pruebas manuales (comentado)
                 /*
                Button(onClick = onAddManualSplit) {
                    Text("Agregar Split")
                }
                   */
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp), // altura fija para evitar salto de contenido
                contentAlignment = Alignment.Center
            ) {
                guidanceMessage?.let { message ->
                    val color = when (message) {
                        "¡Desacelerar!" -> Color.White
                        "¡Acelerar!" -> Color.White
                        "¡Perfecto!" -> Color.White
                        else -> Color.White
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF222222), shape = RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = message,
                            color = color,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Km",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Medido",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Ideal",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Pasamos..",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)//.padding(end = 8.dp)
                )
            }

            val listState = rememberLazyListState()
            val coroutineScope = rememberCoroutineScope()

            LaunchedEffect(splits.size) {
                if (splits.isNotEmpty()) {
                    coroutineScope.launch {
                        listState.animateScrollToItem(splits.lastIndex)
                    }
                }
            }

            LazyColumn(state = listState) {
                itemsIndexed(splits) { index, split ->
                    val km = index + 1
                    val ideal = idealTimes.getOrNull(index)
                    val difference = if (ideal != null) split - ideal else null

                    val (diffText, diffColor) = when {
                        difference == null -> "-" to Color.Gray
                        difference == 0L -> formatTime(0) to Color.White
                        difference > 0L -> "+${formatTime(difference)}" to Color.Red
                        else -> "-${formatTime(abs(difference))}" to Color.Cyan
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(Color(0xFF333333)),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Km $km",
                            color = Color.White,
                            modifier = Modifier.weight(1f).padding(8.dp)
                        )
                        Text(
                            text = formatTime(split),
                            color = Color.White,
                            modifier = Modifier.weight(1f).padding(8.dp)
                        )
                        Text(
                            text = ideal?.let { formatTime(it) } ?: "-",
                            color = Color.LightGray,
                            modifier = Modifier.weight(1f).padding(8.dp)
                        )
                        Text(
                            text = diffText,
                            color = diffColor,
                            modifier = Modifier.weight(1f).padding(8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}

fun formatTime(milliseconds: Long?): String {
    if (milliseconds == null || milliseconds < 0L) return "00:00,0"
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val tenths = ((milliseconds % 1000) / 100.0).roundToInt()
    return "%02d:%02d,%01d".format(minutes, seconds, tenths)
}

fun formatTimeTitle(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
