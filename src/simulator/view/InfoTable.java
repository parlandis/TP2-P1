package simulator.view;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

public class InfoTable extends JPanel {

	private String title;
	private TableModel tableModel;

	InfoTable(String title, TableModel tableModel) {
		this.title = title;
		this.tableModel = tableModel;
		initGUI();
	}

	private void initGUI() {
		this.setLayout(new BorderLayout()); //cambiar el layout del panel a BorderLayout()  
		this.setBorder(BorderFactory.createTitledBorder(this.title));  // añadir un borde con título al JPanel, con el texto this.title
		
		JTable tabla = new JTable(this.tableModel); // crear una nueva tabla JTable utilizando el modelo de tabla this.tableModel con desplazamiento vertical
		this.add(new JScrollPane(tabla), BorderLayout.CENTER);
	}
}
