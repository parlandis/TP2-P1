package simulator.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.Diet;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.MapInfo.RegionData;
import simulator.model.RegionInfo;

public class ExtinctTableModel extends AbstractTableModel implements EcoSysObserver {

	class InfoPaso {
		double time;
		Map<String, Integer> cuenta; // (InDanger / Not ) - numero

		InfoPaso(Double time) {
			this.time = time;
			this.cuenta = new HashMap<>();
		}
	}

	List<InfoPaso> historial;
	int limit;

	ExtinctTableModel(Controller ctrl, int limit) {
		historial = new ArrayList<>();
		this.limit = limit;
		ctrl.addObserver(this);
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return historial.size();
	}

	@Override
	public int getColumnCount() {
		return 3;
	}

	@Override
	public String getColumnName(int col) {
		switch (col) {
			case 0:
				return "Time";
			case 1:
				return "In Danger";
			case 2:
				return "Not In Danger";
		}
		return null;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		InfoPaso ip = historial.get(rowIndex);

		switch (columnIndex) {
			case 0:
				return ip.time;
			case 1:
				return ip.cuenta.get("Si");
			case 2:
				return ip.cuenta.get("No");
		}

		return null;
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		InfoPaso primero = new InfoPaso(time);

		// Vamos a contar las que estan en peligro
		int si = 0, no = 0;
		for (RegionData rd : map) {
			if (rd.r().getAnimalsInfo().size() < limit) {
				si++;
			} else if (rd.r().getAnimalsInfo().size() > (limit * 2)) {
				no++;
			}
		}

		primero.cuenta.put("Si", si);
		primero.cuenta.put("No", no);

		historial.add(primero);
		fireTableDataChanged();
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		historial.clear();
		onRegister(time, map, animals);

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
		onRegister(time, map, animals);

	}

}
