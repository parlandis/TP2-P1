package simulator.view;

import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import simulator.control.Controller;

public class ExtinctDialog extends JDialog {

	private ExtinctTableModel extinctTableModel;
	private Controller ctrl;
	private JSpinner limitText;

	int limit;

	public ExtinctDialog(Controller ctrl) {
		super((Frame) null, true);
		this.limit = 5;
		this.ctrl = ctrl;
		initGUI();

	}

	private void initGUI() {
		setTitle("Change Regions");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		setContentPane(mainPanel);

		// 1. Ayuda
		JPanel helpPanel = new JPanel();
		helpPanel.setAlignmentX(CENTER_ALIGNMENT);
		JLabel helpLabel = new JLabel(
				"Region extinction for extinction danger limit: " + this.limit);
		helpPanel.add(helpLabel);
		mainPanel.add(helpPanel);

		// Tabla
		InfoTable extinctionTable = new InfoTable("Extinction", new ExtinctTableModel(ctrl, limit));
		extinctionTable.setPreferredSize(new Dimension(500, 250));
		mainPanel.add(extinctionTable);

		//
		// 3. Selectores
		JPanel selectorsPanel = new JPanel();
		selectorsPanel.setPreferredSize(new Dimension(700, 50));
		mainPanel.add(selectorsPanel);

		selectorsPanel.add(new JLabel("Extinction danger limit"));
		this.limitText = new JSpinner(new SpinnerNumberModel(5, 1, 1000, 1)); // Marcamos valor ini, minimo, maximo y
																				// paso (incremento/decremento)
		this.limitText.setMinimumSize(new Dimension(80, 30));
		this.limitText.setPreferredSize(new Dimension(80, 30));
		this.limitText.setMaximumSize(new Dimension(80, 30));
		selectorsPanel.add(this.limitText);

		JButton set = new JButton("SET");
		set.addActionListener((e -> {
			try {
				this.limit = (Integer) this.limitText.getValue();
				// Update table
				extinctionTable.fireTableDataChanged();
				setVisible(false);

			} catch (Exception ex) {
				ViewUtils.showErrorMsg(ex.getMessage());
			}

		}));

		JButton close = new JButton("CLOSE");
		close.addActionListener((e) -> {
			setVisible(false);
		});

		selectorsPanel.add(set);
		selectorsPanel.add(close);

	}

	public void open(Frame parent) {
		setLocation(
				parent.getLocation().x + parent.getWidth() / 2 - getWidth() / 2,
				parent.getLocation().y + parent.getHeight() / 2 - getHeight() / 2);
		pack();
		setVisible(true);
	}
}
