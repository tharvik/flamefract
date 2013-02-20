package ch.epfl.flamemaker.ifs;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

import ch.epfl.flamemaker.geometry2d.AffineTransformation;
import ch.epfl.flamemaker.geometry2d.Point;
import ch.epfl.flamemaker.geometry2d.Rectangle;

public class IFSMaker {

	public static void main(String[] args) {
		
		try {
			PrintStream file = new PrintStream("SierpinskiTriangle.bpm");

			writeToBPM(generateSierpinskiTriangle(), file);

			file.close();
		} catch (FileNotFoundException e) {
			System.out.println("Not able to open \"SierpinskiTriangle.bpm\"! "
					+ "Abort..");
			System.exit(1);
		}

	}

	public static IFSAccumulator generateSierpinskiTriangle() {
		ArrayList<AffineTransformation> transformations = new ArrayList<AffineTransformation>();
		transformations.add(new AffineTransformation(0.5, 0, 0, 0, 0.5, 0));
		transformations.add(new AffineTransformation(0.5, 0, 0.5, 0, 0.5, 0));
		transformations
				.add(new AffineTransformation(0.5, 0, 0.25, 0, 0.5, 0.5));

		IFS ifs = new IFS(transformations);
		Rectangle center = new Rectangle(new Point(0.5, 0.5), 1, 1);
		return ifs.compute(center, 100, 100, 1);
	}

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
