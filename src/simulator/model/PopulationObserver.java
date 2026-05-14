package simulator.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PopulationObserver implements EcoSysObserver {

	private double limit;
	private Map<String, Integer> lastCounts;
	private Map<String, Integer> exceedCounts;

	public PopulationObserver(double limit) {
		this.limit = limit;
		this.lastCounts = new HashMap<>();
		this.exceedCounts = new TreeMap<>();
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		updateCounts(animals);
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		lastCounts.clear();
		exceedCounts.clear();
		updateCounts(animals);
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		updateCounts(animals);
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
	}

	@Override
	public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		Map<String, Integer> currentCounts = new HashMap<>();
		for (AnimalInfo a : animals) {
			String species = a.getGeneticCode();
			currentCounts.put(species, currentCounts.getOrDefault(species, 0) + 1);
		}

		for (Map.Entry<String, Integer> entry : currentCounts.entrySet()) {
			String species = entry.getKey();
			int currentCount = entry.getValue();
			int lastCount = lastCounts.getOrDefault(species, 0);

			if (lastCount <= limit && currentCount > limit) {
				exceedCounts.put(species, exceedCounts.getOrDefault(species, 0) + 1);
			}
		}

		lastCounts = currentCounts;
	}

	private void updateCounts(List<AnimalInfo> animals) {
		lastCounts.clear();
		for (AnimalInfo a : animals) {
			String species = a.getGeneticCode();
			lastCounts.put(species, lastCounts.getOrDefault(species, 0) + 1);
		}
	}

	public void printReport() {
		System.out.println("Population change for r=" + limit + ":");
		for (Map.Entry<String, Integer> entry : exceedCounts.entrySet()) {
			System.out.println(entry.getKey() + " => " + entry.getValue());
		}
	}
}
