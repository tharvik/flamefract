package ch.epfl.flamemaker.flame;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

/**
 * Main class, will generate the following colored fractals to PGM files
 * <ol>
 * <li>Turbulence</li>
 * <li>Barnsley's Fougere</li>
 * <li>Shark Fin</li>
 * </ol>
 */
public class FlamePGMMaker {
	/**
	 * Generate all fractals of the class, by using threads
	 * 
	 * @param args
	 *                Not used
	 */
	public static void main(String[] args) {

		final Runnable sharkfin = new Runnable() {

			@Override
			public void run() {
				try {
					final PrintStream file = new PrintStream("SharkFin.pgm");

					FlamePGMMaker.writeToPGM(FlamePGMMaker.generateSharkFin(), file);

					file.close();
				} catch (final FileNotFoundException e) {
					System.out.println("Not able to open \"SharkFin.pgm\"! " + "Abort..");
					System.exit(1);
				}

			}
		};

		final Runnable turbulence = new Runnable() {

			@Override
			public void run() {

				try {
					final PrintStream file = new PrintStream("Turbulence.pgm");

					FlamePGMMaker.writeToPGM(FlamePGMMaker.generateTurbulence(), file);

					file.close();
				} catch (final FileNotFoundException e) {
					System.out.println("Not able to open \"Turbulence.pgm\"! " + "Abort..");
					System.exit(1);
				}
			}
		};

		final Runnable barnsleyFougere = new Runnable() {

			@Override
			public void run() {
				try {
					final PrintStream file = new PrintStream("BarnsleyFougere.bpm");

					FlamePGMMaker.writeToPGM(FlamePGMMaker.generateBarnsleyFougere(), file);

					file.close();
				} catch (final FileNotFoundException e) {
					System.out.println("Not able to open \"BarnsleyFougere.bpm\"! " + "Abort..");
					System.exit(1);
				}
			}
		};

		final Thread sharkFinThread = new Thread(sharkfin);
		final Thread turbulenceThread = new Thread(turbulence);
		final Thread barnsleyFougereThread = new Thread(barnsleyFougere);

		sharkFinThread.start();
		turbulenceThread.start();
		barnsleyFougereThread.start();
	}

	/**
	 * Generate the Barnsley fractal
	 * 
	 * @return A {@link FlameAccumulator} containing the fractal
	 */
	private static FlameAccumulator generateBarnsleyFougere() {

		final Flame.Builder builder = new Flame.Builder(new Flame(new ArrayList<FlameTransformation>()));
		final double[] array = { 1, 0, 0, 0, 0, 0 };
		final Rectangle center = new Rectangle(new Point(0, 4.5), 6, 10);
		
		AffineTransformation affine = new AffineTransformation(0, 0, 0, 0, 0.16, 0);
		builder.addTransformation(new FlameTransformation(affine, array));

		affine = new AffineTransformation(0.2, -0.26, 0, 0.23, 0.22, 1.6);
		builder.addTransformation(new FlameTransformation(affine, array));

		affine = new AffineTransformation(-0.15, 0.28, 0, 0.26, 0.24, 0.44);
		builder.addTransformation(new FlameTransformation(affine, array));
		
		affine = new AffineTransformation(0.85, 0.04, 0, -0.04, 0.85, 1.6);
		builder.addTransformation(new FlameTransformation(affine, array));
		
		return builder.build().compute(center, 120, 200, 150);
	}

	/**
	 * Generate the Shark Fin fractal
	 * 
	 * @return A {@link FlameAccumulator} containing the fractal
	 */
	private static FlameAccumulator generateSharkFin() {
		final Flame.Builder builder = new Flame.Builder(new Flame(new ArrayList<FlameTransformation>()));
		final double[][] array = { { 1, 0.1, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0.8, 1 }, { 1, 0, 0, 0, 0, 0 } };
		final Rectangle center = new Rectangle(new Point(-0.25, 0), 5, 4);

		AffineTransformation affine = new AffineTransformation(-0.4113504, -0.7124804, -0.4, 0.7124795,
				-0.4113508, 0.8);
		builder.addTransformation(new FlameTransformation(affine, array[0]));

		affine = new AffineTransformation(-0.3957339, 0, -1.6, 0, -0.3957337, 0.2);
		builder.addTransformation(new FlameTransformation(affine, array[1]));

		affine = new AffineTransformation(0.4810169, 0, 1, 0, 0.4810169, 0.9);
		builder.addTransformation(new FlameTransformation(affine, array[2]));

		return builder.build().compute(center, 500, 400, 50);
	}

	/**
	 * Generate the Turbulence fractal
	 * 
	 * @return A {@link FlameAccumulator} containing the fractal
	 */
	private static FlameAccumulator generateTurbulence() {
		final Flame.Builder builder = new Flame.Builder(new Flame(new ArrayList<FlameTransformation>()));
		final double[][] array = { { 0.5, 0, 0, 0.4, 0, 0 }, { 1, 0, 0.1, 0, 0, 0 }, { 1, 0, 0, 0, 0, 0 } };
		final Rectangle center = new Rectangle(new Point(0.1, 0.1), 3, 3);

		AffineTransformation affine = new AffineTransformation(0.7124807, -0.4113509, -0.3, 0.4113513,
				0.7124808, -0.7);
		builder.addTransformation(new FlameTransformation(affine, array[0]));

		affine = new AffineTransformation(0.3731079, -0.6462417, 0.4, 0.6462414, 0.3731076, 0.3);
		builder.addTransformation(new FlameTransformation(affine, array[1]));

		affine = new AffineTransformation(0.0842641, -0.314478, -0.1, 0.314478, 0.0842641, 0.3);
		builder.addTransformation(new FlameTransformation(affine, array[2]));

		return builder.build().compute(center, 500, 400, 50);
	}

	/**
	 * Write a {@link FlameAccumulator} to a stream
	 * 
	 * @param accu
	 *                The {@link FlameAccumulator} containing to write
	 * @param stream
	 *                The stream to write the fractal to
	 */
	private static void writeToPGM(FlameAccumulator accu, PrintStream stream) {
		stream.println("P2");
		stream.println(accu.width() + " " + accu.height());
		stream.println(100);

		for (int y = accu.height() - 1; y >= 0; y--) {
			String line = new String();
			for (int x = 0; x < accu.width(); x++) {
				line += (int) (accu.intensity(x, y) * 100.0);
				line += ((x + 1 == accu.width()) ? "" : " ");
			}
			stream.println(line);
		}
	}
}
