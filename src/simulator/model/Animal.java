package simulator.model;

import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public abstract class Animal implements Entity, AnimalInfo {
	// Constantes
	final static double INIT_ENERGY = 100.0;
	final static double MUTATION_TOLERANCE = 0.2;
	final static double NEARBY_FACTOR = 60.0;
	final static double COLLISION_RANGE = 8;
	final static double HUNGER_DECAY_EXP_FACTOR = 0.007;
	final static double MAX_ENERGY = 100.0;
	final static double MAX_DESIRE = 100.0;

	// Atributos
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

	// Constructores
	protected Animal(String geneticCode, Diet diet, double sightRange, double initSpeed, SelectionStrategy mateStrategy,
			Vector2D pos) throws IllegalArgumentException {
		if (geneticCode == null || geneticCode.isBlank() || sightRange <= 0.0 || initSpeed <= 0.0
				|| mateStrategy == null) {
			throw new IllegalArgumentException("Argumentos no validos para crear el animal");
		}

		this.geneticCode = geneticCode;
		this.diet = diet;
		this.sightRange = sightRange;
		this.speed = Utils.getRandomizedParameter(initSpeed, 0.1);
		this.mateStrategy = mateStrategy;
		this.pos = pos;
		this.state = State.NORMAL;
		this.energy = INIT_ENERGY;
		this.age = 0.0;
		this.desire = 0.0;
		this.dest = null;
		this.mateTarget = null;
		this.baby = null;
		// this.regionMngr = null;
		// init(regionMngr);
	}

	// Constructor para la reproducción: crea un nuevo animal a partir de dos progenitores
	protected Animal(Animal p1, Animal p2) {
		this.dest = null;
		this.baby = null;
		this.mateTarget = null;
		// this.regionMngr = null;
		this.state = State.NORMAL;
		// La energía inicial es el promedio de los progenitores
		this.energy = (p1.getEnergy() + p2.getEnergy()) / 2;
		this.age = 0.0;
		this.desire = 0.0;
		this.geneticCode = p1.getGeneticCode();
		this.diet = p1.getDiet();
		this.mateStrategy = p2.mateStrategy;
		// Posición inicial cerca de p1 con un desplazamiento aleatorio (Gaussian para mayor naturalidad)
		this.pos = p1.getPosition().plus(Vector2D.get_random_vector(-1, 1).scale(NEARBY_FACTOR * (Utils.RAND.nextGaussian() + 1)));
		// Los atributos (vista, velocidad) heredan el promedio con una posible mutación aleatoria
		this.sightRange = Utils.getRandomizedParameter((p1.getSightRange() + p2.getSightRange()) / 2,MUTATION_TOLERANCE);
		this.speed = Utils.getRandomizedParameter((p1.getSpeed() + p2.getSpeed()) / 2, MUTATION_TOLERANCE);
	}
	
	//Inicia el animal con pos aleatoria si no tine una , y la ajusta a los limites del mapa 
	protected void init(AnimalMapView regMngr) {
		this.regionMngr = regMngr;

		if (this.pos == null) {
			this.pos = new Vector2D(Utils.RAND.nextDouble(0, regMngr.getWidth()),Utils.RAND.nextDouble(0, regMngr.getHeight()));
		} else {
			this.pos = adjustPos(this.pos, regMngr.getWidth(), regMngr.getHeight());
		}

		this.dest = new Vector2D(Utils.RAND.nextDouble(0, regMngr.getWidth()), Utils.RAND.nextDouble(0, regMngr.getHeight()));
	}

	// Ajusta la posición para que el mapa sea toroidal (si sales por la derecha apareces por la izquierda)
	private Vector2D adjustPos(Vector2D p, int w, int h) {
		double x = p.getX();
		double y = p.getY();
		while (x >= w)
			x -= w;
		while (x < 0)
			x += w;
		while (y >= h)
			y -= h;
		while (y < 0)
			y += h;
		return new Vector2D(x, y);
	}

	protected void adjustPositionToMap() {
		this.pos = adjustPos(this.pos, this.regionMngr.getWidth(), this.regionMngr.getHeight());
	}

	@Override
	public void update(double dt) {

	}
	//Nace el bebe
	// Entrega la cría y resetea el atributo baby para que el animal pueda volver a quedar preñado
	protected Animal deliverBaby() {
		Animal bebe = this.baby;
		this.baby = null;
		return bebe;
	}

	// Calcula el movimiento hacia el destino 'dest' usando aritmética de vectores
	protected void move(double speed) {
		// (dest - pos) da el vector dirección, .direction() lo normaliza (longitud 1) y scale escala por la velocidad
		this.pos = pos.plus(dest.minus(pos).direction().scale(speed));
	}

	// Cambia el estado del animal y ejecuta la acción de configuración correspondiente (Template Method)
	protected void setState(State state) { 
		this.state = state;
		switch (state) {
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

	public JSONObject asJSON() {
		JSONObject obj = new JSONObject();
		obj.put("pos", pos.asJSONArray());
		obj.put("gcode", geneticCode);
		obj.put("diet", diet.toString());
		obj.put("state", state.toString());
		obj.put("age", this.age);
		return obj;
	}

	protected void controlDesireEnergy() {
		if (energy < 0.0)
			energy = 0.0;
		if (energy > MAX_ENERGY)
			energy = MAX_ENERGY;
		if (desire < 0.0)
			desire = 0.0;
		if (desire > MAX_DESIRE)
			desire = MAX_DESIRE;
	}

	abstract protected void setNormalStateAction();

	abstract protected void setMateStateAction();

	abstract protected void setHungerStateAction();

	abstract protected void setDangerStateAction();

	abstract protected void setDeadStateAction();

	@Override
	public State getState() {
		return this.state;
	}

	@Override
	public Vector2D getPosition() {
		return this.pos;
	}

	@Override
	public String getGeneticCode() {
		return this.geneticCode;
	}

	@Override
	public Diet getDiet() {
		return this.diet;
	}

	@Override
	public double getSpeed() {
		return this.speed;
	}

	@Override
	public double getSightRange() {
		return this.sightRange;
	}

	@Override
	public double getEnergy() {

		return this.energy;
	}

	@Override
	public double getAge() {
		return this.age;
	}

	@Override
	public Vector2D getDestination() {
		return this.dest;
	}

	@Override
	public boolean isPregnant() {
		return this.baby != null;
	}

	protected boolean isDead() {
		return this.state == State.DEAD;
	}

}