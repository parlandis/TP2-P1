package simulator.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Vector2D;

public class RegionManager implements AnimalMapView {
	protected int cols;
	protected int rows;
	protected int width;
	protected int height;
	private int regWidth;
	private int regHeight;
	protected Map<Animal, Region> animalRegion;
	protected Region[][] regions;

	public RegionManager(int cols, int rows, int width, int height) {
		this.cols = cols;
		this.rows = rows;
		this.width = width;
		this.height = height;
		this.regions = new Region[rows][cols];
		this.animalRegion = new HashMap<>();
		this.regHeight = height / rows;
		this.regWidth = width / cols;
		this.initRegs();
	}

	protected void setRegion(int row, int col, Region r) {
		this.regions[row][col] = r;

		List<Animal> listaA = this.regions[row][col].getAnimals();
		while (!listaA.isEmpty()) {
			Animal a = listaA.getFirst();
			animalRegion.put(a, r);
			updateAnimalRegion(a);
			listaA.removeFirst();
		}
	}

	protected void registerAnimal(Animal a) {
		a.init(this);
		int c = (int) (a.getPosition().getX() / this.regWidth);
		int r = (int) (a.getPosition().getY() / this.regHeight);

		// Asegurar que los índices están dentro de los límites
		if (c >= cols)
			c = cols - 1;
		if (r >= rows)
			r = rows - 1;
		if (c < 0)
			c = 0;
		if (r < 0)
			r = 0;

		Region reg = regions[r][c];
		animalRegion.put(a, reg);
		reg.addAnimal(a);

		this.updateAnimalRegion(a);
	}

	protected void unregisterAnimal(Animal a) {
		Region r = animalRegion.get(a);
		r.removeAnimal(a);
	}

	private Region regionByPosition(Animal a) {// Devolvemos la Region a la que pertenece el animal segun su regiopn
		// Pos del animal
		int c = (int) (a.getPosition().getX() / this.regWidth);
		int r = (int) (a.getPosition().getY() / this.regHeight);

		if (c >= cols)
			c = cols - 1;
		if (r >= rows)
			r = rows - 1;
		if (c < 0)
			c = 0;
		if (r < 0)
			r = 0;

		Region reg = regions[r][c];
		return reg;

	}

	protected void updateAnimalRegion(Animal a) {
		Region actual = animalRegion.get(a);
		Region region = regionByPosition(a);
		if (actual != region) { // Comprobamos que el animal se ha o no movido de region
			region.addAnimal(a);
			actual.removeAnimal(a);
			animalRegion.put(a, region);
		}

	}

	@Override
	public double getFood(AnimalInfo a, double dt) {
		return animalRegion.get(a).getFood(a, dt);
	}

	protected void updateAllRegions(double dt) {
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				regions[i][j].update(dt);
			}
		}
	}

	public List<Animal> getAnimalsInRange(Animal a, Predicate<Animal> filter) {  // TODO: mirar solo 
		List<Animal> inRange = new ArrayList<>();
		
		//int colsMax = (int) a.getSightRange() 

		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				List<Animal> animals = regions[i][j].getAnimals();
				for (Animal animal : animals) {
					if (animal.getPosition().distanceTo(a.getPosition()) <= a.getSightRange() && filter.test(animal)
							&& !animal.equals(a)) {
						inRange.add(animal);
					}
				}
			}
		}

		return inRange;
	}

	public JSONObject asJSON() {
		JSONObject jo = new JSONObject();
		JSONArray JsRegiones = new JSONArray();

		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				JSONObject r = new JSONObject();

				r.put("row", i);
				r.put("col", j);
				JSONObject reg = this.regions[i][j].asJSON();
				r.put("data", reg);
				JsRegiones.put(r);
			}
		}

		jo.put("regions", JsRegiones);
		return jo;
	}

	private void initRegs() {
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
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
		return regWidth;
	}

	@Override
	public int getRegionHeight() {
		return regHeight;
	}

}