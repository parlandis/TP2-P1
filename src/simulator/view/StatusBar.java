package simulator.view;

import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

class StatusBar extends JPanel implements EcoSysObserver {

	  // TODO Añadir los atributos necesarios.  
	private Controller ctrl;
	private double time;
	private int numAnimals;
	private int numRows;
	private int numCols;
	private int width;
	private int height;
	

	  StatusBar(Controller ctrl) {  
	    initGUI();  
	    // TODO registrar this como observador  
	    this.ctrl = ctrl;
	    
	  }

	  private void initGUI() {  
	    this.setLayout(new FlowLayout(FlowLayout.LEFT));  
	    this.setBorder(BorderFactory.createBevelBorder(1));

	    // TODO Crear varios JLabel para el tiempo, el número de animales, y la
	    //      dimensión y añadirlos al panel. Puedes utilizar el siguiente código
	    //      para añadir un separador vertical:  
	    //
	    //     JSeparator s = new JSeparator(JSeparator.VERTICAL);  
	    //     s.setPreferredSize(new Dimension(10, 20));  
	    //     this.add(s);  
	  }

	  private void setLayout(FlowLayout flowLayout) {
		// TODO Auto-generated method stub
		
	  }

	  @Override
	  public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub
		
	  }

	  @Override
	  public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub
		
	  }

	  @Override
	  public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		// TODO Auto-generated method stub
		
	  }

	  @Override
	  public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		// TODO Auto-generated method stub
		
	  }

	  @Override
	  public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		// TODO Auto-generated method stub
		
	  }

	  // TODO el resto de métodos van aquí…  
	}
