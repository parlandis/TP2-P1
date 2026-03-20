package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public abstract class Region implements Entity, FoodSupplier, RegionInfo {
	protected List<Animal> animales;
	protected int nH;
	protected int nC;

	protected Region() {
		animales = new ArrayList<>();
		nH = 0;
		nC = 0;
	}

	final void addAnimal(Animal a) {
		if (a.diet == Diet.HERBIVORE)
			nH++;
		else
			nC++;
		animales.add(a);
	}

	final void removeAnimal(Animal a) {
		if (a.diet == Diet.HERBIVORE)
			nH--;
		else
			nC--;
		animales.remove(a);
	}

	final List<Animal> getAnimals() { // TODO: NO se si hay que quitar este
		return new ArrayList<>(animales);
	}

	public JSONObject asJSON() {
		JSONObject reg = new JSONObject();
		JSONArray JsAnimales = new JSONArray();

		for (Animal a : this.animales) {
			JsAnimales.put(a.asJSON());
		}

		reg.put("animals", JsAnimales);
		return reg;
	}

	@Override
	public abstract double getFood(AnimalInfo a, double dt);

	@Override
	public abstract void update(double dt);

	@Override
	public List<AnimalInfo> getAnimalsInfo() {
		return Collections.unmodifiableList(animales);
	}

	@Override
	public abstract String toString();

}
