package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.control.Controller;
import simulator.launcher.Main;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

class ChangeRegionsDialog extends JDialog implements EcoSysObserver {

	private DefaultComboBoxModel<String> regionsModel;
	private DefaultComboBoxModel<String> fromRowModel;
	private DefaultComboBoxModel<String> toRowModel;
	private DefaultComboBoxModel<String> fromColModel;
	private DefaultComboBoxModel<String> toColModel;

	private DefaultTableModel dataTableModel;
	private Controller ctrl;
	private List<JSONObject> regionsInfo;
	private String[] headers = { "Key", "Value", "Description" };
	private int status;

	ChangeRegionsDialog(Controller ctrl) {
		super((Frame) null, true);
		this.ctrl = ctrl;
		initGUI();
		this.ctrl.addObserver(this);
	}

	private void initGUI() {
		regionsModel = new DefaultComboBoxModel<>();
		fromRowModel = new DefaultComboBoxModel<>();
		toRowModel = new DefaultComboBoxModel<>();
		fromColModel = new DefaultComboBoxModel<>();
		toColModel = new DefaultComboBoxModel<>();

		setTitle("Change Regions");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		setContentPane(mainPanel);

		// Primer panel de ayuda
		JPanel ayuda = new JPanel();
		ayuda.add(new JLabel("Selecciona un tipo de region, un intervalo de filas/columnas y provee valores para los parametros.\n"));
		mainPanel.add(ayuda);

		// this.regionsInfo se usará para establecer la información en la tabla
		this.regionsInfo = Main._regionFactory.getInfo();

		// this.dataTableModel es un modelo de tabla que incluye todos los parámetros de la region
		this.dataTableModel = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 1;
			}
		};
		this.dataTableModel.setColumnIdentifiers(this.headers);
		JTable dataTable = new JTable(this.dataTableModel);
		JScrollPane tableScroll = new JScrollPane(dataTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tableScroll.setPreferredSize(new Dimension(600, 200));

		mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
		mainPanel.add(tableScroll);

		// Panel de seleccion
		JPanel seleccion = new JPanel();
		mainPanel.add(seleccion);

		regionsInfo.forEach(info -> this.regionsModel.addElement(info.getString("type")));

		JComboBox<String> regionType = new JComboBox<>(this.regionsModel);
		seleccion.add(new JLabel("Region Type: "));
		seleccion.add(regionType);

		// Se inicializan dinámicamente en updateCoordinateModels a través del observador
		
		JComboBox<String> fromRow = new JComboBox<>(this.fromRowModel);
		seleccion.add(new JLabel("From Row: "));
		seleccion.add(fromRow);
		JComboBox<String> toRow = new JComboBox<>(this.toRowModel);
		seleccion.add(new JLabel("To Row: "));
		seleccion.add(toRow);
		JComboBox<String> fromCol = new JComboBox<>(this.fromColModel);
		seleccion.add(new JLabel("From Col: "));
		seleccion.add(fromCol);
		JComboBox<String> toCol = new JComboBox<>(this.toColModel);
		seleccion.add(new JLabel("To Col: "));
		seleccion.add(toCol);

		// Panel de botones
		JPanel botones = new JPanel();
		JButton ok = new JButton("OK");
		ok.addActionListener((e) -> {
			try {
				int fRow = Integer.parseInt((String) fromRow.getSelectedItem());
				int tRow = Integer.parseInt((String) toRow.getSelectedItem());
				int fCol = Integer.parseInt((String) fromCol.getSelectedItem());
				int tCol = Integer.parseInt((String) toCol.getSelectedItem());

				if (fRow > tRow || fCol > tCol) {
					JOptionPane.showMessageDialog(this, "Debe ser Row/Col inicio <= Row/Col fin", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				String type = (String) regionType.getSelectedItem();
				JSONObject data = new JSONObject();
				for (int i = 0; i < dataTableModel.getRowCount(); i++) {
					String key = (String) dataTableModel.getValueAt(i, 0);
					String val = (String) dataTableModel.getValueAt(i, 1);
					if (val != null && !val.isBlank()) {
						try {
							data.put(key, Double.parseDouble(val));
						} catch (NumberFormatException nfe) {
							data.put(key, val);
						}
					}
				}

				JSONObject spec = new JSONObject();
				spec.put("type", type);
				spec.put("data", data);

				JSONObject rs = new JSONObject();
				JSONArray regions = new JSONArray();
				JSONObject update = new JSONObject();
				update.put("row", new JSONArray(new int[] { fRow, tRow }));
				update.put("col", new JSONArray(new int[] { fCol, tCol }));
				update.put("spec", spec);
				regions.put(update);
				rs.put("regions", regions);

				ctrl.setRegions(rs);
				status = 1;
				setVisible(false);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			}
		});
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener((e) -> {
			status = 0;
			setVisible(false);
		});
		botones.add(ok);
		botones.add(cancel);
		mainPanel.add(botones);

		regionType.addActionListener((e) -> {
			updateTableModel((String) regionType.getSelectedItem());
		});

		// Inicializar tabla con el primer tipo
		if (regionsModel.getSize() > 0) {
			updateTableModel((String) regionsModel.getElementAt(0));
		}

		setPreferredSize(new Dimension(700, 400));
		pack();
		setResizable(false);
		setVisible(false);
	}

	private void updateTableModel(String type) {
		dataTableModel.setRowCount(0);
		for (JSONObject info : regionsInfo) {
			if (info.getString("type").equals(type)) {
				JSONObject data = info.getJSONObject("data");
				for (String key : data.keySet()) {
					if (!key.equals("type") && !key.equals("desc")) {
						dataTableModel.addRow(new Object[] { key, "", data.get(key) });
					}
				}
				break;
			}
		}
	}

	private void updateCoordinateModels(MapInfo map) {
		fromRowModel.removeAllElements();
		toRowModel.removeAllElements();
		fromColModel.removeAllElements();
		toColModel.removeAllElements();

		for (int i = 0; i < map.getRows(); i++) {
			String v = String.valueOf(i);
			fromRowModel.addElement(v);
			toRowModel.addElement(v);
		}
		for (int i = 0; i < map.getCols(); i++) {
			String v = String.valueOf(i);
			fromColModel.addElement(v);
			toColModel.addElement(v);
		}
	}

	public void open(Frame parent) {
		setLocation(
				parent.getLocation().x + parent.getWidth() / 2 - getWidth() / 2,
				parent.getLocation().y + parent.getHeight() / 2 - getHeight() / 2);
		pack();
		setVisible(true);
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		updateCoordinateModels(map);
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		updateCoordinateModels(map);
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
	}

	@Override
	public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
	}
}
