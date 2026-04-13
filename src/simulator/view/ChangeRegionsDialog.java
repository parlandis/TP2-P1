package simulator.view;

import java.awt.Dimension;
import java.awt.Frame;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

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

	  // TODO en caso de ser necesario, añadir los atributos aquí…  
	  ChangeRegionsDialog(Controller ctrl) {  
	    super((Frame)null, true);  
	    this.ctrl = ctrl;  
	    this.ctrl.addObserver(this);
	    initGUI();  
	  }

	  public ChangeRegionsDialog(ControlPanel controlPanel) { // TODO No se si hau que crear aeste constructor
		  
		
	  }

	  private void initGUI() {  
	    setTitle("Change Regions");  
	    JPanel mainPanel = new JPanel();  
	    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));  
	    setContentPane(mainPanel);
	    
	    JPanel ayuda = new JPanel();
	    ayuda.add(new JLabel("Selecciona un tipo de region, un intervalo de filas/columnas y provee valores para los parametros.\n"));
	    mainPanel.add(ayuda);
	    
	    //JPanel 
	    
	    JPanel tablaPanel = new JPanel();
	    
	    String[] columnas = {"Key", "Value", "Description"};
	    DefaultTableModel model = new DefaultTableModel(columnas, 0);
        JTable tabla = new JTable(model);	
        tablaPanel.add(tabla);
        JScrollPane scrollPane = new JScrollPane(tabla);
        tablaPanel.add(scrollPane); 
		mainPanel.add(tablaPanel);
        
	    
	    
	  
	    
		  
		  

	    // TODO crea varios paneles para organizar los componentes visuales en el  
	    //      dialogo, y añadelos al mainpanel. P.ej., uno para el texto de ayuda,
	    //      uno para la tabla, uno para los combobox, y uno para los botones.  

	    // TODO crear el texto de ayuda que aparece en la parte superior del diálogo y  
	    //      añadirlo al panel correspondiente diálogo (Ver el apartado Figuras)

	    // this.regionsInfo se usará para establecer la información en la tabla  
	    this.regionsInfo = Main._regionFactory.getInfo();

	    // this.dataTableModel es un modelo de tabla que incluye todos los parámetros de  
	    // la region
	    this.dataTableModel = new DefaultTableModel() {  
	      @Override  
	      public boolean isCellEditable(int row, int column) {
	    	  return false;
	        // TODO hacer editable solo la columna 1  
	      }  
	    };  
	    this.dataTableModel.setColumnIdentifiers(this.headers);

	    // TODO crear un JTable que use dataTableModel, y añadirlo al diálogo

	    // this.regionsModel es un modelo de combobox que incluye los tipos de regiones
	    this.regionsModel = new DefaultComboBoxModel<>();

	    // TODO añadir la descripción de todas las regiones a regionsModel. Para eso
	    //      usa la clave “desc” o “type” de los JSONObject en regionsInfo,
	    //      ya que estos nos dan información sobre lo que puede crear la factoría.

	    // TODO crear un combobox que use regionsModel y añadirlo al diálogo.

	    // TODO crear 4 modelos de combobox para this.fromRowModel, this.toRowModel,
	    //      this.fromColModel y this.toColModel.

	    // TODO crear 4 combobox que usen estos modelos y añadirlos al diálogo.

	    // TODO crear los botones OK y Cancel y añadirlos al diálogo.

	    setPreferredSize(new Dimension(700, 400)); // puedes usar otro tamaño  
	    pack();  
	    setResizable(false);  
	    setVisible(false);  
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
}

	  // TODO el resto de métodos van aquí…
