package simulator.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.Diet;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.MapInfo.RegionData;
import simulator.model.RegionInfo;

class RegionsTableModel extends AbstractTableModel implements EcoSysObserver {
	
	private List<RegionData> regions;
	
	RegionsTableModel(Controller ctrl) {
		regions = new ArrayList<>();
		ctrl.addObserver(this);
	}

	private void update(MapInfo map) {
		regions.clear();
		for (RegionData rd : map) {
			regions.add(rd);
		}
		fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
		return regions.size();
	}

	@Override
	public int getColumnCount() {
		return 3 + Diet.values().length;
	}

	@Override
	public String getColumnName(int col) { //Cogemos el valor de la columna, directamente usando el enum diet 
		switch (col) {
		case 0:
			return "Row";
		case 1:
			return "Col";
		case 2:
			return "Desc.";
		default:
			return Diet.values()[col - 3].name();
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {  // Devuelve el valor de la celda en la posición especificada por rowIndex y columnIndex
		RegionData rd = regions.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return rd.row();
		case 1:
			return rd.col();
		case 2:
			return rd.r().toString();
		default:
			return countAnimals(rd.r(), Diet.values()[columnIndex - 3]);
		}
	}

	private int countAnimals(RegionInfo r, Diet diet) {
		int count = 0;
		for (AnimalInfo a : r.getAnimalsInfo()) {
			if (a.getDiet() == diet) {
				count++;
			}
		}
		return count;
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		SwingUtilities.invokeLater(() -> update(map));
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		SwingUtilities.invokeLater(() -> update(map));
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		SwingUtilities.invokeLater(() -> update(map));
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		SwingUtilities.invokeLater(() -> update(map));
	}

	@Override
	public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		SwingUtilities.invokeLater(() -> update(map));
	}
}
