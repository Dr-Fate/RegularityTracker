# 📝 Changelog - Regularity Tracker

## Versión 2.0.0 (última versión)
- 🆕 Agregado de cuarta columna en pantalla de medición con diferencia Medido - Ideal.
- 🟥 Tiempos positivos en rojo, 🟦 tiempos negativos en celeste, valores exactos en blanco.
- ✅ Mensaje de orientación ("¡Acelerar!", "¡Desacelerar!", "¡Perfecto!") tras cada nuevo km.
- 🎨 Estilo de botones "Start", "Stop" y "Reset" animado al presionar.
- 🚫 Filtro de ruido refinado: se ignoran puntos con velocidad menor a 12 km/h.
- 🟦 Se restauró correctamente la funcionalidad de pausa (Stop).
- 📊 Ajuste visual de columnas para mayor alineación y claridad.
- 🔄 Reset reinicia completamente distancia, cronómetro y lógica de cálculo.
- 🔢 Mejora en el redondeo de tiempos (corrección de 52,17 → 52,2).
- ✅ Correcciones generales de interfaz y lógica.

## Versión 1.0.0 (primera versión funcional en APK)
- 🚘 Medición automática de tiempos por km y cálculo de distancia vía GPS.
- 🛰️ Indicador de señal GPS (Débil, Media, Excelente).
- 📥 Entrada de velocidad deseada (entre 30 y 120 km/h).
- 📊 Cálculo de tiempos ideales acumulados por km.
- 🧮 Diferencia entre tiempo ideal y medido.
- 🟢 Botón Start inicia la medición.
- 🟥 Botón Reset resetea todo.
- 🛑 Botón Stop pausa el cronómetro y la recolección de datos.
- 👆 Posibilidad de ajustar velocidad deseada con flechas o ingreso manual.
- 🧪 Botón auxiliar de pruebas para simular un km adicional (comentado).
- 🌙 Pantalla siempre activa durante la medición.
- ✅ Lógica refinada para evitar errores al quedarse detenido.

## Versión 0.9.0 (pruebas)
- funciones básicas de medición de tiempo x km
- se exige que sólo use GPS (sin datos, sin wifi, etc)
- se cambian colores, botones, alineaciones
- la pantalla de inicio sólo sirve para indicar el GPS antes de iniciar
- y un millón de quilombos que ni me acuerdo... o tal vez mas...