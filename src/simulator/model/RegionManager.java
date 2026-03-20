package simulator.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.json.JSONArray;
import org.json.JSONObject;

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

	protected void setRegion(int row, int col, Region r) { // Cambia la region de la posicion row col por r
		Region oldRegion = regions[row][col];
		regions[row][col] = r;

		if (oldRegion != null) {
			for (Animal a : oldRegion.getAnimals()) {
				r.addAnimal(a);
				animalRegion.put(a, r);
			}
		}
	}

	protected void registerAnimal(Animal a) { // Añade el animal a la region correspondiente a su posicion
		a.init(this);
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
		animalRegion.put(a, reg);
		reg.addAnimal(a);

		this.updateAnimalRegion(a);
	}

	protected void unregisterAnimal(Animal a) {
		Region r = animalRegion.get(a);
		r.removeAnimal(a);
	}

	private Region regionByPosition(Animal a) {// Devolvemos la Region a la que pertenece el animal segun su regiopn

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

	public List<Animal> getAnimalsInRange(Animal a, Predicate<Animal> filter) { // la hacemos para que sea dentro del
																				// mapa y en rangop
		List<Animal> inRange = new ArrayList<>();

		double x = a.getPosition().getX();
		double y = a.getPosition().getY();
		double r = a.getSightRange();

		int cFrom = (int) ((x - r) / regWidth);
		int cTo = (int) ((x + r) / regWidth);
		int rFrom = (int) ((y - r) / regHeight);
		int rTo = (int) ((y + r) / regHeight);

		// Ajustar límites
		cFrom = Math.max(0, cFrom);
		cTo = Math.min(cols - 1, cTo);
		rFrom = Math.max(0, rFrom);
		rTo = Math.min(rows - 1, rTo);

		for (int i = rFrom; i <= rTo; i++) { // Recorremos las regiones dentro del rango calculado
			for (int j = cFrom; j <= cTo; j++) { // Recorremos los animales de cada region y comprobamos si estan dentro
													// del rango de vision y cumplen el filtro
				List<Animal> animals = regions[i][j].getAnimals();
				for (Animal animal : animals) {
					if (!animal.equals(a) && animal.getPosition().distanceTo(a.getPosition()) <= r
							&& filter.test(animal)) {
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

	@Override
	public Iterator<RegionData> iterator() {
		List<RegionData> res = new ArrayList<>();
		for (int i = 0; i < this.rows; i++) {
			for (int j = 0; j < this.cols; j++) {
				res.add(new RegionData(i, j, regions[i][j]));
			}
		}
		return res.iterator();
	}

}