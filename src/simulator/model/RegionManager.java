package simulator.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.json.JSONObject;

public class RegionManager implements AnimalMapView {
	private int cols;
	private int rows;
	private int width;
	private int height;
	private Map<Animal, Region> animalRegion;
	private Region[][] regions;
	
	public RegionManager(int cols, int rows, int width, int height) {
		this.cols = cols;
		this.rows = rows;
		this.width = width;
		this.height = height;
		this.regions = new Region[rows][cols];
		this.animalRegion = new HashMap<>();
		
	}
	
	protected void setRegion(int row, int col, Region r) {
		this.regions[row][col] = r;
		
		List<Animal> listaA = this.regions[row][col].getAnimals();
		while(!listaA.isEmpty()) {
			Animal a = listaA.getFirst();
			animalRegion.put(a, r);
			updateAnimalRegion(a);
			listaA.removeFirst();
		}
	}
	
	protected void registerAnimal(Animal a) {  // TODO: No entiendo lo de la posicion 

	}
	
	protected void unregisterAnimal(Animal a) {
		Region r = animalRegion.get(a);
		r.removeAnimal(a);
	}
	
	protected void updateAnimalRegion(Animal a) {
		
	}
	
	@Override
	public double getFood(AnimalInfo a, double dt) {
		return animalRegion.get(a).getFood(a, dt);
	}
	

	protected void updateAllRegions(double dt) {
		for(int i = 0; i < this.rows; i++) {
			for(int j = 0; j < this.cols; j++) {
				regions[i][j].update(dt);
			}
		}
	}
	
	public List<Animal> getAnimalsInRange(Animal a, Predicate<Animal> filter){  // TODO: tienen que ser de la misma region??
		List<Animal> inRange = new ArrayList<>();
		
		for(int i = 0; i < this.rows; i++) {
			for(int j = 0; j < this.cols; j++) {
				List<Animal> animals = regions[i][j].getAnimals();
				for(Animal animal : animals) {
					if(animal.getPosition().distanceTo(a.getPosition()) <= a.getSightRange() && filter.test(animal)) {
						inRange.add(animal);
					}
				}
			}
		}
		
		return null;	
	}
	
	public JSONObject asJSON() {
		return null;
		
	}
	
	private void initRegs() {
		for(int i = 0; i < this.rows; i++) {
			for(int j = 0; j < this.cols; j++) {
				regions[i][j] = new DefaultRegion();
			}
		}
	}
	
	@Override
	public int getCols() {
		return cols;
	}

	@Override
	public int getRows() {
		return rows;	
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public int getRegionWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getRegionHeight() {
		// TODO Auto-generated method stub
		return 0;
	}



}
