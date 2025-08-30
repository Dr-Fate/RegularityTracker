## [2.6.0] - 2025-08-30
### Mejoras en la precisión de medición
- 🚀 Implementada **interpolación exacta** para el cruce de cada kilómetro, eliminando el retraso sistemático en la detección.
- 🛡️ Añadido **filtro de outliers**: se descartan lecturas con velocidad instantánea superior a 160 km/h (valores imposibles para el vehículo).
- 🔧 Corregido error de tipos (`Double` vs `Float`) en cálculos de distancia y tiempo.
- ✅ Se mantiene frecuencia de muestreo del GPS en **500 ms** y sin distancia mínima, priorizando precisión máxima.
- 🔊 Conservada señal sonora (beep) al registrar un nuevo kilómetro.
- 📄 Exportación CSV intacta con formato: `Kilómetro, Medido, Ideal, Diferencia`.

## v2.5.0 - Precisión mejorada en el GPS y control de permisos

- Eliminado el filtro de distancia mínima (`distanceDelta > 3f`) para aumentar la precisión en la medición real de kilómetros recorridos.
- Reducción del umbral de velocidad mínima para registrar movimiento (antes ≥12 km/h, ahora cualquier valor).
- Intervalo de actualizaciones de GPS reducido a 0.5 segundos para mejorar la sincronización con la ruta real.
- Mejora de rendimiento en la lógica de ubicación para evitar retrasos acumulados en la distancia.
- Se verifica correctamente el permiso `POST_NOTIFICATIONS` en Android 13+ antes de mostrar notificaciones, cumpliendo con nuevas políticas de seguridad.

## v2.4.0 - Notificación persistente y mejoras de experiencia

- Se agrega una notificación persistente con texto actualizado al completar cada kilómetro.
- Aparece `TwingoTime! en progreso`, con el delta y mensaje de estado.
- Se oculta la notificación al pausar o reiniciar.
- Se mantiene el icono y prioridad baja para no interferir.
- Se implementa canal de notificaciones (`NotificationChannel`) en Android 8+.
## v2.3.2 - Rebranding
- Cambia ícono y nombre para vos... pillo.

## v2.3.1 - Mejora visual menor
- Agregado un `padding` izquierdo al bloque de "Tiempo total" y "Distancia".

## v2.3.0 - Scroll automático en mediciones
- Agregado scroll automático a la tabla de tiempos al registrar nuevos splits.
- El usuario puede hacer scroll manual para ver splits anteriores.
- El mensaje de guía (“¡Acelerar!” / “¡Desacelerar!” / “¡Perfecto!”) ahora aparece en una zona fija debajo de los botones, sin desplazar contenido.
- Se reserva el espacio para el mensaje aunque no haya mensaje activo.

## v2.2.1 - Mejora de sonido por Bluetooth
- Se cambió el canal de audio del beep para que se escuche correctamente por Bluetooth.
- Se agregó control de volumen independiente para el beep.

## v2.2.0 - Alerta sonora de split
- Se reproduce un beep corto cada vez que se registra automáticamente un nuevo kilómetro.
- El sonido funciona también en reproducción por Bluetooth.

## v2.1.0 - Menú de opciones y exportación
- Se agregó un menú de tres puntos junto al indicador GPS.
- Opción para exportar la tabla de splits a un archivo CSV.

## v2.0.0 - Pantalla de medición completa
- Tabla con columnas: Km, Tiempo medido, Tiempo ideal, Diferencia.
- Colores diferenciados para la columna de diferencia:
  - Rojo: tiempo excedido.
  - Azul: tiempo adelantado.
  - Blanco: tiempo perfecto.
- Mensajes de guía visual (¡Acelerar!, ¡Desacelerar!, ¡Perfecto!) al registrar un nuevo split.
- Botones de Stop, Reset y Agregar Split Manual.
- Scroll manual en la tabla.
- Calidad de señal GPS visible.

## v1.x - Implementación base
- Pantalla principal con botón de inicio y visualización de señal GPS.
- Comienzo de la lógica de medición y distancia por GPS.
