package simulator.view;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

public class preg3Dialog extends JDialog {
	
	private DefaultTableModel dataTableModel;
	private Controller ctrl;
	private List<String> listaEspecies;
	private Map<String,Integer> datos;
	
	private DefaultComboBoxModel<String>  especiesSel;
	
	
	
	public preg3Dialog(Controller ctrl) {
		super((Frame) null, true);
		this.listaEspecies = new ArrayList<>();
		this.datos = new TreeMap<>();
		this.ctrl = ctrl;
		initGUI();
	}
	
	

	private void initGUI() {
		// TODO Auto-generated method stub
		especiesSel = new DefaultComboBoxModel<String>();
		
		setTitle("Show nuember of especies");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setContentPane(mainPanel);
		
		//Tabla rows = paso de simulacion 
		//Tbala cols = tiempo , dieta
		
		
		
		
		
		// 3. Selectores
		JPanel selectorsPanel = new JPanel();
		selectorsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
		mainPanel.add(selectorsPanel);
		
		// 4. Botones
		JPanel buttonPanel = new JPanel();
		mainPanel.add(buttonPanel);
		
		JButton clear = new JButton("Clear");
		
		
		JButton close = new JButton("close");
		close.addActionListener((e) -> {

			setVisible(false);
		});
		
		
		buttonPanel.add(clear);
		buttonPanel.add(close);
		
	}


	public void open(Frame parent) {
		setLocation(
				parent.getLocation().x + parent.getWidth() / 2 - getWidth() / 2,
				parent.getLocation().y + parent.getHeight() / 2 - getHeight() / 2);
		pack();
		setVisible(true);
	}

}
