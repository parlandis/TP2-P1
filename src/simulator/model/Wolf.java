package simulator.model;

import simulator.misc.Vector2D;

public class Wolf extends Animal  {
	private Animal huntTarget;
	private SelectionStrategy huntingStrategy;
	
	
	
	public Wolf(SelectionStrategy mateStrategy, SelectionStrategy huntingStrategy,  Vector2D pos) {
		super("wolf", Diet.CARNIVORE,50.0, 60.0, mateStrategy, pos);
		this.huntingStrategy = huntingStrategy;
		this.huntTarget = null;
	}
	
	
	
	protected Wolf(Wolf p1, Animal p2) {
		super(p1, p2);
		huntingStrategy = p1.huntingStrategy;
		huntTarget = null;
	}
	
	
	@Override
	public void update(double dt) {
		if(state == State.DEAD) return;
		switch(state) {
		case State.NORMAL:
			doNormal(dt);
			break;
		case State.HUNGER:
			doHunger(dt);
			break;
		case State.MATE:
			doMate(dt);
			break;
		default:
			return;
		}
		
		if(pos.)
	}



	private void doMate(double dt) {

	}



	private void doHunger(double dt) {
		//1 
		if(huntTarget == null || huntTarget.getState().equals(State.DEAD) || huntTarget.getPosition().distanceTo(getPosition()) > 8.0) {
			huntTarget = this.huntingStrategy.select(huntTarget, null)  ;
		}
		if(huntTarget == null) {
			//1.1
			if(getDestination().distanceTo(this.getPosition()) < 8.0) {
				Vector2D destination = getRandomDestination();       // TODO: get new random destination
				this.setDestination(destination);
			}
			//1.2
			move(speed*dt*Math.exp((energy-100.0)*0.007));
			this.age += dt;
			this.energy -= 18.0*dt;
			this.desire += 30.0 *dt;
			if(energy < 0.0) energy = 0.0;
			if(energy > 100.0) energy = 100.0;
			if(desire < 0.0) desire = 0.0;
			if(desire > 100.0) desire = 100.0;
		}
		
	}



	private void doNormal(double dt) {
		//1.1
		if(getDestination().distanceTo(this.getPosition()) < 8.0) {
			new Vector2D();
			Vector2D destination = Vector2D.get_random_vector(0.0, this.regionMngr.getCols());       // TODO: get new random destination
			this.dest = destination;
		}
		//1.2
		move(speed*dt*Math.exp((energy-100.0)*0.007));
		this.age += dt;
		this.energy -= 18.0*dt;
		this.desire += 30.0 *dt;
		if(energy < 0.0) energy = 0.0;
		if(energy > 100.0) energy = 100.0;
		if(desire < 0.0) desire = 0.0;
		if(desire > 100.0) desire = 100.0;
		//2
		if(energy < 50.0) {
			setState(State.HUNGER);
		}
		else if(desire > 65.0) {
			setState(State.MATE);
		}	
	}



	@Override
	protected void setNormalStateAction() {
		// TODO Auto-generated method stub
		
	}



	@Override
	protected void setMateStateAction() {
		// TODO Auto-generated method stub
		
	}



	@Override
	protected void setHungerStateAction() {
		// TODO Auto-generated method stub
		
	}



	@Override
	protected void setDangerStateAction() {
		// TODO Auto-generated method stub
		
	}



	@Override
	protected void setDeadStateAction() {
		// TODO Auto-generated method stub
		
	}	

	

	
	
}
