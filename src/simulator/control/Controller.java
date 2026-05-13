package simulator.control;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.Simulator;
import simulator.view.SimpleObjectViewer;
import simulator.view.SimpleObjectViewer.ObjInfo;

public class Controller {
	private Simulator sim;

	public Controller(Simulator sim) {
		this.sim = sim;
	}

	// Carga los datos iniciales de la simulación desde un objeto JSON
	public void loadData(JSONObject data) {
		// 1) Primero configuramos las regiones
		setRegions(data);

		// 2) Después añadimos los grupos de animales especificados
		if (data.has("animals")) {
			JSONArray animales = data.getJSONArray("animals");
			for (int i = 0; i < animales.length(); i++) {
				JSONObject aniGroup = animales.getJSONObject(i);
				int amount = aniGroup.getInt("amount"); // Cuántos animales de este tipo crear
				JSONObject spec = aniGroup.getJSONObject("spec"); // Especificación del animal

				// Añadimos el número indicado de animales al simulador
				for (int j = 0; j < amount; j++) {
					sim.addAnimal(spec);
				}
			}
		}
	}

	// Configura el tipo de región para rangos específicos de celdas
	public void setRegions(JSONObject rs) {
		if (rs.has("regions")) {
			JSONArray regiones = rs.getJSONArray("regions");
			for (int i = 0; i < regiones.length(); i++) {
				JSONObject regGroup = regiones.getJSONObject(i);

				int rStart, rEnd, cStart, cEnd;
				
				// Lógica para determinar el rango de FILAS:
				JSONArray rRange = regGroup.optJSONArray("row");
				if (rRange != null) { // Caso A: [inicio, fin]
					rStart = rRange.getInt(0);
					rEnd = rRange.getInt(1);
				} else if (regGroup.has("row")) { // Caso B: una única fila
					rStart = rEnd = regGroup.getInt("row");
				} else { // Caso C: si no se especifica, afecta a todas las filas
					rStart = 0;
					rEnd = sim.getMapInfo().getRows() - 1;
				}

				// Lógica idéntica para determinar el rango de COLUMNAS:
				JSONArray cRange = regGroup.optJSONArray("col");
				if (cRange != null) {
					cStart = cRange.getInt(0);
					cEnd = cRange.getInt(1);
				} else if (regGroup.has("col")) {
					cStart = cEnd = regGroup.getInt("col");
				} else {
					cStart = 0;
					cEnd = sim.getMapInfo().getCols() - 1;
				}

				JSONObject spec = regGroup.getJSONObject("spec");

				// Aplicamos el cambio de región a cada celda dentro del área calculada
				for (int r = rStart; r <= rEnd; r++) {
					for (int c = cStart; c <= cEnd; c++) {
						sim.setRegion(r, c, spec);
					}
				}
			}
		}
	}

	public void reset(int cols, int rows, int width, int height) {
		sim.reset(cols, rows, width, height);
	}

	public void advance(double dt) {
		sim.advance(dt);
	}

	public void addObserver(EcoSysObserver o) {
		sim.addObserver(o);
	}

	public void removeObserver(EcoSysObserver o) {
		sim.removeObserver(o);
	}

	// Funciona para poder coonvertir la info de los animales para el visor
	private List<ObjInfo> toAnimalsInfo(List<? extends AnimalInfo> animals) {
		List<ObjInfo> ol = new ArrayList<>(animals.size());
		for (AnimalInfo a : animals)
			ol.add(new ObjInfo(a.getGeneticCode(), (int) a.getPosition().getX(), (int) a.getPosition().getY(), (int) Math.round(a.getAge()) + 2));
		return ol;
	}

	// MODO BATCH: Ejecuta la simulación sin GUI hasta el tiempo t y escribe el resultado en JSON
	public void run(double t, double dt, boolean sv, OutputStream out) {
		// Guardamos el estado inicial
		JSONObject initState = sim.asJSON();

		SimpleObjectViewer view = null;
		if (sv) { // Si el visualizador simple está activado
			MapInfo m = sim.getMapInfo();
			view = new SimpleObjectViewer("[ECOSYSTEM]", m.getWidth(), m.getHeight(), m.getCols(), m.getRows());
			view.update(toAnimalsInfo(sim.getAnimals()), sim.getTime(), dt);
		}

		// Bucle de ejecución: avanza de dt en dt hasta alcanzar el tiempo t
		while (t > sim.getTime()) {
			sim.advance(dt);
			if (sv)
				view.update(toAnimalsInfo(sim.getAnimals()), sim.getTime(), dt);
		}

		// Guardamos el estado final y generamos el JSON de salida
		JSONObject finalState = sim.asJSON();
		JSONObject salida = new JSONObject();
		salida.put("in", initState);
		salida.put("out", finalState);

		PrintStream ps = new PrintStream(out);
		ps.println(salida.toString(2));

		if (sv) view.close();
	}

}
