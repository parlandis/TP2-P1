package simulator.model;

import simulator.misc.Utils;

public class DynamicSupplyRegion extends Region{
	protected double food;
	protected double factor;
	
	public DynamicSupplyRegion(double food, double factor) {
		this.food = food;
		this.factor = factor;
	}
	
	@Override
	public double getFood(AnimalInfo a, double dt) {
		
		
		if(a.getDiet().equals(Diet.CARNIVORE)) return 0.0;
		double comido =  Math.min(food,60.0*Math.exp(-Math.max(0,this.nH-5.0)*2.0)*dt);
		food -= comido;
		return comido;
	}
	
	@Override
	public void update(double dt) {
		if(Utils.RAND.nextDouble() < 0.5) {
			food += factor*dt;
		}
	}
}
