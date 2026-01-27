package simulator.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class Region implements Entity, FoodSupplier, RegionInfo {
	List<Animal> animales;
	protected int nH;
	protected int nC;
	
	protected Region() {
		animales = new ArrayList<Animal>();
	}
	
	final void addAnimal(Animal a) {
		if(a.diet == Diet.HERBIVORE) nH++;
		else nC++;
		animales.add(a);
	}
	
	final void removeAnimal(Animal a) {
		if(a.diet == Diet.HERBIVORE) nH--;
		else nC--;
		animales.remove(a);
	}
	
	final List<Animal> getAnimals(){
		return animales;
	}
	
	public JSONObject asJSON() {
		return null;
	}

	@Override
	public double getFood(AnimalInfo a, double dt) {

		return 0;
	}

	@Override
	public void update(double dt) {

		
	}

}
