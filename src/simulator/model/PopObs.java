package simulator.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import simulator.control.Controller;
import simulator.model.MapInfo.RegionData;

public class PopObs implements EcoSysObserver {
	

	Map<String, Integer> popActual; //String : (row,col) - Poblacion 
	Map<String, Integer> popPrev;
	Map<String, Integer> rachaAct; 
	Map<String, Integer> maxRacha;  //(row,col) - numero de pasos 
	
	public PopObs(Controller ctrl) {
		popActual = new HashMap<>();
		popPrev = new HashMap<>();
		rachaAct = new  HashMap<>();
		maxRacha = new HashMap<>();
		ctrl.addObserver(this);
	}
	
	
	public void write() {
		Set<String> keys = maxRacha.keySet();  // (row,col)
		System.out.println("Population Stability: ");
		for(String s : keys ) {
			int n = maxRacha.get(s);
			System.out.println(s + " => " + n );
		}
	}

	
	
	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		
		for(RegionData rd: map) {
			String s = rd.row() + "," + rd.col();	
			int p = rd.r().getAnimalsInfo().size();
			popActual.put(s, p);
			rachaAct.put(s, 0);
			maxRacha.put(s, 0);
		}
		
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		popActual.clear();
		popPrev.clear();
		rachaAct.clear();
		maxRacha.clear();
		onRegister(time, map, animals);
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {

	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {

		
	}

	@Override
	public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		popPrev = new HashMap(popActual);
		popActual.clear();
		
		for(RegionData rd: map) {
			String s = rd.row() + "," + rd.col();	
			int p = rd.r().getAnimalsInfo().size();
			popActual.put(s, p);
			if(popActual.get(s) > popPrev.get(s)) {
				rachaAct.put(s, rachaAct.get(s) + 1);
				if(rachaAct.get(s) > maxRacha.get(s)) {
					maxRacha.put(s, rachaAct.get(s));
				}
			}
			else {
				rachaAct.put(s, 0);
			}
		}
	}

}
