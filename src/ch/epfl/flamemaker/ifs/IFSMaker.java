package ch.epfl.flamemaker.ifs;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

/**
 * Main class, will generate the following fractals to BPM files
 * <ol>
 * <li>Barnsley's Fougere</li>
 * <li>Sierpinski's Triangle</li>
 * </ol>
 */
public class IFSMaker {

	/**
	 * Generate all fractals of the class
	 * 
	 * @param args
	 *                Not used
	 */

	public static void main(final String[] args) {

		try {
			final PrintStream file = new PrintStream("SierpinskiTriangle.bpm");

			IFSMaker.writeToBPM(IFSMaker.generateSierpinskiTriangle(), file);

			file.close();
		} catch (final FileNotFoundException e) {
			System.out.println("Not able to open \"SierpinskiTriangle.bpm\"! " + "Abort..");
			System.exit(1);
		}

		try {
			final PrintStream file = new PrintStream("BarnsleyFougere.bpm");

			IFSMaker.writeToBPM(IFSMaker.generateBarnsleyFougere(), file);

			file.close();
		} catch (final FileNotFoundException e) {
			System.out.println("Not able to open \"BarnsleyFougere.bpm\"! " + "Abort..");
			System.exit(1);
		}

	}

	/**
	 * Creates the actual image in BPM format
	 * 
	 * @param accu
	 *                The accumulator to write
	 * @param stream
	 *                The stream to write to
	 */
	public static void writeToBPM(final IFSAccumulator accu, final PrintStream stream) {
		stream.println("P1");
		stream.println(accu.width() + " " + accu.height());

		for (int y = accu.height() - 1; y >= 0; y--) {
			String line = new String();
			for (int x = 0; x < accu.width(); x++) {
				line += ((accu.isHit(x, y)) ? "1" : "0");
				line += ((x + 1 == accu.width()) ? "" : " ");
			}
			stream.println(line);
		}
	}

	/**
	 * Generate the Barnsley fractal
	 * 
	 * @return An {@link IFSAccumulator} containing the fractal
	 */
	private static IFSAccumulator generateBarnsleyFougere() {
		final ArrayList<AffineTransformation> transformations = new ArrayList<AffineTransformation>();

		transformations.add(new AffineTransformation(0, 0, 0, 0, 0.16, 0));
		transformations.add(new AffineTransformation(0.2, -0.26, 0, 0.23, 0.22, 1.6));
		transformations.add(new AffineTransformation(-0.15, 0.28, 0, 0.26, 0.24, 0.44));
		transformations.add(new AffineTransformation(0.85, 0.04, 0, -0.04, 0.85, 1.6));

		final IFS ifs = new IFS(transformations);
		final Rectangle center = new Rectangle(new Point(0, 4.5), 6, 10);
		return ifs.compute(center, 120, 200, 150);
	}

	/**
	 * Generate the Sierpinski's triangle fractal
	 * 
	 * @return An {@link IFSAccumulator} containing the fractal
	 */
	private static IFSAccumulator generateSierpinskiTriangle() {
		final ArrayList<AffineTransformation> transformations = new ArrayList<AffineTransformation>();

		transformations.add(new AffineTransformation(0.5, 0, 0, 0, 0.5, 0));
		transformations.add(new AffineTransformation(0.5, 0, 0.5, 0, 0.5, 0));
		transformations.add(new AffineTransformation(0.5, 0, 0.25, 0, 0.5, 0.5));

		final IFS ifs = new IFS(transformations);
		final Rectangle center = new Rectangle(new Point(0.5, 0.5), 1, 1);
		return ifs.compute(center, 100, 100, 1);
	}
}
