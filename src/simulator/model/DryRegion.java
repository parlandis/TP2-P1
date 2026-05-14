package simulator.model;

import java.security.InvalidParameterException;
import java.util.List;

import simulator.misc.Utils;

public class DryRegion extends Region {
	protected double R1;
	protected double R2;
	protected double N;
	protected double M;
	double cont;
	double tiempoSequia;

	public DryRegion(double R1, double R2, double N, double M) {
		if((R1 < 0 || R1 > 1 )|| ( R2 < 0 || R2 > 1) || N < 0 || M < 0) {
			throw new InvalidParameterException();
		}	
		this.R1 = R1;
		this.R2 = R2;
		this.N = N;
		this.M = M;
		this.cont = 0;
		this.tiempoSequia = Utils.RAND.nextDouble(1, M);
	}

	@Override
	public double getFood(AnimalInfo a, double dt) {
		if(cont < N) {
			if(a.getDiet() == Diet.HERBIVORE) {
				return 60.0 * Math.exp(-Math.max(0, this.nH - 5.0) * 2.0) * dt;
			}
		}
		else {
			
			return R1 * 60.0 * Math.exp(-Math.max(0, this.nH - 5.0) * 2.0) * dt;
		}
		return 0.0;
	}

	@Override
	public void update(double dt) {
		this.cont += dt;
		if(cont < N) {
			if(cont - N > tiempoSequia) {
				R1 *= R2;
				cont = 0;
			}
		}
	}

	@Override
	public double getFoodAmount() {
		// TODO Auto-generated method stub
		return 0.0;
	}

	@Override
	public String toString() {
		return "dry";
	}
}