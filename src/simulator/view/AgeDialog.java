package simulator.view;

import java.awt.Dimension;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

public class AgeDialog extends JDialog implements EcoSysObserver {
	
	
	class InfoPaso{
		Map<String, Integer> edades;  // Young o Old y el numero 
		double time;
		
		InfoPaso(Double dt){
			edades = new HashMap<>();
			this.time = dt;
		}
	}
	
	private List<InfoPaso> historial;
	
	private String[] headers = { "Time", "Young", "Old" };
	private DefaultTableModel dataTableModel;
	private Controller ctrl;
	private JTextField limit;
	private double limitDouble;
	
	AgeDialog(Controller ctrl){
		super((Frame) null, true);
		this.ctrl = ctrl;
		this.limitDouble = 4.5;
		historial = new ArrayList<>();
		initGUI();
		this.ctrl.addObserver(this);
	}
	
	

	private void initGUI() {
		setTitle("Age limit Statistics");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		setContentPane(mainPanel);
		
		// 1. Ayuda
		JPanel helpPanel = new JPanel();
		helpPanel.setAlignmentX(CENTER_ALIGNMENT);
		JLabel helpLabel = new JLabel(
				"Age Statistics for age limit: " + Double.toString(limitDouble)) ;
		helpPanel.add(helpLabel);
		mainPanel.add(helpPanel);
		
		//Tabla 
		
		this.dataTableModel = new DefaultTableModel();
		this.dataTableModel.setColumnIdentifiers(this.headers);

		JTable dataTable = new JTable(this.dataTableModel);
		JScrollPane tableScroll = new JScrollPane(dataTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tableScroll.setPreferredSize(new Dimension(650, 250));
		mainPanel.add(tableScroll);
		
		// 3. Selectores
		JPanel selectorsPanel = new JPanel();
		selectorsPanel.setPreferredSize(new Dimension(700, 50));
		mainPanel.add(selectorsPanel);
		selectorsPanel.add(new JLabel("Age limit: "));
		this.limit = new JTextField(Double.toString(limitDouble));
		this.limit.setMinimumSize(new Dimension(80, 30));
		this.limit.setPreferredSize(new Dimension(80, 30));
		this.limit.setMaximumSize(new Dimension(80, 30));
		selectorsPanel.add(this.limit);
		
		//Bottones
		JButton set = new JButton("SET");
		set.addActionListener(( e -> {
			setLimit(limitDouble);
		}));
		
		JButton close = new JButton("CLOSE");
		close.addActionListener((e -> {
					setVisible(false);
		}));
		
		selectorsPanel.add(set);
		selectorsPanel.add(close);
	}
	
	
	private void setLimit(Double limite) {
		try {
			this.limitDouble = Double.parseDouble(limit.getText());
			if(limitDouble <= 0) {
				ViewUtils.showErrorMsg("Valor no valido - Double > 0");
				return;
			}
			
			updateTableModel(limitDouble);
		} catch (Exception e) {
			ViewUtils.showErrorMsg(e.getMessage());
		}
	}
	
	private void updateTableModel(Double limite) {
		
		
	}


	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		// TODO Auto-generated method stub
		
	}

	public void open(Frame parent) {
		setLocation(
				parent.getLocation().x + parent.getWidth() / 2 - getWidth() / 2,
				parent.getLocation().y + parent.getHeight() / 2 - getHeight() / 2);
		pack();
		setVisible(true);
	}

}
