package ch.epfl.flamemaker.ifs;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

/** 
 * The aim of this class is to create two fractals known as
 * #1 Sierpinskis' Triangle
 * #2 Barnsley's Fougere
 *
 */
public class IFSMaker {

	public static void main(String[] args) {

		try {
			PrintStream file = new PrintStream("SierpinskiTriangle.bpm");

			writeToBPM(generateSierpinskiTriangle(), file);

			file.close();
		} catch (FileNotFoundException e) {
			System.out.println("Not able to open \"SierpinskiTriangle.bpm\"! " + "Abort..");
			System.exit(1);
		}

		try {
			PrintStream file = new PrintStream("BarnsleyFougere.bpm");

			writeToBPM(generateBarnsleyFougere(), file);

			file.close();
		} catch (FileNotFoundException e) {
			System.out.println("Not able to open \"BarnsleyFougere.bpm\"! " + "Abort..");
			System.exit(1);
		}

	}

	private static IFSAccumulator generateSierpinskiTriangle() {
		ArrayList<AffineTransformation> transformations = new ArrayList<AffineTransformation>();

		transformations.add(new AffineTransformation(0.5, 0, 0, 0, 0.5, 0));
		transformations.add(new AffineTransformation(0.5, 0, 0.5, 0, 0.5, 0));
		transformations.add(new AffineTransformation(0.5, 0, 0.25, 0, 0.5, 0.5));

		IFS ifs = new IFS(transformations);
		Rectangle center = new Rectangle(new Point(0.5, 0.5), 1, 1);
		return ifs.compute(center, 100, 100, 1);
	}

	private static IFSAccumulator generateBarnsleyFougere() {
		ArrayList<AffineTransformation> transformations = new ArrayList<AffineTransformation>();

		transformations.add(new AffineTransformation(0, 0, 0, 0, 0.16, 0));
		transformations.add(new AffineTransformation(0.2, -0.26, 0, 0.23, 0.22, 1.6));
		transformations.add(new AffineTransformation(-0.15, 0.28, 0, 0.26, 0.24, 0.44));
		transformations.add(new AffineTransformation(0.85, 0.04, 0, -0.04, 0.85, 1.6));

		IFS ifs = new IFS(transformations);
		Rectangle center = new Rectangle(new Point(0, 4.5), 6, 10);
		return ifs.compute(center, 120, 200, 150);
	}

	/**
	 * Creates the actual image
	 * @param accu The accumulator
	 * @param stream The image file
	 */
	public static void writeToBPM(IFSAccumulator accu, PrintStream stream) {
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
}
