package simulator.model;

import simulator.misc.Utils;
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
		switch(state) {
		//1
		case State.DEAD:
			return;
		//2
		case State.NORMAL:
			doNormal(dt);
			break;
		case State.DANGER:
			doHunger(dt);
			break;
		default:
			return;
		}
		//3
		double maxX = this.regionMngr.getWidth();
		double maxY = this.regionMngr.getHeight();
		
		if(pos.getX() <= 0.0 || pos.getX() >= maxX || pos.getY() <= 0.0 || pos.getY() >= maxY) {
            double validX = Utils.constrainValueInRange(pos.getX(), 0, maxX);
            double validY = Utils.constrainValueInRange(pos.getY(), 0, maxY);
            this.pos = new Vector2D(validX, validY);
            
           this.state = State.NORMAL;
		}
		//4
		if(this.energy <= 0.0 || this.age >= Constantes.MAX_AGE_WOLF) {
			this.state = State.DEAD;
		}
		//5
		if(this.state != State.DEAD) {
			double food = this.regionMngr.getFood(this, dt);
			this.energy += food;
			if(this.energy > 100.0) this.energy = 100.0;
			
		}
		
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
