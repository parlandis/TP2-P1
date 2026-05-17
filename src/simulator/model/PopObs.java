package simulator.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import simulator.control.Controller;
import simulator.launcher.Main;

public class PopObs implements EcoSysObserver {
	
	private Map<String, Integer> poblacionEspAct; // Nombre especie -> población al final del paso
	private Map<String, Integer> poblacionEsp;    // Nombre especie -> población al inicio del paso
	private Map<String, Integer> numLimite;       // Nombre especie -> veces que ha superado el límite
	
	public PopObs(Controller ctrl) {
		poblacionEspAct = new HashMap<>();
		poblacionEsp = new HashMap<>();
		numLimite = new TreeMap<>(); // TreeMap para que la salida se ordene alfabéticamente
		
		ctrl.addObserver(this);
	}
	
	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		for (AnimalInfo a : animals) {
			String species = a.getGeneticCode();
			poblacionEspAct.put(species, poblacionEspAct.getOrDefault(species, 0) + 1);
			numLimite.putIfAbsent(species, 0);
		}
	}

	public void write() {
		System.out.println("Population change for r=" + Main.popChange + ":");
		for (Map.Entry<String, Integer> e : numLimite.entrySet()) {
			System.out.println(e.getKey() + " => " + e.getValue());
		}
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		poblacionEspAct.clear();
		poblacionEsp.clear();
		numLimite.clear();
		onRegister(time, map, animals);
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		// ¡CRÍTICO! No debemos sumar al mapa poblacionEspAct aquí, ya que los nacimientos
		// ocurren durante el paso de simulación. Al final del paso, en onAdvance, 
		// contaremos los animales reales de la lista. Si lo sumáramos aquí antes del onAdvance,
		// la población inicial y final del paso parecerían idénticas, ocultando el incremento.
		String species = a.getGeneticCode();
		numLimite.putIfAbsent(species, 0);
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {}
	
	@Override
	public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		// poblacionEspAct contiene la población al final del paso anterior (es decir, el inicio de este paso).
		// Hacemos un snapshot de esa población inicial:
		poblacionEsp = new HashMap<>(poblacionEspAct);
		poblacionEspAct.clear();
		
		// 1. Contamos la población real al final de este paso
		for (AnimalInfo a : animals) {
			String species = a.getGeneticCode();
			poblacionEspAct.put(species, poblacionEspAct.getOrDefault(species, 0) + 1);
			numLimite.putIfAbsent(species, 0);
		}
		
		// 2. Comparamos los valores para ver quién ha superado el límite
		Set<String> keys = poblacionEspAct.keySet();
		for (String s : keys) {
			int prev = poblacionEsp.getOrDefault(s, 0); // Evitamos NPE si la especie no existía antes del paso
			int act = poblacionEspAct.get(s);
			
			// Si al principio del paso era <= r y al final es > r
			if (prev <= Main.popChange && act > Main.popChange) {
				numLimite.put(s, numLimite.getOrDefault(s, 0) + 1);
			}
		}
	}
}
