package simulator.factories;

import org.json.JSONObject;

import simulator.model.LimitedRegion;
import simulator.model.Region;

public class LimitedRegionBuilder extends Builder<Region>  {

	public LimitedRegionBuilder() {
		super("limited", "Region Limitada");
	}

	@Override
	protected Region createInstance(JSONObject data) {
		int MIN = 1, MAX = 100;
		double T = 5.0;
		
		if(data.has("min")) MIN = data.getInt("min");
		if(data.has("max")) MAX = data.getInt("max");
		if(data.has("t")) T = data.getDouble("t");
		

		return new LimitedRegion(MIN, MAX, T);
	}

}
