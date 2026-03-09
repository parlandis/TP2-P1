package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;
import simulator.model.Sheep;

public class SheepBuilder extends Builder<Animal> {
	private Factory<SelectionStrategy> strategyFactory;

	public SheepBuilder(Factory<SelectionStrategy> strategyFactory) {
		super("sheep", "Sheep animal");
		this.strategyFactory = strategyFactory;
	}

	@Override
	protected Sheep createInstance(JSONObject data) {
		Vector2D pos = null;
		if (data.has("pos")) {
			JSONObject p = data.getJSONObject("pos");

			JSONArray xRange = p.getJSONArray("x_range");
			JSONArray yRange = p.getJSONArray("y_range");

			double x = Utils.RAND.nextDouble(xRange.getDouble(0), xRange.getDouble(1));
			double y = Utils.RAND.nextDouble(yRange.getDouble(0), yRange.getDouble(1));
			pos = new Vector2D(x, y);
		}

		SelectionStrategy mateStrategy = null;
		SelectionStrategy dangerStrategy = null;

		if (data.has("mate_strategy")) {
			mateStrategy = strategyFactory.createInstance(data.getJSONObject("mate_strategy"));
		} else {
			mateStrategy = new SelectFirst();
		}

		if (data.has("danger_strategy")) {
			dangerStrategy = strategyFactory.createInstance(data.getJSONObject("danger_strategy"));
		} else {
			dangerStrategy = new SelectFirst();
		}

		return new Sheep(mateStrategy, dangerStrategy, pos);
	}

}
