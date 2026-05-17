package simulator.view;

import java.awt.Dimension;
import java.awt.Frame;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

public class ExtincionDialog  extends JDialog implements EcoSysObserver  {
	
	private DefaultTableModel dataTableModel;
	private Controller ctrl;
	private String[] headers = { "Time", "In Danger", "Not In Danger" };
	
	private JSpinner steps; // Boton de flechas para subir o bajar el numero de pasos
	private int limit;
	
	public ExtincionDialog(Controller ctrl) {
		super((Frame) null, true);
		this.ctrl = ctrl;
		this.limit = 5;
		initGUI();
		this.ctrl.addObserver(this);

	}

	private void initGUI() {
		
		setTitle("Extinction Regions");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		setContentPane(mainPanel);
		
		// 1. Ayuda
		JPanel helpPanel = new JPanel();
		helpPanel.setAlignmentX(CENTER_ALIGNMENT);
		JLabel helpLabel = new JLabel(
				"<html>Region Extinction Statistics for extincion Danger limit: </html>".concat(Integer.toString(limit)));
		
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
		
		
		
		//Selectores
		// 3. Selectores
		JPanel selectorsPanel = new JPanel();
		selectorsPanel.setPreferredSize(new Dimension(700, 50));
		mainPanel.add(selectorsPanel);
		
		selectorsPanel.add(new JLabel("Extinction danger limit: "));
		this.steps = new JSpinner(new SpinnerNumberModel(5, 1, 10, 1)); // Marcamos valor ini, minimo, maximo y paso (incremento/decremento)
		this.steps.setMinimumSize(new Dimension(80, 30));
		this.steps.setPreferredSize(new Dimension(80, 30));
		this.steps.setMaximumSize(new Dimension(80, 30));
		selectorsPanel.add(steps);
		
		JButton set = new JButton("SET");
		set.addActionListener((e -> {
			try {
				int n = (Integer) this.steps.getValue();
				limit = n;
			} catch (Exception ex) {
				ViewUtils.showErrorMsg(ex.getMessage());
			}
		}));
		
		selectorsPanel.add(set);
		
		JButton close = new JButton("CLOSE");
		close.addActionListener((e -> {
			setVisible(false);
		}));
		selectorsPanel.add(close);
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
		// TODO Auto-generated method stub
		setLocation(
				parent.getLocation().x + parent.getWidth() / 2 - getWidth() / 2,
				parent.getLocation().y + parent.getHeight() / 2 - getHeight() / 2);
		pack();
		setVisible(true);
	}

}
