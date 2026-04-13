package simulator.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;


import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

class StatusBar extends JPanel implements EcoSysObserver {
	private Controller ctrl;
	private double time;
	private int numAnimals;
	private int numRows;
	private int numCols;
	private int width;
	private int height;
	private JLabel timeLabel;
	private JLabel animalsLabel;
	private JLabel dimensionLabel;


	StatusBar(Controller ctrl) {
		initGUI();
		this.ctrl = ctrl;
		this.ctrl.addObserver(this);

	}

	private void initGUI() {
		this.setLayout(new FlowLayout(FlowLayout.LEFT));
		this.setBorder(BorderFactory.createBevelBorder(1));

		this.time = 0.0;
		this.numAnimals = 0;
		this.numRows = 0;
		this.numCols = 0;
		this.width = 0;
		this.height = 0;
		
		JSeparator s = new JSeparator(JSeparator.VERTICAL);
		s.setPreferredSize(new Dimension(10, 20));
		JSeparator j = new JSeparator(JSeparator.VERTICAL);
		j.setPreferredSize(new Dimension(10, 20));

		
		timeLabel = new JLabel("Time: 0.00");
		this.add(timeLabel);
		this.add(j);
		animalsLabel = new JLabel("Total Animals: 0");
		this.add(animalsLabel);
		this.add(s);
		dimensionLabel = new JLabel("Dimension: 0x0 (0x0)");
		this.add(dimensionLabel);
	}


	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) { //Actualiza el observador con la informacion inicial del mapa y los animales
		timeLabel.setText("Time:" + String.format("%.3f", time));
		animalsLabel.setText("Total Animals:" + animals.size());
		dimensionLabel.setText("Dimensions:" + map.getWidth() + "x" + map.getHeight() + " (" + map.getCols() + "x" + map.getRows() + ")");

	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) { // Actualiza el observador con la informacion del mapa y los animales después de un reset
		timeLabel.setText("Time:" + String.format("%.3f", time));
		animalsLabel.setText("Total Animals: " + animals.size());
		dimensionLabel.setText("Dimensions: " + map.getWidth() + "x" + map.getHeight() + " (" + map.getCols() + "x" + map.getRows() + ")");

	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) { // Actualiza al añadio animal
		animalsLabel.setText("Total Animals:" + animals.size());
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) { 
		//TODO: aqui creo que nada 
	}

	@Override
	public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		timeLabel.setText("Time:" + String.format("%.3f", time));
		animalsLabel.setText("Total Animals:" + animals.size());
		

	}

}
