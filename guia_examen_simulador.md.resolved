# 📘 Guía de Examen – Simulador de Ecosistemas (TP2-P1)

Basada en los exámenes reales trabajados. Cada tipo de pregunta tiene su **patrón repetible**.

---

## 🗂️ TIPOS DE PREGUNTA QUE SALEN EN EXAMEN

| Tipo | Puntos típicos | Dónde se toca |
|---|---|---|
| **P1** – Nueva clase Animal (herencia) | 2 pts | `simulator/model/` |
| **P2** – Observador en modo batch (consola) | 2-3 pts | `simulator/model/` + `Main.java` |
| **P3** – Diálogo con tabla estadística (GUI) | 3-3.5 pts | `simulator/view/` |

---

## 🐑 TIPO 1 – Nueva clase Animal (Herencia de `Animal`)

### Patrón general
Crear una subclase de `Sheep` o `Wolf` que **modifica el comportamiento** en función del tiempo (día/noche, edad, etc.).

### Checklist obligatorio

- [ ] Extiende la clase base correcta (`Sheep` o `Wolf`)
- [ ] El constructor **valida todos los parámetros** del enunciado y lanza `IllegalArgumentException`
- [ ] Sobreescribes `update(double dt)` para modificar el comportamiento en cada paso
- [ ] Llamas a `super.update(dt)` dentro de tu `update()` para no romper la lógica base
- [ ] Si hay un estado "diurno/nocturno": restauras el valor original al cambiar de estado

### Esqueleto base – `TiredSightSheep`

```java
package simulator.model;

public class TiredSightSheep extends Sheep {

    private double D;    // duración del día (segundos)
    private double N;    // duración de la noche (segundos)
    private double RN;   // reducción de vista por la noche (0-1)
    private double RD;   // reducción de vista cada día (0-1)

    private double timerDia;   // cronómetro dentro del día actual
    private boolean esNoche;   // ¿estamos en periodo nocturno?
    private double sightRangeDia; // sightRange base del día actual

    public TiredSightSheep(double D, double N, double RN, double RD,
                           SelectionStrategy mateStrategy,
                           SelectionStrategy dangerStrategy) {
        super(mateStrategy, dangerStrategy); // Llama al constructor de Sheep

        // ✅ Validación de parámetros obligatoria
        if (D <= 0 || N <= 0 || RN < 0 || RN > 1 || RD < 0 || RD > 1)
            throw new IllegalArgumentException("Parámetros no válidos");

        this.D = D;
        this.N = N;
        this.RN = RN;
        this.RD = RD;
        this.timerDia = 0.0;
        this.esNoche = false;
        this.sightRangeDia = this.sightRange; // Guardamos el valor base heredado
    }

    @Override
    public void update(double dt) {
        // 1. Actualizar el cronómetro
        timerDia += dt;

        if (!esNoche && timerDia >= D) {
            // Empieza la noche
            esNoche = true;
            timerDia = 0.0;
            this.sightRange = RN * sightRangeDia; // Se reduce por la noche
        } else if (esNoche && timerDia >= N) {
            // Termina la noche, empieza nuevo día
            esNoche = false;
            timerDia = 0.0;
            sightRangeDia = RD * sightRangeDia; // Se reduce para el nuevo día
            this.sightRange = sightRangeDia;
        }

        // 2. Llamar al comportamiento base de Sheep
        super.update(dt);
    }
}
```

### Trampas frecuentes ⚠️
- Olvidar restaurar `sightRange` al valor diurno cuando termina la noche.
- Olvidar llamar a `super.update(dt)` → el animal se queda quieto.
- No validar `RN` y `RD` en el rango `[0,1]`.
- Modificar `sightRange` directamente sin guardar el valor original del día.

---

## 📊 TIPO 2 – Observador en Modo Batch

### Patrón general
Crear una clase que implementa `EcoSysObserver` para recopilar estadísticas durante la simulación, y mostrarlo al final en la consola. Se activa desde `Main.java` con una nueva opción de línea de comandos (ej. `-ps r`).

### La interfaz `EcoSysObserver` – los 5 métodos

```java
public interface EcoSysObserver {
    void onRegister(double time, MapInfo map, List<AnimalInfo> animals);  // Al registrarse (inicio)
    void onReset(double time, MapInfo map, List<AnimalInfo> animals);     // Al resetear
    void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a); // Nace animal
    void onRegionSet(int row, int col, MapInfo map, RegionInfo r);        // Se cambia una región
    void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt); // Cada paso
}
```

### Cómo iterar las regiones (con `MapInfo`)

```java
// MapInfo implementa Iterable<MapInfo.RegionData>
// RegionData es un record con: int row(), int col(), RegionInfo r()
for (MapInfo.RegionData rd : map) {
    int fila = rd.row();
    int col  = rd.col();
    RegionInfo region = rd.r();
    int numAnimales = region.getAnimalsInfo().size(); // ✅ Así se obtiene el número de animales
}
```

### Esqueleto base – `PopObs` (cuenta veces que una especie supera límite `r`)

```java
package simulator.model;

public class PopObs implements EcoSysObserver {

    // ¿Cuántos animales de cada especie al FINAL del paso anterior?
    private Map<String, Integer> popPrev;
    // ¿Cuántos animales de cada especie al FINAL del paso actual?
    private Map<String, Integer> popActual;
    // Cuántas veces ha superado el límite cada especie
    private Map<String, Integer> superaciones;
    private double r;

    public PopObs(Controller ctrl, double r) {
        this.r = r;
        this.popPrev = new HashMap<>();
        this.popActual = new HashMap<>();
        this.superaciones = new HashMap<>();
        ctrl.addObserver(this);
    }

    @Override
    public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
        // Registramos la población inicial por especie (genetic code = especie)
        popActual.clear();
        superaciones.clear();
        for (AnimalInfo a : animals) {
            String especie = a.getGeneticCode();
            popActual.merge(especie, 1, Integer::sum);
            superaciones.putIfAbsent(especie, 0);
        }
    }

    @Override
    public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
        popPrev = new HashMap<>(popActual); // Guardamos el estado anterior
        popActual.clear();
        for (AnimalInfo a : animals) {
            String especie = a.getGeneticCode();
            popActual.merge(especie, 1, Integer::sum);
        }
        // Comprobamos superación: la pop era <= r antes y > r ahora
        for (String especie : popActual.keySet()) {
            double antes = popPrev.getOrDefault(especie, 0);
            double ahora = popActual.get(especie);
            if (antes <= r && ahora > r) {
                superaciones.merge(especie, 1, Integer::sum);
            }
        }
    }

    // Llamar al final de la simulación en Main.java
    public void write() {
        System.out.println("Population change for r=" + (int)r + ":");
        for (Map.Entry<String, Integer> e : superaciones.entrySet()) {
            System.out.println(e.getKey() + " => " + e.getValue());
        }
    }

    @Override
    public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
        popPrev.clear(); popActual.clear(); superaciones.clear();
        onRegister(time, map, animals);
    }
    @Override
    public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {}
    @Override
    public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {}
}
```

### Integración en `Main.java`

```java
// 1. Añadir la opción en parseArgs():
options.addOption(Option.builder("ps")
    .hasArg().argName("r")
    .desc("Population stability limit")
    .build());

// 2. Parsear el valor en parse():
private static Double psLimit = null;
// ...
if (cmd.hasOption("ps")) {
    psLimit = Double.parseDouble(cmd.getOptionValue("ps"));
}

// 3. Crear el observador en startBatchMode() y llamar a write():
PopObs popObs = null;
if (psLimit != null) {
    popObs = new PopObs(ctrl, psLimit);
}
ctrl.run(steps, dt, outStream); // <- La simulación corre aquí
if (popObs != null) {
    popObs.write();
}
```

### Trampas frecuentes ⚠️
- Usar `popPrev = popActual` en lugar de `popPrev = new HashMap<>(popActual)` → ambas variables apuntarían al mismo mapa.
- Comprobar la superación **antes** de actualizar `popActual`.
- Olvidar inicializar las especies en `onRegister` → `NullPointerException` en el primer `onAdvance`.
- Usar el nombre del animal en lugar del `geneticCode` para identificar la especie.

---

## 🖥️ TIPO 3 – Diálogo con Tabla Estadística (GUI)

### Patrón general
Añadir un botón en el `ControlPanel` que abre un diálogo. El diálogo tiene: texto descriptivo, tabla con historial, control de límite (`JTextField` o `JSpinner`), botón **SET** y botón **CLOSE**.

### Arquitectura: 2 clases + 1 botón

```
ControlPanel.java         --> Añadir botón + declarar el Dialog como atributo
MiDialog.java             --> El JDialog (o usa AbstractTableModel directamente)
MiTableModel.java         --> AbstractTableModel + EcoSysObserver (opcional)
```

### Cuándo usar `DefaultTableModel` vs `AbstractTableModel`

| | `DefaultTableModel` | `AbstractTableModel` |
|---|---|---|
| Ventaja | Más rápido de codificar | Más control, más limpio |
| Cuándo | Si el diálogo maneja todo | Si separas la lógica en una clase |
| `isCellEditable` | Sobreescribir para retornar `false` | Sobreescribir para retornar `false` |

### Esqueleto del diálogo (con `DefaultTableModel` – patrón AgeDialog)

```java
package simulator.view;

public class MiDialog extends JDialog implements EcoSysObserver {

    class InfoPaso {
        double time;
        List<Double> datosOriginales; // ✅ Guardar los datos RAW, nunca los calculados
        InfoPaso(double time, List<Double> datos) {
            this.time = time;
            this.datosOriginales = new ArrayList<>(datos);
        }
    }

    private List<InfoPaso> historial = new ArrayList<>();
    private DefaultTableModel tableModel;
    private JLabel helpLabel;
    private JTextField limitField;  // O JSpinner
    private double limite = 3.0;    // Valor inicial según enunciado
    private Controller ctrl;

    MiDialog(Controller ctrl) {
        super((Frame) null, true);
        this.ctrl = ctrl;
        initGUI();
        this.ctrl.addObserver(this); // ✅ Registrarse siempre al final del constructor
    }

    private void initGUI() {
        setTitle("Mi Estadística");
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        setContentPane(mainPanel);

        // 1. Texto explicativo (ENCIMA de la tabla, requerido por el enunciado)
        helpLabel = new JLabel("Estadística con límite: " + limite);
        JPanel helpPanel = new JPanel();
        helpPanel.add(helpLabel);
        mainPanel.add(helpPanel);

        // 2. Tabla
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        tableModel.setColumnIdentifiers(new String[]{"Time", "Col1", "Col2"});
        mainPanel.add(new JScrollPane(new JTable(tableModel)));

        // 3. Controles: label + campo + botones
        JPanel controls = new JPanel();
        controls.add(new JLabel("Age Limit")); // Texto a la IZQUIERDA del campo
        limitField = new JTextField(String.valueOf(limite), 8);
        controls.add(limitField);

        JButton set = new JButton("SET");
        set.addActionListener(e -> setLimit());
        controls.add(set);

        JButton close = new JButton("CLOSE");
        close.addActionListener(e -> setVisible(false));
        controls.add(close);
        mainPanel.add(controls);
    }

    private void setLimit() {
        try {
            double nuevo = Double.parseDouble(limitField.getText().trim());
            if (nuevo <= 0) {
                ViewUtils.showErrorMsg("El límite debe ser mayor que 0");
                return; // ✅ return aquí, no seguir ejecutando
            }
            this.limite = nuevo;
            helpLabel.setText("Estadística con límite: " + limite);
            // a) Borrar tabla
            tableModel.setRowCount(0);
            // b) Reconstruir con nuevo límite
            recalcularTabla();
            // c) Cerrar
            setVisible(false);
        } catch (NumberFormatException ex) {
            ViewUtils.showErrorMsg("Introduce un número válido");
        }
    }

    private void recalcularTabla() {
        // ✅ Recalcular usando los datos RAW del historial y el límite actual
        for (InfoPaso paso : historial) {
            int young = 0, old = 0;
            for (double edad : paso.datosOriginales) {
                if (edad < limite) young++;
                else old++;
            }
            tableModel.addRow(new Object[]{
                String.format("%.2f", paso.time), young, old
            });
        }
    }

    @Override
    public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
        historial.clear();
        tableModel.setRowCount(0);
    }

    @Override
    public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
        historial.clear();
        tableModel.setRowCount(0);
    }

    @Override
    public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
        // 1. Guardar datos RAW
        List<Double> edades = new ArrayList<>();
        for (AnimalInfo a : animals) edades.add(a.getAge());
        historial.add(new InfoPaso(time, edades));

        // 2. Calcular fila con límite actual
        int young = 0, old = 0;
        for (double edad : edades) {
            if (edad < limite) young++;
            else old++;
        }
        final int y = young, o = old;

        // 3. Actualizar tabla en el hilo de Swing ✅
        SwingUtilities.invokeLater(() ->
            tableModel.addRow(new Object[]{String.format("%.2f", time), y, o})
        );
    }

    @Override
    public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {}
    @Override
    public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {}

    public void open(Frame parent) {
        setLocation(
            parent.getLocation().x + parent.getWidth() / 2 - getWidth() / 2,
            parent.getLocation().y + parent.getHeight() / 2 - getHeight() / 2);
        pack();
        setVisible(true);
    }
}
```

### Esqueleto del diálogo con `AbstractTableModel` separado (patrón ExtinctDialog)

Cuando el enunciado pide una clase de modelo separada, la lógica se divide así:

```java
// MiTableModel.java
public class MiTableModel extends AbstractTableModel implements EcoSysObserver {

    class InfoPaso {
        double time;
        List<Integer> regionCounts; // Datos RAW: nº de animales por región
        InfoPaso(double time, List<Integer> counts) {
            this.time = time;
            this.regionCounts = new ArrayList<>(counts);
        }
    }

    private List<InfoPaso> historial = new ArrayList<>();
    private int limite;

    MiTableModel(Controller ctrl, int limite) {
        this.limite = limite;
        ctrl.addObserver(this);
    }

    // ✅ Método público para que el Dialog cambie el límite y refresque
    public void setLimite(int nuevoLimite) {
        this.limite = nuevoLimite;
        fireTableDataChanged(); // Redibuja toda la tabla con el nuevo límite
    }

    @Override
    public int getRowCount() { return historial.size(); }
    @Override
    public int getColumnCount() { return 3; }
    @Override
    public String getColumnName(int col) {
        return new String[]{"Time", "In Danger", "Not In Danger"}[col];
    }
    @Override
    public boolean isCellEditable(int r, int c) { return false; }

    @Override
    public Object getValueAt(int row, int col) {
        InfoPaso paso = historial.get(row);
        switch (col) {
            case 0: return String.format("%.2f", paso.time);
            case 1: // Calcular al vuelo con el límite actual
                return paso.regionCounts.stream()
                    .filter(c -> c > 0 && c < limite).count();
            case 2:
                return paso.regionCounts.stream()
                    .filter(c -> c > 2 * limite).count();
        }
        return null;
    }

    @Override
    public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
        List<Integer> counts = new ArrayList<>();
        for (MapInfo.RegionData rd : map) {
            counts.add(rd.r().getAnimalsInfo().size());
        }
        historial.add(new InfoPaso(time, counts));
        SwingUtilities.invokeLater(this::fireTableDataChanged);
    }

    @Override
    public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
        historial.clear();
        // Opcionalmente añadir el estado inicial
    }
    @Override
    public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
        historial.clear();
        onRegister(time, map, animals);
    }
    @Override
    public void onAnimalAdded(...) {}
    @Override
    public void onRegionSet(...) {}
}
```

```java
// MiDialog.java – conecta el modelo con el JSpinner
public class MiDialog extends JDialog {

    private MiTableModel tableModel;
    private JSpinner spinner;
    private JLabel helpLabel;
    private int limite = 5; // Valor inicial según enunciado

    public MiDialog(Controller ctrl) {
        super((Frame) null, true);
        initGUI(ctrl);
    }

    private void initGUI(Controller ctrl) {
        // ...
        this.tableModel = new MiTableModel(ctrl, limite);
        InfoTable tabla = new InfoTable("Extinción", tableModel);
        // ...
        spinner = new JSpinner(new SpinnerNumberModel(5, 1, 10000, 1));

        JButton set = new JButton("SET");
        set.addActionListener(e -> {
            this.limite = (Integer) spinner.getValue();
            helpLabel.setText("Extinction danger limit: " + limite);
            tableModel.setLimite(limite); // ✅ El modelo refresca la tabla
            setVisible(false);
        });
    }
}
```

### Añadir el botón en `ControlPanel.java`

```java
// 1. Declarar el diálogo como atributo de la clase:
private MiDialog miDialog;

// 2. En initGUI(), inicializarlo y crear el botón:
this.miDialog = new MiDialog(this.ctrl);

JButton miBoton = new JButton();
miBoton.setToolTipText("Mi estadística");
miBoton.setIcon(new ImageIcon("resources/icons/stats.png")); // El icono del enunciado
miBoton.addActionListener(e -> this.miDialog.open(ViewUtils.getWindow(this)));
this.toolaBar.add(miBoton);
```

### Trampas frecuentes ⚠️

- **El error más común:** Guardar en `InfoPaso` los contadores ya calculados con el límite antiguo, y luego no poder recalcularlos. **Siempre guardar los datos RAW** (edades, tamaños de región, etc.).
- Llamar `fireTableDataChanged()` o `addRow()` directamente desde `onAdvance` sin `SwingUtilities.invokeLater()` → race condition con el hilo de Swing.
- No registrarse como observador en el constructor del diálogo o del modelo.
- En el `JSpinner`: no usar `(Integer) spinner.getValue()` para obtener el valor (requiere el cast).
- En `setLimit()` con `JTextField`: no poner `return` después de mostrar el error de validación → el código continúa ejecutándose con el valor inválido.
- El texto descriptivo va **encima** de la tabla, no debajo.

---

## 🔑 Reglas de Oro para el Examen

1. **Datos RAW en el historial, cálculos en `getValueAt`:** No guardes resultados ya calculados si el límite puede cambiar después.
2. **`SwingUtilities.invokeLater`** para toda actualización de la UI desde un callback del simulador.
3. **Validar parámetros siempre** en constructores de animales → `IllegalArgumentException`.
4. **`super.update(dt)` siempre** en subclases de `Animal` → sin esto el animal se queda inmóvil.
5. **`ctrl.addObserver(this)`** al final del constructor del observador.
6. **Restaurar el estado** al salir de una condición temporal (noche → día: restaurar `sightRange`).
7. **`return` inmediato** después de `showErrorMsg()` para no continuar con datos inválidos.
8. **`popPrev = new HashMap<>(popActual)`** y nunca `popPrev = popActual` (referencias vs copia).
