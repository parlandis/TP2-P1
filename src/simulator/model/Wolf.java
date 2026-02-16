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

	private void doHunger(double dt) {
		//1 
		if(huntTarget == null || huntTarget.getState().equals(State.DEAD) || huntTarget.getPosition().distanceTo(getPosition()) > Constantes.INIT_SIGHT_WOLF) {
			huntTarget = this.huntingStrategy.select(huntTarget, this.regionMngr.getAnimalsInRange(this, a ->a.getDiet() == Diet.HERBIVORE));  ;
		}
		if(huntTarget == null) {  //TODO: No entiendo como va lo de seguir el punto 1 
			//1.1
			Vector2D destination = this.getDestination();
			if(destination.distanceTo(this.getPosition()) < 8.0) {
				double x = Utils.RAND.nextDouble(0, this.regionMngr.getWidth());
				double y = Utils.RAND.nextDouble(0, this.regionMngr.getHeight());
				
				destination = new Vector2D(x,y);       
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
		}
		else {
			//2.1 
			this.dest = huntTarget.getPosition();
			move(3.0*speed*dt*Math.exp((energy-100.0)*0.007));
			this.age += dt;
			this.energy -= 18.0*1.2*dt;
			this.desire += 30.0*dt;
			if(energy < 0.0) energy = 0.0;
			if(energy > 100.0) energy = 100.0;
			if(desire < 0.0) desire = 0.0;
			if(desire > 100.0) desire = 100.0;
			
			if(huntTarget.getPosition().distanceTo(this.getPosition()) < 8.0) {
				huntTarget.setState(State.DEAD);
				huntTarget = null;
				this.energy += 50.0;
				if(this.energy > 100.0) this.energy = 100.0;
			}
		}
		//3
		if(energy > 50.0) {
			//3.2
			if(desire > 65.0) {
				setState(State.MATE);
			}
			else {
				setState(State.NORMAL);
			}
		}
	}

	private void doMate(double dt) {
		//1
		if(this.mateTarget != null && (mateTarget.getState() == State.DEAD || mateTarget.getPosition().distanceTo(pos) > this.sightRange)) {
			mateTarget = null;
		}
		//2
		if(mateTarget == null) {
			mateTarget =  this.mateStrategy.select(this.mateTarget, this.regionMngr.getAnimalsInRange(this, a -> a.geneticCode == Constantes.WOLF_GENETIC_CODE));
			if(mateTarget == null) {
				move(speed*dt*Math.exp((energy-100.0)*0.007));
			}
		}
		else {
			// 2.1)
			this.dest = mateTarget.getPosition();
			move(3.0*speed*dt*Math.exp((energy-100.0)*0.007));
			this.age += dt;
			this.energy -= 18.0*1.2*dt;
			this.desire += 30.0*dt;
			if(energy < 0.0) energy = 0.0;
			if(energy > 100.0) energy = 100.0;
			if(desire < 0.0) desire = 0.0;
			if(desire > 100.0) desire = 100.0;
			
			if(this.pos.distanceTo(mateTarget.getPosition()) < 8.0) {
				this.desire = 0.0;
				mateTarget.desire = 0.0;
				if(this.baby == null) { 
					if(Utils.RAND.nextDouble() < 0.75) {
						this.baby = new Wolf(this, mateTarget);
					}
				}
				this.energy -= 10.0;
				if(this.energy < 0.0) this.energy = 0.0;	
				mateTarget = null;
			}
		}
		if(this.energy < 50.0) {
			setState(State.HUNGER);
		}
		 else if(this.desire < 65.0) {
			 setState(State.NORMAL);
		 }	
	}

	@Override  // TODO: No entiendo como va lo de los estados, si es que hay que hacer algo en cada uno o no
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
