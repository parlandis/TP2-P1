# 🎯 Guía de Examen — TP2: Simulador de Ecosistemas

> Guía de resolución rápida basada en los patrones reales del examen.  
> Cada sección corresponde a uno de los 3 tipos de pregunta.

---

## ⚡ Antes de Empezar: Las 5 Reglas de Oro

1. **Solo puedes modificar `Main.java`** (para builders en P1 y P2) **y `ControlPanel.java`** (para el botón en P3). El resto de clases existentes son intocables.
2. **`addObserver(obs)` ANTES de `loadData(json)`** — si lo haces al revés, el observer no ve los animales iniciales.
3. **`fireTableDataChanged()`** — sin esta llamada, la tabla existe pero no se repinta nunca.
4. **`TreeMap`** para contadores que se van a imprimir — garantiza orden alfabético en el output.
5. **Lee el enunciado palabra por palabra** — la diferencia entre `<` y `<=` puede ser el 50% de la nota de un ejercicio.

---

## 📦 PREGUNTA 1: Nueva Región o Animal (~2 puntos)

### Checklist Completo

- [ ] Crear `MiRegion extends Region` en `simulator/model/`
- [ ] Validar parámetros en constructor (lanza `IllegalArgumentException`)
- [ ] Implementar `getFood(AnimalInfo a, double dt)` — devuelve 0 a carnívoros
- [ ] Implementar `update(double dt)` — lógica de ciclos/estado
- [ ] Implementar `getFoodAmount()` y `toString()`
- [ ] Crear `MiRegionBuilder extends Builder<Region>` en `simulator/factories/`
- [ ] En `createInstance`: leer parámetros con `data.optDouble("clave", valorDefault)`
- [ ] En `Main.initFactories()`: añadir `regionsBuilder.add(new MiRegionBuilder())`

### Plantilla Completa de Región

```java
package simulator.model;

import simulator.misc.Utils;

public class MiRegion extends Region {

    // --- Atributos de estado ---
    private double param;    // parámetro recibido
    private double cont;     // contador de tiempo acumulado
    private boolean enFase1; // estado interno del ciclo

    public MiRegion(double param) {
        // 1. VALIDAR — siempre lo primero
        if (param <= 0 || param > 1)
            throw new IllegalArgumentException("param debe estar entre 0 y 1");

        // 2. Inicializar atributos
        this.param = param;
        this.cont = 0.0;
        this.enFase1 = true;
    }

    @Override
    public double getFood(AnimalInfo a, double dt) {
        // Los carnívoros NUNCA obtienen comida de una región
        if (a.getDiet() != Diet.HERBIVORE) return 0.0;

        // Fórmula base de DefaultRegion (la más común en el examen)
        double baseFood = 60.0 * Math.exp(-Math.max(0, this.nH - 5.0) * 2.0) * dt;

        // Aplica tu modificación según el enunciado
        if (enFase1) {
            return baseFood;         // Fase normal: comida completa
        } else {
            return baseFood * param; // Fase especial: comida reducida
        }
    }

    @Override
    public void update(double dt) {
        // Actualizar el contador de tiempo
        this.cont += dt;

        // Lógica de transición de fases
        if (enFase1 && cont >= N) {
            enFase1 = false;
            // Generar duración aleatoria de la fase 2 si hace falta:
            // duracionFase2 = Utils.RAND.nextDouble(1.0, M);
        } else if (!enFase1 && (cont - N) >= duracionFase2) {
            enFase1 = true;
            cont = 0.0; // Resetear contador
            // Actualizar parámetros si el enunciado lo pide:
            // param *= otroFactor;
        }
    }

    @Override
    public double getFoodAmount() {
        return 0.0; // Para DynamicSupplyRegion sería: return food;
    }

    @Override
    public String toString() {
        return "mi-region"; // Debe coincidir con el type del Builder
    }
}
```

### Plantilla Completa de Builder

```java
package simulator.factories;

import org.json.JSONObject;
import simulator.model.MiRegion;
import simulator.model.Region;

public class MiRegionBuilder extends Builder<Region> {

    public MiRegionBuilder() {
        super("mi-tipo", "Descripción de Mi Región"); // "mi-tipo" = lo que va en el JSON
    }

    @Override
    protected Region createInstance(JSONObject data) {
        // optDouble("clave", valorPorDefecto) — nunca falla aunque no esté la clave
        double param = data.optDouble("param", 0.95);
        double n     = data.optDouble("n", 10.0);
        double m     = data.optDouble("m", 5.0);

        return new MiRegion(param, n, m);
    }

    @Override
    protected void fillInData(JSONObject o) {
        // Documenta los parámetros (para la GUI de cambio de regiones)
        o.put("param", "factor de reducción (opcional, default 0.95)");
        o.put("n", "duración periodo normal en segundos (opcional, default 10.0)");
        o.put("m", "duración máxima periodo especial en segundos (opcional, default 5.0)");
    }
}
```

### Registro en `Main.java`

```java
// En initFactories(), sección de regiones:
List<Builder<Region>> regionBuilders = new ArrayList<>();
regionBuilders.add(new DefaultRegionBuilder());
regionBuilders.add(new DynamicSupplyRegionBuilder());
regionBuilders.add(new DryRegionBuilder());
regionBuilders.add(new MiRegionBuilder()); // ← AÑADIR AQUÍ
_regionFactory = new BuilderBasedFactory<>(regionBuilders);
```

### JSON de Ejemplo para Probar

```json
{
  "type": "mi-tipo",
  "data": {
    "param": 0.8,
    "n": 15.0,
    "m": 6.0
  }
}
```

---

## 📊 PREGUNTA 2: Modo Batch / Observador (~2.5 puntos)

### Checklist Completo

- [ ] Añadir opción en `Main.buildOptions()` con `Option.builder(...)`
- [ ] Crear `parseXxxOption(CommandLine line)` y llamarlo en `parseArgs()`
- [ ] Declarar `private static Double miValor = null;` en `Main`
- [ ] Crear `MiObserver implements EcoSysObserver` en `simulator/model/`
- [ ] Usar `HashMap` para estado interno, `TreeMap` para output ordenado
- [ ] Implementar `onAdvance` con la lógica principal
- [ ] Implementar `onReset` para limpiar el estado
- [ ] En `Main.startBatchMode()`: crear observer, **`addObserver` ANTES de `loadData`**
- [ ] Al final de `startBatchMode()`: llamar `obs.printReport()`

### Modificaciones en `Main.java`

```java
// 1. Nueva variable estática
private static Double miValor = null; // null = opción no activada

// 2. En buildOptions():
cmdLineOptions.addOption(Option.builder("mi")
    .longOpt("mi-opcion")
    .hasArg()                                    // Quitar si no tiene argumento
    .desc("Descripción de la opción.")
    .build());

// 3. Nuevo método parseXxx:
private static void parseMiOpcionOption(CommandLine line) throws ParseException {
    if (line.hasOption("mi")) {
        String val = line.getOptionValue("mi");
        try {
            miValor = Double.parseDouble(val);
            if (miValor <= 0) throw new NumberFormatException();
        } catch (Exception e) {
            throw new ParseException("Valor inválido para -mi: " + val);
        }
    }
}

// 4. En parseArgs(), llamarlo:
parseMiOpcionOption(line); // Antes de los "remaining"

// 5. En startBatchMode():
Simulator sim = new Simulator(cols, rows, width, height, _animalFactory, _regionFactory);
Controller ctrl = new Controller(sim);

// OBSERVER — SIEMPRE antes de loadData
MiObserver obs = null;
if (miValor != null) {
    obs = new MiObserver(miValor);
    ctrl.addObserver(obs); // ← PRIMERO
}

ctrl.loadData(json);       // ← DESPUÉS
ctrl.run(time, deltaTime, simpleViewer, out);

// INFORME — al final
if (obs != null) obs.printReport();
```

### Plantilla Completa de Observer

```java
package simulator.model;

import java.util.*;

public class MiObserver implements EcoSysObserver {

    private double limite;

    // HashMap para cálculos intermedios (eficiencia interna)
    private Map<String, Integer> ultimoConteo;

    // TreeMap para el output: garantiza orden ALFABÉTICO
    private Map<String, Integer> contadores;

    public MiObserver(double limite) {
        this.limite = limite;
        this.ultimoConteo = new HashMap<>();
        this.contadores   = new TreeMap<>();
    }

    @Override
    public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
        // Inicializar el conteo con el estado inicial
        actualizarConteo(animals);
    }

    @Override
    public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
        // Limpiar TODO al resetear
        ultimoConteo.clear();
        contadores.clear();
        actualizarConteo(animals);
    }

    @Override
    public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
        // ─── LÓGICA PRINCIPAL ───
        // Ejemplo: detectar cuándo la población de una especie supera el límite

        // 1. Contar población actual
        Map<String, Integer> conteoActual = new HashMap<>();
        for (AnimalInfo a : animals) {
            String esp = a.getGeneticCode();
            conteoActual.merge(esp, 1, Integer::sum); // equivale a getOrDefault(esp,0)+1
        }

        // 2. Comparar con el conteo anterior
        for (Map.Entry<String, Integer> entry : conteoActual.entrySet()) {
            String esp = entry.getKey();
            int actual = entry.getValue();
            int anterior = ultimoConteo.getOrDefault(esp, 0);

            // Condición del enunciado (adaptar según lo que pida):
            if (anterior <= limite && actual > limite) {
                contadores.merge(esp, 1, Integer::sum);
            }
        }

        // 3. Guardar el conteo actual para el siguiente paso
        ultimoConteo = conteoActual;
    }

    @Override
    public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
        // Actualizar conteo al añadir un animal (nacimiento)
        actualizarConteo(animals);
    }

    @Override
    public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
        // No necesario en la mayoría de casos
    }

    // ─── Método auxiliar ───
    private void actualizarConteo(List<AnimalInfo> animals) {
        ultimoConteo.clear();
        for (AnimalInfo a : animals) {
            String esp = a.getGeneticCode();
            ultimoConteo.merge(esp, 1, Integer::sum);
        }
    }

    // ─── Impresión del informe ───
    public void printReport() {
        System.out.println("Encabezado del informe para limite=" + limite + ":");
        for (Map.Entry<String, Integer> e : contadores.entrySet()) {
            System.out.println(e.getKey() + " => " + e.getValue());
        }
    }
}
```

> **Sobre la eficiencia** (el enunciado dice que importa):
> - No uses `List.contains` → O(n). Usa `Map.containsKey` → O(1).
> - No recorras dos veces la lista si puedes hacerlo en una sola pasada.
> - `merge(key, 1, Integer::sum)` es más compacto que `getOrDefault + put`.

---

## 🖥️ PREGUNTA 3: Interfaz Gráfica / Diálogo (~3.5 puntos)

### Checklist Completo

- [ ] Crear `MiTableModel extends AbstractTableModel implements EcoSysObserver`
  - [ ] Constructor recibe `Controller`, llama `ctrl.addObserver(this)`
  - [ ] Clase interna `Fila` con los datos de cada row
  - [ ] `List<Fila>` como estructura de datos principal
  - [ ] `getRowCount`, `getColumnCount`, `getColumnName`, `getValueAt`
  - [ ] `onAdvance`: añadir fila y llamar `fireTableDataChanged()`
  - [ ] `onRegister` y `onReset`: limpiar lista y `fireTableDataChanged()`
- [ ] Crear `MiDialog extends JDialog`
  - [ ] Constructor `(Frame parent, MiTableModel modelo)` con `super(parent, true)`
  - [ ] `initGUI()` con `BorderLayout`: label NORTH, tabla CENTER, controles SOUTH
  - [ ] Método `open(Frame parent)` que centra y hace visible
- [ ] En `ControlPanel`:
  - [ ] Añadir atributos: `private MiTableModel miModelo` y `private MiDialog miDialog`
  - [ ] En `initGUI()`: crear el modelo, crear el diálogo, crear el botón

### Plantilla Completa de TableModel

```java
package simulator.view;

import javax.swing.table.AbstractTableModel;
import simulator.control.Controller;
import simulator.model.*;
import java.util.*;

public class MiTableModel extends AbstractTableModel implements EcoSysObserver {

    // Clase interna para cada fila de la tabla
    private static class Fila {
        double time;
        int dato1;
        double dato2;

        Fila(double t, int d1, double d2) {
            this.time = t;
            this.dato1 = d1;
            this.dato2 = d2;
        }
    }

    private List<Fila> datos = new ArrayList<>();
    private double parametro = 3.0; // Valor configurable desde el diálogo

    public MiTableModel(Controller ctrl) {
        ctrl.addObserver(this); // Se auto-registra como observer
    }

    // ─── Métodos de AbstractTableModel ───

    @Override public int getRowCount() { return datos.size(); }
    @Override public int getColumnCount() { return 3; }

    @Override
    public String getColumnName(int col) {
        switch (col) {
            case 0: return "Time";
            case 1: return "Dato 1";
            case 2: return "Dato 2";
        }
        return null;
    }

    @Override
    public Object getValueAt(int row, int col) {
        Fila f = datos.get(row);
        switch (col) {
            case 0: return String.format("%.3f", f.time);
            case 1: return f.dato1;
            case 2: return String.format("%.2f", f.dato2);
        }
        return null;
    }

    // ─── Getters/Setters para el parámetro configurable ───

    public double getParametro() { return parametro; }

    public void setParametro(double p) {
        this.parametro = p;
        limpiar(); // (a) borrar contenido de la tabla
    }

    // ─── Métodos Observer ───

    @Override
    public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
        // Calcular los datos de este paso
        int d1 = 0;
        double d2 = 0.0;
        for (AnimalInfo a : animals) {
            if (a.getAge() < parametro) d1++;       // jóvenes
            else d2 += a.getEnergy();               // energía de los viejos
        }
        datos.add(new Fila(time, d1, d2));
        fireTableDataChanged(); // ← OBLIGATORIO para que la tabla se repinte
    }

    @Override
    public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
        limpiar();
    }

    @Override
    public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
        limpiar();
    }

    @Override public void onAnimalAdded(double t, MapInfo m, List<AnimalInfo> a, AnimalInfo an) {}
    @Override public void onRegionSet(int r, int c, MapInfo m, RegionInfo ri) {}

    // ─── Auxiliar ───
    private void limpiar() {
        datos.clear();
        fireTableDataChanged();
    }
}
```

### Plantilla Completa de JDialog

```java
package simulator.view;

import javax.swing.*;
import java.awt.*;

class MiDialog extends JDialog {

    private MiTableModel modelo;
    private JLabel infoLabel;
    private JTextField campoValor;

    MiDialog(Frame parent, MiTableModel modelo) {
        super(parent, true); // true = MODAL (bloquea la ventana padre)
        this.modelo = modelo;
        initGUI();
    }

    private void initGUI() {
        setTitle("Mis Estadísticas");

        // Panel raíz con BorderLayout y padding
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(panel);

        // ─── NORTE: texto explicativo (req. 5 del enunciado) ───
        infoLabel = new JLabel("Estadísticas para parámetro: " + modelo.getParametro());
        panel.add(infoLabel, BorderLayout.NORTH);

        // ─── CENTRO: tabla con scroll ───
        JTable tabla = new JTable(modelo);
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        // ─── SUR: controles (apilados verticalmente) ───
        JPanel sur = new JPanel();
        sur.setLayout(new BoxLayout(sur, BoxLayout.Y_AXIS));
        panel.add(sur, BorderLayout.SOUTH);

        // Fila de input: [Age Limit] [JTextField] [SET]   ← orden exacto del enunciado
        JPanel filaInput = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filaInput.add(new JLabel("Parámetro: "));
        campoValor = new JTextField(String.valueOf(modelo.getParametro()), 6);
        filaInput.add(campoValor);

        JButton btnSet = new JButton("SET");
        btnSet.addActionListener(e -> {
            try {
                double nuevoValor = Double.parseDouble(campoValor.getText());
                if (nuevoValor <= 0) throw new NumberFormatException();

                modelo.setParametro(nuevoValor);                          // (a) limpia y (b) actualiza
                infoLabel.setText("Estadísticas para parámetro: " + nuevoValor);
                setVisible(false);                                        // (c) cierra el diálogo
            } catch (NumberFormatException ex) {
                ViewUtils.showErrorMsg("El valor debe ser un número decimal mayor que 0");
            }
        });
        filaInput.add(btnSet);
        sur.add(filaInput);

        // Fila de botones: [CLOSE]
        JPanel filaBotones = new JPanel();
        JButton btnClose = new JButton("CLOSE");
        btnClose.addActionListener(e -> setVisible(false));
        filaBotones.add(btnClose);
        sur.add(filaBotones);

        setPreferredSize(new Dimension(500, 400));
        pack();
    }

    // Centra el diálogo sobre la ventana padre y lo hace visible
    public void open(Frame parent) {
        setLocation(
            parent.getLocation().x + parent.getWidth()  / 2 - getWidth()  / 2,
            parent.getLocation().y + parent.getHeight() / 2 - getHeight() / 2
        );
        pack();
        setVisible(true);
    }
}
```

### Modificaciones en `ControlPanel.java`

```java
// ─── 1. Nuevos atributos (junto con los demás botones) ───
private MiTableModel miModelo;
private MiDialog miDialog;
private JButton miBoton;

// ─── 2. En initGUI(), ANTES de añadir el botón a la toolbar ───

// Crear modelo (se auto-registra como observer)
this.miModelo = new MiTableModel(this.ctrl);

// Crear diálogo (necesita Frame padre y el modelo)
this.miDialog = new MiDialog(ViewUtils.getWindow(this), this.miModelo);

// Crear y configurar el botón
this.miBoton = new JButton();
this.miBoton.setIcon(new ImageIcon("resources/icons/stats.png")); // icono pedido
this.miBoton.setToolTipText("Ver estadísticas");
this.miBoton.addActionListener(e -> this.miDialog.open(ViewUtils.getWindow(this)));
this.toolaBar.add(this.miBoton);
this.toolaBar.addSeparator();
```

---

## ❌ Tabla de Errores Frecuentes

| # | Error | Síntoma | Solución |
|---|-------|---------|----------|
| 1 | `addObserver` después de `loadData` | Observer no ve animales iniciales; datos incorrectos desde el principio | Invertir el orden: siempre observer primero |
| 2 | Olvidar `fireTableDataChanged()` | La tabla existe pero aparece vacía o no se actualiza | Llamarlo en `onAdvance` y en `limpiar()` |
| 3 | Olvidar registrar el Builder en `initFactories` | `IllegalArgumentException: Unrecognized 'info'` al cargar el JSON | Añadir `new MiRegionBuilder()` a la lista |
| 4 | `HashMap` en vez de `TreeMap` para el output | El informe sale en orden aleatorio en cada ejecución | Usar `TreeMap` cuando los datos se van a imprimir |
| 5 | `getFood` sin filtrar carnívoros | Los lobos obtienen comida de la región | Siempre: `if (diet != HERBIVORE) return 0.0;` |
| 6 | `new MiDialog(this.ctrl)` — constructor incorrecto | Error de compilación o `NullPointerException` en runtime | El constructor necesita `(Frame parent, MiTableModel m)` |
| 7 | `>` en vez de `>=` (o viceversa) en condición | Fallo silencioso: los límites exactos no se detectan | Leer el enunciado: "menor estricto" ≠ "menor o igual" |
| 8 | Usar `Dialog(null, ...)` como padre | El diálogo no tiene padre, puede aparecer en lugar incorrecto | Usar `ViewUtils.getWindow(this)` para obtener el `Frame` |
| 9 | Modificar `listaA` mientras se itera | `ConcurrentModificationException` | Usar `Iterator` o construir lista temporal de cambios |
| 10 | No validar parámetros en el constructor | El simulador crea objetos en estado inválido, errores difíciles de trazar | Validar siempre al inicio del constructor |

---

## 🗂️ Estructura JSON de un Fichero de Configuración

```json
{
  "width": 800,
  "height": 600,
  "rows": 10,
  "cols": 15,

  "regions": [
    {
      "row": [0, 4],
      "col": [0, 7],
      "spec": {
        "type": "dry",
        "data": { "r1": 0.9, "r2": 0.95, "n": 15.0, "m": 5.0 }
      }
    },
    {
      "spec": {
        "type": "default"
      }
    }
  ],

  "animals": [
    {
      "amount": 20,
      "spec": {
        "type": "sheep",
        "data": {
          "mate-strategy": { "type": "closest" },
          "danger-strategy": { "type": "first" }
        }
      }
    },
    {
      "amount": 5,
      "spec": {
        "type": "wolf",
        "data": {
          "mate-strategy": { "type": "youngest" },
          "hunt-strategy": { "type": "closest" }
        }
      }
    }
  ]
}
```

**Notas sobre el formato:**
- Si no se especifica `"row"` o `"col"`, la región se aplica a **todas** las filas/columnas.
- El `"type"` debe coincidir **exactamente** con el `typeTag` del Builder (mayúsculas/minúsculas importan).
- Si no hay sección `"data"`, la factoría pasa un `JSONObject` vacío al Builder → los `optDouble` devuelven sus valores por defecto.
