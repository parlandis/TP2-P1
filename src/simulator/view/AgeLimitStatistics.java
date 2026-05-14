package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableModel;

import simulator.control.Controller;
import simulator.launcher.Main;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

class AgeLimitStatistics extends JDialog {
	private AgeTableModel tableModel;
	private JTextField ageLimitField;
	private JLabel infoLabel;
	
	AgeLimitStatistics(Frame parent, AgeTableModel model) {
		super(parent, true);
		this.tableModel = model;
		initGUI();
	}
	
	private void initGUI() {
		setTitle("Age Statistics");
		JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setContentPane(mainPanel);
		// 5. Texto explicativo superior
		infoLabel = new JLabel("Statistics for age limit: " + tableModel.getAgeLimit());
		mainPanel.add(infoLabel, BorderLayout.NORTH);
		// 1. Tabla central
		JTable table = new JTable(tableModel);
		mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);
		// Panel Inferior (Inputs y Botones)
		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);
		// 2. JTextField para el límite
		JPanel inputPanel = new JPanel();
		inputPanel.add(new JLabel("Age Limit: "));
		ageLimitField = new JTextField(String.valueOf(tableModel.getAgeLimit()), 5);
		inputPanel.add(ageLimitField);
		
		// 3. Botón SET a la derecha del JTextField
		JButton setButton = new JButton("SET");
		setButton.addActionListener(e -> {
			try {
				double newLimit = Double.parseDouble(ageLimitField.getText());
				if (newLimit <= 0) throw new NumberFormatException();
				
				tableModel.setAgeLimit(newLimit); // (a) borra tabla, (b) cambia límite
				infoLabel.setText("Statistics for age limit: " + newLimit);
				setVisible(false); // (c) cierra ventana
			} catch (NumberFormatException ex) {
				ViewUtils.showErrorMsg("Age Limit must be a positive double");
			}
		});
		inputPanel.add(setButton);
		bottomPanel.add(inputPanel);
		
		// 4. Botón CLOSE 
		JPanel buttonsPanel = new JPanel();
		JButton closeButton = new JButton("CLOSE");
		closeButton.addActionListener(e -> setVisible(false));
		buttonsPanel.add(closeButton);
		bottomPanel.add(buttonsPanel);
		
		setPreferredSize(new Dimension(500, 400));
		pack();
	}
	
	public void open(Frame parent) {
		setLocation(
				parent.getLocation().x + parent.getWidth() / 2 - getWidth() / 2,
				parent.getLocation().y + parent.getHeight() / 2 - getHeight() / 2);
		pack();
		setVisible(true);
	}
	
}
