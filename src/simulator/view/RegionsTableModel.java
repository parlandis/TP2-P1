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

class RegionsTableModel extends AbstractTableModel implements EcoSysObserver {

	  // TODO definir atributos necesarios

	private List<MapInfo.RegionData> _regions;
	private String[] _columnNames = { "Row", "Col", "Desc.", "Carnivore", "Herbivore" };

	RegionsTableModel(Controller ctrl) {
		_regions = new ArrayList<>();
		ctrl.addObserver(this);
	}

	private void update(MapInfo map) {
		_regions.clear();
		for (MapInfo.RegionData rd : map) {
			_regions.add(rd);
		}
		fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
		return _regions.size();
	}

	@Override
	public int getColumnCount() {
		return _columnNames.length;
	}

	@Override
	public String getColumnName(int col) {
		return _columnNames[col];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		MapInfo.RegionData rd = _regions.get(rowIndex);
		switch (columnIndex) {
		case 0:
			return rd.row();
		case 1:
			return rd.col();
		case 2:
			return rd.r().toString();
		case 3:
			return countAnimals(rd.r(), Diet.CARNIVORE);
		case 4:
			return countAnimals(rd.r(), Diet.HERBIVORE);
		default:
			return null;
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
		update(map);
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		update(map);
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		update(map);
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		update(map);
	}

	@Override
	public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		update(map);
	}
}
