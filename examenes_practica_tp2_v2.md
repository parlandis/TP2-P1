# Exámenes de Práctica TP2 - Simulador de Ecosistemas (Set 2)

Simulacros de examen de alta fidelidad basados en el formato oficial de TP2 (UCM).

---

## Examen de Práctica A: "Regiones Dinámicas y Análisis por Especie"

### Pregunta 1 [2 puntos]

Añade al simulador una nueva región llamada **`FloodRegion`** que se comporta como `DefaultRegion` pero sufre inundaciones periódicas que incrementan temporalmente la comida disponible.

La clase recibe como parámetros: un factor de incremento **F** (double mayor que 1), la duración del **periodo seco S** en segundos (double mayor que 0), y la duración del **periodo de inundación W** en segundos (double mayor que 0). La constructora tiene que comprobar la validez de los valores.

La región empieza en periodo seco y se comporta exactamente como una `DefaultRegion`. Después de **S** segundos, entra en periodo de inundación, durante el cual la comida que devuelve es la cantidad normal multiplicada por **F**. El periodo de inundación dura exactamente **W** segundos. Al terminar la inundación, vuelve al periodo seco y se reinicia el ciclo.

Finalmente, integra `FloodRegion` en el simulador de manera que las regiones de este tipo puedan crearse usando una estructura JSON de la siguiente forma:

```json
{
  "type": "flood",
  "data": {
    "f": 2.0,
    "s": 15.0,
    "w": 5.0
  }
}
```

Todas las claves son opcionales con los siguientes valores por defecto: `2.0` para `f`, `15.0` para `s` y `5.0` para `w`.

No se permite modificar ninguna clase del simulador excepto la clase `Main` para añadir el correspondiente builder, pero se permite añadir nuevas clases.

---

### Pregunta 2 [2.5 puntos]

Decimos que una especie X ha entrado en **declive** en un determinado paso de la simulación si el número de animales de la especie X es estrictamente mayor que 0 al principio del paso y estrictamente menor al final del mismo (es decir, algún animal ha muerto).

Modifica el modo batch del simulador de manera que si se proporciona la opción **`-dc`** en los argumentos de la línea de comandos, entonces al final de la simulación se imprime para cada especie el número de pasos en los que ha entrado en declive. Por ejemplo:

```
Decline steps:
sheep => 12
wolf => 5
```

Las especies con 0 pasos de declive no deben aparecer en la salida.

No se permite modificar ninguna clase del simulador excepto la clase `Main`, pero se permite añadir nuevas clases.

La eficiencia y la elección correcta de las estructuras de datos son muy importantes y tendrán un peso considerable en la calificación del ejercicio.

---

### Pregunta 3 [3.5 puntos]

Añade un botón en la toolbar, usando el icono `resources/icons/stats.png` que se proporciona, tal que cuando se hace clic en él abre una ventana de diálogo que incluye:

1. Una tabla donde cada fila corresponde a un paso de la simulación e incluye: el tiempo, el número de animales de dieta `HERBIVORE`, y el número de animales de dieta `CARNIVORE` al final de ese paso (con cabeceras "Time", "Herbivores" y "Carnivores").
2. Un `JComboBox` encima de la tabla que permite elegir qué especie filtrar. Las opciones son: "All" (muestra todos los animales), y una opción por cada código genético (`sheep`, `wolf`, etc.) presente en la simulación. Cuando se cambia la selección, la tabla debe actualizarse inmediatamente.
3. Un botón **CLEAR** que borra el historial de la tabla.
4. Un botón **CLOSE** que simplemente cierra la ventana.
5. Por encima de la tabla hay que añadir un texto que indique la especie actualmente seleccionada.

El valor inicial del filtro es "All" y el de la tabla está vacío al abrir por primera vez.

No se permite modificar ninguna clase del simulador excepto la clase `ControlPanel` para añadir el correspondiente botón y su action listener, pero se permite añadir nuevas clases. La eficiencia y la elección correcta de las estructuras de datos son muy importantes y tendrán un peso considerable en la calificación del ejercicio.

---
---

## Examen de Práctica B: "Estrategias y Umbrales de Energía"

### Pregunta 1 [2 puntos]

Añade al simulador una nueva estrategia de selección llamada **`SelectMostEnergetic`**. Un lobo que usa esta estrategia selecciona, de entre todas las presas potenciales visibles, la que tenga **mayor energía**. Si varias presas tienen la misma energía máxima, se elige la primera que aparezca en la lista.

La clase no recibe parámetros adicionales. La constructora no necesita realizar ninguna comprobación.

Integra `SelectMostEnergetic` en el simulador de manera que las estrategias de este tipo puedan especificarse usando una estructura JSON de la siguiente forma:

```json
{
  "type": "most_energetic"
}
```

No se permite modificar ninguna clase del simulador excepto la clase `Main` para añadir el correspondiente builder, pero se permite añadir nuevas clases.

---

### Pregunta 2 [2.5 puntos]

Decimos que un animal está en **estado crítico de energía** en un determinado paso de la simulación si su nivel de energía es estrictamente menor que un umbral **E**, donde E es un double mayor que 0.

Modifica el modo batch del simulador de manera que si se proporciona la opción **`-ce E`** en los argumentos de la línea de comandos, entonces al final de la simulación se imprime el número máximo de animales que han estado en estado crítico simultáneamente en un mismo paso, para cada especie. Por ejemplo, con `-ce 20.0`:

```
Critical energy threshold: 20.0
sheep => max simultaneous critical: 8
wolf => max simultaneous critical: 3
```

No se permite modificar ninguna clase del simulador excepto la clase `Main`, pero se permite añadir nuevas clases.

La eficiencia y la elección correcta de las estructuras de datos son muy importantes y tendrán un peso considerable en la calificación del ejercicio.

---

### Pregunta 3 [3.5 puntos]

Añade un botón en la toolbar, usando el icono `resources/icons/stats.png` que se proporciona, tal que cuando se hace clic en él abre una ventana de diálogo que incluye:

1. Una tabla donde cada fila corresponde a un paso de la simulación e incluye: el tiempo, la energía media de todos los animales, y la energía media solo de los animales con energía por debajo de un umbral dado (con cabeceras "Time", "Avg Energy" y "Avg Energy Below Threshold").
2. Un `JTextField` para introducir el umbral de energía. A su izquierda aparecerá el texto "Energy Threshold".
3. Un botón **SET** que, cuando se pulsa: (a) valida que el valor del campo es un double mayor que 0, mostrando un diálogo de error si no lo es; (b) borra el historial de la tabla; (c) cambia el umbral al valor indicado; y (d) cierra la ventana.
4. Un botón **CLOSE** que simplemente cierra la ventana.
5. Por encima de la tabla, un texto que indique el umbral actual, por ejemplo: `"Showing energy stats for threshold: 30.0"`.

El valor inicial del umbral es `30.0`.

No se permite modificar ninguna clase del simulador excepto la clase `ControlPanel` para añadir el correspondiente botón y su action listener, pero se permite añadir nuevas clases. La eficiencia y la elección correcta de las estructuras de datos son muy importantes y tendrán un peso considerable en la calificación del ejercicio.

---
---

## Examen de Práctica C: "Regiones de Alta Montaña y Estadísticas de Reproducción"

### Pregunta 1 [2 puntos]

Añade al simulador una nueva región llamada **`MountainRegion`** que se comporta como `DefaultRegion` pero aplica una penalización de velocidad a todos los animales que en ella se encuentren, simulando terreno difícil.

La clase recibe como parámetros: un factor de reducción de velocidad **SL** (double entre 0 y 1, exclusivos) y una probabilidad de lesión **P** (double entre 0 y 1 inclusive). La constructora tiene que comprobar la validez de los valores.

En cada paso (`update`), para cada animal en la región, se reducirá su velocidad multiplicándola por `SL`. Adicionalmente, con probabilidad `P` (usando `Utils.RAND.nextDouble() < P`), el animal también pierde `10.0 * dt` unidades de energía por una "lesión". La región no dispone de parámetros de tiempo: la penalización es constante.

> **Nota**: Para aplicar penalizaciones a los animales, puedes añadir un método a la interfaz `RegionInfo` o usar los métodos ya disponibles en `AnimalInfo`. Analiza bien qué se permite modificar.

Integra `MountainRegion` en el simulador usando el tipo JSON `"mountain"`. Las claves son `sl` (por defecto `0.8`) y `p` (por defecto `0.1`).

No se permite modificar ninguna clase del simulador excepto la clase `Main` para añadir el correspondiente builder, pero se permite añadir nuevas clases.

---

### Pregunta 2 [2.5 puntos]

Decimos que en un paso de la simulación se produce un **pico de natalidad** si el número de animales al final del paso supera en más de un valor **N** al número de animales al principio del paso (es decir, nacen más de N animales en ese paso), donde N es un entero mayor que 0.

Modifica el modo batch del simulador de manera que si se proporciona la opción **`-birth N`** en los argumentos de la línea de comandos, entonces al final de la simulación se imprime:
- El número total de picos de natalidad detectados.
- El paso (instante de tiempo) en que se produjo el mayor aumento de población y cuántos animales nacieron en ese paso.

Por ejemplo, con `-birth 5`:

```
Birth peaks (threshold=5):
Total peaks: 4
Largest birth event at t=12.30 with 9 new animals
```

Si no se detecta ningún pico, imprimir `No birth peaks detected.`

No se permite modificar ninguna clase del simulador excepto la clase `Main`, pero se permite añadir nuevas clases.

La eficiencia y la elección correcta de las estructuras de datos son muy importantes y tendrán un peso considerable en la calificación del ejercicio.

---

### Pregunta 3 [3.5 puntos]

Añade un botón en la toolbar, usando el icono `resources/icons/stats.png` que se proporciona, tal que cuando se hace clic en él abre una ventana de diálogo que incluye:

1. Una tabla donde cada fila corresponde a un paso de la simulación e incluye: el tiempo, el código genético de la especie más numerosa en ese paso, y el número de individuos de esa especie. Si hay empate, se muestra la que aparezca primero alfabéticamente. Las cabeceras de las columnas son: "Time", "Dominant Species" y "Count".
2. Una etiqueta encima de la tabla con el texto: `"Dominant species per simulation step"`.
3. Un `JSpinner` (con rango de 1 a 100, valor inicial 10) con la etiqueta "Max Rows" a su izquierda. Al cambiar el valor del spinner, la tabla solo mostrará las últimas N filas del historial (siendo N el valor del spinner).
4. Un botón **RESET** que borra todos los datos de la tabla.
5. Un botón **CLOSE** que simplemente cierra la ventana.

El historial completo se debe guardar internamente aunque la tabla solo muestre los últimas N filas según el spinner.

No se permite modificar ninguna clase del simulador excepto la clase `ControlPanel` para añadir el correspondiente botón y su action listener, pero se permite añadir nuevas clases. La eficiencia y la elección correcta de las estructuras de datos son muy importantes y tendrán un peso considerable en la calificación del ejercicio.

---

## ⚠️ Los 8 puntos que más se penalizan en el examen real

| # | Trampa | Consecuencia |
|---|--------|-------------|
| 1 | No borrar datos en `onReset` / `onRegister` | Los datos de un mapa aparecen en otro |
| 2 | Validar `N < 0` en lugar de `N <= 0` | Acepta valores inválidos (0 es inválido si piden "mayor que 0") |
| 3 | Usar `HashMap` cuando piden salida ordenada | Orden aleatorio, bajan puntos por "estructura incorrecta" |
| 4 | Calcular `tiempoSequia` solo en el constructor | La sequía siempre dura lo mismo, no es aleatoria por ciclo |
| 5 | Olvidar `fireTableDataChanged()` | La tabla nunca se refresca aunque los datos cambien |
| 6 | Crear el `TableModel` dentro del `JDialog` | Cada vez que abres la ventana se pierden todos los datos |
| 7 | Usar `System.exit()` en el botón CLOSE | Cierra toda la aplicación en lugar de solo el diálogo |
| 8 | No usar `JScrollPane` al añadir la `JTable` | Las cabeceras de columna no se ven |
