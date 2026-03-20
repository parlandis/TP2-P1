package simulator.view;

import simulator.control.Controller;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.*;

class ControlPanel extends JPanel {

	private Controller ctrl;
	private ChangeRegionsDialog changeRegionsDialog;

	private JToolBar toolaBar; // ES la propia barra

	// Botones
	private JButton quitButton; // Boton de salir
	private JSpinner steps; // Boton de flechas para subir o bajar el numero de pasos
	private JTextField dt; // Para poder poner el valor de dt
	private JFileChooser fc; // Es el explorador de archivos

	private JButton mapButton; // Boton del mapa
	private JButton runButton; // Boton de run
	private JButton stopButton; // Boton de stop

	private boolean stopped = true; // utilizado en los botones de run/stop

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
		// Todos ellos han de tener su correspondiente tooltip. Puedes utilizar
		// this.toolaBar.addSeparator() para añadir la línea de separación vertical
		// entre las componentes que lo necesiten.
		
	
		

		// Quit Button
		this.toolaBar.add(Box.createGlue()); // this aligns the button to the right
		this.toolaBar.addSeparator();
		this.quitButton = new JButton();
		this.quitButton.setToolTipText("Quit");
		this.quitButton.setIcon(new ImageIcon("resources/resources/icons/exit.png"));
		this.quitButton.addActionListener((e) -> ViewUtils.quit(this));
		this.toolaBar.add(quitButton);
		
		//boton map
		this.mapButton = new JButton();
		this.mapButton.setToolTipText("Show Map");
		this.mapButton.setIcon(new ImageIcon("resources/resources/icons/viewer.png"));
		this.mapButton.addActionListener((e) -> new MapWindow()); //TODO: NO se como se hace, aun no tenemos mapWindow
		this.toolaBar.add(mapButton);
	
		//boton run
		this.runButton = new JButton();
		this.runButton.setToolTipText("Run");
		this.runButton.setIcon(new ImageIcon("resources/resources/icons/run.png"));
		this.runButton.addActionListener((e) -> runSim(dt.get, alignmentX)) ; //TODO: NO se como se hace, pero tiene que llamar a un metodo de controller para que empiece a correr la simulacion
		this.toolaBar.add(runButton);
		
		//boton stop
		this.stopButton = new JButton();
		this.stopButton.setToolTipText("Stop");
		this.stopButton.setIcon(new ImageIcon("resources/resources/icons/stop.png"));
		this.stopButton.addActionListener((e) -> this.stopped = true); //TODO: NO se como se hace, pero tiene que llamar a un metodo de controller para que pare la simulacion
		this.toolaBar.add(stopButton);
		
		

		// TODO Inicializar this.fc con una instancia de JFileChooser. Para que siempre
		// abre en la carpeta de ejemplos puedes usar:
		this.fc = new JFileChooser();
		this.fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/resources/resources/examples"));
		// TODO: creo que falta el action listener pero no se como se hace

		// this.fc.setCurrentDirectory(new File(System.getProperty("user.dir") +
		// "/resources/examples"));

		// TODO Inicializar this.changeRegionsDialog con instancias del diálogo de
		// cambio
		// de regiones
		this.changeRegionsDialog = new ChangeRegionsDialog(this);

	}
	// TODO el resto de métodos van aquí…

	private void initToolBar() {

	}
	
	private void runSim(int n, double dt) {  
		  if (n > 0 && !this.stopped) {  
		    try {  
		          this.ctrl.advance(dt);  
		          SwingUtilities.invokeLater(() -> runSim(n - 1, dt));  
		    } catch (Exception e) {  
		    	
		      // TODO llamar a ViewUtils.showErrorMsg con el mensaje de error
		      //      que corresponda  
		      // TODO activar todos los botones
		      this.stopped = true;  
		    }  
		  } else {  
		    // TODO activar todos los botones  
		    this.stopped = true;  
		  }  
		}
}
