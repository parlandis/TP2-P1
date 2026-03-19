package simulator.view;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import simulator.control.Controller;

public class MainWindow extends JFrame {

	  private static final String DO_NOTHING_ON_CLOSE = null;
	  private Controller ctrl;

	  public MainWindow(Controller ctrl) {  
	    super("[ECOSYSTEM SIMULATOR]");  
	    this.ctrl = ctrl;  
	    initGUI();  
	  }

	  private void initGUI() {  
	    JPanel mainPanel = new JPanel(new BorderLayout());  
	    setContentPane(mainPanel);

	    // TODO crear ControlPanel y añadirlo en PAGE_START de mainPanel

	    // TODO crear StatusBar y añadirlo en PAGE_END de mainPanel  

	    // Definición del panel de tablas (usa un BoxLayout vertical)  
	    JPanel contentPanel = new JPanel();  
	    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));  
	    mainPanel.add(contentPanel, BorderLayout.CENTER);

	    // TODO crear la tabla de especies y añadirla a contentPanel.  
	    //      Usa setPreferredSize(new Dimension(500, 250)) para fijar su tamaño

	    // TODO crear la tabla de regiones.  
	    //      Usa setPreferredSize(new Dimension(500, 250)) para fijar su tamaño

	    // TODO llama a ViewUtils.quit(MainWindow.this) en el método windowClosing
	    addWindowListener(...);

	    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);  
	    pack();  
	    setVisible(true);  
	   }


	  private void setVisible(boolean b) {
		// TODO Auto-generated method stub
		
	  }

	  private void pack() {
		// TODO Auto-generated method stub
		
	}

	  private void setDefaultCloseOperation(String doNothingOnClose) {
		// TODO Auto-generated method stub
		
	}

	  private void addWindowListener() {
		// TODO Auto-generated method stub
		
	}

	  private void setContentPane(JPanel mainPanel) {
		// TODO Auto-generated method stub
		
	  }  
	}
