package simulator.factories;

import org.json.JSONObject;

import simulator.model.DryRegion;
import simulator.model.Region;

public class DryRegionBuilder extends Builder<Region> {

	public DryRegionBuilder() {
		super("dry", "Dry Region");
	}


	@Override
	protected Region createInstance(JSONObject data) {
		double r1 = data.optDouble("r1", 0.95);
		double r2 = data.optDouble("r2", 0.95);
		double n = data.optDouble("n", 10.0);
		double m = data.optDouble("m", 5.0);

		return new DryRegion(r1, r2, n, m);
	}

	@Override
	protected void fillInData(JSONObject o) {
		o.put("r1", "food reduction factor (optional, default 0.95)");
		o.put("r2", "R1 reduction factor (optional, default 0.95)");
		o.put("n", "normal period duration (optional, default 10.0)");
		o.put("m", "maximum drought period duration (optional, default 5.0)");
	}
	

}