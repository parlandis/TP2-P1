package simulator.view;

import java.awt.Dimension;
import java.awt.Frame;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
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
		mainPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setContentPane(mainPanel);

		// 1. Ayuda
		JLabel helpLabel = new JLabel(
				"<html>Select a region type, the rows/cols interval, and provide values for the parameters in the <b>Value column</b> (default values are used for parameters with no value).</html>");
		helpLabel.setAlignmentX(CENTER_ALIGNMENT);
		helpLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
		mainPanel.add(helpLabel);

		// 2. Tabla de parametros
		this.regionsInfo = Main._regionFactory.getInfo();
		this.dataTableModel = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 1;
			}
		};
		this.dataTableModel.setColumnIdentifiers(this.headers);

		JTable dataTable = new JTable(this.dataTableModel);
		JScrollPane tableScroll = new JScrollPane(dataTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		tableScroll.setPreferredSize(new Dimension(650, 250));
		mainPanel.add(tableScroll);

		// 3. Selectores
		JPanel selectorsPanel = new JPanel();
		selectorsPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
		mainPanel.add(selectorsPanel);

		// Region type
		regionsInfo.forEach(info -> this.regionsModel.addElement(info.getString("type")));
		JComboBox<String> regionType = new JComboBox<>(this.regionsModel);
		selectorsPanel.add(new JLabel("Region type: "));
		selectorsPanel.add(regionType);

		// Row from/to
		JComboBox<String> fromRow = new JComboBox<>(this.fromRowModel);
		JComboBox<String> toRow = new JComboBox<>(this.toRowModel);
		selectorsPanel.add(new JLabel("Row from/to: "));
		selectorsPanel.add(fromRow);
		selectorsPanel.add(toRow);

		// Column from/to
		JComboBox<String> fromCol = new JComboBox<>(this.fromColModel);
		JComboBox<String> toCol = new JComboBox<>(this.toColModel);
		selectorsPanel.add(new JLabel("Column from/to: "));
		selectorsPanel.add(fromCol);
		selectorsPanel.add(toCol);

		// 4. Botones
		JPanel buttonPanel = new JPanel();
		mainPanel.add(buttonPanel);

		JButton ok = new JButton("OK");
		ok.addActionListener((e) -> {
			try {
				int fRow = Integer.parseInt((String) fromRow.getSelectedItem());
				int tRow = Integer.parseInt((String) toRow.getSelectedItem());
				int fCol = Integer.parseInt((String) fromCol.getSelectedItem());
				int tCol = Integer.parseInt((String) toCol.getSelectedItem());

				if (fRow > tRow || fCol > tCol || fRow < 0 || fCol < 0) {
					ViewUtils.showErrorMsg(this, "Debe ser Row/Col inicio <= Row/Col fin y no negativos");
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
				ViewUtils.showErrorMsg(this, ex.getMessage());
			}
		});

		JButton cancel = new JButton("Cancel");
		cancel.addActionListener((e) -> {
			status = 0;
			setVisible(false);
		});

		buttonPanel.add(cancel);
		buttonPanel.add(ok);

		regionType.addActionListener((e) -> {
			updateTableModel((String) regionType.getSelectedItem());
		});

		if (regionsModel.getSize() > 0) {
			updateTableModel((String) regionsModel.getElementAt(0));
		}

		setPreferredSize(new Dimension(750, 450));
		pack();
		setResizable(true);
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
		SwingUtilities.invokeLater(() -> updateCoordinateModels(map));
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		SwingUtilities.invokeLater(() -> updateCoordinateModels(map));
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
