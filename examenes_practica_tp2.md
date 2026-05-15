# Exámenes de Práctica TP2 - Simulador de Ecosistemas

Este documento contiene 3 simulacros de examen basados en el formato oficial de la asignatura Tecnología de la Programación 2 (UCM).

---

## Examen de Práctica 1: "Desastres Naturales y Mortalidad"

**Pregunta 1 [2 puntos]**
Añade una nueva región llamada **`VolcanicRegion`**. Esta región se comporta como `DefaultRegion` pero tiene ciclos de erupción.
*   **Parámetros**: `period` (segundos entre erupciones, mayor que 0), `duration` (segundos que dura la erupción, mayor que 0) y `lethality` (double entre 0 y 1).
*   **Comportamiento**: La región empieza en estado "Calma". Cada `period` segundos entra en "Erupción" durante `duration` segundos. Durante la erupción, cualquier animal que esté en esa región pierde una cantidad adicional de energía igual a `lethality * dt` en cada paso. Al terminar la erupción, vuelve a "Calma".
*   **JSON**: Tipo `"volcano"`. Claves `period`, `duration`, `lethality` (opcionales, por defecto: 20.0, 5.0, 0.5).

**Pregunta 2 [2.5 puntos]**
Añade al modo **batch** la opción `-stats`. Si se activa, al final de la simulación debe imprimir el número total de muertes por cada especie y la causa (hambre vs. devorado).
*   **Formato**:
    ```text
    Death Statistics:
    sheep => [Hunger: 10, Eaten: 5]
    wolf => [Hunger: 2, Eaten: 0]
    ```
*   **Restricción**: Solo puedes modificar la clase `Main` y añadir nuevas clases (Observadores).

**Pregunta 3 [3.5 puntos]**
Añade un botón en la toolbar (icono `resources/icons/regions.png`) que abra un diálogo para ver el **Historial de Comida**:
1.  Una tabla que muestre por cada paso: Tiempo, y la suma total de comida disponible en todas las regiones del mapa en ese instante.
2.  Un `JTextField` llamado "Threshold".
3.  Un botón **FILTER**: Borra la tabla y a partir de ahora solo registra los pasos donde la comida total sea superior al valor de "Threshold".
4.  Si el valor de "Threshold" no es un número válido > 0, muestra error.

---

## Examen de Práctica 2: "Evolución y Competencia"

**Pregunta 1 [2 puntos]**
Añade una nueva estrategia de selección llamada **`EnergySelection`**.
*   **Comportamiento**: El depredador elige como presa al animal que tenga **más energía** de entre los disponibles en su rango de visión. Si hay empate, elige el primero.
*   **JSON**: Tipo `"energy_sel"`. No recibe parámetros adicionales.
*   **Integración**: Regístralo en la factoría de estrategias en `Main`.

**Pregunta 2 [2.5 puntos]**
Añade al modo **batch** la opción `-limit x`. Al final de la simulación, imprime qué regiones han tenido en algún momento más de `x` animales simultáneamente.
*   **Formato**:
    ```text
    Crowded Regions (limit 10):
    Region (0,0) reached 12 animals
    Region (2,1) reached 15 animals
    ```
*   **Eficiencia**: No guardes todos los pasos, solo el valor máximo alcanzado por cada región.

**Pregunta 3 [3.5 puntos]**
Añade un botón en la toolbar (icono `resources/icons/stats.png`) que abra un diálogo de **Ranking de Longevidad**:
1.  Una tabla que muestre los 10 animales más viejos que estén **vivos** en el paso actual (columnas: Genetic Code, Age, Energy).
2.  La tabla debe actualizarse en tiempo real mientras el diálogo esté abierto y la simulación corra.
3.  Un botón **REFRESH** que fuerce la actualización manual del ranking.

---

## Examen de Práctica 3: "Migración y Clima"

**Pregunta 1 [2 puntos]**
Crea un tipo de animal llamado **`MigratorySheep`** que hereda de `Sheep`.
*   **Comportamiento**: Se comporta como una oveja normal, pero cada vez que se reproduce, su descendencia nace en una región aleatoria del mapa en lugar de en la región de la madre.
*   **Parámetros**: Los mismos que `Sheep`.
*   **JSON**: Tipo `"m_sheep"`.

**Pregunta 2 [2.5 puntos]**
Añade al modo **batch** la opción `-pos x y d`. Al final de la simulación, imprime cuántas veces un animal ha cruzado el área circular con centro en `(x,y)` y radio `d`.
*   **Restricción**: Un animal "cruza" si en el paso `t` estaba fuera y en el `t+dt` está dentro.

**Pregunta 3 [3.5 puntos]**
Crea un diálogo llamado **"Simulation Control Center"** accesible desde la toolbar:
1.  Una tabla con dos columnas: "Property" y "Value". Debe mostrar el tiempo actual, el número de animales vivos y el número de regiones.
2.  Un botón **RESET** que reinicie la simulación (usando el método `reset` del controlador).
3.  Un botón **STOP AT** con un `JTextField` al lado. Cuando la simulación llegue al tiempo indicado en el cuadro, la simulación debe detenerse automáticamente (tendrás que usar el observador para detectar cuándo llamar a `stop`).

---

## 💡 Guía de "Puntos Críticos" (Trampas de los profesores)

1.  **Gestión de Estados en Observadores**: Asegúrate siempre de implementar `onReset` y `onRegister` para limpiar las estructuras de datos (Mapas, Listas). Si no lo haces, los datos de simulaciones distintas se mezclarán.
2.  **Validación de Parámetros**: En los constructores, comprueba siempre los límites de los parámetros (ej. `r1` entre 0 y 1, `n > 0`). Lanza `IllegalArgumentException` con un mensaje claro.
3.  **Encapsulamiento**: No hagas públicos los atributos de las nuevas clases. Usa métodos `getter` si necesitas acceder desde la vista.
4.  **Uso de la Factoría**: No instancies directamente los Builders en el código. Regístralos en `Main.initFactories()`.
5.  **Orden en la Salida**: Si te piden una lista de especies o regiones, usa siempre `TreeMap` para que la salida en consola esté ordenada alfabéticamente.
6.  **Actualización de Tablas**: Recuerda que si los datos cambian, debes llamar a `fireTableDataChanged()` en el `TableModel`. Sin esto, la tabla no se refrescará.
