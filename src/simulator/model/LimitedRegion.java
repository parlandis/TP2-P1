package simulator.model;

import java.security.InvalidParameterException;

import simulator.misc.Utils;

public class LimitedRegion extends Region{
	int cont, MIN, MAX, X;
	double T, time; 
	
	
	public LimitedRegion(int MIN, int MAX, double T){
		cont = 0; 
		this.MIN = MIN;
		this.MAX = MAX;
		this.T = T;
		this.time = 0.0;
		
		if(MIN <= 0 || MAX <= 0 || MIN >= MAX || T <= 0 ) {
			throw new InvalidParameterException("Error al crear Limited Region");
		}
		
		this.X = Utils.RAND.nextInt(MIN, MAX);
	}

	@Override
	public double getFood(AnimalInfo a, double dt) {
		cont++;
		
		if (a.getDiet() == Diet.HERBIVORE) {
			if(cont < X) {
				return 60.0 * Math.exp(-Math.max(0, this.nH - 5.0) * 2.0) * dt;
			}
		}

		return 0.0;
	}

	@Override
	public void update(double dt) {
		this.time += dt;
		
		if(time >= T) {
			this.X = Utils.RAND.nextInt(MIN, MAX);
			time = 0.0;
		}																											
		cont = 0;
		
		System.out.println("TIME: " + time  +"Limited Region solo pastan: " + X + " Con MIN: " + MIN + " ,COn MAX: " + MAX + ", con T: " + T);
	}

	@Override
	public double getFoodAmount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return null;
	}

}
