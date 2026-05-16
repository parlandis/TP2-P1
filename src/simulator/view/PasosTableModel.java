package simulator.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.Diet;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;
import simulator.model.State;
import simulator.model.MapInfo.RegionData;

public class PasosTableModel extends AbstractTableModel implements EcoSysObserver  {
	
	
	public static class InfoPaso{
		double time;
		Map<String, Integer> numState; // estado - Numero

		InfoPaso(double time, ArrayList<AnimalInfo> animales){
			this.time = time;
			numState = new TreeMap<String, Integer>();
			
			for(State s : State.values()) {
				numState.put(s.name(), 0);
			}
			
			for(AnimalInfo a : animales) {
				State s = a.getState();
				int c = numState.get(s);
				numState.put(s.name(), c + 1);
				}	
		}
	}
	
	List<InfoPaso> historial;
	
	PasosTableModel(Controller ctrl){
		historial = new ArrayList<>();
		ctrl.addObserver(this);
	}

	@Override
	public int getRowCount() {
		return historial.size();
	}

	@Override
	public int getColumnCount() {
		return State.values().length;
	}
	
	@Override
	public String getColumnName(int col) { //Cogemos el valor de la columna, directamente usando el enum diet 
		switch (col) {
		case 0:
			return "Time";
		default:
			return State.values()[col - 1].name();
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		InfoPaso paso = historial.get(rowIndex);
		
		if(columnIndex == 0) {
			return paso.time;
		}
		else {
			String name = getColumnName(columnIndex);
			return paso.numState.get(name);
		}
	}
	
	private void update(MapInfo map) {
		
		fireTableDataChanged();
	}
	
	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub
		
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
