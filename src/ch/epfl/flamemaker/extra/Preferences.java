package ch.epfl.flamemaker.extra;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.InterpolatedPalette;
import ch.epfl.flamemaker.color.Palette;
import ch.epfl.flamemaker.color.RandomPalette;
import ch.epfl.flamemaker.flame.Flame;
import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.flame.Variation;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;
import ch.epfl.flamemaker.gui.ObservableFlameBuilder;

/**
 * Get the preferences from a file to choose which default fractal to use, which
 * color of background...
 */
public class Preferences {

	/**
	 * Builder for {@link Preferences} which will load file, do the ugly
	 * work around it and nicely return the result {@link Preferences}
	 */
	private static class Builder {

		/**
		 * The {@link Color} of the background we use to build the image
		 */
		private Color				background;

		/**
		 * The {@link Builder} we are currently working on
		 */
		private final ObservableFlameBuilder	builder;

		/**
		 * The number of iteration
		 */
		private int				density;

		/**
		 * The scope of the fractal
		 */
		private Rectangle			frame;

		/**
		 * A logger used to output warnings and errors
		 */
		private final Logger			logger;

		/**
		 * The {@link Palette} we use to build the image
		 */
		private Palette				palette;

		/**
		 * The path to the default configuration file
		 */
		private final String			path;

		/**
		 * The time between every refresh of the fractal
		 */
		private int				refresh;

		/**
		 * The number of points computed every time, if -1, then we try
		 * to set the best value at runtime
		 */
		private int				step;

		/**
		 * The number of threads used in computation
		 */
		private int				threads;

		/**
		 * The threshold in number of points where we will increase the
		 * refresh time
		 */
		private int				threshold;

		/**
		 * Return the string without any comments
		 * 
		 * @param line
		 *                The line to clean of comments
		 * @return The string without any comments
		 */
		private static String cleanLine(String line) {
			line = line.replaceAll("[ ]", "");
			return line.replaceAll("#.*", "");
		}

		/**
		 * Generate the Shark Fin fractal
		 * 
		 * @return A {@link Flame} containing the fractal
		 */
		private static ObservableFlameBuilder generateSharkFin() {
			final ObservableFlameBuilder builder = new ObservableFlameBuilder(new Flame(
					new ArrayList<FlameTransformation>()));
			final double[][] array = { { 1, 0.1, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0.8, 1 }, { 1, 0, 0, 0, 0, 0 } };

			AffineTransformation affine = new AffineTransformation(-0.4113504, -0.7124804, -0.4, 0.7124795,
					-0.4113508, 0.8);
			builder.addTransformation(new FlameTransformation(affine, array[0]));

			affine = new AffineTransformation(-0.3957339, 0, -1.6, 0, -0.3957337, 0.2);
			builder.addTransformation(new FlameTransformation(affine, array[1]));

			affine = new AffineTransformation(0.4810169, 0, 1, 0, 0.4810169, 0.9);
			builder.addTransformation(new FlameTransformation(affine, array[2]));

			return builder;
		}

		/**
		 * Return a {@link Color} with the given formated {@link String}
		 * as input
		 * 
		 * @param value
		 *                The {@link String} to parse
		 * 
		 * @return A {@link Color} with the given formated
		 *         {@link String} as input
		 * 
		 * @throws IllegalArgumentException
		 *                 if the {@link String} does start and end with
		 *                 parenthesizes, if the values inside the
		 *                 {@link String} are not {@link Double} or if
		 *                 there is not exactly three values
		 */
		private static Color parseColor(final String value) {

			final double values[] = new double[3];
			if (value.equals("random")) {

				final Random random = new Random();
				for (int i = 0; i < values.length; i++) {
					values[i] = random.nextDouble() * Double.MAX_VALUE / (Double.MAX_VALUE - 1);
				}
			} else {

				final String[] strings = Builder.splitValue(value);

				if (strings.length != 3) {
					throw new IllegalArgumentException(
							"must be only three values seperated by comas");
				}

				for (int i = 0; i < strings.length; i++) {
					values[i] = Builder.parseDouble(strings[i]);
				}
			}

			return new Color(values[0], values[1], values[2]);
		}

		/**
		 * Return a {@link Double} with the given formated
		 * {@link String} as input
		 * 
		 * @param value
		 *                The {@link String} to parse
		 * 
		 * @return A {@link Double} with the given formated
		 *         {@link String} as input
		 * 
		 * @throws IllegalArgumentException
		 *                 if the values inside the {@link String} are
		 *                 not {@link Double}
		 */
		private static double parseDouble(final String value) {
			try {
				return Double.parseDouble(value);
			} catch (final NumberFormatException e) {
				throw new IllegalArgumentException("must contain only numeric values");
			}
		}

		/**
		 * Return a {@link Integer} with the given formated
		 * {@link String} as input
		 * 
		 * @param value
		 *                The {@link String} to parse
		 * 
		 * @return A {@link Integer} with the given formated
		 *         {@link String} as input
		 * 
		 * @throws IllegalArgumentException
		 *                 if the values inside the {@link String} are
		 *                 not {@link Integer}
		 */
		private static int parseInt(final String value) {
			int i;
			try {
				i = Integer.parseInt(value);
			} catch (final NumberFormatException e) {
				throw new IllegalArgumentException("must contain only numeric values");
			}
			return i;
		}

		/**
		 * Add to the {@link ArrayList} the matrix given by the formated
		 * {@link String} as input
		 * 
		 * @param value
		 *                The {@link String} to parse
		 * @param affines
		 *                The {@link ArrayList} to add the
		 *                {@link AffineTransformation} to
		 * 
		 * 
		 * @throws IllegalArgumentException
		 *                 if the {@link String} does start and end with
		 *                 parenthesizes, if these isn't exactly two
		 *                 lines, if the values inside the
		 *                 {@link String} are not double and are not
		 *                 exactly three
		 */
		private static void parseMatrix(final String value, final ArrayList<AffineTransformation> affines) {

			// contain the values
			final double[][] values = new double[2][3];

			if (value.equals("random")) {

				final Random random = new Random();
				for (int i = 0; i < values.length; i++) {
					for (int j = 0; j < values[0].length; j++) {

						// because we need every value
						// between 0 and 1 inclusive
						// (and not exclusive as in
						// Random)
						values[i][j] = (random.nextBoolean() ? 1 : -1) * random.nextDouble()
								* Double.MAX_VALUE / (Double.MAX_VALUE - 1);
					}
				}

			} else {

				final String[] split = Builder.splitValue(value);

				if (split.length != 2) {
					throw new IllegalArgumentException("must be only two lines in the matrix");
				}

				final String[][] lines = new String[2][];

				lines[0] = Builder.splitValue(split[0]);
				lines[1] = Builder.splitValue(split[1]);

				for (final String[] line : lines) {
					if (line.length != 3) {
						throw new IllegalArgumentException(
								"each line must be only three values seperated by comas");
					}
				}

				for (int i = 0; i < lines.length; i++) {
					final String[] line = lines[i];

					for (int j = 0; j < line.length; j++) {
						values[i][j] = Builder.parseDouble(line[j]);
					}
				}
			}

			final AffineTransformation aff = new AffineTransformation(values[0][0], values[0][1],
					values[0][2], values[1][0], values[1][1], values[1][2]);
			affines.add(aff);
		}

		/**
		 * Return a {@link Palette} with the given formated
		 * {@link String} as input
		 * 
		 * @param value
		 *                The {@link String} to parse
		 * 
		 * @return A {@link Palette} with the given formated
		 *         {@link String} as input
		 * 
		 * @throws IllegalArgumentException
		 *                 if the {@link String} does start and end with
		 *                 parenthesizes, if the values inside the
		 *                 {@link String} are not {@link Color} or if
		 *                 there is not exactly three values
		 */
		private static Palette parsePalette(final String value) {

			if (value.equals("random")) {
				return new RandomPalette(3);
			}

			final String[] strings = Builder.splitValue(value);

			if (strings.length != 3) {
				throw new IllegalArgumentException("must be only three values seperated by comas");
			}

			final Color values[] = new Color[3];
			for (int i = 0; i < strings.length; i++) {
				values[i] = Builder.parseColor(strings[i]);
			}

			return new InterpolatedPalette(Arrays.asList(values[0], values[1], values[2]));
		}

		/**
		 * Return a {@link Point} with the given formated {@link String}
		 * as input
		 * 
		 * @param value
		 *                The {@link String} to parse
		 * 
		 * @return A {@link Point} with the given formated
		 *         {@link String} as input
		 * 
		 * @throws IllegalArgumentException
		 *                 if the {@link String} does start and end with
		 *                 parenthesizes, if the values inside the
		 *                 {@link String} are not {@link Double} or if
		 *                 there is not exactly two values
		 */
		private static Point parsePoint(final String value) {

			final String[] strings = Builder.splitValue(value);

			if (strings.length != 2) {
				throw new IllegalArgumentException("must be only two values seperated by comas");
			}

			final double values[] = new double[2];
			for (int i = 0; i < strings.length; i++) {
				values[i] = Builder.parseDouble(strings[i]);

			}

			return new Point(values[0], values[1]);
		}

		/**
		 * Return a {@link Rectangle} with the given formated
		 * {@link String} as input
		 * 
		 * @param value
		 *                The {@link String} to parse
		 * 
		 * @return A {@link Rectangle} with the given formated
		 *         {@link String} as input
		 * 
		 * @throws IllegalArgumentException
		 *                 if the {@link String} does start and end with
		 *                 parenthesizes, if the first value is not a
		 *                 {@link Point}, if the others values inside
		 *                 the {@link String} are not {@link Double} or
		 *                 if there is not exactly three values
		 */
		private static Rectangle parseRectangle(final String value) {
			final String[] strings = Builder.splitValue(value);

			if (strings.length != 3) {
				throw new IllegalArgumentException("must be only three values seperated by comas");
			}

			final double values[] = new double[2];
			for (int i = 1; i < strings.length; i++) {
				values[i - 1] = Builder.parseDouble(strings[i]);
			}

			return new Rectangle(Builder.parsePoint(strings[0]), values[0], values[1]);
		}

		/**
		 * Add to the {@link ArrayList} the weight (an array of double)
		 * given by the formated {@link String} as input
		 * 
		 * @param value
		 *                The {@link String} to parse
		 * @param weights
		 *                The {@link ArrayList} to add the array of
		 *                double to
		 * 
		 * 
		 * @throws IllegalArgumentException
		 *                 if the {@link String} does start and end with
		 *                 parenthesizes, if the values inside the
		 *                 {@link String} are not double and are not
		 *                 exactly six
		 * 
		 */
		private static void parseWeight(final String value, final ArrayList<double[]> weights) {
			final double[] array = new double[6];

			if (value.equals("random")) {

				final Random random = new Random();
				for (int i = 0; i < array.length; i++) {

					// because we need every value
					// between 0 and 1 inclusive
					// (and not exclusive as in
					// Random)
					array[i] = random.nextDouble() * Double.MAX_VALUE / (Double.MAX_VALUE - 1);
				}
			} else {
				final String[] split = Builder.splitValue(value);

				if (split.length != 6) {
					throw new IllegalArgumentException("must be only six values seperated by comas");
				}

				for (int i = 0; i < split.length; i++) {
					array[i] = Builder.parseDouble(split[i]);
				}
			}

			weights.add(array);
		}

		/**
		 * Return the split array of the given {@link String}
		 * 
		 * @param value
		 *                The {@link String} to parse
		 * 
		 * @return The split array of the given {@link String}
		 */
		private static String[] splitValue(String value) {

			if (!value.startsWith("(") || !value.endsWith(")")) {
				throw new IllegalArgumentException("must begin and end with parenthesizes");
			}

			value = value.replaceAll("^[(]|[)]$", "");
			final char[] values = new char[value.length()];
			value.getChars(0, value.length(), values, 0);

			final ArrayList<String> array = new ArrayList<String>();
			array.add("");

			int parenthesizes = 0, index = 0;
			for (final char c : values) {

				switch (c) {
				case '(':
					parenthesizes++;
					array.set(index, array.get(index) + '(');
					break;

				case ')':
					parenthesizes--;
					if (parenthesizes < 0) {
						throw new IllegalArgumentException(
								"closing parenthesizes before opening one");
					}
					array.set(index, array.get(index) + ')');
					break;

				case ',':
					if (parenthesizes == 0 && index + 1 == array.size()) {
						index++;
						array.add("");
					} else if (parenthesizes > 0) {
						array.set(index, array.get(index) + ',');
					}
					break;

				default:
					array.set(index, array.get(index) + c);
				}
			}

			final String[] vals = new String[array.size()];
			for (int i = 0; i < array.size(); i++) {
				vals[i] = array.get(i);
			}

			return vals;
		}

		/**
		 * Construct a new {@link Builder} which try to load the file or
		 * fall back on default configuration
		 */
		private Builder() {

			this.path = "flamefract.conf";
			this.logger = Logger.getLogger(Builder.class.getName());

			// Set the basic values, to avoid any empty values
			this.builder = new ObservableFlameBuilder(Preferences.defaults.builder);
			this.background = Preferences.defaults.background;
			this.palette = Preferences.defaults.palette;
			this.frame = Preferences.defaults.frame;
			this.density = Preferences.defaults.density;
			this.step = Preferences.defaults.step;
			this.refresh = Preferences.defaults.refresh;
			this.threshold = Preferences.defaults.threshold;

			if (Preferences.defaults.threads == -1) {
				this.threads = Runtime.getRuntime().availableProcessors() + 1;
			} else {
				this.threads = Preferences.defaults.threads;
			}

			try {
				final BufferedReader reader = Files.newBufferedReader(Paths.get(this.path),
						StandardCharsets.UTF_8);
				this.readConf(reader);

			} catch (final IOException e) {

				try {
					final PrintStream file = new PrintStream(this.path);
					this.build().writeConfiguration(file);
					file.close();

				} catch (final FileNotFoundException e1) {
					this.logger.log(Level.WARNING,
							"Unable to read or write configuration file, using default values");
				}
			}

		}

		/**
		 * Add the content of the arrays by creating a new
		 * {@link FlameTransformation} with an
		 * {@link AffineTransformation} and an array of double
		 * 
		 * @param affines
		 *                The {@link ArrayList} of
		 *                {@link AffineTransformation}
		 * @param weights
		 *                The {@link ArrayList} of array of double
		 * 
		 * @throws IllegalArgumentException
		 *                 if the arrays aren't of the same size
		 */
		private void addArrays(final ArrayList<AffineTransformation> affines, final ArrayList<double[]> weights) {
			if (affines.size() != weights.size()) {
				throw new IllegalArgumentException(
						"The number of matrix is not equal to the number of weight");
			}

			// Add to the builder
			final Iterator<AffineTransformation> affIter = affines.iterator();
			final Iterator<double[]> weightIter = weights.iterator();
			for (; affIter.hasNext() && weightIter.hasNext();) {
				final AffineTransformation trans = affIter.next();
				final double[] weight = weightIter.next();

				this.builder.addTransformation(new FlameTransformation(trans, weight));
			}
		}

		/**
		 * Return a new {@link Preferences} with the actual state of the
		 * {@link Builder}
		 * 
		 * @return A new {@link Preferences} with the actual state of
		 *         the {@link Builder}
		 */
		private Preferences build() {
			return new Preferences(this.background, this.builder, this.density, this.frame, this.palette,
					this.threads, this.refresh, this.step, this.threshold, this.path);
		}

		/**
		 * Read the given stream, parse it and assign the correct values
		 * 
		 * @param reader
		 *                The {@link DataInputStream} to parse
		 */
		private void readConf(final BufferedReader reader) {

			int num = 0;

			final ArrayList<AffineTransformation> affines = new ArrayList<AffineTransformation>();
			final ArrayList<double[]> weights = new ArrayList<double[]>();

			// remove already set builder
			while (this.builder.transformationCount() > 0) {
				this.builder.removeTransformation(0);
			}

			try {

				do {
					try {
						final String line = reader.readLine();
						if (line == null) {
							break;
						}

						num++;

						final String l = Builder.cleanLine(line);
						if (l.isEmpty()) {
							continue;
						}

						final int pos = l.indexOf('=');
						if (pos == -1) {
							throw new IllegalArgumentException("unrecognized value");
						}

						final String value = l.substring(pos + 1);

						switch (l.substring(0, pos)) {

						case "density":
							this.density = Builder.parseInt(value);
							break;

						case "color":
							this.background = Builder.parseColor(value);
							break;

						case "palette":
							this.palette = Builder.parsePalette(value);
							break;

						case "frame":
							this.frame = Builder.parseRectangle(value);
							break;

						case "matrix":
							Builder.parseMatrix(value, affines);
							break;

						case "weight":
							Builder.parseWeight(value, weights);
							break;

						case "threads":
							this.threads = Builder.parseInt(value);
							break;

						case "refresh":
							this.refresh = Builder.parseInt(value);
							break;

						case "step":
							this.step = Builder.parseInt(value);
							break;

						case "threshold":
							this.threshold = Builder.parseInt(value);
							break;

						default:
							throw new IllegalArgumentException("unrecognized value");
						}

					} catch (final IOException e) {
						break;
					}

				} while (true);

				this.addArrays(affines, weights);

			} catch (final IllegalArgumentException e) {
				final StackTraceElement[] elements = e.getStackTrace();
				String string = elements[0].getMethodName();
				for (int i = 1; elements[i].getMethodName() != "readConf" && string != "readConf"; i++) {
					string = elements[i].getMethodName() + " --> " + string;
				}
				if (string != "readConf") {
					string = "readConf --> " + string;
				}
				this.logger.log(Level.SEVERE, "Error in configuration at line " + num + ": " + string
						+ ": " + e.getMessage());
				System.exit(1);
			}
		}
	}

	/**
	 * All the defaults values of the {@link Preferences}
	 */
	public final static Preferences		defaults	= new Preferences(Color.BLACK,
										Preferences.Builder.generateSharkFin(),
										50, new Rectangle(new Point(-0.25, 0),
												5, 4),
										new InterpolatedPalette(Arrays.asList(
												Color.RED, Color.GREEN,
												Color.BLUE)), -1, 100,
										-1, 10000, "flamefract.conf");

	/**
	 * All the values of the {@link Preferences} set by the builder
	 */
	public final static Preferences		values		= new Preferences();

	/**
	 * The {@link Color} of the background we use to build the image
	 */
	public final Color			background;

	/**
	 * The {@link Builder} we are currently working on
	 */
	public final ObservableFlameBuilder	builder;

	/**
	 * The number of iteration
	 */
	public final int			density;

	/**
	 * The scope of the fractal
	 */
	public final Rectangle			frame;

	/**
	 * The {@link Palette} we use to build the image
	 */
	public final Palette			palette;

	/**
	 * The time between every refresh of the fractal
	 */
	public final int			refresh;

	/**
	 * The number of points computed every time, if -1, then we try to set
	 * the best value at runtime
	 */
	public final int			step;

	/**
	 * The number of threads used in computation
	 */
	public final int			threads;

	/**
	 * The threshold in number of points where we will increase the refresh
	 * time
	 */
	public final int			threshold;

	/**
	 * Path to the configuration file
	 */
	public final String			path;

	/**
	 * Construct a new {@link Preferences} by loading the file in
	 * {@link Builder}
	 */
	private Preferences() {
		this(new Preferences.Builder().build());
	}

	/**
	 * Write the default configuration file to the given {@link PrintStream}
	 * (usually a file)
	 * 
	 * @param stream
	 *                The stream to write to
	 */
	public void writeConfiguration(final PrintStream stream) {
		stream.println("# Default configuration file for flamefract");
		stream.println();
		stream.println("# Every times the is a '#', it means the begin of a comment which won't be relevant");
		stream.println("# and thus won't be parsed");
		stream.println("# Every settings will have the following template");
		stream.println("#  variable = value # eventually a comment");
		stream.println();
		stream.println("## Core part (everything which is not directly GUI)");
		stream.println();
		stream.println("# Density of the computation (the more, the more point it will generate)");
		stream.println("density = " + this.density);
		stream.println();
		stream.println("# Number of threads used in the computation (will be set to numberOfCore + 1");
		stream.println("# if there is no value or the magic \"-1\")");
		if (this.threads == -1) {
			stream.println("#threads = 8");
		} else {
			stream.println("threads = " + this.threads);
		}
		stream.println();
		stream.println("# Color of the background (in RGB), values as double, min 0, max 1");
		stream.println("# The magic word \"random\" will generate a color with random values (quite ugly)");
		stream.println("color = " + this.background);
		stream.println();
		stream.println("# Palette of colors, same behavious as above");
		stream.println("# The magic word \"random\" will generate a palette with random values (still ugly)");
		stream.println("palette = " + this.palette);
		stream.println();
		stream.println("# Frame of the fractal, the default relevant part to consider in computation.");
		stream.println("# The first value is the center of the frame (as a point), then come the width");
		stream.println("# and height");
		stream.println("frame = " + this.frame);
		stream.println();
		stream.println("# The matrix part is a bit more tricky: the value name have a bit different");
		stream.println("# meaning. But first, how to write a matrix. We only need the first two lines");
		stream.println("# of the matrix, the last is only (0,0,1) and thus isn't relevant for you.");
		stream.println("# You only have to write, as an array, the first two line, each in a sub-array.");
		stream.println("# Notice the name is only matrix; the order of the matrix is relevant and thus");
		stream.println("# each new matrix will not overwrite the older value rather will add a new");
		stream.println("# matrix to the default list");
		stream.println("# The magic word \"random\" means to generate a new matrix with random values");
		for (int i = 0; i < this.builder.transformationCount(); i++) {
			stream.println("matrix = " + this.builder.affineTransformation(i));
		}

		stream.println();
		stream.println("# Then come the weight of every variation, as a size six array, the order is");

		stream.print("# ");
		for (final Variation var : Variation.ALL_VARIATIONS) {
			stream.print(var.name() + ", ");
		}
		stream.println();
		stream.println("# As for the matrix, every weight add up rather than crush");
		stream.println("# As for the matrix, \"random\" will generate a random weight between 0 and 1");
		for (int i = 0; i < this.builder.transformationCount(); i++) {
			stream.print("weight = (");
			for (Variation var : Variation.ALL_VARIATIONS) {
				stream.print(this.builder.variationWeight(i, var) + (var.index() == 5 ? ")" : ","));
			}
			stream.println();
		}
		stream.println();
		stream.println("## GUI part");
		stream.println();
		stream.println("# The time in ms between every refresh of the fractal");
		stream.println("refresh = " + this.refresh);
		stream.println();
		stream.println("# The number of points computed beetween every refresh");
		stream.println("# If unset, we will monitor the computation to set the best value");
		stream.println("# (not really recommanded to set it yourself, but hey, it's your choice!)");
		if (this.step == -1) {
			stream.println("#step = 20000");
		} else {
			stream.println("step = " + this.step);
		}
		stream.println("");
		stream.println("# The threshold in number of points where we will increase the refresh time");
		stream.println("# It won't be used if we have a fixed value for step");
		stream.println("threshold = " + this.threshold);
	}

	/**
	 * Construct a {@link Preferences} with the given values
	 * 
	 * @param background
	 *                The {@link Color} of the background we use to build
	 *                the image
	 * @param builder
	 *                The {@link Builder} we are currently working on
	 * @param density
	 *                The number of iteration
	 * @param frame
	 *                The scope of the fractal
	 * @param palette
	 *                The {@link Palette} we use to build the image
	 * @param threads
	 *                The number of {@link Thread} used in computation
	 * @param refresh
	 *                The time between every refresh of the fractal
	 * @param step
	 *                The number of points computed every time, if -1, then
	 *                we try to set the best value at runtime
	 * @param threshold
	 *                The threshold in number of points where we will
	 *                increase the refresh time
	 * @param path
	 *                The path to the configuation file
	 */
	public Preferences(final Color background, final ObservableFlameBuilder builder, final int density,
			final Rectangle frame, final Palette palette, final int threads, final int refresh,
			final int step, final int threshold, final String path) {
		this.background = background;
		this.builder = new ObservableFlameBuilder(builder);
		this.density = density;
		this.frame = frame;
		this.palette = palette;
		this.threads = threads;
		this.refresh = refresh;
		this.step = step;
		this.threshold = threshold;
		this.path = path;
	}

	/**
	 * Copy-construct a {@link Preferences} with the given
	 * {@link Preferences}
	 * 
	 * @param pref
	 *                The {@link Preferences} to copy
	 */
	private Preferences(final Preferences pref) {
		this.background = pref.background;
		this.builder = new ObservableFlameBuilder(pref.builder);
		this.density = pref.density;
		this.frame = pref.frame;
		this.palette = pref.palette;
		this.threads = pref.threads;
		this.refresh = pref.refresh;
		this.step = pref.step;
		this.threshold = pref.threshold;
		this.path = pref.path;
	}
}
