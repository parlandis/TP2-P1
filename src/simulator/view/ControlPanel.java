package simulator.view;

import simulator.control.Controller;
import simulator.launcher.Main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.swing.*;

import org.json.JSONObject;
import org.json.JSONTokener;

class ControlPanel extends JPanel {

	private Controller ctrl;
	private ChangeRegionsDialog changeRegionsDialog;
	private preg3Dialog dialogoP3;

	private JToolBar toolaBar;

	// Botones
	private JButton quitButton; // Boton de salir
	private JButton openButton;
	private JButton regionsButton;
	private JSpinner steps; // Boton de flechas para subir o bajar el numero de pasos
	private JTextField dt; // Para poder poner el valor de dt
	private JFileChooser fc; // Es el explorador de archivos
	
	//para pregunta 3
	private JButton preg3;
	
	
	private JSpinner nuevo; // JSPinner para practica 3

	private JButton mapButton; // Boton del mapa
	private JButton runButton; // Boton de run
	private JButton stopButton; // Boton de stop

	//private boolean stopped = true; // utilizado en los botones de run/stop
	private volatile Thread hilo; // El hilo que usaremos 

	ControlPanel(Controller ctrl) {
		this.ctrl = ctrl;
		initGUI();
	}

	private void initGUI() {
		setLayout(new BorderLayout());
		toolaBar = new JToolBar();
		add(toolaBar, BorderLayout.PAGE_START);

		// inicializamos el JFileChooser
		this.fc = new JFileChooser();
		this.fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/resources/examples"));

		// inicializamos ChangeRegionsDialog
		this.changeRegionsDialog = new ChangeRegionsDialog(this.ctrl);
		this.dialogoP3 = new preg3Dialog(this.ctrl);

		// Botón Open
		this.openButton = new JButton();
		this.openButton.setToolTipText("Cargar un fichero de configuración en la simulación");
		this.openButton.setIcon(new ImageIcon("resources/icons/open.png"));
		this.openButton.addActionListener((e) -> openFile());
		this.toolaBar.add(openButton);

		this.toolaBar.addSeparator();

		// Botón Map
		this.mapButton = new JButton();
		this.mapButton.setToolTipText("Show Map");
		this.mapButton.setIcon(new ImageIcon("resources/icons/viewer.png"));
		this.mapButton.addActionListener((e) -> new MapWindow(ViewUtils.getWindow(this), this.ctrl));
		this.toolaBar.add(mapButton);

		// Botón Regions
		this.regionsButton = new JButton();
		this.regionsButton.setToolTipText("Change Regions");
		this.regionsButton.setIcon(new ImageIcon("resources/icons/regions.png"));
		this.regionsButton.addActionListener((e) -> this.changeRegionsDialog.open(ViewUtils.getWindow(this)));
		this.toolaBar.add(regionsButton);

		this.toolaBar.addSeparator();
		
		//botonm preg3
		this.preg3 = new JButton();
		this.preg3.setToolTipText("Preg3");
		this.preg3.setIcon(new ImageIcon("resources/icons/regions.png"));
		this.preg3.addActionListener((e) -> this.dialogoP3.open(ViewUtils.getWindow(this)));  
		this.toolaBar.add(preg3);
		
		// Botón Run
		this.runButton = new JButton();
		this.runButton.setToolTipText("Run");
		this.runButton.setIcon(new ImageIcon("resources/icons/run.png"));
		this.runButton.addActionListener((e) -> startSim());
		this.toolaBar.add(runButton);

		// Botón Stop
		this.stopButton = new JButton();
		this.stopButton.setToolTipText("Stop");
		this.stopButton.setIcon(new ImageIcon("resources/icons/stop.png"));
		this.stopButton.addActionListener((e) -> {
			if(this.hilo != null) {
				this.hilo.interrupt();
			}
		});
		this.toolaBar.add(stopButton);

		this.toolaBar.addSeparator();
		
		//Boton de delay
		this.toolaBar.add(new JLabel("Delay: "));
		this.nuevo = new JSpinner(new SpinnerNumberModel(1, 0, 1000, 1));
		this.nuevo.setToolTipText("Delay 0-1000, 1 ");  // Definimos min: 0, max 1000 , tamaño de paso 1
		this.nuevo.setMinimumSize(new Dimension(80, 30));
		this.nuevo.setPreferredSize(new Dimension(80, 30));
		this.nuevo.setMaximumSize(new Dimension(80, 30));
		this.toolaBar.add(nuevo);

		// Pasos (Steps)
		this.toolaBar.add(new JLabel(" Steps: "));
		this.steps = new JSpinner(new SpinnerNumberModel(10000, 1, 10000, 100)); // Marcamos valor ini, minimo, maximo y paso (incremento/decremento)
		this.steps.setToolTipText("Simulation steps to run: 1 - 10000");
		this.steps.setMinimumSize(new Dimension(80, 30));
		this.steps.setPreferredSize(new Dimension(80, 30));
		this.steps.setMaximumSize(new Dimension(80, 30));
		this.toolaBar.add(this.steps);

		// Delta-Time
		this.toolaBar.add(new JLabel(" Delta-Time: "));
		this.dt = new JTextField(Main.deltaTime != null ? Main.deltaTime.toString() : "0.03"); // Si Main.deltaTime es// null, se pone 0.03 																							// por defecto
		this.dt.setToolTipText("Real time (in seconds) corresponding to a step");
		this.dt.setMinimumSize(new Dimension(80, 30));
		this.dt.setPreferredSize(new Dimension(80, 30));
		this.dt.setMaximumSize(new Dimension(80, 30));
		this.toolaBar.add(this.dt);

		// Quit Button
		this.toolaBar.add(Box.createGlue()); // Para que el botón de salir se quede a la derecha
		this.toolaBar.addSeparator();
		this.quitButton = new JButton();
		this.quitButton.setToolTipText("Quit");
		this.quitButton.setIcon(new ImageIcon("resources/icons/exit.png"));
		this.quitButton.addActionListener((e) -> ViewUtils.quit(this)); // Cierra la ventana actual (y por tanto la aplicación, al ser la ventana principal)
		this.toolaBar.add(quitButton);
	}

	private void openFile() {
		int ret = this.fc.showOpenDialog(ViewUtils.getWindow(this));
		if (ret == JFileChooser.APPROVE_OPTION) {
			File f = this.fc.getSelectedFile();
			try {
				InputStream in = new FileInputStream(f);
				JSONObject json = new JSONObject(new JSONTokener(in)); // Cargamos el JSON del fichero
				in.close();
				int width = json.optInt("width", 800);
				int height = json.optInt("height", 600);
				int rows = json.optInt("rows", 20);
				int cols = json.optInt("cols", 15);
				this.ctrl.reset(cols, rows, width, height);
				this.ctrl.loadData(json);
			} catch (Exception e) {
				ViewUtils.showErrorMsg(e.getMessage());
			}
		}
	}

	private void startSim() {

		setButtonsEnabled(false); // Desactivamos los botones mientras se ejecuta la simulación (excepto el de stop)
		try {
			int n = (Integer) this.steps.getValue();
			double delta = Double.parseDouble(this.dt.getText());
			int delay = (Integer) this.nuevo.getValue();
			this.hilo = new Thread(() -> {
				runSim(n, delta, delay);
	            SwingUtilities.invokeLater(() -> setButtonsEnabled(true));
	            this.hilo = null;
			});
			
			this.hilo.start(); 
			

		} catch (Exception e) {
			ViewUtils.showErrorMsg(e.getMessage());
			setButtonsEnabled(true);
		}
	}

	private void setButtonsEnabled(boolean enabled) {
		this.openButton.setEnabled(enabled);
		this.mapButton.setEnabled(enabled);
		this.regionsButton.setEnabled(enabled);
		this.runButton.setEnabled(enabled);
		this.steps.setEnabled(enabled);
		this.dt.setEnabled(enabled);
		// this.quitButton.setEnabled(enabled); // Se suele dejar el de salir siempre
		// activado (o el usuario puede elegir salir cuando sea)
	}
	
	private void runSim(int n, double dt, int delay) {
		while (n > 0 && !Thread.currentThread().isInterrupted()){
			try {
				ctrl.advance(dt);
			} catch(Exception e) {
				// TODO: handle exception
			}
			
			try {
				Thread.sleep(delay);
			} catch (Exception e) {
				// TODO: handle exception
		        Thread.currentThread().interrupt();
		        return;
			}
			
			n--;
		}
	}

	/*
	private void runSim(int n, double dt) {
		if (n > 0 && !this.stopped) {
			try {
				this.ctrl.advance(dt);
				SwingUtilities.invokeLater(() -> runSim(n - 1, dt)); // Llamamos a runSim de nuevo con n-1 pasos, pero lo hacemos en el hilo de la interfaz gráfica para evitar bloquearla
			} catch (Exception e) {
				ViewUtils.showErrorMsg(e.getMessage());
				this.stopped = true;
				setButtonsEnabled(true);
			}
		} else {
			this.stopped = true;
			setButtonsEnabled(true);
		}
	}
	*/
}
