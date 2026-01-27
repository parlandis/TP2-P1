package simulator.model;

import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;



public abstract class Animal implements Entity, AnimalInfo {
	protected String geneticCode;
	protected Diet diet;
	protected State state;
	protected Vector2D pos;
	protected Vector2D dest;
	protected double energy;
	protected double speed;
	protected double age;
	protected double desire;
	protected double sightRange;
	protected Animal mateTarget;
	protected Animal baby;
	protected AnimalMapView regionMngr;
	protected SelectionStrategy mateStrategy;
	
	
	protected Animal(String geneticCode, Diet diet, double sightRange, double initSpeed, SelectionStrategy mateStrategy, Vector2D pos) {
		this.geneticCode = geneticCode;
		this.diet = diet;
		this.sightRange = sightRange;
		this.speed = Utils.getRandomizedParameter(initSpeed, 0.1);
		this.mateStrategy = mateStrategy;
		this.pos = null;  // Pone que se inicializa despues en init 
		this.state = State.NORMAL;
		this.energy = 100.0;
		this.age = 0.0;  // NO dice nada 
		this.desire = 0.0;
		this.dest = null;
		this.mateTarget = null;
		this.baby = null;
		this.regionMngr = null;
	}
	
	protected Animal(Animal p1, Animal p2) {
		this.dest = null;
		this.baby = null;
		this.mateTarget = null;
		this.regionMngr = null;
		this.state = State.NORMAL;
		this.desire = 0.0;
		this.geneticCode = p1.getGeneticCode();
		this.diet = p1.getDiet();
		this.mateStrategy = p2.mateStrategy;
		this.energy = p1.getPosition().plus(Vector2D.getRandomVector(-1,1).scale(60.0*(Utils.RAND.nextGaussian()+1)))
		this.sightRange = Utils.getRandomizedParameter((p1.getSightRange()+p2.getSightRange())/2, 0.2);
		this.speed = Utils.getRandomizedParameter((p1.getSpeed()+p2.getSpeed())/2, 0.2);
	}
	
	private void init(AnimalMapView regMngr) {
		this.regionMngr = regMngr;
		
		if(pos == null) {                     // TODO: Hacer primero la clase regionMngr
			this.pos = regionMngr.getRandomPosition();
		}
		else {
			
		}
	}
	
	private Animal deliverBaby() {   // TODO: Haver que nazca el Bebe
		Animal baby = this.baby;
		this.baby = null;
		return baby;
	}
	
	protected void move(double speed) {
		this.pos = pos.plus(dest.minus(pos).direction().scale(speed));
	}
	
	protected void setState(State state) {
		this.state = state;
		switch(state) {
		case NORMAL:
			setNormalStateAction();
			break;
		case HUNGER:
			setHungerStateAction();
			break;
		case MATE:
			setMateStateAction();
			break;
		case DANGER:
			setDangerStateAction();
			break;
		case DEAD:
			setDeadStateAction();
			break;
		}
	}
	
	public JSONObject  asJSON() {
		
	}
	
	abstract protected void setNormalStateAction();
	abstract protected void setMateStateAction();
	abstract protected void setHungerStateAction();
	abstract protected void setDangerStateAction();
	abstract protected void setDeadStateAction();
	
	@Override
	public State getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vector2D getPosition() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getGeneticCode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Diet getDiet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getSpeed() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getSightRange() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getEnergy() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getAge() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Vector2D getDestination() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isPregnant() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void update(double dt) {
		// TODO Auto-generated method stub
		
	}

}
