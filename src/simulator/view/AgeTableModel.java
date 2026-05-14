package simulator.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.Diet;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;
import simulator.model.MapInfo.RegionData;

public class AgeTableModel  extends AbstractTableModel implements EcoSysObserver  {
	
	private static class datosPorStep{
		double time;
		int young, old;
		datosPorStep(double time, int young, int old){
			this.time = time;
			this.young = young;
			this.old = old;
		}
	}
	
	List<datosPorStep> listaDatos = new ArrayList<>();
	double ageLimit = 3.0;
	
	AgeTableModel(Controller ctrl) {
		ctrl.addObserver(this);
	}
	
	private void update(MapInfo map) {
		
		
		fireTableDataChanged();
	}
	
	@Override
	public int getRowCount() {
		return listaDatos.size();
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return 3;
	}
	
	@Override
	public String getColumnName(int col) { //Cogemos el valor de la columna, directamente usando el enum diet 
		switch (col) {
		case 0:
			return "Time";
		case 1:
			return "Young";
		case 2:
			return "Old";
			
		}
		return null;
	}

	public double getAgeLimit() {
		return ageLimit;
	}
	
	public void setAgeLimit(double limit) {
		this.ageLimit = limit;
		clear();
	}
	
	private void clear() {
		listaDatos.clear();
		fireTableDataChanged();
		
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		datosPorStep d = listaDatos.get(rowIndex);
		switch (columnIndex) {
		case 0: 
			return String.format("%.3f", d.time);
		case 1: return d.young;
		case 2: return d.old;
		}
		return null;
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		clear();
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		clear();
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
		int youngN = 0;
		int oldN = 0;
		for(AnimalInfo a: animals) {
			if(a.getAge() >= ageLimit) {
				oldN++;
			}
			else youngN++;
		}
		datosPorStep actual = new datosPorStep(time, youngN, oldN);
		listaDatos.add(actual);
		fireTableDataChanged();
	}
}
