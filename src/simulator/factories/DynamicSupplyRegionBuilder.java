package simulator.factories;

import org.json.JSONObject;

import simulator.model.DynamicSupplyRegion;
import simulator.model.Region;

public class DynamicSupplyRegionBuilder extends Builder<Region> {

	public DynamicSupplyRegionBuilder() {
		super("dynamic", "Dynamic food supply");
	}

	@Override
	protected Region createInstance(JSONObject data) {
		double food = 100.0;
		double factor = 2.0;

		if (data.has("food")) {
			food = data.getDouble("food");
		}
		if (data.has("factor"))
			factor = data.getDouble("factor");

		return new DynamicSupplyRegion(food, factor);
	}

	@Override
	protected void fillInData(JSONObject o) {
		o.put("type", "dynamic");
		o.put("desc", "Dynamic food supply");

		o.put("factor", "food increase factor (optional, default 2.0)");
		o.put("food", "initial amount of food (optional, default 100.0)");
		o.put("power", "power of food (optional, default 0.5)");
	}

}
