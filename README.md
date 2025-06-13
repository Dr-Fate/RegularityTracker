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
