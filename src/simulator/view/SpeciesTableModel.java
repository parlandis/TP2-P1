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

	private static final long serialVersionUID = 1L;

	private List<String> _species;
	private Map<String, Map<State, Integer>> _data;

	SpeciesTableModel(Controller ctrl) {
		_species = new ArrayList<>();
		_data = new HashMap<>();
		ctrl.addObserver(this);
	}

	private void update(List<AnimalInfo> animals) {
		_species.clear();
		_data.clear();

		for (AnimalInfo a : animals) {
			String g = a.getGeneticCode();
			if (!_data.containsKey(g)) {
				_species.add(g);
				Map<State, Integer> statesCount = new HashMap<>();
				// Inicializar todos los estados posibles a 0 dinámicamente
				for (State s : State.values()) {
					statesCount.put(s, 0);
				}
				_data.put(g, statesCount);
			}
			Map<State, Integer> counts = _data.get(g);
			counts.put(a.getState(), counts.get(a.getState()) + 1);
		}

		Collections.sort(_species);
		fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
		return _species.size();
	}

	@Override
	public int getColumnCount() {
		// 1 para 'Species' + el número de estados en el enum
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
		String speciesName = _species.get(rowIndex);
		if (columnIndex == 0) {
			return speciesName;
		} else {
			State state = State.values()[columnIndex - 1];
			return _data.get(speciesName).get(state);
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
