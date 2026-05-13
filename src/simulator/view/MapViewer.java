package simulator.view;

import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.AnimalInfo;
import simulator.model.MapInfo;
import simulator.model.State;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/*
 * An incomplete version of  the map viewer, to be completed by students.
 */

@SuppressWarnings("serial")
public class MapViewer extends AbstractMapViewer {

	// Anchura/altura de la simulación -- se supone que siempre van a ser
	// iguales al tamaño del componente
	//
	// Width/height of the simulation -- they will always be equal to the size
	// of the component
	//
	private int width;
	private int height;

	// Número de filas/columnas de la simulación (regiones)
	//
	// Number of rows/cols of the simulation (regions)
	//
	private int rows;
	private int cols;

	// Anchura/altura de una región
	//
	// Width/height of a region
	//
	int rWidth;
	int rHeight;

	// Mostramos sólo animales con este estado. Los posibles valores de currState
	// son null, y los valores de Animal.State.values(). Si es null mostramos todo.
	//
	// We show the animals that have this state. The possible values of currSrare
	// are: null and the values returned by Animal.State.values(). If it is null we
	// show all animals.
	//
	State currState;

	// En estos atributos guardamos la lista de animales y el tiempo que hemos
	// recibido la última vez para dibujarlos.
	//
	// The value of these attributes are the list of animals and the time that we
	// have received in the notification (those will be shown).
	//
	volatile private Collection<AnimalInfo> objs;
	volatile private Double time;

	// Una clase auxilar para almacenar información sobre una especie.
	//
	// An auxiliary class to store information about species.
	//
	private static class SpeciesInfo {
		private Integer count;
		private Color color;

		SpeciesInfo(Color color) {
			count = 0;
			this.color = color;
		}
	}

	// Un mapa para la información sobre las especies.
	//
	// A map with the information for each species.
	//
	Map<String, SpeciesInfo> kindsInfo = new HashMap<>();

	// El font que usamos para dibujar texto.
	//
	// The font to be used for drawing text.
	//
	private Font textFont = new Font("Arial", Font.BOLD, 12);

	// Indica si mostramos el texto la ayuda o no.
	//
	// Indicates if the 'help' information is visible or hidden.
	//
	private boolean showHelp;

	public MapViewer() {
		initGUI();
	}

	private void initGUI() {

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyChar()) {
					case 'h':
						showHelp = !showHelp;
						repaint();
						break;
					case 's':
						if (currState == null) {
							currState = State.values()[0];
						} else {
							int next = currState.ordinal() + 1;
							if (next < State.values().length) {
								currState = State.values()[next];
							} else {
								currState = null;
							}
						}
						repaint();
						break;
					default:
				}
			}

		});

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				// Esto es necesario para capturar las teclas cuando el ratón está sobre este
				// componente.
				//
				// This is needed to capture keystroke when the mouse is over this component.
				//
				requestFocus();
			}
		});

		// Por defecto mostramos todos los animales.
		//
		// By default, we show all animals.
		//
		currState = null;

		// Por defecto mostramos el texto de ayuda.
		//
		// By default, the 'help' message is visible.
		//
		showHelp = true;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		// Graphics2D permite un renderizado de mayor calidad (suavizado de bordes)
		Graphics2D gr = (Graphics2D) g;
		gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g.setFont(textFont);

		// 1) Limpiar el fondo: pintamos todo el área de blanco antes de dibujar el nuevo estado
		gr.setBackground(Color.WHITE);
		gr.clearRect(0, 0, width, height);

		// 2) Dibujar los objetos (animales, rejilla, etc.) delegando en drawObjects
		if (objs != null)
			drawObjects(gr, objs, time);

		// 3) Dibujar la información de ayuda si está activa
		if (showHelp) {
			g.setColor(Color.RED);
			g.drawString("h: toggle help", 5, 20);
			g.drawString("s: show animals of a specific state", 5, 35);
		}

	}

	private boolean visible(AnimalInfo a) {
		return currState == null || a.getState() == currState;
	}

	private void drawObjects(Graphics2D g, Collection<AnimalInfo> animals, Double time) {

		g.setColor(Color.LIGHT_GRAY);
		for (int i = 0; i <= rows; i++) {
			g.drawLine(0, i * rHeight, width, i * rHeight);
		}
		for (int j = 0; j <= cols; j++) {
			g.drawLine(j * rWidth, 0, j * rWidth, height);
		}

		// Dibujar los animales.
		//
		// Draw the animals
		//
		for (AnimalInfo a : animals) {

			// Si no es visible saltamos la iteración.
			//
			// If the animal is not visible, we skip to the next iteration.
			//
			if (!visible(a))
				continue;

			// La información sobre la especie de 'a'.
			//
			// Information of the species of 'a'
			//
			SpeciesInfo speciesInfo = kindsInfo.get(a.getGeneticCode());

			if (speciesInfo == null) {
				speciesInfo = new SpeciesInfo(ViewUtils.getColor(a.getGeneticCode()));
				kindsInfo.put(a.getGeneticCode(), speciesInfo);
			}

			speciesInfo.count++;

			g.setColor(speciesInfo.color);
			int size = (int) (a.getAge() / 2 + 2);
			Vector2D pos = a.getPosition();
			int x = (int)pos.getX();
			int y = (int)pos.getY();
			g.fillOval(x - size / 2, y - size / 2, size, size);
		}

		if (currState != null) {
			g.setColor(Color.BLUE);
			drawStringWithRect(g, 5, height - 20, "State: " + currState.toString());
		}

		g.setColor(Color.MAGENTA);
		String timeText = String.format("Time: %.3f", time);
		drawStringWithRect(g, width - 100, 20, timeText);

		int y_info = height - 40;
		for (Entry<String, SpeciesInfo> entry : kindsInfo.entrySet()) {
			g.setColor(entry.getValue().color);
			g.drawString(entry.getKey() + ": " + entry.getValue().count, 5, y_info);
			y_info -= 15;
			entry.getValue().count = 0;
		}
	}

	// Un método que dibujar un texto con un rectángulo.
	//
	// A method for drawing a text with a rectangular border.
	//
	void drawStringWithRect(Graphics2D g, int x, int y, String s) {
		Rectangle2D rect = g.getFontMetrics().getStringBounds(s, g);
		g.drawString(s, x, y);
		g.drawRect(x - 1, y - (int) rect.getHeight(), (int) rect.getWidth() + 1, (int) rect.getHeight() + 5);
	}

	@Override
	public void update(List<AnimalInfo> objs, Double time) {
		this.objs = objs;
		this.time = time;
		repaint();
	}

	@Override
	public void reset(double time, MapInfo map, List<AnimalInfo> animals) {
		this.width = map.getWidth();
		this.height = map.getHeight();
		this.cols = map.getCols();
		this.rows = map.getRows();
		this.rWidth = map.getRegionWidth();
		this.rHeight = map.getRegionHeight();

		// Esto cambia el tamaño del componente, y así cambia el tamaño de la ventana
		// porque en MapWindow llamamos a pack() después de llamar a reset.
		//
		// This line changes the size of the component, and thus the size of the window
		// because MapWindow calls pack() after calling reset().
		//
		setPreferredSize(new Dimension(map.getWidth(), map.getHeight()));

		// Dibuja el estado.
		//
		// Draw the state.
		//
		update(animals, time);
	}

}
