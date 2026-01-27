package simulator.model;

import simulator.misc.Vector2D;

public class Sheep extends Animal {
	private Animal dangerSource;
	private SelectionStrategy dangerStrategy;
	
	
	public Sheep(SelectionStrategy mateStrategy, SelectionStrategy dangerStrategy,  Vector2D pos) {
		super("sheep", Diet.HERBIVORE,40.0, 35.0, mateStrategy, pos);
		this.dangerStrategy = dangerStrategy;
		
	}
	public Sheep(Sheep p1, Animal p2) {
		super(p1, p2);
		dangerStrategy = p1.dangerStrategy;
		dangerSource = null;
	}
	
	@Override
	public void update(double dt) {
		if(state == State.DEAD) return;
		switch(state) {
		case State.NORMAL:
			setNormalStateAction();
			break;
		case State.DANGER:
			setDangerStateAction();
			break;
		case State.HUNGER:
			setHungerStateAction();
			break;
		case State.MATE:
			setMateStateAction();
			break;
		default:
			return;
		}
	}
	
	
	
	private void doNormal(double dt) {        //TODO: mirar bien como funciona lo del normalStateAction
		Vector2D destination = this.getDestination();
		if(destination.distanceTo(this.getPosition()) < 8.0) {
			destination = destination.get_random_vector();       // TODO: get new random destination
			this.dest = destination;
		}
		
		
		move(speed*dt*Math.exp((energy-100.0)*0.007));
		this.age += dt;
		this.energy -= 20.0*dt;
		this.desire += 40.0 *dt;
		
		if(dangerSource == null) {  //TODO: Falta implementar las estrategias
			
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
	@Override
	protected void setHungerStateAction() {
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
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void setMateStateAction() {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void setDangerStateAction() {
		// TODO Auto-generated method stub
		
	}

}
