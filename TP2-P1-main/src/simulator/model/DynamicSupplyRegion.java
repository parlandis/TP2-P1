package simulator.model;

public class DynamicSupplyRegion extends Region{
	protected double food;
	protected double dt;
	
	public DynamicSupplyRegion(double food, double dt) {

	}
	
	@Override
	public double getFood(AnimalInfo a, double dt) {
		if(a.getDiet().equals(Diet.CARNIVORE)) return 0.0;
		double comido =  Math.min(food,60.0*Math.exp(-Math.max(0,this.nH-5.0)*2.0)*dt);
		food -= comido;
		return comido;
	}
}
