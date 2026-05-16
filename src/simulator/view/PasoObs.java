package simulator.view;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.Region;
import simulator.model.RegionInfo;

public class PasoObs implements EcoSysObserver{
	
	static class InfoPaso{
	
		Map<String, Integer> especiesNum;  // Especie - numero de pop
		Map<Region, Map<String, Integer>> matriz;  //Region - < especies - numeropop>  
		
		InfoPaso(){
			especiesNum = new HashMap<String, Integer>();
			matriz = new TreeMap<Region, Map<String,Integer>>();
			
			
		}
	}
	
	
	public PasoObs(Controller ctrl) {
		ctrl.addObserver(this);
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub
		int c = map.getCols();
		int r = map.getRows();
		
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}

}
