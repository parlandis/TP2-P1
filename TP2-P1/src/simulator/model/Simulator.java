package simulator.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.json.JSONObject;

import simulator.factories.Factory;
import simulator.misc.Vector2D;

import simulator.view.SimpleObjectViewer;
import simulator.view.SimpleObjectViewer.ObjInfo;

public class Simulator implements JSONable {
	private RegionManager reMng;
	private Factory<Animal> animalsFactory;
	private Factory<Region> regionsFactory;
	private List<Animal> listaA;
	double time;

	public Simulator(int cols, int rows, int width, int height, Factory<Animal> animalsFactory,
			Factory<Region> regionsFactory) {
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

	public List<? extends AnimalInfo> getAnimals() {
		return Collections.unmodifiableList(listaA);
	}

	public double getTime() {
		return this.time;
	}

	private static List<ObjInfo> toAnimalsInfo(List<? extends AnimalInfo> animals) {
		List<ObjInfo> ol = new ArrayList<>(animals.size());
		for (AnimalInfo a : animals)
			ol.add(new ObjInfo(a.getGeneticCode(), (int) a.getPosition().getX(), (int) a.getPosition().getY(),
					(int) Math.round(a.getAge()) + 2));
		return ol;
	}

	public void advance(double dt) {
		// 1. Incrementar el tiempo
		this.time += dt;

		// 2. Quitar animales muertos de forma segura
		Iterator<Animal> it = listaA.iterator();
		while (it.hasNext()) {
			Animal a = it.next();
			if (a.getState() == State.DEAD) {
				reMng.unregisterAnimal(a);
				it.remove();
			}
		}

		// 3. Actualizar animales vivos
		for (Animal a : listaA) {
			a.update(dt);
			reMng.updateAnimalRegion(a);
		}

		// 4. Actualizar regiones
		reMng.updateAllRegions(dt);

		// 5. Nacimientos (usando lista temporal para evitar CME)
		List<Animal> babies = new ArrayList<>();
		for (Animal a : listaA) {
			if (a.isPregnant()) {
				babies.add(a.deliverBaby());
			}
		}
		for (Animal b : babies) {
			addAnimal(b);
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

		Simulator sim = new Simulator(1, 1, 100, 100, null, null);
		Vector2D posO1 = new Vector2D(50.0, 50.0);
		Vector2D posO2 = new Vector2D(50.0, 51.0);

		Vector2D posL1 = new Vector2D(60.0, 60.0);
		Vector2D posL2 = new Vector2D(80.0, 50.0);

		Animal oveja1 = new Sheep(new SelectFirst(), new SelectFirst(), posO1);
		//Animal oveja2 = new Sheep(new SelectFirst(), new SelectFirst(), posO2);
		//Animal lobo = new Wolf(new SelectFirst(), new SelectFirst(), posL1);

		sim.addAnimal(oveja1);
		//sim.addAnimal(oveja2);
		//sim.addAnimal(lobo);
		System.out.println("Simulador 2 O");

		MapInfo m = sim.getMapInfo();
		SimpleObjectViewer view = new SimpleObjectViewer("[ECOSYSTEM]", m.getWidth(), m.getHeight(), m.getCols(),
				m.getRows());
		double dt = 0.03;

		while (!sim.listaA.isEmpty()) {

			System.out.println("Estado INicial: " + sim.asJSON().toString(3));

			sim.advance(dt);
			view.update(toAnimalsInfo(sim.getAnimals()), sim.getTime(), dt);

			System.out.println("Estado Final:" + sim.asJSON().toString(3));
		}

		view.close();

	}
}
