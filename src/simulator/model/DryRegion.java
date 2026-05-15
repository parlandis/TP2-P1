package simulator.model;

import java.security.InvalidParameterException;
import java.util.List;

import simulator.misc.Utils;

public class DryRegion extends Region {
	private double R1;
	private double R2;
	private double N;
	private double M;
	private double cont;
	private double tiempoSequia;
	private boolean enSequía;

	public DryRegion(double R1, double R2, double N, double M) {
		if (R1 < 0 || R1 > 1 || R2 < 0 || R2 > 1 || N <= 0 || M <= 0) {
			throw new IllegalArgumentException("Invalid parameters for DryRegion");
		}
		this.R1 = R1;
		this.R2 = R2;
		this.N = N;
		this.M = M;
		this.cont = 0;
		this.enSequía = false;
	}

	@Override
	public double getFood(AnimalInfo a, double dt) {
		if (a.getDiet() == Diet.HERBIVORE) {
			double food = 60.0 * Math.exp(-Math.max(0, this.nH - 5.0) * 2.0) * dt;
			return enSequía ? food * R1 : food;
		}
		return 0.0;
	}

	@Override
	public void update(double dt) {
		this.cont += dt;
		if (!enSequía) {
			System.out.println("Region no en sequia, R1 : " + R1 + ",,  Con tiempo: " + cont);
			if (cont >= N)
				enSequía = true;
		} else {
			if (cont - N > tiempoSequia) {
				R1 *= R2;
				cont = 0;
				enSequía = false;
			}
			System.out.println("Region en sequia, R1 : " + R1);
		}

	}

	@Override
	public double getFoodAmount() {
		return 0.0;
	}

	@Override
	public String toString() {
		return "dry";
	}
}