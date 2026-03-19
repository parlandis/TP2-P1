package simulator.view;

import simulator.control.Controller;

import java.awt.BorderLayout;

import javax.swing.*;

class ControlPanel extends JPanel {

	  private Controller ctrl;  
	  private ChangeRegionsDialog changeRegionsDialog;

	  private JToolBar toolaBar;  
	  private JFileChooser fc;  
	  private boolean stopped = true; // utilizado en los botones de run/stop  
	  private JButton quitButton;

	  // TODO añade más atributos aquí …

	  ControlPanel(Controller ctrl) {  
	    this.ctrl = ctrl;  
	    initGUI();  
	  }

	  private void initGUI() {  
	    setLayout(new BorderLayout());  
	    toolaBar = new JToolBar();  
	    add(toolaBar, BorderLayout.PAGE_START);

	    // TODO crear los diferentes botones/atributos y añadirlos a la toolBar.
	    //      Todos ellos han de tener su correspondiente tooltip. Puedes utilizar  
	    //      this.toolaBar.addSeparator() para añadir la línea de separación vertical  
	    //      entre las componentes que lo necesiten.  

	    // Quit Button  
	    this.toolaBar.add(Box.createGlue()); // this aligns the button to the right  
	    this.toolaBar.addSeparator();  
	    this.quitButton = new JButton();  
	    this.quitButton.setToolTipText("Quit");  
	    this.quitButton.setIcon(new ImageIcon("resources/icons/exit.png"));  
	    this.quitButton.addActionListener((e) -> ViewUtils.quit(this));  
	    this.toolaBar.add(quitButton);

	    // TODO Inicializar this.fc con una instancia de JFileChooser. Para que siempre  
	    // abre en la carpeta de ejemplos puedes usar:  
	    //  
	    //   this.fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/resources/examples"));
	   
	    // TODO Inicializar this.changeRegionsDialog con instancias del diálogo de cambio   
	    // de regiones   

	  }   
	  // TODO el resto de métodos van aquí…  
	}
