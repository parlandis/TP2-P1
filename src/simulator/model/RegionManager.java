package simulator.model;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

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
		this.regions = new Region[rows][cols](DefaultRegion);
		regions = Array.
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
		// TODO Auto-generated method stub
		return width;
	}

	@Override
	public int getHeight() {
		// TODO Auto-generated method stub
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

	@Override
	public double getFood(AnimalInfo a, double dt) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Animal> getAnimalsInRange(Animal e, Predicate<Animal> filter) {
		// TODO Auto-generated method stub
		
		return null;
	}

}
