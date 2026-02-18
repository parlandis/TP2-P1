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
		super("wolf", "Wolf");
		this.strategyFactory = strategyFactory;
	}

	@Override
	protected Wolf createInstance(JSONObject data) {
		Vector2D pos = null;
		if(data.has("pos")) {
			JSONArray p = data.getJSONArray("pos");
			pos = new Vector2D(p.getDouble(0), p.getDouble(1));
		}
		
		SelectionStrategy mateStrategy = null;
		SelectionStrategy huntStrategy = null;
		
		if(data.has("mate_strategy")) {
			mateStrategy = strategyFactory.createInstance(data.getJSONObject("mate_strategy"));
		}
		else mateStrategy = new SelectFirst();
		
		if(data.has("hunt_strategy")) {
			huntStrategy = strategyFactory.createInstance(data.getJSONObject("danger_strategy"));
		}
		else huntStrategy = new SelectFirst();
		
		return new Wolf(mateStrategy, huntStrategy, pos);
	}
}
