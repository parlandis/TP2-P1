package simulator.view;

import java.awt.Frame;
import java.awt.event.WindowListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.*;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

class MapWindow extends JFrame implements EcoSysObserver {

	  private Controller ctrl;  
	  private AbstractMapViewer viewer;  
	  private Frame parent;

	  MapWindow(Frame parent, Controller ctrl) {  
	    super("[MAP VIEWER]");  
	    this.ctrl = ctrl;  
	    this.parent = parent;  
	    intiGUI();  
	    // TODO registrar this como observador  
	  }

	  private void intiGUI() {  
	    JPanel mainPanel = new JPanel(new BorderLayout());  
	    // TODO poner contentPane como mainPanel

	    // TODO crear el viewer y añadirlo a mainPanel (en el centro)

	    // TODO en el método windowClosing, eliminar ‘MapWindow.this’ de los
	    //      observadores
	    addWindowListener(new WindowListener() { });

	    pack();
	    if (this.parent != null)
	      setLocation(
	        this.parent.getLocation().x + parent.getWidth() / 2 - getWidth() / 2,
	        this.parent.getLocation().y + parent.getHeight() / 2 - getHeight() / 2);
	      setResizable(false);  
	      setVisible(true);  
	  }  
	  // TODO otros métodos van aquí….  
	  
	  void addWindowListener() {
		  
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
	}