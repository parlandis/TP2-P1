package simulator.model;

import java.security.InvalidParameterException;

public class FloodRegion extends Region{
	double F, S, W, time;
	boolean dry;
	
	
	public FloodRegion(double F, double S, double W) {
		 if(F < 1 || S <= 0 || W <= 0) {
			 throw new InvalidParameterException("Paramentros incorrectos para la FlooRegion");
		 }
		 this.F = F;
		 this.S = S;
		 this.W = W;
		 this.time = 0.0;
		 this.dry = true;
	}

	@Override
	public double getFood(AnimalInfo a, double dt) {
		double food = 60.0 * Math.exp(-Math.max(0, this.nH - 5.0) * 2.0) * dt;
		if(dry) {
			if (a.getDiet() == Diet.HERBIVORE)
				return food;
			else
				return 0.0;
		}
		else {
			return food * F;
		}
	}

	@Override
	public void update(double dt) {
		this.time += dt;

		if(time > W + S) {
			System.out.println("Flooded Region , now dry, with F : " + F + " time :" + time);
			dry = true;
			time = 0.0;
		}
		if(time >= S) {
			dry = false;
			System.out.println("Flooded Region , now flood, with F : " + F);
		}
	}

	@Override
	public double getFoodAmount() {
		return 0.0;
	}

	@Override
	public String toString() {
	
		return "flood";
	}

}
