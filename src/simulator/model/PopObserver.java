package simulator.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import simulator.control.Controller;
import simulator.misc.Vector2D;
import simulator.model.MapInfo.RegionData;

import java.util.Map;

public class PopObserver implements EcoSysObserver {
	
	Map<String, Integer> numeroPop;
	Map<String, Integer> numeroPopAct;
	Map<String, Integer> maxConsec;
	
	

	public PopObserver(Controller ctrl) {
		numeroPop = new HashMap<String, Integer>();
		numeroPopAct = new HashMap<String, Integer>();
		maxConsec = new HashMap<String, Integer>();
		ctrl.addObserver(this);
	}
	
	public void write() {
		
		
		
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		
		for(MapInfo.RegionData rd : map) {  //Iteramos todas las regiones para los datos iniciales
			int col = rd.col();
			int row  =rd.row();
			RegionInfo reg = rd.r();
			int numA = reg.getAnimalsInfo().size();  
			
			numeroPop.put(row + "," + col , numA);        // Metemos como nombre (1,1) y el num de animales
		}
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		
		
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		for(MapInfo.RegionData rd : map) {  //Iteramos todas las regiones para los datos iniciales
			int col = rd.col();
			int row  =rd.row();
			RegionInfo reg = rd.r();
			int numA = reg.getAnimalsInfo().size();  
			
			numeroPopAct.put(row + "," + col , numA);        // Metemos como nombre (1,1) y el num de animales
			if(numeroPop.get(row + "," + col) < numA) { //Si es menor significa que es estable 
				
				
				
				if(maxConsec.containsKey(row + "," + col)) {  //Si ya tiene racha sumamos si no 1 
					maxConsec.put(row + "," + col, maxConsec.get(row + "," + col) + 1);
				} 
				else maxConsec.put(row + "," + col, 1);
			}
			else max
		}
		
		
		
		
		
	}
	
	

}
