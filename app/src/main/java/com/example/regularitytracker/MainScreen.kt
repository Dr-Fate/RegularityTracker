package com.example.regularitytracker

import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.graphics.graphicsLayer


@Composable
fun MainScreen(
    onStartClick: () -> Unit,
    satelliteCount: Int,
    selectedSpeed: Int,
    onSpeedChange: (Int) -> Unit
) {
    val gpsQuality = when {
        satelliteCount >= 9 -> "Señal GPS: Excelente"
        satelliteCount >= 6 -> "Señal GPS: Media"
        satelliteCount >= 1 -> "Señal GPS: Débil"
        else -> "Sin señal GPS"
    }

    var speedInput by remember { mutableStateOf(selectedSpeed.toString()) }
    var showError by remember { mutableStateOf(false) }

    val upInteraction = remember { MutableInteractionSource() }
    val downInteraction = remember { MutableInteractionSource() }

    // Flecha arriba - long press funcional
    LaunchedEffect(upInteraction) {
        upInteraction.interactions.collectLatest { interaction ->
            if (interaction is PressInteraction.Press) {
                delay(500) // Espera medio segundo antes de iniciar repetición
                while (true) {
                    val value = speedInput.toIntOrNull() ?: selectedSpeed
                    if (value < 120) {
                        val newValue = value + 1
                        speedInput = newValue.toString()
                        onSpeedChange(newValue)
                        showError = false
                    }
                    delay(150)
                }
            }
        }
    }

// Repetición controlada para decremento
    LaunchedEffect(downInteraction) {
        downInteraction.interactions.collectLatest { interaction ->
            if (interaction is PressInteraction.Press) {
                delay(500) // Espera medio segundo antes de iniciar repetición
                while (true) {
                    val value = speedInput.toIntOrNull() ?: selectedSpeed
                    if (value > 30) {
                        val newValue = value - 1
                        speedInput = newValue.toString()
                        onSpeedChange(newValue)
                        showError = false
                    }
                    delay(150)
                }
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF1B1B1B)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Box(modifier = Modifier.height(40.dp))
            Icon(
                imageVector = Icons.Default.GpsFixed,
                contentDescription = "GPS",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = gpsQuality,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(220.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = speedInput,
                    onValueChange = {
                        speedInput = it
                        val parsed = it.toIntOrNull()
                        showError = it.isNotBlank() && (parsed == null || parsed !in 30..120)
                        if (!showError && parsed != null) {
                            onSpeedChange(parsed)
                        }
                    },
                    label = { Text("Vel. Ideal (km/h)", color = Color.White) },
                    singleLine = true,
                    isError = showError,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.width(140.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color(0xFF64B5F6),
                        unfocusedIndicatorColor = Color.White,
                        errorIndicatorColor = Color.Red,
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        errorTextColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    IconButton(
                        onClick = {
                            val value = speedInput.toIntOrNull() ?: selectedSpeed
                            if (value < 120) {
                                val newValue = value + 1
                                speedInput = newValue.toString()
                                onSpeedChange(newValue)
                                showError = false
                            }
                        },
                        interactionSource = upInteraction
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropUp,
                            contentDescription = "Aumentar",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = {
                            val value = speedInput.toIntOrNull() ?: selectedSpeed
                            if (value > 30) {
                                val newValue = value - 1
                                speedInput = newValue.toString()
                                onSpeedChange(newValue)
                                showError = false
                            }
                        },
                        interactionSource = downInteraction
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Disminuir",
                            tint = Color.White
                        )
                    }
                }
            }

            Box(modifier = Modifier.height(20.dp)) {
                if (showError) {
                    Text(
                        text = "Debe estar entre 30 y 120 km/h",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            val startInteractionSource = remember { MutableInteractionSource() }
            val isStartPressed by startInteractionSource.collectIsPressedAsState()

            val startColor = if (isStartPressed) Color(0xFF004F6A) else Color(0xFF00668A)
            val scale by animateFloatAsState(targetValue = if (isStartPressed) 0.95f else 1f)

            Button(
                onClick = { if (!showError) onStartClick() },
                interactionSource = startInteractionSource,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = startColor,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .width(180.dp)
                    .height(64.dp)
                    .graphicsLayer(scaleX = scale, scaleY = scale)
            ) {
                Text("Start!", color = Color.White)
            }
        }
    }
}
