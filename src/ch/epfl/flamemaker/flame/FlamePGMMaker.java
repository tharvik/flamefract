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
