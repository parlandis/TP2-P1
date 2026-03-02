package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;
import simulator.model.Wolf;

public class WolfBuilder extends Builder<Animal> {
	private Factory<SelectionStrategy> strategyFactory;

	public WolfBuilder(Factory<SelectionStrategy> strategyFactory) {
		super("wolf", "Wolf animal");
		this.strategyFactory = strategyFactory;
	}

	@Override  // Creamos un objeto lobo a partir del jSOn
	protected Wolf createInstance(JSONObject data) {
		Vector2D pos = null;
		if (data.has("pos")) {
			JSONArray p = data.getJSONArray("pos");
			pos = new Vector2D(p.getDouble(0), p.getDouble(1));
		}

		SelectionStrategy mateStrategy = null;
		SelectionStrategy huntingStrategy = null;

		if (data.has("mate_strategy")) { // Si el JSON tiene una estrategia de apareamiento, la creamos a partir del JSON llamando a la factoria de estretegoias
			mateStrategy = strategyFactory.createInstance(data.getJSONObject("mate_strategy"));
		} else {
			mateStrategy = new SelectFirst();
		}

		if (data.has("hunt_strategy")) {
			huntingStrategy = strategyFactory.createInstance(data.getJSONObject("hunt_strategy"));
		} else {
			huntingStrategy = new SelectFirst();
		}

		return new Wolf(mateStrategy, huntingStrategy, pos);
	}

}
