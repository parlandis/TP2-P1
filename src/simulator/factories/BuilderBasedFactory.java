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
		
		// 1) Obtenemos el tipo del objeto que queremos crear (ej: "sheep", "wolf")
		String type = info.getString("type");
		// 2) Buscamos en nuestro mapa de builders cuál sabe manejar ese tipo
		Builder<T> builder = this.builders.get(type);
		
		if(builder != null) {
			// 3) Extraemos la sección "data" del JSON (los parámetros específicos)
			// Si no existe, pasamos un JSON vacío
			JSONObject data = info.has("data") ? info.getJSONObject("data") : new JSONObject();
			
			// PATRÓN FACTORY METHOD: El builder utiliza un método de fábrica para instanciar 
			// el objeto específico. Aquí delegamos la lógica de creación al builder concreto.
			
			// 4) Delegamos la creación real al builder específico
			T instance = builder.createInstance(data); 
			
			if(instance != null) {
				return instance; 
			}
			
		}
		
		// Si no encontramos un builder que coincida con el "type", lanzamos error
		throw new IllegalArgumentException("Unrecognized ‘info’:" + info.toString());  
	}

	@Override  
	public List<JSONObject> getInfo() {   
		return Collections.unmodifiableList(buildersInfo);  
	}


}
