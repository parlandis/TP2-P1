package simulator.model;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Sheep extends Animal {
	// Constantes
	final static String SHEEP_GENETIC_CODE = "Oveja";
	final static double INIT_SIGHT_SHEEP = 40.0;
	final static double INIT_SPEED_SHEEP = 35.0;
	final static double BOOST_FACTOR_SHEEP = 2.0;
	final static double MAX_AGE_SHEEP = 8.0;
	final static double FOOD_DROP_BOOST_FACTOR_SHEEP = 1.2;
	final static double FOOD_DROP_RATE_SHEEP = 20.0;
	final static double DESIRE_THRESHOLD_SHEEP = 65.0;
	final static double DESIRE_INCREASE_RATE_SHEEP = 40.0;
	final static double PREGNANT_PROBABILITY_SHEEP = 0.9;

	private Animal dangerSource;
	private SelectionStrategy dangerStrategy;

	public Sheep(SelectionStrategy mateStrategy, SelectionStrategy dangerStrategy, Vector2D pos) {
		super(SHEEP_GENETIC_CODE, Diet.HERBIVORE, INIT_SIGHT_SHEEP, INIT_SPEED_SHEEP, mateStrategy, pos);
		this.dangerStrategy = dangerStrategy;

	}

	public Sheep(Sheep p1, Animal p2) {
		super(p1, p2);
		this.dangerStrategy = p1.dangerStrategy;
		this.dangerSource = null;
	}

	@Override
	public void update(double dt) {
		switch (state) {
			// 1
			case DEAD:
				return;
			// 2
			case NORMAL:
				doNormal(dt);
				break;
			case DANGER:
				doDanger(dt);
				break;
			case HUNGER:
				doHunger(dt);
				break;
			case MATE:
				doMate(dt);
				break;
			default:
				return;
		}
		// 3 - Limitar posición dentro de los límites de la región
		double maxX = this.regionMngr.getWidth();
		double maxY = this.regionMngr.getHeight();

		if (pos.getX() <= 0.0 || pos.getX() >= maxX || pos.getY() <= 0.0 || pos.getY() >= maxY) {
			double validX = Utils.constrainValueInRange(pos.getX(), 0, maxX - 1);
			double validY = Utils.constrainValueInRange(pos.getY(), 0, maxY - 1);
			this.pos = new Vector2D(validX, validY);

			this.setState(State.NORMAL);
		}
		// 4 - Comprobar si muere por edad o energía
		if (this.energy <= 0.0 || this.age >= MAX_AGE_SHEEP) {
			this.setState(State.DEAD);
		}
		// 5 - Comida de la región
		if (this.state != State.DEAD) {
			double food = this.regionMngr.getFood(this, dt);
			this.energy += food;
			if (this.energy > MAX_ENERGY)
				this.energy = MAX_ENERGY;

		}

	}

	private void doNormal(double dt) {
		// 1
		Vector2D destination = this.getDestination();
		// 1.1
		if (destination.distanceTo(this.getPosition()) < COLLISION_RANGE) {
			double x = Utils.RAND.nextDouble(0, this.regionMngr.getWidth());
			double y = Utils.RAND.nextDouble(0, this.regionMngr.getHeight());

			destination = new Vector2D(x, y);
			this.dest = destination;
		}

		// 1.2
		move(speed * dt * Math.exp((energy - 100.0) * HUNGER_DECAY_EXP_FACTOR));
		this.age += dt;
		this.energy -= FOOD_DROP_RATE_SHEEP * dt;
		this.desire += DESIRE_INCREASE_RATE_SHEEP * dt;
		controlDesireEnergy();

		// 2
		// 2.1
		if (dangerSource == null) {
			dangerSource = dangerStrategy.select(this,
					this.regionMngr.getAnimalsInRange(this, a -> a.getDiet() == Diet.CARNIVORE));
		}

		if (dangerSource != null) {
			this.setState(State.DANGER);
		} else {
			if (this.desire > DESIRE_THRESHOLD_SHEEP) {
				this.setState(State.MATE);

			}
		}
	}

	private void doDanger(double dt) {
		if (dangerSource != null && dangerSource.getState() == State.DEAD) {
			dangerSource = null;
		}

		// 1)
		if (dangerSource == null) {
			move(speed * dt * Math.exp((energy - 100.0) * HUNGER_DECAY_EXP_FACTOR));
		}
		// 2)
		else {
			Vector2D destination = pos.plus(pos.minus(dangerSource.getPosition()).direction());
			this.dest = destination;
			move(BOOST_FACTOR_SHEEP * speed * dt * Math.exp((energy - 100.0) * HUNGER_DECAY_EXP_FACTOR));
			this.age += dt;
			this.energy -= FOOD_DROP_RATE_SHEEP * FOOD_DROP_BOOST_FACTOR_SHEEP * dt;
			this.desire += DESIRE_INCREASE_RATE_SHEEP * dt;
			controlDesireEnergy();
		}
		// 3)
		if (dangerSource == null || dangerSource.getPosition().distanceTo(pos) > this.sightRange) {
			// 3.1)
			dangerSource = dangerStrategy.select(this,
					this.regionMngr.getAnimalsInRange(this, a -> a.getDiet() == Diet.CARNIVORE));

			// 3.1.b)
			if (dangerSource == null) {
				// 3.1.b.a)
				if (desire < DESIRE_THRESHOLD_SHEEP) {
					this.setState(State.NORMAL);
				}
				// 3.1.b.b
				else {
					this.setState(State.MATE);
				}
			}
		}
	}

	protected void doMate(double dt) {
		// 1)
		if (mateTarget != null && (mateTarget.getState() == State.DEAD
				|| mateTarget.getPosition().distanceTo(pos) > this.sightRange)) {
			mateTarget = null;
		}
		// 2)
		if (mateTarget == null) {
			mateTarget = this.mateStrategy.select(this,
					this.regionMngr.getAnimalsInRange(this, a -> a.getGeneticCode().equals(SHEEP_GENETIC_CODE)));
			if (mateTarget == null) {
				move(speed * dt * Math.exp((energy - 100.0) * HUNGER_DECAY_EXP_FACTOR));
			}
		} else {
			// 2.1)
			this.dest = mateTarget.getPosition();
			move(BOOST_FACTOR_SHEEP * speed * dt * Math.exp((energy - 100.0) * HUNGER_DECAY_EXP_FACTOR));
			this.age += dt;
			this.energy -= FOOD_DROP_RATE_SHEEP * HUNGER_DECAY_EXP_FACTOR * dt;
			this.desire += DESIRE_INCREASE_RATE_SHEEP * dt;
			controlDesireEnergy();
			// 2.6
			if (this.pos.distanceTo(mateTarget.getPosition()) < COLLISION_RANGE) {
				this.desire = 0.0;
				mateTarget.desire = 0.0;
				// 2.6.b)
				if (this.baby == null) {
					if (Utils.RAND.nextDouble() < PREGNANT_PROBABILITY_SHEEP) {
						this.baby = new Sheep(this, mateTarget);
					}
					mateTarget = null;
				}
				mateTarget = null;
			}
		}
		// 3)
		if (dangerSource == null) {
			dangerSource = dangerStrategy.select(this,
					this.regionMngr.getAnimalsInRange(this, a -> a.getDiet() == Diet.CARNIVORE));
		}
		if (dangerSource != null) {
			setState(State.DANGER);
		} else if (desire < DESIRE_THRESHOLD_SHEEP) {
			setState(State.NORMAL);
		}
	}

	protected void doHunger(double dt) {
	}

	@Override
	protected void setDeadStateAction() {
		dangerSource = null;
		mateTarget = null;
	}

	@Override
	protected void setNormalStateAction() {
		dangerSource = null;
		mateTarget = null;
	}

	@Override
	protected void setMateStateAction() {
		dangerSource = null;
	}

	@Override
	protected void setDangerStateAction() {
		mateTarget = null;
	}

	@Override
	protected void setHungerStateAction() {
		mateTarget = null;
	}

}
