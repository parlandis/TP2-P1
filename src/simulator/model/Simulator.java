package simulator.model;

import java.util.ArrayList;
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
		this.reMng = new RegionManager(cols, rows, width, height);
		this.animalsFactory = animalsFactory;
		this.regionsFactory = regionsFactory;
		listaA = new ArrayList<>();
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


		this.reMng.registerAnimal(a);
		listaA.add(a);
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
	
	
	
	public static void main(String[] args) {
		
		Simulator sim = new Simulator(1,1,100,100,null,null);
		Vector2D pos = new Vector2D(50.0,50.0);
		
		
		Animal oveja = new Sheep(new SelectFirst(), new SelectClosest(), pos);
		
		sim.addAnimal(oveja);
		
		while(!sim.listaA.isEmpty()) {
			
		
		
		System.out.println("Simulador con una oveja");
		System.out.println("Estado INicial: "+ sim.asJSON());
		
		sim.advance(0.03);
		
		System.out.println("Estado Final:" + sim.asJSON());
		}
		
	}
	
	
}
