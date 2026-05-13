package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;
import simulator.model.Sheep;

public class SheepBuilder extends Builder<Animal> {
	private Factory<SelectionStrategy> strategyFactory;

	public SheepBuilder(Factory<SelectionStrategy> strategyFactory) {
		super("sheep", "Sheep animal");
		this.strategyFactory = strategyFactory;
	}

	@Override
	protected Sheep createInstance(JSONObject data) {
		Vector2D pos = null;
		// 1) Procesamiento de la posición: si el JSON trae "pos", calculamos una posición aleatoria en el rango
		if (data.has("pos")) {
			JSONObject p = data.getJSONObject("pos");

			JSONArray xRange = p.getJSONArray("x_range");
			JSONArray yRange = p.getJSONArray("y_range");

			double x = Utils.RAND.nextDouble(xRange.getDouble(0), xRange.getDouble(1));
			double y = Utils.RAND.nextDouble(yRange.getDouble(0), yRange.getDouble(1));
			pos = new Vector2D(x, y);
		}

		SelectionStrategy mateStrategy = null;
		SelectionStrategy dangerStrategy = null;

		// 2) Procesamiento de estrategias: usamos OTRA FACTORÍA (strategyFactory) para delegar
		// la creación de las estrategias (SelectFirst, SelectClosest, etc.)
		if (data.has("mate_strategy")) {
			mateStrategy = strategyFactory.createInstance(data.getJSONObject("mate_strategy"));
		} else {
			// Valor por defecto si no se especifica en el JSON
			mateStrategy = new SelectFirst();
		}

		if (data.has("danger_strategy")) {
			dangerStrategy = strategyFactory.createInstance(data.getJSONObject("danger_strategy"));
		} else {
			dangerStrategy = new SelectFirst();
		}

		// 3) Finalmente creamos el objeto Sheep con todos sus componentes
		return new Sheep(mateStrategy, dangerStrategy, pos);
	}

}
