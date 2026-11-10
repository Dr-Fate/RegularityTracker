## v2.9.7 - 2026-11-10
### Selector de carpeta para exportar CSV y optimización de build
- 📁 **Selector nativo de ubicación para CSV**: La opción del menú ahora abre el explorador de archivos nativo de Android (`ActivityResultContracts.CreateDocument`), permitiendo elegir libremente la carpeta de destino (Descargas, Documentos, Google Drive, SD o cualquier carpeta personalizada) y editar el nombre antes de guardar.
- 🏷️ Nombre sugerido automático inteligente con fecha, hora y velocidad: `regularidad_{velocidad}kmh_YYYYMMDD_HHMMSS.csv`.
- 🧹 **Limpieza y modernización de Gradle**:
  - Eliminada la dependencia en desuso `androidx.fragment.ktx`.
  - Migrada la configuración `jvmTarget` obsoleta a `kotlin.compilerOptions.jvmTarget.set(JVM_11)`.
  - Integrada la dependencia `navigation-compose` dentro de `libs.versions.toml`.
  - Limpieza de imports redundantes en pantallas de Compose.

## v2.9.6 - 2026-11-02
### Naranja de alto contraste y blindaje anti-interferencias
- 🎨 **Color Naranja de alto contraste (`#FFB74D`)**: Reemplazado el color rojo en el cartel `"¡Desacelerar!"` y en las diferencias positivas de la tabla (`+00:0X,X`), logrando óptima legibilidad con luz solar directa o reflejos en el habitáculo.
- 🛡️ **Dead Reckoning inteligente anti-interferencias**: Eliminado el descarte ciego de fixes (`return`). Si la precisión se degrada (`> 20m`) o hay saltos anómalos (por torres de alta tensión, puentes o túneles de árboles), la odometría continúa calculando el avance exacto mediante la velocidad Doppler/inercial del Pixel 6 y $\Delta t$. Mantiene la precisión en centésimas y evita saltos repentinos como el de +2.5s.
- 📡 **Alerta en vivo de señal**: El indicador superior de GPS cambia a naranja y muestra `GPS: Estimando (interferencia)` durante los tramos con sombra satelital.

## v2.9.5 - 2026-11-02
### Estabilización extendida y alineación de odometría inicial
- ⏱️ **Calibración a 8 fixes consecutivos**: Se aumenta a 8 segundos seguidos de vector de velocidad estable a $\ge 20$ km/h y precisión $\le 15$ m antes de habilitar la carrera.
- 🎯 **Alineación exacta del Km 0**: El fix donde se decreta la estabilización se fija inmediatamente como origen espacial (`lastLocation`), sumando los metros reales recorridos entre el fix 8 y el fix 9 sin pérdida de distancia en el odómetro (resolviendo la diferencia arrastrada al Km 1 y Km 2).

## v2.9.4 - 2026-10-26
### Optimización GNSS L1/L5 y reloj atómico GPS (Pixel 6)
- 🛰️ **Migración a `FusedLocationProviderClient`**: Activado el uso de la doble frecuencia satelital (L1/L5) del Pixel 6 con `Priority.PRIORITY_HIGH_ACCURACY` y fusión de sensores inerciales de Google para filtrar rebotes (multipath).
- ⚛️ **Unificación al reloj satelital atómico**: La largada de la medición (`measurementStartTime`) y las interpolaciones de cruce de km se sincronizan con `location.time` (tiempo satelital UTC atómico), eliminando desfasajes entre el reloj del teléfono y el GPS.
- ⏱️ Tasa de muestreo consolidada en **1 segundo (1 Hz)** para evitar ruido por micro-oscilaciones.

## v2.9.3 - 2026-10-26
### Beeps diferenciados por rendimiento y coherencia visual
- 🔊 **Audio-feedback por resultado**:
  - **1 Beep**: Paso perfecto (dentro del rango de $\pm 100$ ms).
  - **2 Beeps rítmicos**: Vehículo rápido / adelantado (*"¡Desacelerar!"*).
  - **3 Beeps rítmicos**: Vehículo lento / atrasado (*"¡Acelerar!"*).
- 👁️ **Feedback sin desviar la vista**: El piloto puede corregir el ritmo por audio sin necesidad de mirar la pantalla.
- 🎨 **Coherencia cromática**: El mensaje flotante adopta los mismos colores de la tabla (Blanco para perfecto, Rojo para rápido, Cian para lento).

## v2.9.2 - 2026-10-26
### Flujo completo de Reset e indicador visual de calibración
- 🔄 **Reinicio integral con botón Reset**: El botón Reset ahora limpia mediciones, detiene el tracking y regresa a `MainScreen` para reajustar o confirmar la velocidad ideal y esperar señal GPS limpia antes de iniciar un nuevo tramo.
- 📊 **Feedback visual en vivo de estabilización**: Banner que indica `Estabilizando GPS: Esperando movimiento...` o el progreso numérico de calibración antes de largar el cronómetro.
- 🎯 Calibración inicial elevada a 5 fixes con precisión $\le 18$ m.

## v2.9.1 - 2025-11-13
### Delay Start y corrección en formato numérico
- 🚦 Introducción del modo de **estabilización inicial** ("Delay Start"): el cronómetro y la distancia no inician hasta registrar los primeros fixes de GPS estables con velocidad mínima.
- 🔢 Corrección en redondeos y formato de visualización de tiempos y décimas de segundo.

## v2.7.0 / v2.8.0 / v2.9.0 - 2025-09 a 2025-10
### Ajustes de estabilidad y sincronización
- 🔧 Mantenimiento de estabilidad en la captura de posiciones continuas.
- 🧹 Limpieza en la gestión de estados y navegación interna.

## v2.6.0 - 2025-08-30
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
