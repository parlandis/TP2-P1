package simulator.model;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Wolf extends Animal {

	final static String WOLF_GENETIC_CODE = "Wolf";
	final static double INIT_SIGHT_WOLF = 50.0;
	final static double INIT_SPEED_WOLF = 60.0;
	final static double BOOST_FACTOR_WOLF = 3.0;
	final static double MAX_AGE_WOLF = 14.0;
	final static double FOOD_THRESHOLD_WOLF = 50.0;
	final static double FOOD_DROP_BOOST_FACTOR_WOLF = 1.2;
	final static double FOOD_DROP_RATE_WOLF = 18.0;
	final static double FOOD_EAT_VALUE_WOLF = 50.0;
	final static double DESIRE_THRESHOLD_WOLF = 65.0;
	final static double DESIRE_INCREASE_RATE_WOLF = 30.0;
	final static double PREGNANT_PROBABILITY_WOLF = 0.75;

	private Animal huntTarget;
	private SelectionStrategy huntingStrategy;

	public Wolf(SelectionStrategy mateStrategy, SelectionStrategy huntingStrategy, Vector2D pos) {
		super(WOLF_GENETIC_CODE, Diet.CARNIVORE, INIT_SIGHT_WOLF, INIT_SPEED_WOLF, mateStrategy, pos);
		this.huntingStrategy = huntingStrategy;
		this.huntTarget = null;
	}

	protected Wolf(Wolf p1, Animal p2) {
		super(p1, p2);
		this.huntingStrategy = p1.huntingStrategy;
		this.huntTarget = null;
	}

	@Override
	public void update(double dt) {
		if (state == State.DEAD)
			return;

		switch (state) {
			case NORMAL:
				doNormal(dt);
				break;
			case HUNGER:
				doHunger(dt);
				break;
			case MATE:
				doMate(dt);
				break;
			default:
				break;
		}

		if (pos.getX() < 0.0 || pos.getX() >= regionMngr.getWidth() || pos.getY() < 0.0
				|| pos.getY() >= regionMngr.getHeight()) {
			pos = new Vector2D(Utils.constrainValueInRange(pos.getX(), 0, regionMngr.getWidth() - 1),
					Utils.constrainValueInRange(pos.getY(), 0, regionMngr.getHeight() - 1));
			setState(State.NORMAL);
		}

		if (energy <= 0.0 || age > MAX_AGE_WOLF) {
			setState(State.DEAD);
		}

		// 5. Comida de la region
		if (this.state != State.DEAD) {
			double food = this.regionMngr.getFood(this, dt);
			this.energy += food;
			if (this.energy > 100.0)
				this.energy = 100.0;
		}
	}

	private void doNormal(double dt) {
		if (dest.distanceTo(pos) < 8.0) {
			this.dest = new Vector2D(Utils.RAND.nextDouble(0, this.regionMngr.getWidth()),
					Utils.RAND.nextDouble(0, this.regionMngr.getHeight()));
		}

		move(speed * dt * Math.exp((energy - 100.0) * 0.007));
		this.age += dt;
		this.energy = Math.max(0, this.energy - FOOD_DROP_RATE_WOLF * dt);
		this.desire = Math.min(100.0, this.desire + DESIRE_INCREASE_RATE_WOLF * dt);

		if (energy < FOOD_THRESHOLD_WOLF) {
			setState(State.HUNGER);
		} else if (desire > DESIRE_THRESHOLD_WOLF) {
			setState(State.MATE);
		}
	}

	private void doHunger(double dt) {
		if (huntTarget == null || huntTarget.getState() == State.DEAD
				|| huntTarget.getPosition().distanceTo(pos) > sightRange) {
			huntTarget = huntingStrategy.select(this,
					this.regionMngr.getAnimalsInRange(this, a -> a.getDiet() == Diet.HERBIVORE));
		}

		if (huntTarget == null) {
			doNormal(dt);
		} else {
			this.dest = huntTarget.getPosition();
			move(BOOST_FACTOR_WOLF * speed * dt * Math.exp((energy - 100.0) * 0.007));
			this.age += dt;
			this.energy = Math.max(0, this.energy - FOOD_DROP_RATE_WOLF * FOOD_DROP_BOOST_FACTOR_WOLF * dt);
			this.desire = Math.min(100.0, this.desire + DESIRE_INCREASE_RATE_WOLF * dt);

			if (pos.distanceTo(huntTarget.getPosition()) < 8.0) {
				huntTarget.setState(State.DEAD);
				huntTarget = null;
				this.energy = Math.min(100.0, this.energy + FOOD_EAT_VALUE_WOLF);
			}
		}

		if (energy > FOOD_THRESHOLD_WOLF) {
			if (desire > DESIRE_THRESHOLD_WOLF)
				setState(State.MATE);
			else
				setState(State.NORMAL);
		}
	}

	private void doMate(double dt) {
		if (mateTarget != null
				&& (mateTarget.getState() == State.DEAD || mateTarget.getPosition().distanceTo(pos) > sightRange)) {
			mateTarget = null;
		}

		if (mateTarget == null) {
			mateTarget = mateStrategy.select(this,
					this.regionMngr.getAnimalsInRange(this, a -> a.geneticCode.equals(WOLF_GENETIC_CODE)));
			if (mateTarget == null) {
				doNormal(dt);
			}
		} else {
			this.dest = mateTarget.getPosition();
			move(BOOST_FACTOR_WOLF * speed * dt * Math.exp((energy - 100.0) * 0.007));
			this.age += dt;
			this.energy = Math.max(0, this.energy - FOOD_DROP_RATE_WOLF * FOOD_DROP_BOOST_FACTOR_WOLF * dt);
			this.desire = Math.min(100.0, this.desire + DESIRE_INCREASE_RATE_WOLF * dt);

			if (pos.distanceTo(mateTarget.getPosition()) < 8.0) {
				this.desire = 0.0;
				mateTarget.desire = 0.0;
				if (baby == null && Utils.RAND.nextDouble() < PREGNANT_PROBABILITY_WOLF) {
					baby = new Wolf(this, mateTarget);
				}
				this.energy = Math.max(0, this.energy - 10.0);
				mateTarget = null;
			}
		}

		if (energy < FOOD_THRESHOLD_WOLF) {
			setState(State.HUNGER);
		} else if (desire < DESIRE_THRESHOLD_WOLF) {
			setState(State.NORMAL);
		}
	}

	@Override
	protected void setNormalStateAction() {
		huntTarget = null;
		mateTarget = null;
	}

	@Override
	protected void setMateStateAction() {
		huntTarget = null;
	}

	@Override
	protected void setHungerStateAction() {
		mateTarget = null;
	}

	@Override
	protected void setDangerStateAction() {
	}

	@Override
	protected void setDeadStateAction() {
		huntTarget = null;
		mateTarget = null;
	}
}
