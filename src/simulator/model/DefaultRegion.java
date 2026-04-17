package simulator.model;

public class DefaultRegion extends Region {

	@Override
	public double getFood(AnimalInfo a, double dt) {
		if (a.getDiet() == Diet.HERBIVORE)
			return 60.0 * Math.exp(-Math.max(0, this.nH - 5.0) * 2.0) * dt;
		else
			return 0.0;
	}

	@Override
	public void update(double dt) {

	}

	@Override
	public double getFoodAmount() {
		return 0.0;
	}

	@Override
	public String toString() {
		return "Default";
	}

}
