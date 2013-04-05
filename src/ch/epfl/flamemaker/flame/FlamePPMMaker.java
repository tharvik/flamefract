package ch.epfl.flamemaker.flame;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

import ch.epfl.flamemaker.color.Color;
import ch.epfl.flamemaker.color.InterpolatedPalette;
import ch.epfl.flamemaker.color.Palette;
import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

/**
 * Main class, will generate all the fractals with colors to PPM files
 * <ol>
 * <li>Turbulence</li>
 * <li>Shark Fin</li>
 * </ol>
 */
public class FlamePPMMaker {

	/**
	 * Generate all fractals, by using threads
	 * 
	 * @param args
	 *                Not used
	 */
	public static void main(String[] args) {

		final Runnable sharkfin = new Runnable() {

			@Override
			public void run() {
				try {
					final PrintStream file = new PrintStream("shark-fin.ppm");

					FlamePPMMaker.writeToPPM(FlamePPMMaker.generateSharkFin(), file);

					file.close();
				} catch (final FileNotFoundException e) {
					System.out.println("Not able to open \"shark-fin.ppm\"! " + "Abort..");
					System.exit(1);
				}

			}
		};

		final Runnable turbulence = new Runnable() {

			@Override
			public void run() {

				try {
					final PrintStream file = new PrintStream("turbulence.ppm");

					FlamePPMMaker.writeToPPM(FlamePPMMaker.generateTurbulence(), file);

					file.close();
				} catch (final FileNotFoundException e) {
					System.out.println("Not able to open \"turbulence.ppm\"! " + "Abort..");
					System.exit(1);
				}
			}
		};

		final Thread sharkFinThread = new Thread(sharkfin);
		final Thread turbulenceThread = new Thread(turbulence);

		sharkFinThread.start();
		turbulenceThread.start();
	}

	/**
	 * Generate the Shark Fin fractal
	 * 
	 * @return An accumulator containing the fractal
	 */
	private static FlameAccumulator generateSharkFin() {

		// TODO use new Flame.Builder()
		final ArrayList<FlameTransformation> transformations = new ArrayList<FlameTransformation>();
		final double[][] array = { { 1, 0.1, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0.8, 1 }, { 1, 0, 0, 0, 0, 0 } };

		AffineTransformation affine = new AffineTransformation(-0.4113504, -0.7124804, -0.4, 0.7124795,
				-0.4113508, 0.8);
		transformations.add(new FlameTransformation(affine, array[0]));

		affine = new AffineTransformation(-0.3957339, 0, -1.6, 0, -0.3957337, 0.2);
		transformations.add(new FlameTransformation(affine, array[1]));

		affine = new AffineTransformation(0.4810169, 0, 1, 0, 0.4810169, 0.9);
		transformations.add(new FlameTransformation(affine, array[2]));

		final Flame flame = new Flame(transformations);
		final Rectangle center = new Rectangle(new Point(-0.25, 0), 5, 4);
		return flame.compute(center, 500, 400, 50);
	}

	/**
	 * Generate the Turbulence fractal
	 * 
	 * @return An accumulator containing the fractal
	 */
	private static FlameAccumulator generateTurbulence() {

		// TODO use new Flame.Builder()
		final ArrayList<FlameTransformation> transformations = new ArrayList<FlameTransformation>();
		final double[][] array = { { 0.5, 0, 0, 0.4, 0, 0 }, { 1, 0, 0.1, 0, 0, 0 }, { 1, 0, 0, 0, 0, 0 } };

		AffineTransformation affine = new AffineTransformation(0.7124807, -0.4113509, -0.3, 0.4113513,
				0.7124808, -0.7);
		transformations.add(new FlameTransformation(affine, array[0]));

		affine = new AffineTransformation(0.3731079, -0.6462417, 0.4, 0.6462414, 0.3731076, 0.3);
		transformations.add(new FlameTransformation(affine, array[1]));

		affine = new AffineTransformation(0.0842641, -0.314478, -0.1, 0.314478, 0.0842641, 0.3);
		transformations.add(new FlameTransformation(affine, array[2]));

		final Flame flame = new Flame(transformations);
		final Rectangle center = new Rectangle(new Point(0.1, 0.1), 3, 3);
		return flame.compute(center, 500, 400, 50);
	}

	/**
	 * Write an accumulator to a stream
	 * 
	 * @param accu
	 *                The accumulator containing a fractal
	 * @param stream
	 *                The stream to write the fractal to
	 */
	private static void writeToPPM(FlameAccumulator accu, PrintStream stream) {
		stream.println("P3");
		stream.println(accu.width() + " " + accu.height());
		stream.println(100);

		final ArrayList<Color> list = new ArrayList<Color>(3);
		list.add(Color.RED);
		list.add(Color.GREEN);
		list.add(Color.BLUE);
		final Palette palette = new InterpolatedPalette(list);

		for (int y = accu.height() - 1; y >= 0; y--) {
			String line = new String();
			for (int x = 0; x < accu.width(); x++) {
				final Color c = accu.color(palette, Color.BLACK, x, y);
				line += (Color.sRGBEncode(c.red(), 100)) + " ";
				line += (Color.sRGBEncode(c.green(), 100)) + " ";
				line += (Color.sRGBEncode(c.blue(), 100)) + " ";
			}
			stream.println(line);
		}
	}
}
