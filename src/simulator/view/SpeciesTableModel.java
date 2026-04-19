package simulator.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;
import simulator.model.State;

class SpeciesTableModel extends AbstractTableModel implements EcoSysObserver {

	private List<String> species;
	private Map<String, Map<State, Integer>> data;

	SpeciesTableModel(Controller ctrl) {
		species = new ArrayList<>();
		data = new HashMap<>();
		ctrl.addObserver(this);
	}

	private void update(List<AnimalInfo> animals) {
		species.clear();
		data.clear();

		for (AnimalInfo a : animals) {
			String g = a.getGeneticCode();
			if (!data.containsKey(g)) {
				species.add(g);
				Map<State, Integer> statesCount = new HashMap<>();
				// Inicializar todos los estados posibles a 0 dinámicamente
				for (State s : State.values()) {
					statesCount.put(s, 0);
				}
				data.put(g, statesCount);
			}
			Map<State, Integer> counts = data.get(g);
			counts.put(a.getState(), counts.get(a.getState()) + 1);
		}

		Collections.sort(species); // Ordenar alfabéticamente las especies
		fireTableDataChanged(); // Notificar a la tabla que los datos han cambiado para que se actualice la vista
	}

	@Override
	public int getRowCount() {
		return species.size();
	}

	@Override
	public int getColumnCount() {
		return 1 + State.values().length;
	}

	@Override
	public String getColumnName(int col) {
		if (col == 0) {
			return "Species";
		} else {
			// Usamos el nombre del enum directamente
			return State.values()[col - 1].name();
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		String speciesName = species.get(rowIndex);
		if (columnIndex == 0) {
			return speciesName;
		} else {
			State state = State.values()[columnIndex - 1];
			return data.get(speciesName).get(state);
		}
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		update(animals);
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		update(animals);
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		update(animals);
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		// El cambio de tipo de región no afecta a la población directamente
	}

	@Override
	public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		update(animals);
	}
}
