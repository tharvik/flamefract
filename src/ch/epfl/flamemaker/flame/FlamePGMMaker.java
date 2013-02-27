package ch.epfl.flamemaker.flame;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

public class FlamePGMMaker {

	public static void main(String[] args) {

		try {
			PrintStream file = new PrintStream("SharkFin.pgm");

			writeToPGM(generateSharkFin(), file);

			file.close();
		} catch (FileNotFoundException e) {
			System.out.println("Not able to open \"SharkFin.pgm\"! " + "Abort..");
			System.exit(1);
		}

		try {
			PrintStream file = new PrintStream("Turbulence.pgm");

			writeToPGM(generateTurbulence(), file);

			file.close();
		} catch (FileNotFoundException e) {
			System.out.println("Not able to open \"Turbulence.pgm\"! " + "Abort..");
			System.exit(1);
		}

		try {
			PrintStream file = new PrintStream("BarnsleyFougere.bpm");

			writeToPGM(generateBarnsleyFougere(), file);

			file.close();
		} catch (FileNotFoundException e) {
			System.out.println("Not able to open \"BarnsleyFougere.bpm\"! " + "Abort..");
			System.exit(1);
		}

	}

	private static FlameAccumulator generateSharkFin() {
		ArrayList<FlameTransformation> transformations = new ArrayList<FlameTransformation>();
		double[][] array = { { 1, 0.1, 0, 0, 0, 0 }, { 0, 0, 0, 0, 0.8, 1 }, { 1, 0, 0, 0, 0, 0 } };

		AffineTransformation affine = new AffineTransformation(-0.4113504, -0.7124804, -0.4, 0.7124795,
				-0.4113508, 0.8);
		transformations.add(new FlameTransformation(affine, array[0]));

		affine = new AffineTransformation(-0.3957339, 0, -1.6, 0, -0.3957337, 0.2);
		transformations.add(new FlameTransformation(affine, array[1]));

		affine = new AffineTransformation(0.4810169, 0, 1, 0, 0.4810169, 0.9);
		transformations.add(new FlameTransformation(affine, array[2]));

		Flame flame = new Flame(transformations);
		Rectangle center = new Rectangle(new Point(-0.25, 0), 5, 4);
		return flame.compute(center, 500, 400, 50);
	}

	private static FlameAccumulator generateTurbulence() {
		ArrayList<FlameTransformation> transformations = new ArrayList<FlameTransformation>();
		double[][] array = { { 0.5, 0, 0, 0.4, 0, 0 }, { 1, 0, 0.1, 0, 0, 0 }, { 1, 0, 0, 0, 0, 0 } };

		AffineTransformation affine = new AffineTransformation(0.7124807, -0.4113509, -0.3, 0.4113513,
				0.7124808, -0.7);
		transformations.add(new FlameTransformation(affine, array[0]));

		affine = new AffineTransformation(0.3731079, -0.6462417, 0.4, 0.6462414, 0.3731076, 0.3);
		transformations.add(new FlameTransformation(affine, array[1]));

		affine = new AffineTransformation(0.0842641, -0.314478, -0.1, 0.314478, 0.0842641, 0.3);
		transformations.add(new FlameTransformation(affine, array[2]));

		Flame flame = new Flame(transformations);
		Rectangle center = new Rectangle(new Point(0.1, 0.1), 3, 3);
		return flame.compute(center, 500, 500, 50);
	}

	private static FlameAccumulator generateBarnsleyFougere() {
		ArrayList<FlameTransformation> transformations = new ArrayList<FlameTransformation>();
		double[] array = { 1, 0, 0, 0, 0, 0 };

		AffineTransformation affine = new AffineTransformation(0, 0, 0, 0, 0.16, 0);
		transformations.add(new FlameTransformation(affine, array));

		affine = new AffineTransformation(0.2, -0.26, 0, 0.23, 0.22, 1.6);
		transformations.add(new FlameTransformation(affine, array));

		affine = new AffineTransformation(-0.15, 0.28, 0, 0.26, 0.24, 0.44);
		transformations.add(new FlameTransformation(affine, array));

		affine = new AffineTransformation(0.85, 0.04, 0, -0.04, 0.85, 1.6);
		transformations.add(new FlameTransformation(affine, array));

		Flame flame = new Flame(transformations);
		Rectangle center = new Rectangle(new Point(0, 4.5), 6, 10);
		return flame.compute(center, 120, 200, 150);
	}

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