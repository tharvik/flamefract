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

import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.InterpolatedPalette;
import ch.epfl.flamemaker.color.Palette;
import ch.epfl.flamemaker.flame.Flame;
import ch.epfl.flamemaker.flame.FlameTransformation;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;
import ch.epfl.flamemaker.geometry2d.Transformation;
import ch.epfl.flamemaker.gui.ObservableFlameBuilder;

// TODO Use a class which will automatically parse the values

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
		 * The {@link Palette} we use to build the image
		 */
		private Palette				palette;

		/**
		 * The path to the default configuration file
		 */
		private final String			path;

		/**
		 * The {@link FlameTransformation} actually selected in the list
		 * of {@link Transformation}
		 */
		private final int			selectedTransformationIndex;

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
		private static Color parseColor(String value) {

			value = value.replaceAll("^[(]|[)]$", "");
			final String[] strings = value.split(",");

			if (strings.length != 3) {
				throw new IllegalArgumentException("must be only three values seperated by comas");
			}

			final double values[] = new double[3];
			for (int i = 0; i < strings.length; i++) {
				values[i] = Builder.parseDouble(strings[i]);
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
		private static double parseDouble(String value) {
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
		private static int parseInt(String value) {
			int i;
			try {
				i = Integer.parseInt(value);
			} catch (final NumberFormatException e) {
				throw new IllegalArgumentException("must contain only numeric values");
			}
			return i;
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
		private static Palette parsePalette(String value) {
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
		private static Point parsePoint(String value) {

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
		private static Rectangle parseRectangle(String value) {
			final String[] strings = Builder.splitValue(value);

			for (final String string : strings) {
				System.out.println(string);
			}

			if (strings.length != 3) {
				throw new IllegalArgumentException("must be only three values seperated by comas");
			}

			final double values[] = new double[2];
			for (int i = 1; i < strings.length; i++) {
				values[i] = Builder.parseDouble(strings[i]);
			}

			return new Rectangle(Builder.parsePoint(strings[0]), values[1], values[2]);
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
			final String[] strings = value.split("[)],[(]");
			for (int i = 0; i < strings.length; i++) {
				final String string = strings[i];
				if (!string.startsWith("(")) {
					strings[i] = "(" + string;
				}
				if (!string.endsWith(")")) {
					strings[i] += ")";
				}
			}
			return strings;
		}

		/**
		 * Write the default configuration file to the given
		 * {@link PrintStream} (usually a file)
		 * 
		 * @param stream
		 *                The stream to write to
		 */
		private static void writeConf(PrintStream stream) {
			stream.println("# Default configuration file for flamefract");
			stream.println("# Every times the is a '#', it means the begin of a comment which won't be relevant");
			stream.println("# and thus won't be parsed");
			stream.println("# Every settings will have the following template");
			stream.println("#  variable = value # eventually a comment");
			stream.println("");
			stream.println("# Density of the computation (the more, the more point it'll generate)");
			stream.println("density = 50");
			stream.println("");
			stream.println("# Color of the background (in RGB), values as double, min 0, max 1");
			stream.println("color = (0,0,0)");
			stream.println("");
			stream.println("# Palette of colors, same behavious as above");
			stream.println("palette = ((1,0,0),(0,1,0),(0,0,1))");
			stream.println("");
			stream.println("# Frame of the fractal, the default relevant part to consider in computation");
			stream.println("frame = ((-0.25,0),5,4)");
		}

		/**
		 * Construct a new {@link Builder} which try to load the file or
		 * fall back on default configuration
		 */
		public Builder() {

			this.path = "flamefract.conf";

			// this.builder = Preferences.generateSharkFin();
			// this.frame = new Rectangle(new Point(-0.25, 0), 5,
			// 4);
			// this.selectedTransformationIndex = 0;

			// Set the basic values, to avoid any empty values
			this.builder = Preferences.Builder.generateSharkFin();
			this.background = Color.BLACK;
			this.palette = new InterpolatedPalette(Arrays.asList(Color.RED, Color.GREEN, Color.BLUE));
			this.frame = new Rectangle(new Point(-0.25, 0), 5, 4);
			this.density = 50;
			this.selectedTransformationIndex = 0;

			try {
				final BufferedReader reader = Files.newBufferedReader(Paths.get(this.path),
						StandardCharsets.UTF_8);
				this.readConf(reader);

			} catch (final IOException e) {

				try {
					final PrintStream file = new PrintStream(this.path);
					Preferences.Builder.writeConf(file);
					file.close();

				} catch (final FileNotFoundException e1) {
					System.out.println("Unable to read or write configuration file, using default values");
				}
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
					this.selectedTransformationIndex);
		}

		/**
		 * Read the given stream, parse it and assign the correct values
		 * 
		 * @param reader
		 *                The {@link DataInputStream} to parse
		 */
		private void readConf(BufferedReader reader) {

			int num = 0;

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

					default:
						throw new IllegalArgumentException("unrecognized value");
					}

				} catch (final IOException e) {
					break;
				} catch (final IllegalArgumentException e) {
					final StackTraceElement[] elements = e.getStackTrace();
					String string = elements[0].getMethodName();
					for (int i = 1; elements[i].getMethodName() != "readConf"
							&& string != "readConf"; i++) {
						string = elements[i].getMethodName() + " --> " + string;
					}
					if (string != "readConf") {
						string = "readConf --> " + string;
					}
					System.out.println("at line " + num + ": " + string + ": " + e.getMessage());
					System.exit(1);
				}
			} while (true);
		}
	}

	/**
	 * The {@link Color} of the background we use to build the image
	 */
	private final Color			background;

	/**
	 * The {@link Builder} we are currently working on
	 */
	private final ObservableFlameBuilder	builder;

	/**
	 * The number of iteration
	 */
	private final int			density;

	/**
	 * The scope of the fractal
	 */
	private final Rectangle			frame;

	/**
	 * The {@link Palette} we use to build the image
	 */
	private final Palette			palette;

	/**
	 * The {@link FlameTransformation} actually selected in the list of
	 * {@link Transformation}
	 */
	private final int			selectedIndex;

	/**
	 * Construct a new {@link Preferences} by loading the file hardcode
	 * (still ugly way) in {@link Builder}
	 */
	public Preferences() {
		this(new Preferences.Builder().build());
	}

	/**
	 * Copy-construct a {@link Preferences} with the given
	 * {@link Preferences}
	 * 
	 * @param pref
	 *                The {@link Preferences} to copy
	 */
	Preferences(Preferences pref) {
		this.background = pref.background;
		this.builder = pref.builder;
		this.density = pref.density;
		this.frame = pref.frame;
		this.palette = pref.palette;
		this.selectedIndex = pref.selectedIndex;
	}

	/**
	 * Construct a {@link Preferences} with the given values
	 * 
	 * @param background
	 *                The {@link Color} of the background we use to build
	 *                the image
	 * 
	 * @param builder
	 *                The {@link Builder} we are currently working on
	 * 
	 * @param density
	 *                The number of iteration
	 * 
	 * @param frame
	 *                The scope of the fractal
	 * @param palette
	 *                The {@link Palette} we use to build the image
	 * @param selectedTransformationIndex
	 *                The {@link FlameTransformation} actually selected in
	 *                the list of {@link Transformation}
	 */
	private Preferences(Color background, ObservableFlameBuilder builder, int density, Rectangle frame,
			Palette palette, int selectedTransformationIndex) {
		super();
		this.background = background;
		this.builder = builder;
		this.density = density;
		this.frame = frame;
		this.palette = palette;
		this.selectedIndex = selectedTransformationIndex;
	}

	/**
	 * Return the background {@link Color} value wanted by the user
	 * 
	 * @return The background {@link Color} value wanted by the user
	 */
	public Color getBackground() {
		return this.background;
	}

	/**
	 * Return the density value wanted by the user
	 * 
	 * @return The density value wanted by the user
	 */
	public int getDensity() {
		return this.density;
	}

	/**
	 * Return the {@link Rectangle} value wanted by the user
	 * 
	 * @return The {@link Rectangle} value wanted by the user
	 */

	public Rectangle getFrame() {
		return this.frame;
	}

	/**
	 * Return the {@link Palette} value wanted by the user
	 * 
	 * @return The {@link Palette} value wanted by the user
	 */
	public Palette getPalette() {
		return this.palette;
	}
}
