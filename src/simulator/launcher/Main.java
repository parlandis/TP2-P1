package simulator.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.control.Controller;
import simulator.factories.Builder;
import simulator.factories.BuilderBasedFactory;
import simulator.factories.DefaultRegionBuilder;
import simulator.factories.DynamicSupplyRegionBuilder;
import simulator.factories.Factory;
import simulator.factories.SelectClosestBuilder;
import simulator.factories.SelectFirstBuilder;
import simulator.factories.SheepBuilder;
import simulator.factories.SelectYoungestBuilder;
import simulator.factories.WolfBuilder;
import simulator.misc.Utils;
import simulator.model.Animal;
import simulator.model.Region;
import simulator.model.SelectionStrategy;
import simulator.model.Simulator;

public class Main {

	private enum ExecMode {
		BATCH("batch", "Batch mode"), GUI("gui", "Graphical User Interface mode");

		private String tag;
		private String desc;

		private ExecMode(String modeTag, String modeDesc) {
			tag = modeTag;
			desc = modeDesc;
		}

		public String getTag() {
			return tag;
		}

		public String getDesc() {
			return desc;
		}
	}

	// default values for some parameters
	//
	private final static Double DEFAULT_TIME = 10.0; // in seconds
	private final static Double DEFAULT_DELTA_TIME = 0.03; // in seconds

	// some attributes to stores values corresponding to command-line parameters
	//
	private static Double time = null;
	public static Double deltaTime = null;
	private static String inFile = null;
	private static String outFile = null;
	private static boolean simpleViewer = false;
	private static ExecMode mode = ExecMode.GUI;
	private static Long seed = null;
	public static Factory<Animal> _animalFactory;
	public static Factory<Region> _regionFactory;
	public static Factory<SelectionStrategy> _strategyFactory;

	private static void parseArgs(String[] args) {

		// define the valid command line options
		//
		Options cmdLineOptions = buildOptions();

		// parse the command line as provided in args
		//
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse(cmdLineOptions, args);
			parseHelpOption(line, cmdLineOptions);
			parseModeOption(line);
			parseInFileOption(line);
			parseOutFileOption(line);
			parseTimeOption(line);
			parseDeltaTimeOption(line);
			parseSimpleViewerOption(line);
			parseSeedOption(line);

			// if there are some remaining arguments, then something wrong is
			// provided in the command line!
			//
			String[] remaining = line.getArgs();
			if (remaining.length > 0) {
				String error = "Illegal arguments:";
				for (String o : remaining)
					error += (" " + o);
				throw new ParseException(error);
			}

		} catch (ParseException e) {
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}

	}

	private static Options buildOptions() {
		Options cmdLineOptions = new Options();

		// help
		cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message.").build());

		// input file
		cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("A configuration file.").build());

		// steps
		cmdLineOptions.addOption(Option.builder("t").longOpt("time").hasArg()
				.desc("An real number representing the total simulation time in seconds. Default value: "
						+ DEFAULT_TIME + ".")
				.build());

		// delta-time
		cmdLineOptions.addOption(Option.builder("dt").longOpt("delta-time").hasArg()
				.desc("A double representing actual time, in seconds, per simulation step. Default value: "
						+ DEFAULT_DELTA_TIME + ".")
				.build());

		// output file
		cmdLineOptions.addOption(
				Option.builder("o").longOpt("output").hasArg().desc("Output file, where output is written.").build());

		// simple-viewer
		cmdLineOptions.addOption(
				Option.builder("sv").longOpt("simple-viewer").desc("Show the viewer window in console mode.").build());

		// mode
		cmdLineOptions.addOption(
				Option.builder("m").longOpt("mode").hasArg().desc("Execution mode. Values: 'batch' (default), 'gui'.")
						.build());

		// seed
		cmdLineOptions.addOption(
				Option.builder("sr").longOpt("seed").hasArg().desc("A seed for the random number generator.").build());

		return cmdLineOptions;
	}

	// Parseamos todas las opciones

	private static void parseHelpOption(CommandLine line, Options cmdLineOptions) {
		if (line.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
			System.exit(0);
		}
	}

	private static void parseInFileOption(CommandLine line) throws ParseException {
		inFile = line.getOptionValue("i");
		if (mode == ExecMode.BATCH && inFile == null) {
			throw new ParseException("In batch mode an input configuration file is required");
		}
	}

	private static void parseTimeOption(CommandLine line) throws ParseException {
		String t = line.getOptionValue("t", DEFAULT_TIME.toString());
		try {
			time = Double.parseDouble(t);
			assert (time >= 0);
		} catch (Exception e) {
			throw new ParseException("Invalid value for time: " + t);
		}
	}

	private static void parseDeltaTimeOption(CommandLine line) throws ParseException {
		String dt = line.getOptionValue("dt", DEFAULT_DELTA_TIME.toString());
		try {
			deltaTime = Double.parseDouble(dt);
			assert (deltaTime > 0);
		} catch (Exception e) {
			throw new ParseException("Invalid value for delta-time: " + dt);
		}
	}

	private static void parseOutFileOption(CommandLine line) {
		outFile = line.getOptionValue("o");
	}

	private static void parseSimpleViewerOption(CommandLine line) {
		simpleViewer = line.hasOption("sv");
	}

	private static void parseModeOption(CommandLine line) throws ParseException {
		String m = line.getOptionValue("m", "gui");
		for (ExecMode em : ExecMode.values()) {
			if (em.getTag().equalsIgnoreCase(m)) {
				mode = em;
				return;
			}
		}
		throw new ParseException("Invalid value for mode: " + m);
	}

	private static void parseSeedOption(CommandLine line) throws ParseException {
		if (line.hasOption("sr")) {
			try {
				seed = Long.parseLong(line.getOptionValue("sr"));
			} catch (Exception e) {
				throw new ParseException("Invalid value for seed: " + line.getOptionValue("sr"));
			}
		}
	}

	// Inicialización de las factorías: se crean y se registran todos los Builders disponibles
	private static void initFactories() {
		// 1) Factoría de Estrategias de Selección
		List<Builder<SelectionStrategy>> selectionStrategyBuilders = new ArrayList<>();
		selectionStrategyBuilders.add(new SelectFirstBuilder());
		selectionStrategyBuilders.add(new SelectClosestBuilder());
		selectionStrategyBuilders.add(new SelectYoungestBuilder());
		_strategyFactory = new BuilderBasedFactory<SelectionStrategy>(selectionStrategyBuilders);

		// 2) Factoría de Animales: inyectamos la factoría de estrategias en los builders de animales
		List<Builder<Animal>> animalsBuilders = new ArrayList<>();
		animalsBuilders.add(new SheepBuilder(_strategyFactory));
		animalsBuilders.add(new WolfBuilder(_strategyFactory));
		_animalFactory = new BuilderBasedFactory<Animal>(animalsBuilders);

		// 3) Factoría de Regiones
		List<Builder<Region>> regionsBuilder = new ArrayList<>();
		regionsBuilder.add(new DefaultRegionBuilder());
		regionsBuilder.add(new DynamicSupplyRegionBuilder());
		_regionFactory = new BuilderBasedFactory<Region>(regionsBuilder);

	}

	// Cargamos es Json
	private static JSONObject loadJSONFile(InputStream in) {
		return new JSONObject(new JSONTokener(in));
	}

	// Inicia la simulación en modo consola (sin ventana)
	private static void startBatchMode() throws Exception {
		// 1) Cargamos el fichero de configuración JSON
		InputStream is = new FileInputStream(new File(inFile));
		JSONObject json = loadJSONFile(is);
		is.close();

		int width = json.optInt("width", 800);
		int height = json.optInt("height", 600);
		int rows = json.optInt("rows", 20);
		int cols = json.optInt("cols", 15);

		// 2) Instanciamos el modelo (Simulador) y el controlador
		Simulator sim = new Simulator(cols, rows, width, height, _animalFactory, _regionFactory);
		Controller ctrl = new Controller(sim);

		// 3) Cargamos los datos (animales y regiones)
		ctrl.loadData(json);

		// 4) Configuramos la salida (consola o fichero)
		OutputStream out = outFile == null ? System.out : new FileOutputStream(new File(outFile));

		// 5) Ejecutamos el bucle de simulación en el controlador
		ctrl.run(time, deltaTime, simpleViewer, out);

		if (out != System.out) {
			out.close();
		}
	}

	// Inicia la simulación con interfaz gráfica
	private static void startGUIMode() throws Exception {
		Simulator sim;
		Controller ctrl;
		
		// Si se proporciona un fichero de entrada, lo cargamos para inicializar el simulador
		if (inFile != null) {
			InputStream is = new FileInputStream(new File(inFile));
			JSONObject json = loadJSONFile(is);
			is.close();

			int width = json.optInt("width", 800);
			int height = json.optInt("height", 600);
			int rows = json.optInt("rows", 20);
			int cols = json.optInt("cols", 15);

			sim = new Simulator(cols, rows, width, height, _animalFactory, _regionFactory);
			ctrl = new Controller(sim);
			ctrl.loadData(json);
		} else {
			// Si no hay fichero, iniciamos un simulador vacío por defecto
			sim = new Simulator(20, 15, 800, 600, _animalFactory, _regionFactory);
			ctrl = new Controller(sim);
		}
		
		// Lanzamos la ventana principal en el hilo de Swing (EDT)
		javax.swing.SwingUtilities.invokeAndWait(() -> new simulator.view.MainWindow(ctrl));
	}

	// Punto de entrada real de la aplicación
	private static void start(String[] args) throws Exception {
		// 1) Inicializar factorías (Define qué animales y regiones existen)
		initFactories();
		// 2) Parsear los argumentos de la línea de comandos
		parseArgs(args);
		// 3) Configurar la semilla aleatoria para que los experimentos sean reproducibles
		if (seed != null) {
			Utils.RAND.setSeed(seed);
		} else {
			Utils.RAND.setSeed(2147483647l);
		}
		// 4) Lanzar el modo correspondiente (Batch o GUI)
		switch (mode) {
			case BATCH:
				startBatchMode();
				break;
			case GUI:
				startGUIMode();
				break;
		}
	}

	public static void main(String[] args) {
		try {
			start(args);
		} catch (Exception e) {
			System.err.println("Something went wrong ...");
			System.err.println();
			e.printStackTrace();
		}
	}

}
