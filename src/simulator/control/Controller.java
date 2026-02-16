package simulator.control;

import java.io.OutputStream;

import javax.print.attribute.standard.JobSheets;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.model.Simulator;

public class Controller {  //TODO: Tiene que estar aqui las factorias o en sim ???
	private Simulator sim;
	
	public Controller(Simulator sim) {
		this.sim = sim;
	}
	
	
	public void loadData(JSONObject data) {
		if(data.has("regions")) {
			JSONArray regiones = data.getJSONArray("regions");
			for(int i = 0; i < regiones.length(); i++) {
				JSONObject reg = regiones.getJSONObject(i);
				int col = reg.getInt("col");
				int row = reg.getInt("row");

				
				sim.setRegion(col, row, reg);
			}
		}
		
		if(data.has("animals")) {
			
			JSONArray animales = data.getJSONArray("animals");
			for(int i = 0; i < animales.length(); i++) {
				JSONObject ani = animales.getJSONObject(i);
				sim.addAnimal(ani);
			}
		}
	}
	
	public void run(double t, double dt, boolean sv, OutputStream out) {
		JSONObject initState = new JSONObject();
		JSONObject finalState = new JSONObject();
		initState = sim.asJSON();
		
		while(t > sim.getTime()) {
			sim.advance(dt);
		}
		finalState = sim.asJSON();
		
		JSONObject salida = new JSONObject();
		salida.put("in", initState);
		salida.put("out", finalState);
		
		//if(sv){ visor}
	}
}