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

		// TODO crear ControlPanel y añadirlo en PAGE_START de mainPanel

		ControlPanel cp = new ControlPanel(ctrl);
		mainPanel.add(cp, BorderLayout.PAGE_START);

		// TODO crear StatusBar y añadirlo en PAGE_END de mainPanel

		StatusBar sb = new StatusBar(ctrl);
		mainPanel.add(sb, BorderLayout.PAGE_END);

		// Definición del panel de tablas (usa un BoxLayout vertical)

		JPanel contentPanel = new JPanel();
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		mainPanel.add(contentPanel, BorderLayout.CENTER);

		// Usa setPreferredSize(new Dimension(500, 250)) para fijar su tamaño

		SpeciesTableModel st = new SpeciesTableModel(ctrl);
		st.setPreferredSize(new Dimension(500, 250));
		contentPanel.add(st);
		
		// Usa setPreferredSize(new Dimension(500, 250)) para fijar su tamaño

		RegionsTableModel rt = new RegionsTableModel(ctrl);
		contentPanel.add(rt);

		// TODO llama a ViewUtils.quit(MainWindow.this) en el método windowClosing
		// addWindowListener(...);
		
		ViewUtils.quit(this);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		pack();
		setVisible(true);
	}

	public void setVisible(boolean b) {
		// TODO Auto-generated method stub
		super.setVisible(b);

	}

	public void pack() {
		super.pack();

	}

	public void setDefaultCloseOperation(int operation) {
		super.setDefaultCloseOperation(operation);
	}

}
