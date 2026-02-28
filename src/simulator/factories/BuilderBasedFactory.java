package simulator.factories;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

public class BuilderBasedFactory<T> implements Factory<T> {  
	private Map<String, Builder<T>> builders;  
	private List<JSONObject> buildersInfo;
		
	public BuilderBasedFactory() {  
		this.builders = new HashMap<>();
		this.buildersInfo = new LinkedList<>();
	}

	public BuilderBasedFactory(List<Builder<T>> builders) {  
		this();
		for(Builder<T> b: builders) {
			addBuilder(b);
		}
	}

	public void addBuilder(Builder<T> b) {  
		this.builders.put(b.getTypeTag(), b);
		this.buildersInfo.add(b.getInfo());  
	}

	@Override  
	public T createInstance(JSONObject info) {  
		if (info == null) {  
			throw new IllegalArgumentException("’info’ cannot be null");  
		}
		
		String type = info.getString("type");
		Builder<T> builder = this.builders.get(type);
		
		if(builder != null) {
			JSONObject data = info.has("data") ? info.getJSONObject("data") : new JSONObject();
			T instance = builder.createInstance(data); 
			
			if(instance != null) {
				return instance; 
			}
			
		}
		
		throw new IllegalArgumentException("Unrecognized ‘info’:" + info.toString());  
	}

	@Override  
	public List<JSONObject> getInfo() {  
		return Collections.unmodifiableList(buildersInfo);  
	}


}
