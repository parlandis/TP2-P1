package simulator.factories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import simulator.model.SelectionStrategy;

public class BuilderBasedFactory<T> implements Factory<T> {  
	private Map<String, Builder<T>> builders;  
	private List<JSONObject> buildersInfo;
	
	// initialize the strategies factory  
	
	public BuilderBasedFactory() {  
      // Create a HashMap for builders, and a LinkedList buildersInfo  
      // …  
		this.builders = new HashMap<>();
		this.buildersInfo = new LinkedList<>();
	}

	public BuilderBasedFactory(List<Builder<T>> builders) {  
		this();
		for(Builder<T> b: builders) {
			addBuilder(b);
		}
       // call addBuilder(b) for each builder b in builder  
       // …  
	}

	public void addBuilder(Builder<T> b) {  
		this.builders.put(b.getTypeTag(), b);
		this.buildersInfo.add(b.getInfo());
		
		
      // add an entry "b.getTypeTag() |−> b" to builders.   
      // ...  
      // add b.getInfo() to buildersInfo  
      // ...  
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

		// Look for a builder with a tag equals to info.getString("type"), in the
    //  map _builder, and call its createInstance method and return the result
    // if it is not null. The value you pass to createInstance is the following
    // because 'data' is optional:
    //
    //   info.has("data") ? info.getJSONObject("data") : new JSONObject()
    // …

    // If no builder is found or the result is null ...  

	}

	@Override  
	public List<JSONObject> getInfo() {  
		return Collections.unmodifiableList(buildersInfo);  
	}


}
