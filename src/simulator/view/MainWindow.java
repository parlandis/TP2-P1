package simulator.view;

import java.awt.*;
import javax.swing.*;
import simulator.control.Controller;

public class MainWindow extends JFrame {

	// private static final String DO_NOTHING_ON_CLOSE = null;
	private Controller ctrl;

	public MainWindow(Controller ctrl) {
		super("[ECOSYSTEM SIMULATOR]");
		this.ctrl = ctrl;
		initGUI();
	}

	private void initGUI() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		setContentPane(mainPanel);

		ControlPanel cp = new ControlPanel(ctrl);
		mainPanel.add(cp, BorderLayout.PAGE_START);

		StatusBar sb = new StatusBar(ctrl);
		mainPanel.add(sb, BorderLayout.PAGE_END);

		// Definición del panel de tablas (usa un BoxLayout vertical)

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		mainPanel.add(contentPanel, BorderLayout.CENTER);

		// Species table
		InfoTable speciesTable = new InfoTable("Species", new SpeciesTableModel(ctrl));
		speciesTable.setPreferredSize(new Dimension(500, 250));
		contentPanel.add(speciesTable);

		// Regions table
		InfoTable regionsTable = new InfoTable("Regions", new RegionsTableModel(ctrl));
		regionsTable.setPreferredSize(new Dimension(500, 250));
		contentPanel.add(regionsTable);

		// Window listener
		addWindowListener(new java.awt.event.WindowAdapter() {
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
