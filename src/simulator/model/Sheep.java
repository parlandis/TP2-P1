package simulator.model;

import java.util.function.Predicate;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Sheep extends Animal {
	private Animal dangerSource;
	private SelectionStrategy dangerStrategy;
	
	
	public Sheep(SelectionStrategy mateStrategy, SelectionStrategy dangerStrategy,  Vector2D pos) {
		super(Constantes .SHEEP_GENETIC_CODE, Diet.HERBIVORE,Constantes.INIT_SIGHT_SHEEP, Constantes.INIT_SPEED_SHEEP, mateStrategy, pos);
		this.dangerStrategy = dangerStrategy;
		
	}
	public Sheep(Sheep p1, Animal p2) {
		super(p1, p2);
		this.dangerStrategy = p1.dangerStrategy;
		this.dangerSource = null;
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
			doDanger(dt);
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
		if(this.energy <= 0.0 || this.age >= Constantes.MAX_AGE_SHEEP) {
			this.state = State.DEAD;
		}
		//5
		if(this.state != State.DEAD) {
			double food = this.regionMngr.getFood(this, dt);
			this.energy += food;
			if(this.energy > 100.0) this.energy = 100.0;
			
		}
		
		//TODO: faltan los demas pasos 
	}
	
	
	
	private void doNormal(double dt) {        //TODO: mirar bien como funciona lo del normalStateAction
		//1
		Vector2D destination = this.getDestination();
		//1.1
		if(destination.distanceTo(this.getPosition()) < 8.0) {
			double x = Utils.RAND.nextDouble(0, this.regionMngr.getWidth());
			double y = Utils.RAND.nextDouble(0, this.regionMngr.getHeight());
			
			destination = new Vector2D(x,y);       
			this.dest = destination;
		}
		
		//1.2
		move(speed*dt*Math.exp((energy-100.0)*0.007));
		this.age += dt;
		this.energy -= 20.0*dt;
		this.desire += 40.0 *dt;
		
		//2
		//2.1
		if(dangerSource == null) {  //TODO: Falta implementar las estrategias

			
			dangerSource = SelectionStrategy.select(dangerSource, this.regionMngr.getAnimalsInRange(this, ));
		}
		
		if(dangerSource != null) {
			this.setState(State.DANGER);
		}
		else {
			if(this.desire > 65.0) {
				this.setState(State.MATE);
			}
		}
	}

	
	protected void doMate(double dt) {
		//1)
		if(mateTarget != null && (mateTarget.getState() == State.DEAD || mateTarget.getPosition().distanceTo(pos) > this.sightRange)) {
			mateTarget = null;
		}
		//2)
		if(mateTarget == null) {  
			mateTarget =      ;   //TODO: falta la estrategia 
			if(mateTarget == null) {
				move(speed*dt*Math.exp((energy-100.0)*0.007));
			}
		}
		else {
			// 2.1)
			this.dest = mateTarget.getPosition();
			move(2.0*speed*dt*Math.exp((energy-100.0)*0.007));
			this.age += dt;
			this.energy -= 20.0*1.2*dt;
			if(energy < 0.0) energy = 0.0;
			if(energy > 100.0) energy = 100.0;
			this.desire += 40.0 *dt;
			if(desire < 0.0) desire = 0.0;
			if(desire > 100.0) desire = 100.0;
			//2.6
			if(this.pos.distanceTo(mateTarget.getPosition()) < 8.0) {
				this.desire = 0.0;
				mateTarget.desire = 0.0;
				//2.6.b)
				if(this.baby == null) {
					this.baby = new Sheep(this, mateTarget); //TODO:NO entiendo lo de con probabilidad 0.9
					mateTarget = null;
				}
				mateTarget = null;	
			}
		}
		//3)
		if(dangerSource == null) {
			dangerSource =    ; // no hay 
		}
		if(dangerSource == null) {
			
		}
	}

	protected void doHunger(double dt) {
		// TODO Auto-generated method stub
		
	}

	private void doDanger(double dt) {
		if(dangerSource != null && dangerSource.state == state.DEAD) {
			dangerSource = null;
		}
		// 1)
		if(dangerSource == null) {
			move(speed*dt*Math.exp((energy-100.0)*0.007));
		}
		// 2)
		else {
			Vector2D destination = pos.plus(pos.minus(dangerSource.getPosition()).direction());
			this.dest = destination;
			move(2.0*speed*dt*Math.exp((energy-100.0)*0.007));
			this.age += dt;
			this.energy -= 20.0*1.2*dt;
			if(energy < 0.0) energy = 0.0;
			if(energy > 100.0) energy = 100.0;
			this.desire += 40.0 *dt;
			if(desire < 0.0) desire = 0.0;
			if(desire > 100.0) desire = 100.0;
		}
		//3)
		if(dangerSource == null || dangerSource.getPosition().distanceTo(pos) > this.sightRange) {
			//3.1)
			dangerSource =     ;// TODO: Implementar la estrategia
			//3.1.b) 
			if(dangerSource == null) {
				//3.1.b.a) 
				if(desire < 65.0) {
					this.state = State.NORMAL;
				}
				//3.1.b.b
				else {
					this.state = State.MATE;
				}
			}
		}
	}
	@Override
	protected void setDeadStateAction() {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void setNormalStateAction() {
		this.dangerSource = null;
		this.mateTarget = null;
		
	}
	@Override
	protected void setMateStateAction() {
		this.dangerSource = null;
		
	}
	@Override
	protected void setDangerStateAction() {
		this.mateTarget = null;
	}
	@Override
	protected void setHungerStateAction() {
		// TODO Auto-generated method stub
		
	}

}
