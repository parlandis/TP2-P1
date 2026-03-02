package simulator.factories;

import java.util.List;
import org.json.JSONObject;

public interface Factory<T> {  
	public T createInstance(JSONObject info);    //Crea objeto a partit del JSON
	public List<JSONObject> getInfo();      //Devuelve la lista de los objetos que se pueden crear
}
