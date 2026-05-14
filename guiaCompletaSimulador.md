# 📚 Guía Completa del Proyecto: EcoSimulator

> Simulador biológico de ecosistemas con interacción dinámica entre especies y regiones.  
> Stack: **Java Swing**, patrón **MVC**, **Observer**, **Factory** y **Builder**.

---

## 📋 Índice

1. [Arquitectura General](#1-arquitectura-general)
2. [Modelo — El Motor Biológico](#2-modelo--el-motor-biológico)
3. [Control — El Puente](#3-control--el-puente)
4. [Vista — Interfaz Gráfica](#4-vista--interfaz-gráfica)
5. [Factorías y Builders](#5-factorías-y-builders)
6. [Flujo Completo de un Paso](#6-flujo-completo-de-un-paso-advance)
7. [El Patrón Observer en Detalle](#7-el-patrón-observer-en-detalle)
8. [Estructura de Paquetes](#8-estructura-de-paquetes)

---

## 1. Arquitectura General

El proyecto sigue una arquitectura **MVC (Modelo-Vista-Controlador)** desacoplada mediante el patrón **Observer**. La regla fundamental es:

> **La Vista NUNCA llama directamente al Modelo. Solo habla con el Controlador.**  
> **El Modelo NUNCA conoce a la Vista. Solo notifica a sus observadores.**

```
┌──────────────────────────────────────────────────────────────┐
│  VISTA (view/)                                               │
│  ControlPanel, MainWindow, MapViewer, *TableModel...         │
│  → Llama a Controller para ejecutar acciones                 │
│  → Implementa EcoSysObserver para recibir cambios            │
└────────────────────────┬─────────────────────────────────────┘
                         │ llama a
                         ▼
┌──────────────────────────────────────────────────────────────┐
│  CONTROL (control/)                                          │
│  Controller.java                                             │
│  → Recibe órdenes de la Vista                                │
│  → Las delega al Simulador                                   │
│  → Gestiona la lista de observadores                         │
└────────────────────────┬─────────────────────────────────────┘
                         │ delega a
                         ▼
┌──────────────────────────────────────────────────────────────┐
│  MODELO (model/)                                             │
│  Simulator → RegionManager → Region[][] + Animal[]           │
│  → Contiene toda la lógica biológica                         │
│  → Notifica cambios a los EcoSysObserver                     │
└──────────────────────────────────────────────────────────────┘
```

**¿Por qué este diseño?**  
Si el modelo notificara directamente a las vistas concretas, cada vez que añades una vista nueva tienes que modificar el modelo. Con Observer, el modelo solo conoce la interfaz `EcoSysObserver` y no sabe cuántas vistas hay, ni qué son.

---

## 2. Modelo — El Motor Biológico

### 2.1 `Simulator.java` — El Núcleo

Es la clase más importante. Gestiona:
- El tiempo global (`time`)
- La lista de todos los animales vivos (`listaA`)
- La cuadrícula de regiones (delegada en `RegionManager`)
- La lista de observadores (`listaObserv`)

**El bucle principal (`advance(double dt)`)** ejecuta estos pasos en orden cada vez que avanza un tick:

```
1. time += dt                           → Incrementa el reloj global
2. Eliminar animales DEAD               → Iterator para evitar ConcurrentModificationException
3. Animal.update(dt) para cada vivo    → Cada animal decide su comportamiento
4. RegionManager.updateAllRegions(dt)  → Regeneración de comida en regiones
5. Recoger crías (deliverBaby)         → Los animales preñados dan a luz
6. Añadir crías al sistema             → Se notifica onAnimalAdded por cada cría
7. notifyOnAdvance(dt)                 → Notifica a todos los observadores
```

> ⚠️ **Importante:** Las crías se recogen en una lista temporal ANTES de añadirlas, para no modificar `listaA` mientras se itera sobre ella (evitar `ConcurrentModificationException`).

### 2.2 `RegionManager.java` — La Cuadrícula

Gestiona una matriz `Region[rows][cols]` y un mapa `Map<Animal, Region>` que relaciona cada animal con su región actual.

**Responsabilidades clave:**

| Método | Qué hace |
|--------|----------|
| `registerAnimal(a)` | Inicializa el animal (`a.init(this)`), calcula su región por posición y lo añade a ella |
| `updateAnimalRegion(a)` | Si el animal se ha movido a otra cuadrícula, actualiza el mapa `animalRegion` |
| `unregisterAnimal(a)` | Lo elimina de su región cuando muere |
| `getFood(a, dt)` | Delega en la región del animal: `animalRegion.get(a).getFood(a, dt)` |
| `getAnimalsInRange(a, filter)` | **Optimización:** Solo mira las regiones que se solapan con el círculo de visión, no todo el mapa |

**Cómo calcula la región de un animal por posición:**
```java
int col = (int)(posX / regWidth);   // regWidth = width / cols
int row = (int)(posY / regHeight);  // regHeight = height / rows
```

### 2.3 `Region.java` — Una Celda del Mapa

La clase base. Contiene:
- `List<Animal> animales` — los animales que hay en esta celda
- `int nH` — número de herbívoros (actualizado al añadir/quitar animales)
- `int nC` — número de carnívoros

Las subclases implementan la lógica real:

| Clase | Comportamiento |
|-------|----------------|
| `DefaultRegion` | Comida infinita. `getFood` devuelve `60 * e^(-max(0,nH-5)*2) * dt`. A más herbívoros, menos comida por animal |
| `DynamicSupplyRegion` | Tiene un depósito de comida `food` que se consume y se regenera aleatoriamente |
| `DryRegion` | Como `DefaultRegion` pero con ciclos de sequía que reducen la comida por un factor R1 |

**La fórmula de DefaultRegion explicada:**
```
60 * e^(-max(0, nH-5) * 2) * dt
```
- Si hay ≤5 herbívoros: `e^0 = 1` → comida completa
- Con 6 herbívoros: `e^(-2) ≈ 0.135` → solo 13.5% de comida
- Con 10 herbívoros: `e^(-10) ≈ 0.00005` → casi nada

Simula el sobrepastoreo de forma realista.

### 2.4 `Animal.java` — La Clase Abstracta Base

Define los atributos comunes y la lógica compartida. Las subclases (`Sheep`, `Wolf`) implementan el comportamiento específico.

**Atributos importantes:**

| Atributo | Tipo | Descripción |
|----------|------|-------------|
| `geneticCode` | String | Identifica la especie (ej: `"Oveja"`, `"Lobo"`) |
| `diet` | `Diet` | `HERBIVORE` o `CARNIVORE` |
| `state` | `State` | `NORMAL`, `HUNGER`, `MATE`, `DANGER`, `DEAD` |
| `energy` | double | 0-100. Muere si llega a 0 |
| `age` | double | Se incrementa en `update`. Muere si supera MAX_AGE |
| `desire` | double | 0-100. Si supera DESIRE_THRESHOLD, busca pareja |
| `baby` | Animal | No nulo si está preñada |
| `regionMngr` | AnimalMapView | Referencia al mapa, para pedir comida y ver animales cercanos |

**Máquina de estados de la Oveja:**
```
NORMAL ──(ve lobo)──────────────────► DANGER ──(lobo desaparece, desire<65)──► NORMAL
  │                                      │   ──(lobo desaparece, desire≥65)──► MATE
  └──(desire ≥ 65 y no hay peligro)──► MATE ──(ve lobo)──────────────────────► DANGER
                                         │   ──(desire < 65 sin pareja)──────► NORMAL
```

**Constructor de reproducción:**  
Cuando dos animales se aparean, se crea el bebé con `new Sheep(p1, p2)`:
- La energía inicial es la media de los dos progenitores
- La velocidad y vista son la media ± mutación aleatoria (20%)
- La estrategia de selección de pareja se hereda del progenitor 2

### 2.5 `EcoSysObserver` — La Interfaz del Observador

```java
public interface EcoSysObserver {
    void onRegister(double time, MapInfo map, List<AnimalInfo> animals);
    void onReset(double time, MapInfo map, List<AnimalInfo> animals);
    void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a);
    void onRegionSet(int row, int col, MapInfo map, RegionInfo r);
    void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt);
}
```

| Método | Cuándo se llama | Uso típico |
|--------|-----------------|------------|
| `onRegister` | Al registrar el observador | Inicializar el estado de la vista |
| `onReset` | Al hacer reset del simulador | Limpiar tablas, contadores |
| `onAnimalAdded` | Cada vez que nace/se añade un animal | Actualizar contadores de especie |
| `onRegionSet` | Al cambiar una región por tipo | Actualizar tabla de regiones |
| `onAdvance` | **Después de cada paso** | ← Aquí va la lógica principal de observación |

> **Nota:** El parámetro `List<AnimalInfo>` es **inmutable** (`Collections.unmodifiableList`). No puedes modificarla, solo leerla. Esto protege el modelo.

### 2.6 `AnimalInfo` — Vista de Solo Lectura de un Animal

```java
public interface AnimalInfo {
    State getState();
    Vector2D getPosition();
    String getGeneticCode();   // ← nombre de la especie ("Oveja", "Lobo")
    Diet getDiet();             // ← HERBIVORE o CARNIVORE
    double getSpeed();
    double getSightRange();
    double getEnergy();         // ← 0.0 a 100.0
    double getAge();            // ← en segundos de simulación
    Vector2D getDestination();
    boolean isPregnant();
}
```

---

## 3. Control — El Puente

### `Controller.java`

Actúa como **Facade**: la Vista tiene una sola referencia (`ctrl`) y no necesita conocer ni `Simulator` ni `RegionManager`.

```java
// Lo que puede hacer la Vista a través del Controller:
ctrl.advance(dt);                    // Avanzar un paso
ctrl.reset(cols, rows, w, h);       // Reiniciar
ctrl.loadData(JSONObject json);      // Cargar animales y regiones desde JSON
ctrl.addObserver(EcoSysObserver o); // Registrar una vista como observador
ctrl.removeObserver(EcoSysObserver o);
```

**`loadData` en detalle:**
1. Llama a `setRegions(json)` → itera el array `"regions"` del JSON y llama a `sim.setRegion(r, c, spec)` para cada celda
2. Itera el array `"animals"` → por cada grupo, llama a `sim.addAnimal(spec)` tantas veces como indique `"amount"`

---

## 4. Vista — Interfaz Gráfica

### 4.1 Componentes de la Ventana Principal

```
MainWindow
├── ControlPanel (JPanel, parte superior)
│   └── JToolBar
│       ├── [Open] [Map] [Regions] [Stats] ... ← Botones de acción
│       ├── [Run] [Stop]
│       ├── Steps: [JSpinner]
│       └── Delta-Time: [JTextField]
├── Panel Central con tablas
│   ├── SpeciesTableModel  ← Actualizada via EcoSysObserver.onAdvance
│   └── RegionsTableModel  ← Actualizada via EcoSysObserver.onRegionSet
└── StatusBar (JPanel, parte inferior)
    └── Muestra tiempo y número de animales
```

### 4.2 Cómo Funciona un `TableModel`

Un `TableModel` (concretamente `AbstractTableModel`) es el "cerebro" de una `JTable`. Cuando los datos cambian, llamas a `fireTableDataChanged()` y la tabla se repinta automáticamente.

Para el simulador, los `TableModel` implementan además `EcoSysObserver`, de modo que:
- Se suscriben ellos mismos: `ctrl.addObserver(this)` en el constructor
- Reciben `onAdvance` en cada paso y actualizan sus datos internos
- Llaman a `fireTableDataChanged()` para que Swing repinte la tabla

```java
// Métodos obligatorios de AbstractTableModel:
int getRowCount()                         // Número de filas
int getColumnCount()                      // Número de columnas
Object getValueAt(int row, int col)       // Contenido de una celda
String getColumnName(int col)             // Cabecera de columna
```

### 4.3 Cómo Funciona un `JDialog`

Un `JDialog` es una ventana secundaria (hija de la principal). Los puntos clave:

```java
// Constructor: el segundo parámetro 'true' hace el diálogo MODAL
// (bloquea la ventana padre mientras está abierto)
super(parent, true);

// Para abrirlo (centrado en la ventana padre):
public void open(Frame parent) {
    setLocation(
        parent.getX() + parent.getWidth() / 2 - getWidth() / 2,
        parent.getY() + parent.getHeight() / 2 - getHeight() / 2
    );
    pack();           // Ajusta el tamaño al contenido
    setVisible(true); // Muestra el diálogo (bloquea si es modal)
}

// Para cerrarlo (sin destruirlo, solo ocultarlo):
setVisible(false);
```

### 4.4 `ControlPanel` — Donde Se Añaden los Botones

Es la clase que puedes modificar para añadir nuevos botones. El patrón es siempre:

```java
// 1. Declarar el botón como atributo
private JButton miBoton;

// 2. En initGUI(): crear, configurar y añadir a la toolbar
this.miBoton = new JButton();
this.miBoton.setIcon(new ImageIcon("resources/icons/miIcono.png"));
this.miBoton.setToolTipText("Descripción del botón");
this.miBoton.addActionListener(e -> miDialog.open(ViewUtils.getWindow(this)));
this.toolaBar.add(this.miBoton);
```

> **`ViewUtils.getWindow(this)`** busca el `Frame` (ventana principal) que contiene este panel. Se usa como padre del `JDialog`.

---

## 5. Factorías y Builders

### El Problema que Resuelven

Necesitamos crear objetos (`Sheep`, `DryRegion`, etc.) a partir de un JSON. No queremos que el `Simulator` o el `Controller` tengan código como `if (type.equals("sheep")) return new Sheep(...)`. Eso viola el principio Open/Closed.

### La Solución: Factory + Builder

```
JSON {"type": "dry", "data": {...}}
         │
         ▼
BuilderBasedFactory<Region>.createInstance(json)
         │
         ├── Lee "type" = "dry"
         ├── Busca en su Map<String, Builder>: builders.get("dry")
         │       → Encuentra DryRegionBuilder
         ├── Extrae "data" del JSON
         └── Llama a DryRegionBuilder.createInstance(data)
                     → return new DryRegion(r1, r2, n, m)
```

### `Builder<T>` — Clase Abstracta

```java
public abstract class Builder<T> {
    private String typeTag;  // El "type" del JSON, ej: "dry"
    private String desc;     // Descripción para mostrar en la GUI

    // Método de fábrica que las subclases DEBEN implementar
    protected abstract T createInstance(JSONObject data);

    // Hook method: las subclases pueden sobreescribir para documentar sus parámetros
    protected void fillInData(JSONObject o) { }
}
```

### `BuilderBasedFactory<T>` — La Factoría

```java
public class BuilderBasedFactory<T> implements Factory<T> {
    private Map<String, Builder<T>> builders; // type → builder

    public T createInstance(JSONObject info) {
        String type = info.getString("type");
        Builder<T> builder = builders.get(type); // busca el builder correcto
        JSONObject data = info.optJSONObject("data"); // puede no haber "data"
        return builder.createInstance(data);
    }
}
```

### Registro en `Main.initFactories()`

Todo builder debe registrarse aquí. Hay **3 factorías** independientes:

```java
// 1. Estrategias de selección (cómo eligen pareja/presa)
List<Builder<SelectionStrategy>> selBuilders = new ArrayList<>();
selBuilders.add(new SelectFirstBuilder());
selBuilders.add(new SelectClosestBuilder());
selBuilders.add(new SelectYoungestBuilder());
_strategyFactory = new BuilderBasedFactory<>(selBuilders);

// 2. Animales (necesitan la factoría de estrategias)
List<Builder<Animal>> animalBuilders = new ArrayList<>();
animalBuilders.add(new SheepBuilder(_strategyFactory));
animalBuilders.add(new WolfBuilder(_strategyFactory));
_animalFactory = new BuilderBasedFactory<>(animalBuilders);

// 3. Regiones
List<Builder<Region>> regionBuilders = new ArrayList<>();
regionBuilders.add(new DefaultRegionBuilder());
regionBuilders.add(new DynamicSupplyRegionBuilder());
regionBuilders.add(new DryRegionBuilder()); // ← Nuevas regiones van aquí
_regionFactory = new BuilderBasedFactory<>(regionBuilders);
```

---

## 6. Flujo Completo de un Paso (`advance`)

```
Usuario pulsa [Run] en ControlPanel
    │
    ▼
ControlPanel.runSim(n, dt)
    │  llama a
    ▼
Controller.advance(dt)
    │  delega en
    ▼
Simulator.advance(dt)
    │
    ├─ 1. time += dt
    │
    ├─ 2. Elimina muertos de listaA y los desregistra del RegionManager
    │
    ├─ 3. Para cada Animal vivo:
    │      Animal.update(dt)
    │          ├─ Ejecuta lógica del estado actual (doNormal/doDanger/doMate/...)
    │          ├─ Se mueve: pos += dirección * velocidad * dt
    │          ├─ Envejece: age += dt
    │          ├─ Consume energía: energy -= foodDropRate * dt
    │          └─ Come de la región: energy += regionMngr.getFood(this, dt)
    │      RegionManager.updateAnimalRegion(a)  ← Actualiza en qué celda está
    │
    ├─ 4. RegionManager.updateAllRegions(dt)
    │      Para cada Region: region.update(dt)  ← Regenera comida
    │
    ├─ 5. Recoge crías de animales preñados
    │      Animal.deliverBaby() → devuelve la cría y pone baby=null
    │
    ├─ 6. Añade crías al sistema
    │      Simulator.addAnimal(bebe) → registerAnimal + notifyOnAnimalAdded
    │
    └─ 7. notifyOnAdvance(dt)
           Para cada EcoSysObserver:
               observer.onAdvance(time, reMng, animalesInmutables, dt)
               └─ SpeciesTableModel.onAdvance → actualiza tabla
               └─ StatusBar.onAdvance → actualiza tiempo y count
               └─ MapViewer.onAdvance → repinta el mapa
               └─ [Tu observador].onAdvance → tu lógica
```

---

## 7. El Patrón Observer en Detalle

### Ciclo de vida de un Observer

```
ctrl.addObserver(miObserver)          ← (1) Registro
    └─ sim.addObserver(miObserver)
        └─ listaObserv.add(miObserver)
        └─ miObserver.onRegister(...)  ← (2) Inicialización inmediata

ctrl.loadData(json)                   ← (3) Carga de datos
    └─ sim.setRegion(r, c, reg)
        └─ miObserver.onRegionSet(...)
    └─ sim.addAnimal(a)
        └─ miObserver.onAnimalAdded(...)

ctrl.advance(dt)                      ← (4) Cada paso
    └─ sim.advance(dt)
        └─ miObserver.onAdvance(...)   ← Tu lógica principal va aquí

ctrl.reset(...)                       ← (5) Reset
    └─ sim.reset(...)
        └─ miObserver.onReset(...)     ← Limpiar estado interno
```

### Regla de Oro del Observer

**`addObserver` SIEMPRE antes de `loadData`.**

```java
// ✅ CORRECTO — el observer recibe onRegister y luego todos los onAnimalAdded
ctrl.addObserver(miObserver);
ctrl.loadData(json);

// ❌ INCORRECTO — el observer se pierde todos los eventos de carga inicial
ctrl.loadData(json);
ctrl.addObserver(miObserver); // ← Solo recibirá onRegister, NO los animales cargados
```

---

## 8. Estructura de Paquetes

```
src/simulator/
├── launcher/
│   └── Main.java              ← Punto de entrada. Inicializa factorías, parsea CLI, arranca GUI/Batch
│
├── control/
│   └── Controller.java        ← Facade entre Vista y Modelo
│
├── model/
│   ├── Simulator.java         ← Motor central. Bucle advance, lista de observers
│   ├── RegionManager.java     ← Cuadrícula de regiones, rastreo de animales por celda
│   ├── Region.java            ← Clase base de región (abstract)
│   ├── DefaultRegion.java     ← Región con comida infinita por fórmula exponencial
│   ├── DynamicSupplyRegion.java ← Región con depósito de comida regenerable
│   ├── DryRegion.java         ← Región con ciclos de sequía
│   ├── Animal.java            ← Clase base de animal (abstract)
│   ├── Sheep.java             ← Oveja (herbívoro): estados NORMAL/DANGER/MATE
│   ├── Wolf.java              ← Lobo (carnívoro): caza herbívoros
│   ├── EcoSysObserver.java    ← Interfaz Observer con 5 métodos de notificación
│   ├── AnimalInfo.java        ← Interfaz de solo lectura de Animal
│   ├── MapInfo.java           ← Interfaz de solo lectura del mapa (RegionManager)
│   ├── RegionInfo.java        ← Interfaz de solo lectura de Region
│   ├── Diet.java              ← Enum: HERBIVORE, CARNIVORE
│   ├── State.java             ← Enum: NORMAL, HUNGER, MATE, DANGER, DEAD
│   └── SelectionStrategy.java ← Interfaz: select(me, candidatos) → Animal
│
├── factories/
│   ├── Factory<T>.java        ← Interfaz: createInstance(JSONObject) → T
│   ├── Builder<T>.java        ← Clase base para builders (typeTag + createInstance)
│   ├── BuilderBasedFactory<T> ← Implementación: mapea typeTag → Builder
│   ├── DefaultRegionBuilder   ← type="default"
│   ├── DynamicSupplyRegionBuilder ← type="dynamic"
│   ├── DryRegionBuilder       ← type="dry"
│   ├── SheepBuilder           ← type="sheep"
│   ├── WolfBuilder            ← type="wolf"
│   ├── SelectFirstBuilder     ← type="first"
│   ├── SelectClosestBuilder   ← type="closest"
│   └── SelectYoungestBuilder  ← type="youngest"
│
└── view/
    ├── MainWindow.java        ← Ventana raíz, organiza paneles
    ├── ControlPanel.java      ← Toolbar con botones. ÚNICA clase de vista modificable
    ├── MapViewer.java         ← Dibuja el mapa con cuadrícula y animales
    ├── MapWindow.java         ← Ventana secundaria que contiene MapViewer
    ├── SpeciesTableModel.java ← Tabla de animales por especie (Observer)
    ├── RegionsTableModel.java ← Tabla de regiones por celda (Observer)
    ├── StatusBar.java         ← Barra inferior: tiempo y número de animales
    ├── AgeTableModel.java     ← Tabla de jóvenes/viejos por paso (Observer)
    ├── AgeLimitStatistics.java ← Diálogo que muestra AgeTableModel
    ├── ChangeRegionsDialog.java ← Diálogo para cambiar regiones en tiempo real
    ├── InfoTable.java         ← Componente auxiliar para tablas informativas
    └── ViewUtils.java         ← Utilidades: showErrorMsg, getWindow, quit
```
