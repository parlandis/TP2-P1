package simulator.factories;

import org.json.JSONObject;

import simulator.model.FloodRegion;
import simulator.model.Region;

public class FloodRegionBuilder extends Builder<Region> {

	public FloodRegionBuilder() {
		super("flood", "Flooded region");
	}

	@Override
	protected Region createInstance(JSONObject data) {
		double F = 2.0, S = 15.0, W = 5.0;
		
		if(data.has("f")) F = data.getDouble("f");
		if(data.has("s")) S = data.getDouble("s");
		if(data.has("w")) W = data.getDouble("w");
		
		
		return new FloodRegion(F, S, W);
	}
	
	@Override
	protected void fillInData(JSONObject o) {
		o.put("f", "food increase factor while flood");
		o.put("s", "dry time");
		o.put("w", "flooded time");
	}

}
