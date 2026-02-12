package simulator.model;

import java.util.List;

import org.json.JSONObject;

import simulator.factories.Factory;
import simulator.misc.Vector2D;

public class Simulator implements JSONable{
	private RegionManager reMng;
	private Factory<Animal> animalsFactory;
	private Factory<Region> regionsFactory;
	private List<Animal> listaA;
	double time;
	
	public Simulator(int cols, int rows, int width, int height, Factory<Animal> animalsFactory, Factory<Region> regionsFactory) {
		reMng.cols = cols;
		reMng.rows = rows;
		reMng.width = width;
		reMng.height = height;
		this.animalsFactory = animalsFactory;
		this.regionsFactory = regionsFactory;
		this.time = 0.0;
	}
	
	private void setRegion(int row, int col, Region r) {
		reMng.setRegion(row, col, r);
	}

	public void setRegion(int row, int col, JSONObject rJson) {
		Region r = regionsFactory.createInstance(rJson);
		setRegion(row, col, r);
	}
	
	private void addAnimal(Animal a) { 
		listaA.add(a);
		this.reMng.registerAnimal(a);
	}
	
	public void addAnimal(JSONObject aJson) {
		Animal a = animalsFactory.createInstance(aJson);
		addAnimal(a);
	}
	
	public MapInfo getMapInfo() {
		return reMng;	
	}
	
	public List<? extends AnimalInfo> getAnimals(){
		List<AnimalInfo> listaAI = null;
		return listaAI;
	}
	
	public double getTime() {
		return this.time;
	}
	
	public void advance(double dt) {
		//1
		this.time += dt;
		//2
		for(Animal animal : listaA) {
			if(animal.getState() == State.DEAD ) {
				reMng.unregisterAnimal(animal);
				listaA.remove(animal);
			}
		}
		
		//3
		for(Animal animal : listaA) {
			animal.update(dt);
			reMng.updateAnimalRegion(animal);
		}
		
		//4 
		reMng.updateAllRegions(dt);
		//5
		for(Animal animal : listaA) {
			if(animal.isPregnant()) {
				Animal bebe = animal.deliverBaby();
				addAnimal(bebe);
			}
		}
	}
	
	public JSONObject asJSON() {
		JSONObject jo = new JSONObject();
		jo.put("time", this.time);
		JSONObject s = this.reMng.asJSON();
		jo.put("state", s);
		return jo;
	}
}
