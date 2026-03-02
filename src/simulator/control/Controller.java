package simulator.control;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;


import org.json.JSONArray;
import org.json.JSONObject;

import simulator.model.AnimalInfo;
import simulator.model.MapInfo;
import simulator.model.Simulator;
import simulator.view.SimpleObjectViewer;
import simulator.view.SimpleObjectViewer.ObjInfo;

public class Controller { 
	private Simulator sim;

	public Controller(Simulator sim) {
		this.sim = sim;
	}

	public void loadData(JSONObject data) {

		// Si hay regiones se cargan primero
		if (data.has("regions")) {
			JSONArray regiones = data.getJSONArray("regions");
			for (int i = 0; i < regiones.length(); i++) {
				JSONObject regGroup = regiones.getJSONObject(i);

				int rStart, rEnd, cStart, cEnd;
				//Determinamos el rango de filas con el inicio y el final 
				JSONArray rRange = regGroup.optJSONArray("row");
				if (rRange != null) {
					rStart = rRange.getInt(0);
					rEnd = rRange.getInt(1);
				} else {
					rStart = rEnd = regGroup.getInt("row");
				}

				JSONArray cRange = regGroup.optJSONArray("col");
				if (cRange != null) {
					cStart = cRange.getInt(0);
					cEnd = cRange.getInt(1);
				} else {
					cStart = cEnd = regGroup.getInt("col");
				}

				JSONObject spec = regGroup.getJSONObject("spec");

				for (int r = rStart; r <= rEnd; r++) {
					for (int c = cStart; c <= cEnd; c++) {
						sim.setRegion(r, c, spec);
					}
				}
			}
		}

		if (data.has("animals")) {
			JSONArray animales = data.getJSONArray("animals");
			for (int i = 0; i < animales.length(); i++) {
				JSONObject aniGroup = animales.getJSONObject(i);
				int amount = aniGroup.getInt("amount");
				JSONObject spec = aniGroup.getJSONObject("spec");

				for (int j = 0; j < amount; j++) {
					sim.addAnimal(spec);
				}
			}
		}
	}
    //Funciona para poder coonvertir la info de los animales para el visor
	private List<ObjInfo> toAnimalsInfo(List<? extends AnimalInfo> animals) {
		List<ObjInfo> ol = new ArrayList<>(animals.size());
		for (AnimalInfo a : animals)
			ol.add(new ObjInfo(a.getGeneticCode(), (int) a.getPosition().getX(), (int) a.getPosition().getY(), (int) Math.round(a.getAge()) + 2));
		return ol;
	}

	public void run(double t, double dt, boolean sv, OutputStream out) {
		
		JSONObject initState = new JSONObject();
		JSONObject finalState = new JSONObject();
		//Estado inicial de la simulación
		initState = sim.asJSON();

		SimpleObjectViewer view = null;
		if (sv) {
			MapInfo m = sim.getMapInfo();
			view = new SimpleObjectViewer("[ECOSYSTEM]", m.getWidth(), m.getHeight(), m.getCols(), m.getRows());
			view.update(toAnimalsInfo(sim.getAnimals()), sim.getTime(), dt);
		}
		
		//Bucle de simulación
		while (t > sim.getTime()) {
			sim.advance(dt);
			if (sv) view.update(toAnimalsInfo(sim.getAnimals()), sim.getTime(), dt);
		}
		
		finalState = sim.asJSON();
		JSONObject salida = new JSONObject();
		salida.put("in", initState);
		salida.put("out", finalState);

		PrintStream ps = new PrintStream(out);
		ps.println(salida.toString(2));

		if (sv) view.close();
	}

}
