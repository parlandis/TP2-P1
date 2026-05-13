package simulator.model;

import java.util.List;

import simulator.misc.Utils;

public class DynamicSupplyRegion extends Region {
	protected double food;
	protected double factor;

	public DynamicSupplyRegion(double food, double factor) {
		this.food = food;
		this.factor = factor;
	}

	@Override
	public double getFood(AnimalInfo a, double dt) { 
		// Solo los herbívoros consumen comida de la región
		if (a.getDiet().equals(Diet.CARNIVORE))
			return 0.0;
		
		// Lógica de "Sobrepastoreo": la cantidad de comida disponible disminuye exponencialmente
		// con el número de herbívoros (nH) presentes en la región.
		// Si hay más de 5 herbívoros (nH > 5.0), la comida obtenida cae drásticamente.
		double comido = Math.min(food, 60.0 * Math.exp(-Math.max(0, this.nH - 5.0) * 2.0) * dt);
		food -= comido;
		return comido;
	}

	@Override
	public void update(double dt) {
		if (Utils.RAND.nextDouble() < 0.5) {
			food += factor * dt;
		}
	}

	@Override
	public double getFoodAmount() {
		return food;
	}

	@Override
	public String toString() {
		return "Dynamic food supply";
	}
}
