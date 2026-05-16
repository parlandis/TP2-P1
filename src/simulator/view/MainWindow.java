package simulator.view;

import java.awt.*;
import javax.swing.*;
import simulator.control.Controller;

public class MainWindow extends JFrame {

	private Controller ctrl;

	public MainWindow(Controller ctrl) {
		super("[ECOSYSTEM SIMULATOR]");
		this.ctrl = ctrl;
		initGUI();
	}

	private void initGUI() {
		//Panel principal 
		JPanel mainPanel = new JPanel(new BorderLayout());
		setContentPane(mainPanel);

		//Barra de estado y control
		ControlPanel cp = new ControlPanel(ctrl);
		mainPanel.add(cp, BorderLayout.PAGE_START);
		StatusBar sb = new StatusBar(ctrl);
		mainPanel.add(sb, BorderLayout.PAGE_END);

		// Definición del panel de tablas
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		mainPanel.add(contentPanel, BorderLayout.CENTER);

		// La tabla de especies
		InfoTable speciesTable = new InfoTable("Species", new SpeciesTableModel(ctrl));
		speciesTable.setPreferredSize(new Dimension(500, 250));
		contentPanel.add(speciesTable);

		// tabal regiones
		InfoTable regionsTable = new InfoTable("Regions", new RegionsTableModel(ctrl));
		regionsTable.setPreferredSize(new Dimension(500, 250));
		contentPanel.add(regionsTable);
		
		InfoTable pasosTable = new InfoTable("Region/State info", new PasosTableModel(ctrl));
		pasosTable.setPreferredSize(new Dimension(500, 250));
		contentPanel.add(pasosTable);

		// Window listener
		addWindowListener(new java.awt.event.WindowAdapter() { // para controlar el cierre de la ventana
			@Override
			public void windowClosing(java.awt.event.WindowEvent e) {
				ViewUtils.quit(MainWindow.this);
			}
		});

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		pack();
		setVisible(true);
	}

	public void setVisible(boolean b) {
		super.setVisible(b);
	}

	public void pack() {
		super.pack();
	}

	public void setDefaultCloseOperation(int operation) {
		super.setDefaultCloseOperation(operation);
	}

}
