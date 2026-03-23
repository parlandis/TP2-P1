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

	private JToolBar toolaBar;

	// Botones
	private JButton quitButton; // Boton de salir
	private JButton openButton;
	private JButton regionsButton;
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

		// Inicializar JFileChooser
		this.fc = new JFileChooser();
		this.fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/resources/examples"));

		// Inicializar ChangeRegionsDialog
		this.changeRegionsDialog = new ChangeRegionsDialog(this.ctrl);

		// Botón Open
		this.openButton = new JButton();
		this.openButton.setToolTipText("Open");
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
		this.stopButton.addActionListener((e) -> this.stopped = true);
		this.toolaBar.add(stopButton);

		this.toolaBar.addSeparator();

		// Pasos (Steps)
		this.toolaBar.add(new JLabel(" Steps: "));
		this.steps = new JSpinner(new SpinnerNumberModel(10000, 1, 100000, 100));
		this.steps.setMinimumSize(new Dimension(80, 30));
		this.steps.setPreferredSize(new Dimension(80, 30));
		this.steps.setMaximumSize(new Dimension(80, 30));
		this.toolaBar.add(this.steps);

		// Delta-Time
		this.toolaBar.add(new JLabel(" Delta-Time: "));
		this.dt = new JTextField(Main.deltaTime != null ? Main.deltaTime.toString() : "0.03");
		this.dt.setMinimumSize(new Dimension(80, 30));
		this.dt.setPreferredSize(new Dimension(80, 30));
		this.dt.setMaximumSize(new Dimension(80, 30));
		this.toolaBar.add(this.dt);

		// Quit Button
		this.toolaBar.add(Box.createGlue());
		this.toolaBar.addSeparator();
		this.quitButton = new JButton();
		this.quitButton.setToolTipText("Quit");
		this.quitButton.setIcon(new ImageIcon("resources/icons/exit.png"));
		this.quitButton.addActionListener((e) -> ViewUtils.quit(this));
		this.toolaBar.add(quitButton);
	}

	private void openFile() {
		int ret = this.fc.showOpenDialog(ViewUtils.getWindow(this));
		if (ret == JFileChooser.APPROVE_OPTION) {
			File f = this.fc.getSelectedFile();
			try {
				InputStream in = new FileInputStream(f);
				JSONObject json = new JSONObject(new JSONTokener(in));
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
		this.stopped = false;
		setButtonsEnabled(false);
		try {
			int n = (Integer) this.steps.getValue();
			double delta = Double.parseDouble(this.dt.getText());
			runSim(n, delta);
		} catch (Exception e) {
			ViewUtils.showErrorMsg(e.getMessage());
			this.stopped = true;
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

	private void runSim(int n, double dt) {
		if (n > 0 && !this.stopped) {
			try {
				this.ctrl.advance(dt);
				SwingUtilities.invokeLater(() -> runSim(n - 1, dt));
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
}
