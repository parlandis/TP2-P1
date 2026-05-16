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

public class StatsTableModel extends AbstractTableModel implements EcoSysObserver {
	
	
	//Tabla rows = paso de simulacion 
	//Tbala cols = tiempo , dieta
	
	
	private static class StepData{
		double time;
		Map<Diet, Integer> Dietas; // Dieta cantidad
		Map<String, Integer> species; // Especie cantidad
		
		StepData(double time, List<AnimalInfo> animales){
			this.time = time ;
			Dietas = new TreeMap<Diet, Integer>();
			species = new TreeMap<String, Integer>();
			
			for(AnimalInfo a: animales) {
				if(!Dietas.containsKey(a.getDiet())) {
					Dietas.put(a.getDiet(), 1);
				}
				else Dietas.put(a.getDiet(), Dietas.get(a.getDiet()) + 1);
				
				if(!species.containsKey(a.getGeneticCode())){
					species.put(a.getGeneticCode(), 1);
				}
				else species.put(a.getGeneticCode(), species.get(a.getGeneticCode()) + 1);
			}
		}
	}
	
	List<StepData> listaPasos;
	private Map<String, Diet> speciesDiets = new HashMap<>();
    private String currentFilter = "All";
    private String[] columnNames = {"Time", "Herbivores", "Carnivores"};

	
	StatsTableModel(Controller ctrl){
		listaPasos = new ArrayList<>();
		ctrl.addObserver(this);
	}
	
	   public void clear() {
		   listaPasos.clear();
	        fireTableDataChanged();
	    }
	
	@Override
	public int getRowCount() {

		return listaPasos.size();
	}

	@Override
	public int getColumnCount() {
		
		return Diet.values().length + 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		StepData step = listaPasos.get(rowIndex);
		
		if(columnIndex == 0) {
			return Double.toString(step.time);
		}
		
		else 
			return Diet.values()[columnIndex -1].name();
		
	}
	
    private int getCountForDiet(StepData step, Diet diet) {
        if (currentFilter.equals("All")) {
            return step.dietCounts.get(diet);
        } else {
            // Si la especie filtrada coincide con la dieta de la columna, damos su número
            if (speciesDiets.get(currentFilter) == diet) {
                return step.speciesCounts.getOrDefault(currentFilter, 0);
            }
            return 0; // Si no es de esa dieta, hay 0
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
