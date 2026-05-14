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
		double r1 = 0.95;
		double r2 = 0.95;
		double n = 10.0;
		double m = 5.5;
		
		if(data.has("r1")) r1 = data.getDouble("r1");
		if(data.has("r2")) r2 = data.getDouble("r2");
		if(data.has("n")) n = data.getDouble("n");
		if(data.has("m")) m = data.getDouble("m");
		
		return new DryRegion(r1,r2,n,m);
	}
	
	
	@Override
	protected void fillInData(JSONObject o) {
		o.put("r1", "food increase factor (optional, default 2.0)");
		o.put("r2", "initial amount of food (optional, default 100.0)");
		o.put("n", "food increase factor (optional, default 2.0)");
		o.put("m", "initial amount of food (optional, default 100.0)");
	}
	

}