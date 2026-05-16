package simulator.model;

import java.security.spec.ECGenParameterSpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import simulator.control.Controller;

public class DecliveObs implements EcoSysObserver{
	private Map<String, Integer> prevPop = new HashMap<>();
	private Map<String, Integer> declineCont = new TreeMap<>();
	
	
	public DecliveObs(Controller ctrl) {
		
		ctrl.addObserver(this);
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		//Al registrar el observer cogemos el numero de animales
		for(AnimalInfo a : animals) {
			String g = a.getGeneticCode();
			if(!prevPop.containsKey(g)) {    //Si no tenemos eld codigo ej sheep lo metemos 
				prevPop.put(g, 0);
			}
			prevPop.put(g, prevPop.getOrDefault(g, 0) +1 ); //Sumamos en 1 la pop de ese tipo de animal
		}
		
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
	    prevPop.clear();
	    declineCont.clear();
	    onRegister(time, map, animals); // Re-usamos la lógica de carga inicial
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		if(prevPop.containsKey(a.getGeneticCode())) {
			prevPop.put(a.getGeneticCode(), prevPop.get(a.getGeneticCode()) + 1); 
		}
		else {
			prevPop.put(a.getGeneticCode(), 1); 
		}
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		Map<String , Integer> popAc = new HashMap<>();
		
		for(AnimalInfo a : animals) {
			String g = a.getGeneticCode();
			 popAc.put(g, popAc.getOrDefault(g, 0) + 1);
		}
		
		for(String species : prevPop.keySet()) {
			int prev = prevPop.get(species);
			int act = popAc.getOrDefault(species, 0);
			
			if(prev > 0 && act < prev) {
				  declineCont.put(species, declineCont.getOrDefault(species, 0) + 1);
			}
		}
		prevPop = popAc;

	}
	
    public void printReport() {
        System.out.println("Decline steps:");
        for (Map.Entry<String, Integer> e : declineCont.entrySet()) {
            System.out.println(e.getKey() + " => " + e.getValue());
        }
    }

}
